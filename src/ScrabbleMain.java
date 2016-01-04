/**
 * Plays a game of Scrabble between two players with the option of one computer player
 * 
 * @version January 2014
 * 
 */

import java.awt.*;

import javax.swing.*;

import java.awt.event.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;

public class ScrabbleMain extends JFrame implements ActionListener
{
	// Coordinates of each side of the board
	public static final int TOP_OFFSET = 23;
	public static final int LEFT_OFFSET = 43;
	public static final int BOTTOM_COORDINATE = 529;
	public static final int RIGHT_COORDINATE = 521;

	public static final int ROW_SPACING = Tile.HEIGHT_SMALL + 2;
	public static final int COL_SPACING = Tile.WIDTH_SMALL + 3;

	private static final int ANIMATION_FRAMES = 6;
	private static final boolean ANIMATION_ON = true;
	private static final Point[] LOCATION_OF_RACK_TILES = { new Point(30, 553),
			new Point(103, 553), new Point(176, 553), new Point(249, 553),
			new Point(322, 553), new Point(395, 553), new Point(468, 553) };
	private static final int RACK_SPACING = 73;
	public static final int RACK_TOP_OFFSET = 553;
	public static final int RACK_LEFT_OFFSET = 30;

	private static final int TITLE_SCREEN = 1;
	private static final int PLAYER_SELECTION = 2;
	private static final int GAME_SCREEN = 3;
	private static final int PLAYER_ONE_TURN_SCREEN = 1;
	private static final int PLAYER_TWO_TURN_SCREEN = 2;

	private JMenuItem newOption, exitOption, viewScoresOption, optionsMenuItem,
			aboutMenuItem;

	private DrawingPanel boardArea;
	private Image titleScreen, playerScreen, playerOneTurnScreen,
			playerTwoTurnScreen, gameScreen, passIcon, playIcon, helpScreen1,
			helpScreen2, helpScreen3;
	private Image[] tileImages;

	private ArrayList<Player> topPlayers;
	private Board gameBoard;
	private Pile deck;
	private StringBuilder playerData;
	private Player playerOne;
	private Player playerTwo;
	private boolean gameOver;
	private ArrayList<Tile> playedTiles;
	private ArrayList<Tile> turnTiles;
	private Point lastPoint;
	private int originalRow, originalCol;
	private int screenNo;
	private int turnScreenNo;
	private int helpScreenNo;
	private int computerType;
	private Tile currentTile;
	private Player currentPlayer;
	private int currentScore;
	private Point previousPos;
	private boolean wordIsValid;
	private int noOfTurns;
	private int turnsLeft;
	private boolean countdownStarted;
	private boolean exchangeClicked;
	private boolean isComputer;
	private boolean computerThinking;
	private boolean computerPassed;
	private Word currentWord;

	/**
	 * Creates a simple Scrabble Frame Application
	 */
	public ScrabbleMain()
	{
		super("Scrabble");
		setResizable(false);

		// Load up the icon image
		setIconImage(Toolkit.getDefaultToolkit().getImage(
				"images\\Scrabble Icon.png"));

		// Load up game images
		titleScreen = new ImageIcon("images\\TitleScreen.png").getImage();
		playerScreen = new ImageIcon("images\\ChoosingPlayers.png").getImage();
		playerOneTurnScreen = new ImageIcon("images\\PlayerOneTurn.png")
				.getImage();
		playerTwoTurnScreen = new ImageIcon("images\\PlayerTwoTurn.png")
				.getImage();
		helpScreen1 = new ImageIcon("images\\BasicRules.png").getImage();
		helpScreen2 = new ImageIcon("images\\Scoring.png").getImage();
		helpScreen3 = new ImageIcon("images\\IconsHelp.png").getImage();
		gameScreen = new ImageIcon("images\\Board.png").getImage();
		passIcon = new ImageIcon("images\\Pass.png").getImage();
		playIcon = new ImageIcon("images\\Play.png").getImage();

		// Set up Menu
		JMenuBar menuBar = new JMenuBar();
		JMenu gameMenu = new JMenu("Game");

		// Set up Game Menu items
		newOption = new JMenuItem("New Game");
		newOption.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,
				InputEvent.CTRL_MASK));
		newOption.addActionListener(this);

		exitOption = new JMenuItem("Exit");
		exitOption.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X,
				InputEvent.CTRL_MASK));
		exitOption.addActionListener(this);

		gameMenu.add(newOption);
		gameMenu.addSeparator();
		gameMenu.add(exitOption);

		// Set up High Score Menu Option
		JMenu scoresMenu = new JMenu("High Scores");
		viewScoresOption = new JMenuItem("View");
		viewScoresOption.addActionListener(this);
		scoresMenu.add(viewScoresOption);

		// Set up About Menu
		JMenu helpMenu = new JMenu("Help");

		optionsMenuItem = new JMenuItem("Options");
		optionsMenuItem.addActionListener(this);
		aboutMenuItem = new JMenuItem("About...");
		aboutMenuItem.addActionListener(this);

		helpMenu.add(optionsMenuItem);
		helpMenu.add(aboutMenuItem);

		menuBar.add(gameMenu);
		menuBar.add(scoresMenu);
		menuBar.add(helpMenu);
		setJMenuBar(menuBar);

		// Set up the layout and add in a DrawingPanel for the cardArea
		// Centre the frame in the middle (almost) of the screen
		setLayout(new BorderLayout());
		boardArea = new DrawingPanel();
		add(boardArea, BorderLayout.CENTER);
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((screen.width - boardArea.WIDTH) / 2,
				(screen.height - boardArea.HEIGHT) / 2 - 52);

		// Try to open the file and read in the top player information
		try
		{
			// Read the entire ArrayList from a file
			ObjectInputStream fileIn = new ObjectInputStream(
					new FileInputStream("topPlayers.dat"));
			topPlayers = (ArrayList<Player>) fileIn.readObject();
			fileIn.close();
		}
		catch (Exception fileNotFound)
		{
			// If we had trouble reading the file (e.g. it doesn’t exist) or
			// if our file has errors an Exception will be thrown and we can
			// create a new empty list
			topPlayers = new ArrayList<Player>();
		}

		// Initialize variables, including board and pile
		gameBoard = new Board();
		deck = new Pile();

		playedTiles = new ArrayList<Tile>();
		turnTiles = new ArrayList<Tile>();
		playerData = new StringBuilder();

		screenNo = TITLE_SCREEN;
		helpScreenNo = 0;
	}

	/**
	 * Responds to a Menu Event
	 * 
	 * @param event the event that triggered this method
	 */
	public void actionPerformed(ActionEvent event)
	{
		if (event.getSource() == newOption) // Selected "New Game"
		{
			newGame();
		}
		else if (event.getSource() == exitOption) // Selected "Exit"
		{
			System.exit(0);
		}
		else if (event.getSource() == viewScoresOption) // Selected "View"
		{
			JOptionPane.showMessageDialog(boardArea, playerData.toString(),
					"High Scores", JOptionPane.INFORMATION_MESSAGE);
		}
		else if (event.getSource() == aboutMenuItem) // Selected "About"
		{
			JOptionPane
					.showMessageDialog(
							boardArea,
							"Scrabble by Victor Nechita,\n Melissa Li, and Ivy Zhou\n2013",
							"About Scrabble", JOptionPane.INFORMATION_MESSAGE);
		}
	}

	/**
	 * Starts a new game by shuffling the deck and re-initialising the racks and
	 * clearing the board and tile lists. The screen remains in the game board
	 * screen.
	 */
	public void newGame()
	{
		screenNo = GAME_SCREEN;
		turnScreenNo = 0;

		// Clear the game board, recall all tiles into the deck, shuffle the
		// deck, and reset the number of turns
		gameBoard.clear();
		deck.recall();
		deck.shuffle();
		noOfTurns = 0;

		// Re-initializing other variables
		exchangeClicked = false;
		currentWord = null;
		computerThinking = false;
		gameOver = false;
		wordIsValid = false;

		// Counts down when there are no tiles left in the bag
		turnsLeft = 5;

		// Make all tiles in the deck large again
		deck.resetSizes();

		// Clear tile lists
		playedTiles.clear();
		turnTiles.clear();

		// Create new racks of 7 tiles
		ArrayList<Tile> firstRack = new ArrayList<Tile>();
		ArrayList<Tile> secondRack = new ArrayList<Tile>();

		// Deal tiles to the two racks and set the tile locations
		for (int tile = 0; tile < 7; tile++)
		{
			Tile nextTile = deck.deal();
			firstRack.add(nextTile);
			nextTile.setLocation(LOCATION_OF_RACK_TILES[tile]);

			nextTile = deck.deal();
			secondRack.add(nextTile);
			nextTile.setLocation(LOCATION_OF_RACK_TILES[tile]);
		}

		// Create two new players
		playerOne = new Player(firstRack, 1);

		// Create the second player depending on what the player chooses
		if (computerType == Player.EASY_COMPUTER)
		{
			playerTwo = new Player(gameBoard, secondRack, Player.EASY_COMPUTER,
					gameBoard.getWordsOnBoard(), playedTiles, 2);
			isComputer = true;
		}
		else if (computerType == Player.HARD_COMPUTER)
		{
			playerTwo = new Player(gameBoard, secondRack, Player.HARD_COMPUTER,
					gameBoard.getWordsOnBoard(), playedTiles, 2);
			isComputer = true;
		}
		else
		{
			playerTwo = new Player(secondRack, 2);
			isComputer = false;
		}

		// Sets the current player
		currentPlayer = playerOne;

		repaint();
	}

	/**
	 * Refresh the drawing area immediately Immediate refresh is needed to show
	 * the animation
	 */
	private void rePaintDrawingAreaImmediately()
	{
		boardArea.paintImmediately(new Rectangle(0, 0, boardArea.getWidth(),
				boardArea.getHeight()));
	}

	/**
	 * Inner class to keep track of the board area
	 */
	private class DrawingPanel extends JPanel
	{
		final int WIDTH = 743;
		final int HEIGHT = 650;

		public DrawingPanel()
		{
			setPreferredSize(new Dimension(WIDTH, HEIGHT));
			setFont(new Font("Arial", Font.PLAIN, 18));
			this.addMouseListener(new ScrabbleMouseHandler());
			this.addMouseMotionListener(new MouseMotionHandler());
		}

		/**
		 * Paints the drawing area
		 * 
		 * @param g the graphics context to paint
		 */
		public void paintComponent(Graphics g)
		{
			super.paintComponent(g);

			g.setFont(new Font("Arial", Font.BOLD, 15));

			// Draw the screens
			if (screenNo == TITLE_SCREEN)
			{
				g.drawImage(titleScreen, 0, 0, this);

				// Draw the help screens if selected
				if (helpScreenNo == 1)
					g.drawImage(helpScreen1, 0, 0, this);
				else if (helpScreenNo == 2)
					g.drawImage(helpScreen2, 0, 0, this);
				else if (helpScreenNo == 3)
					g.drawImage(helpScreen3, 0, 0, this);
			}

			// Draw the player selection screen
			else if (screenNo == PLAYER_SELECTION)
				g.drawImage(playerScreen, 0, 0, this);

			// Draw game items
			else
			{
				// Draw the board
				g.drawImage(gameScreen, 0, 0, this);

				// Draw the play or pass icon and string depending on whether
				// the word is valid or not and string showing whether it is
				// valid

				g.setColor(Color.red);
				if (wordIsValid)
				{
					g.setColor(Color.green);
					g.drawString("Valid Word", 600, 30);
					g.drawString("Word Score: " + currentScore, 600, 50);

					// If the computer is playing, we don't want the play icon
					// to be drawn
					if (!(isComputer && currentPlayer.equals(playerTwo)))
						g.drawImage(playIcon, 613, 520, this);
					else
						g.drawImage(passIcon, 613, 520, this);
				}

				else
				{
					g.drawString("Word(s) Not Valid", 600, 30);
					g.drawImage(passIcon, 613, 520, this);
				}

				// Draw when the computer is making its turn
				if (computerThinking)
				{
					g.drawString("CPU is thinking...", 600, 50);
				}
				else if (computerPassed)
				{
					g.drawString("CPU has passed", 600, 50);
				}

				// Draw the player scores
				g.setColor(Color.black);
				g.drawString("Scores", 600, 80);

				// Highlight a player depending on who's turn it is
				if (currentPlayer.equals(playerOne))
				{
					g.setColor(Color.green);
					g.drawString("Player One: " + playerOne.getScore(), 600,
							110);
					g.setColor(Color.black);
					g.drawString("Player Two: " + playerTwo.getScore(), 600,
							130);
				}
				else
				{
					g.drawString("Player One: " + playerOne.getScore(), 600,
							110);
					g.setColor(Color.green);
					g.drawString("Player Two: " + playerTwo.getScore(), 600,
							130);
				}

				// Draw the tiles of the current player's rack
				ArrayList<Tile> currentRack = currentPlayer.getRack();
				for (Tile tile : currentRack)
				{
					tile.draw(g);
				}

				// Draw all the tiles on the board
				for (Tile tile : playedTiles)
				{
					tile.draw(g);
				}

				// Draw the current tile last so it's on top
				if (currentTile != null)
				{
					currentTile.draw(g);
				}

				// Draw player turn screens if it's the next turn
				if (turnScreenNo == PLAYER_ONE_TURN_SCREEN)
					g.drawImage(playerOneTurnScreen, 0, 0, this);
				if (turnScreenNo == PLAYER_TWO_TURN_SCREEN)
					g.drawImage(playerTwoTurnScreen, 0, 0, this);
			}

		}
	}

	/**
	 * Places a tile in the given row and column on the board and marks this
	 * spot as taken
	 * 
	 * @param tile the Tile to place
	 * @param row the row to place the tile
	 * @param column the column to place the tile Precondition row and column
	 *            are on the board
	 */
	private void placeATile(Tile tile, int row, int column)
	{
		// Calculate the location on the board based on row and column
		Point newTilePos = new Point(column * COL_SPACING + LEFT_OFFSET, row
				* ROW_SPACING + TOP_OFFSET);

		// Animate the tile moving to its location
		if (ANIMATION_ON)
		{
			moveATile(tile, newTilePos);
		}
		else
			tile.setLocation(newTilePos);

		// If the tile is Blank, set the tile before playing it
		if (tile.isBlank() && !tile.isSet)
		{
			String input = (String) JOptionPane
					.showInputDialog(boardArea,
							"Please set a letter for this tile (A to Z): ",
							"Set Blank Tile", JOptionPane.PLAIN_MESSAGE, null,
							null, "");

			// If there was no input, set it to A
			if (input == null || input.equals(""))
			{
				JOptionPane.showMessageDialog(boardArea,
						"Invalid letter. Tile set to A");
				input = "A";
			}

			// Read the letter in
			char letter = input.toUpperCase().charAt(0);

			// Check if they entered a valid alphabet letter
			if (letter >= 'A' && letter <= 'Z')
			{
				tile.set(letter);
			}

			// If they did not enter a valid character, automatically set it to
			// A
			else
			{
				JOptionPane.showMessageDialog(boardArea,
						"Invalid letter. Tile set to A");
				tile.set('A');
			}
		}

		// Set the location of the tile on the board
		tile.setPos(row, column);

		// Add tile to the board
		gameBoard.getBoard()[row][column] = tile;

		// Add to the playedTiles if not already in there
		if (!playedTiles.contains(tile))
		{
			playedTiles.add(tile);
		}

		// Add to the turnTiles if not already in there
		if (!turnTiles.contains(tile))
		{
			turnTiles.add(tile);
		}

		// Update score for the word being played
		updateTurnScore();
	}

	/**
	 * Animates a single tile being returned to the rack
	 * 
	 * @param tile the Tile to place
	 * @param pos the point on the rack to place the tile at
	 */
	private void returnToRack(Tile tile, Point pos)
	{
		currentPlayer.getRack().add(tile);

		if (tile.isBlank())
		{
			tile.reset();
		}

		// Get the point on the rack to return to
		int tileNo = (pos.x - RACK_LEFT_OFFSET) / RACK_SPACING;
		Point rackPos = new Point(LOCATION_OF_RACK_TILES[tileNo]);

		if (ANIMATION_ON)
			moveATile(tile, rackPos);
		else
			tile.setLocation(rackPos);
	}

	/**
	 * Deals new tiles until the current rack is filled again. To be used after
	 * a turn has been made.
	 */
	private void fillRack()
	{
		// Find the spot of the rack to deal to
		Point nextSpot = findNextSpot(currentPlayer.getRack());
		while (nextSpot != null)
		{
			// Deal new tiles as long as there are empty spots on the rack
			Tile nextTile = deck.deal();
			if (nextTile == null)
			{
				// If the deck no longer has tiles, begin the countdown for
				// turns to the end of the game
				countdownStarted = true;
				nextSpot = null;
			}
			else
			{
				currentPlayer.getRack().add(nextTile);
				nextTile.setLocation(nextSpot);
				nextSpot = findNextSpot(currentPlayer.getRack());
			}
		}
	}

	/**
	 * Finds the next available spot on the rack
	 * 
	 * @param the rack to check
	 * @return next available spot as a point
	 */
	public static Point findNextSpot(ArrayList<Tile> rack)
	{
		boolean[] spotsTaken = new boolean[7];

		// Find all the spots on the rack being taken
		for (Tile next : rack)
		{
			Point tileLocation = new Point(next.getLocation());

			// Find the spot being taken by this tile
			int spotIndex = (tileLocation.x - RACK_LEFT_OFFSET) / RACK_SPACING;
			spotsTaken[spotIndex] = true;
		}

		// Go through the spots, find the next free spot, and return the
		// location
		for (int spot = 0; spot < spotsTaken.length; spot++)
		{
			if (!spotsTaken[spot])
			{
				return LOCATION_OF_RACK_TILES[spot];
			}
		}

		// If there were no free spots
		return null;
	}

	/**
	 * Switches the turn to the next player and displays the screen notifying
	 * the players
	 */
	public void changeTurns()
	{
		// Change turns
		if (currentPlayer.equals(playerOne))
		{
			currentPlayer = playerTwo;
			turnScreenNo = PLAYER_TWO_TURN_SCREEN;
		}
		else
		{
			currentPlayer = playerOne;
			turnScreenNo = PLAYER_ONE_TURN_SCREEN;
		}
	}

	/**
	 * Inner class to handle mouse events Extends MouseAdapter instead of
	 * implementing MouseListener since we only need to override mousePressed
	 */
	private class ScrabbleMouseHandler extends MouseAdapter
	{
		/**
		 * Handles a mousePress when selecting a spot to place a card
		 * 
		 * @param event the event information
		 */
		public void mousePressed(MouseEvent event)
		{
			setCursor(Cursor.getDefaultCursor());
			// If the game is over, we disable any mouse presses
			if (gameOver)
			{
				if (JOptionPane.showConfirmDialog(boardArea,
						"Do you want to Play Again?", "Game Over",
						JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
					newGame();
				return;
			}

			Point clickedPoint = event.getPoint();

			// If we're at the title screen
			if (screenNo == TITLE_SCREEN)
			{
				// If we're at the help screens, check if the player is clicking
				// the errors and change the help screens accordingly
				if (helpScreenNo >= 1)
				{
					// If the next button was clicked go to the next help screen
					if (clickedPoint.x >= 620 && clickedPoint.x <= 679
							&& clickedPoint.y >= 542 && clickedPoint.y <= 562)
					{
						helpScreenNo++;

						// If we're at the end, set it to 0 (will draw no help
						// screen in draw code)
						if (helpScreenNo > 3)
							helpScreenNo = 0;
					}
					// If the back button was clicked, go to the previous help
					// screen
					else if (clickedPoint.x >= 68 && clickedPoint.x <= 125
							&& clickedPoint.y >= 544 && clickedPoint.y <= 565)
					{
						helpScreenNo--;
					}
				}

				// Check if the rules button has been clicked
				else if (clickedPoint.x >= 220 && clickedPoint.x <= 367
						&& clickedPoint.y >= 539 && clickedPoint.y <= 611)
				{
					helpScreenNo = 1;
				}

				// Check if the play button has been clicked
				else if (clickedPoint.x >= 37 && clickedPoint.x <= 182
						&& clickedPoint.y >= 536 && clickedPoint.y <= 611)
				{
					// Go to the player selection screen to determine who to
					// play against
					screenNo = PLAYER_SELECTION;
				}
			}

			// If we're at the player selection screen
			else if (screenNo == PLAYER_SELECTION)
			{
				// Check if a human player was clicked
				if (clickedPoint.x >= 51 && clickedPoint.x <= 629
						&& clickedPoint.y >= 237 && clickedPoint.y <= 319)
				{
					computerType = 0;
					newGame();
				}

				// Check if the easy computer player was clicked
				else if (clickedPoint.x >= 98 && clickedPoint.x <= 354
						&& clickedPoint.y >= 449 && clickedPoint.y <= 528)
				{
					computerType = 1;
					newGame();
				}

				// Check if the hard computer player was clicked
				else if (clickedPoint.x >= 412 && clickedPoint.x <= 652
						&& clickedPoint.y >= 445 && clickedPoint.y <= 525)
				{
					computerType = 2;
					newGame();
				}
			}

			// If we're displaying the screen that shows whose turn it is
			else if (turnScreenNo == PLAYER_ONE_TURN_SCREEN
					|| turnScreenNo == PLAYER_TWO_TURN_SCREEN)
			{
				turnScreenNo = 0;
				rePaintDrawingAreaImmediately();

				// Clear the tiles played this turn
				turnTiles.clear();

				// If it's the computer's turn, make a move
				if (isComputer && currentPlayer.equals(playerTwo))
				{
					// Show that the computer is thinking
					computerThinking = true;
					rePaintDrawingAreaImmediately();

					currentWord = currentPlayer.makeMove();

					// If the computer could make a valid word
					if (currentWord != null)
					{
						// Calculate the score
						currentScore = currentWord.getScore();

						// Add the word to the list of words on the board
						gameBoard.addWord(currentWord);
						computerThinking = false;

						// Animate the tiles moving to their locations and
						// lock
						// them into the board
						for (int i = 0; i < currentWord.length(); i++)
						{
							Tile next = currentWord.getTile(i);
							Point boardLocation = new Point(next.getCol()
									* ScrabbleMain.COL_SPACING
									+ ScrabbleMain.LEFT_OFFSET, next.getRow()
									* ScrabbleMain.ROW_SPACING
									+ ScrabbleMain.TOP_OFFSET);
							moveATile(next, boardLocation);
							next.lock();
						}

						// Show how the CPU's word is valid and add to its
						// score
						currentPlayer.addScore(currentScore);
						wordIsValid = true;
						computerThinking = false;
						rePaintDrawingAreaImmediately();

						// Deal new tiles to the computer
						fillRack();

						// Delay to let the user see the tiles played
						delay(2000);
					}
					else
					{
						// If not, show that the computer has passed
						computerPassed = true;
						computerThinking = false;
						rePaintDrawingAreaImmediately();
						delay(1500);
					}

					// Switch the turns and reset the variables for the next
					// turn
					changeTurns();
					computerPassed = false;
					wordIsValid = false;
					currentScore = 0;
					currentWord = null;

					// Show changes
					rePaintDrawingAreaImmediately();
				}
			}

			// If we're on the game screen
			else
			{
				// If the computer is making a turn, disable any mouse presses
				if (currentPlayer.equals(playerTwo) && isComputer)
					return;

				// If the mouse is in the board area
				if (isOnBoard(clickedPoint))
				{
					// If the exchange icon was previously clicked
					if (exchangeClicked)
					{
						// Let the user know
						JOptionPane.showMessageDialog(boardArea,
								"A rack tile was not selected.");
						exchangeClicked = false;
					}

					// Otherwise try to find whether a tile was clicked
					else
					{
						// Figure out the selected row and column on the board
						int row = (clickedPoint.y - TOP_OFFSET) / ROW_SPACING;
						int column = (clickedPoint.x - LEFT_OFFSET)
								/ COL_SPACING;

						// Find out the selected tile;
						currentTile = gameBoard.getBoard()[row][column];

						// If there is a tile and it wasn't locked in
						if (currentTile != null && !currentTile.isPlayed())
						{
							// Keep track of its original position in case we
							// need to return it
							lastPoint = new Point(event.getPoint());
							originalRow = row;
							originalCol = column;

							// For translating the tile if dragged or moved
							previousPos = clickedPoint;

							// Remove it from the board, total played tiles, and
							// tiles played this turn
							gameBoard.getBoard()[row][column] = null;
							playedTiles.remove(currentTile);
							turnTiles.remove(currentTile);

							// Update score for this turn
							updateTurnScore();
						}

						// If a tile wasn't clicked, we can return early
						else
						{
							currentTile = null;
							return;
						}
					}
				}

				// If the mouse is on one of the rack tiles
				for (Tile tile : currentPlayer.getRack())
				{
					// Keep track of the tile clicked and its original position
					if (tile.contains(clickedPoint))
					{
						lastPoint = new Point(event.getPoint());
						previousPos = clickedPoint;
						currentTile = tile;
					}
				}

				// If we previously clicked to exchange the tile
				if (exchangeClicked)
				{
					// Exchange the tile that was chosen
					if (currentTile != null)
					{

						// Exchange while checking to see whether it was
						// successful or not
						Tile exchangeTile = exchange(currentTile);

						// Set the currentTile to null so we don't pick it up
						currentTile = null;

						if (exchangeTile == null)
							JOptionPane.showMessageDialog(boardArea,
									"No more tiles in the pile.");

						// If it was successful, go on to the next turn
						else
						{
							// Recall any tiles currently on the board
							recallTiles();

							// Show the tile it was exchanged to
							rePaintDrawingAreaImmediately();
							delay(500);

							if (exchangeTile.isBlank())
								JOptionPane
										.showMessageDialog(boardArea,
												"Tile successfully exchanged. You got a blank tile!");
							else
								JOptionPane.showMessageDialog(boardArea,
										"Tile successfully exchanged. You got a(n) "
												+ exchangeTile.getChar() + "!");

							// Go on the the next turn
							changeTurns();
						}
					}
					else
						JOptionPane.showMessageDialog(boardArea,
								"A tile was not selected.");
					exchangeClicked = false;
				}

				// If the user wishes to pick it up rather than exchanged,
				// remove the tile from the rack
				else if (currentTile != null)
					currentPlayer.getRack().remove(currentTile);

				// If the mouse is on the recall icon
				if (clickedPoint.x >= 615 && clickedPoint.x <= 691
						&& clickedPoint.y >= 196 && clickedPoint.y <= 282)
				{
					recallTiles();
				}
				// If the mouse is on the shuffle icon
				else if (clickedPoint.x >= 617 && clickedPoint.x <= 706
						&& clickedPoint.y >= 329 && clickedPoint.y <= 376)
				{
					currentPlayer.shuffleRack();
				}
				// If the mouse is on the exchange icon
				else if (clickedPoint.x >= 622 && clickedPoint.x <= 711
						&& clickedPoint.y >= 444 && clickedPoint.y <= 497)
				{
					JOptionPane
							.showMessageDialog(boardArea,
									"Click the tile on your rack that you would like to exchange.");
					exchangeClicked = true;
				}
				// If the mouse is on the play or pass icon
				else if (clickedPoint.x >= 618 && clickedPoint.x <= 666
						&& clickedPoint.y >= 552 && clickedPoint.y <= 592)
				{
					if (wordIsValid)
					{
						// Add the current word score to the current player's
						// score and add to the list of words on the board
						currentPlayer.addScore(currentScore);
						currentScore = 0;
						gameBoard.addWord(currentWord);

						// Reset turn variables
						currentWord = null;
						wordIsValid = false;

						// Keep track so we know the first word was played (for
						// special first turn case)
						if (noOfTurns == 0)
							noOfTurns++;
					}
					// If it's not valid, the user passed so recall tiles
					else
					{
						recallTiles();
					}

					// Lock the turn tiles in
					for (Tile next : turnTiles)
						next.lock();

					// Clear the turn tiles
					turnTiles.clear();

					// Add new tiles to the current players rack
					fillRack();

					// If it's near the end of the game, let the user know
					if (countdownStarted)
					{
						turnsLeft--;
						if (turnsLeft == 1)
							JOptionPane.showMessageDialog(boardArea, turnsLeft
									+ " turn left.");

						// If there are no more turns left, the game is over
						// and record player scores
						else if (turnsLeft == 0)
						{
							// Find the top player in the game
							Player topPlayer;
							if (playerOne.getScore() > playerTwo.getScore())
								topPlayer = playerOne;
							else
								topPlayer = playerTwo;

							gameOver = true;
							rePaintDrawingAreaImmediately();

							// Keep track of whether the top scores list changed

							if (!(isComputer && topPlayer.equals(playerTwo)))
							{
								boolean listChanged = false;

								// If the list is less than 5 players or this
								// player's score is high enough to get on the
								// list,
								// add it to the top scores list
								if (topPlayers.size() <= 4
										|| topPlayer.compareTo(topPlayers
												.get(topPlayers.size() - 1)) < 0)
								{
									topPlayers.add(topPlayer);
									Collections.sort(topPlayers);
									listChanged = true;
								}

								// If the size of the list is now larger than 5,
								// remove the last score
								if (topPlayers.size() > 5)
								{
									topPlayers.remove(topPlayers.size() - 1);
								}

								// If the player was added, re-record the top
								// players
								if (listChanged)
								{
									// Ask for and store the name of the player
									String name = JOptionPane
											.showInputDialog(
													boardArea,
													"To save your score, please enter your name: ",
													topPlayer + " Won!",
													JOptionPane.INFORMATION_MESSAGE);

									topPlayer.setName(name);

									// Write the entire ArrayList to a file
									try
									{
										ObjectOutputStream fileOut;
										fileOut = new ObjectOutputStream(
												new FileOutputStream(
														"topPlayers.dat"));
										fileOut.writeObject(topPlayers);
										fileOut.close();
									}
									catch (Exception FileNotFound)
									{
										System.out.println("Can't write file");
									}

									// Build the top scores list
									for (Player player : topPlayers)
									{
										playerData.append(player
												.getPlayerData() + "\n");
									}
								}
							}
							if (JOptionPane.showConfirmDialog(boardArea,
									"Do you want to Play Again?",
									"Game Over", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
							{
								newGame();
								return;
							}
							else
								System.exit(0);
							
						}

						// Otherwise if we still have turns left, show the user
						else
							JOptionPane.showMessageDialog(boardArea, turnsLeft
									+ " turns left.");

					}

					changeTurns();
				}
			}
			// Repaint to show any changes
			repaint();
		}

		public void mouseReleased(MouseEvent event)
		{
			// Only can release a tile we have
			if (currentTile != null)
			{
				setCursor(Cursor.getDefaultCursor());

				// Figure out the selected row and column on the board
				Point releasePoint = event.getPoint();
				int row = (releasePoint.y - TOP_OFFSET) / ROW_SPACING;
				int column = (releasePoint.x - LEFT_OFFSET) / COL_SPACING;

				// If the tile was dropped around the rack area
				if (releasePoint.x >= 23 && releasePoint.x <= 545
						&& releasePoint.y >= 552 && releasePoint.y <= 628)
				{
					// If not occupied, drop it in that spot
					if (!spotOccupied(currentTile, releasePoint))
						returnToRack(currentTile, releasePoint);
					// Otherwise drop it back in its original spot
					else
					{
						// If it was taken from the board
						if (isOnBoard(previousPos))
							placeATile(currentTile, originalRow, originalCol);
						// If it was taken from the rack
						else if (previousPos.x >= RACK_LEFT_OFFSET
								&& previousPos.x <= 540
								&& previousPos.y >= RACK_TOP_OFFSET
								&& previousPos.y <= 628)
							returnToRack(currentTile, previousPos);
					}
				}
				// If off the board or on a taken spot, return to original
				// position
				else if (row < 0 || row > 14 || column < 0 || column > 14
						|| gameBoard.getBoard()[row][column] != null)
				{
					// If it was taken from the board
					if (isOnBoard(previousPos))
						placeATile(currentTile, originalRow, originalCol);
					// If it was taken from the rack
					else if (previousPos.x >= RACK_LEFT_OFFSET
							&& previousPos.x <= 540
							&& previousPos.y >= RACK_TOP_OFFSET
							&& previousPos.y <= 628)
						returnToRack(currentTile, previousPos);
				}
				// Otherwise add to new spot on the board
				else
				{
					placeATile(currentTile, row, column);
				}

				// If off the board or in a taken spot and the last point was in
				// the rack area, return to its original spot

				currentTile = null;
				repaint();
			}
		}
	}

	// Inner Class to handle mouse movements
	private class MouseMotionHandler implements MouseMotionListener
	{
		public void mouseMoved(MouseEvent event)
		{
			Point pos = event.getPoint();

			// If we're at the title screen
			if (screenNo == TITLE_SCREEN)
			{
				// If we're on a help screen and the mouse is on one of the
				// arrows, show the hand
				if (helpScreenNo >= 1)
				{
					if ((pos.x >= 68 && pos.x <= 125 && pos.y >= 544 && pos.y <= 565)
							|| (pos.x >= 620 && pos.x <= 679 && pos.y >= 542 && pos.y <= 562))
					{
						setCursor(Cursor
								.getPredefinedCursor(Cursor.HAND_CURSOR));
					}
					else
					{
						setCursor(Cursor.getDefaultCursor());
					}
				}
				else
				{
					// Check if the mouse is on the play button or rules button
					if ((pos.x >= 37 && pos.x <= 182 && pos.y >= 536 && pos.y <= 611)
							|| (pos.x >= 220 && pos.x <= 367 && pos.y >= 539 && pos.y <= 611))
					{
						setCursor(Cursor
								.getPredefinedCursor(Cursor.HAND_CURSOR));
					}
					else
					{
						setCursor(Cursor.getDefaultCursor());
					}
				}
			}

			// If we're at the player selection screen
			else if (screenNo == PLAYER_SELECTION)
			{
				// Check if the mouse is on one of the buttons
				if ((pos.x >= 51 && pos.x <= 629 && pos.y >= 237 && pos.y <= 319)
						|| (pos.x >= 98 && pos.x <= 354 && pos.y >= 449 && pos.y <= 528)
						|| (pos.x >= 412 && pos.x <= 652 && pos.y >= 445 && pos.y <= 525))
				{
					setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				}
				else
				{
					setCursor(Cursor.getDefaultCursor());
				}
			}

			// If we're in the game screen
			else if (screenNo == GAME_SCREEN)
			{
				// If the computer is making a move, set to default cursor and
				// return
				if (isComputer && currentPlayer.equals(playerTwo))
				{
					setCursor(Cursor.getDefaultCursor());
					return;
				}

				// If the mouse is in the board area
				if (isOnBoard(pos))
				{
					boolean onATile = false;

					int row = (pos.y - TOP_OFFSET) / ROW_SPACING;
					int column = (pos.x - LEFT_OFFSET) / COL_SPACING;
					if (gameBoard.getBoard()[row][column] != null
							&& !gameBoard.getBoard()[row][column].isPlayed())
						onATile = true;

					// Show either a hand (on a tile) or the default cursor
					if (onATile)
					{
						setCursor(Cursor
								.getPredefinedCursor(Cursor.HAND_CURSOR));
						return;
					}
					else
					{
						setCursor(Cursor.getDefaultCursor());
					}
				}

				// If the mouse if on one of the rack tiles, show a hand
				else if (pos.x >= 23 && pos.x <= 545 && pos.y >= 552
						&& pos.y <= 628)
				{
					boolean onATile = false;
					for (Tile next : currentPlayer.getRack())
					{
						if (next.contains(pos))
							onATile = true;
					}
					if (onATile)
						setCursor(Cursor
								.getPredefinedCursor(Cursor.HAND_CURSOR));
					else
						setCursor(Cursor.getDefaultCursor());
				}

				// If the mouse is on one of the icons show a hand
				else if ((pos.x >= 615 && pos.x <= 691 && pos.y >= 196 && pos.y <= 282)
						|| (pos.x >= 617 && pos.x <= 706 && pos.y >= 329 && pos.y <= 376)
						|| (pos.x >= 622 && pos.x <= 711 && pos.y >= 444 && pos.y <= 497)
						|| (pos.x >= 618 && pos.x <= 666 && pos.y >= 552 && pos.y <= 592))

					setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				else
					setCursor(Cursor.getDefaultCursor());

			}
		}

		public void mouseDragged(MouseEvent event)
		{
			Point currentPoint = event.getPoint();

			// Move the tile we're on as we are dragging it
			if (currentTile != null)
			{
				setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

				// Set the size depending on its location and centre it as it
				// changes size
				setSize(currentTile, lastPoint);
				currentTile.centreTile(currentPoint);

				// We use the difference between the lastPoint and the
				// currentPoint to move the tile so that the position of
				// the mouse on the card doesn't matter.
				currentTile.translate(currentPoint.x - lastPoint.x,
						currentPoint.y - lastPoint.y);
				lastPoint = currentPoint;
				repaint();
			}
		}
	}

	/**
	 * Updates the score displayed for the current word
	 */
	private void updateTurnScore()
	{
		// Find the word being played
		Word word = gameBoard.isAligned(turnTiles);

		// If the tiles are in a row and a word exists
		if (word != null)
		{
			// Store the word
			currentWord = word;

			// Special case for if it's the first turn
			if (noOfTurns == 0)
			{
				// Check if one of the tiles is on the star
				boolean isOnStar = false;
				for (int i = 0; i < word.length() && !isOnStar; i++)
				{
					Tile next = word.getTile(i);
					if (next.getRow() == 7 && next.getCol() == 7)
					{
						isOnStar = true;
					}
				}

				// If it's on the star and the word is in the dictionary set the
				// score
				if (isOnStar && gameBoard.dictionary.isWord(word.toString()))
				{
					currentScore = gameBoard.getScore(word, turnTiles);
				}
				else
					currentScore = 0;
			}
			// Otherwise, calculate the score normally
			// Check if the tiles being played are connected to an already
			// played word
			else if (gameBoard.isConnected(turnTiles) > 0)
			{
				currentScore = gameBoard.getTotalScore(word, turnTiles);

				// If seven tiles were played at once, add a fifty point bonus
				if (turnTiles.size() == 7)
					currentScore += 50;
			}

			// Set word is valid
			if (currentScore > 0)
				wordIsValid = true;
			else
				wordIsValid = false;
		}

		// If the word is not aligned
		else
		{
			wordIsValid = false;
			currentScore = 0;
		}
	}

	/**
	 * Checks to see if a given point is on the board area
	 * 
	 * @param pos the point to check
	 * @return true if the point is on the board, false if not
	 */
	private boolean isOnBoard(Point pos)
	{
		return (pos.y >= TOP_OFFSET && pos.y <= BOTTOM_COORDINATE
				&& pos.x >= LEFT_OFFSET && pos.x <= RIGHT_COORDINATE);
	}

	/**
	 * Checks to see if a given spot on the rack is occupied given a point
	 * 
	 * @param the tile being moved to the spot
	 * @param pos the point to check
	 * @return true if the spot is occupied, false if not
	 */
	private boolean spotOccupied(Tile tile, Point pos)
	{
		// Find the spot trying to be taken by the tile
		int tileNo = (pos.x - RACK_LEFT_OFFSET) / RACK_SPACING;
		Point rackPos = new Point(LOCATION_OF_RACK_TILES[tileNo]);

		// Go through the rack and see if the current spot is taken
		for (Tile next : currentPlayer.getRack())
		{
			if (next.contains(rackPos) && !next.equals(tile))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns and animates all the tiles being played on the board back to the
	 * rack
	 */
	private void recallTiles()
	{
		for (Tile next : turnTiles)
		{
			// Special case for blank tiles
			if (next.isBlank())
				next.reset();

			// Figure out the original row and column of the tile on the
			// board and clear that location on the board
			int row = (next.getLocation().y - TOP_OFFSET) / ROW_SPACING;
			int column = (next.getLocation().x - LEFT_OFFSET) / COL_SPACING;
			gameBoard.getBoard()[row][column] = null;

			// Send the tiles back to the rack and add to it
			moveATile(next, findNextSpot(currentPlayer.getRack()));
			currentPlayer.getRack().add(next);

			// Remove the tile from the tiles played
			playedTiles.remove(next);
		}

		currentScore = 0;
		wordIsValid = false;
		turnTiles.clear();

	}

	/**
	 * Exchanges a tile of the player's choice with a random tile from the deck
	 * 
	 * @param the position of the player's click when choosing which tile to
	 *            exchange
	 * @return the tile the original was exchanged to
	 */
	private Tile exchange(Tile selectedTile)
	{
		// If the deck is empty, exit
		if (deck.getNoOfTilesLeft() == 0)
			return null;

		ArrayList<Tile> currentRack = currentPlayer.getRack();

		// Find a random tile from the pile and add to the current player's
		// rack, setting it's location
		int exchangeIndex = (int) (Math.random() * deck.getNoOfTilesLeft());
		Tile exchangeTile = deck.dealTile(exchangeIndex);
		currentRack.add(exchangeTile);
		exchangeTile.setLocation(currentTile.getLocation());

		// Remove original tile from the current player's rack and swap the
		// old tile and exchange tile locations in deck
		currentRack.remove(selectedTile);
		deck.insertTile(exchangeTile, deck.getLocation(selectedTile));
		deck.insertTile(selectedTile, exchangeIndex);

		return exchangeTile;
	}

	/**
	 * Sets the size of the tile depending on whether it's on the board or not
	 * 
	 * @param tile the tile to set the size of
	 * @param pos the location of the tile
	 */
	private boolean setSize(Tile tile, Point pos)
	{
		// Shrink the tile if it's in the board area
		if (isOnBoard(pos))
		{
			return tile.changeSize(0);
		}
		// Grow the tile if it's off the board
		else
		{
			return tile.changeSize(1);
		}
	}

	/**
	 * Moves a card with a simple animation
	 * 
	 * @param tileToMove the Tile you want to move
	 * @param finalPos the final position of the Tile
	 */
	private void moveATile(Tile tileToMove, Point finalPos)
	{
		int x = tileToMove.x;
		int y = tileToMove.y;
		int dx = (finalPos.x - x) / ANIMATION_FRAMES;
		int dy = (finalPos.y - y) / ANIMATION_FRAMES;

		// Animate the tile moving toward its location
		for (int times = 1; times <= ANIMATION_FRAMES; times++)
		{
			x += dx;
			y += dy;
			tileToMove.setLocation(x, y);
			rePaintDrawingAreaImmediately();
			delay(50);
		}

		// Set the tile's final location and size
		tileToMove.setLocation(finalPos);
		setSize(tileToMove, finalPos);

		// Clear up whole card area
		rePaintDrawingAreaImmediately();
	}

	/**
	 * Delays the given number of milliseconds
	 * 
	 * @param msec the number of milliseconds to delay
	 */
	private void delay(int msec)
	{
		try
		{
			Thread.sleep(msec);
		}
		catch (Exception e)
		{
		}
	}

	public static void main(String[] args)
	{
		ScrabbleMain game = new ScrabbleMain();
		game.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		game.pack();
		game.setVisible(true);

	}
}
