import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Comparator;

import javax.swing.ImageIcon;

/**
 * Keeps track of a Scrabble tile and models tile behaviour
 * 
 * 
 */
public class Tile extends Rectangle implements Comparable<Tile>
{

	char letter; // the character of the letter on the tile
	int score; // the score of the character on the tile if played successfully
	boolean isPlayed; // if the tile has been played or not
	boolean isShrinked; // if the size of the tile has changed

	int row;
	int col;

	public static final int SMALL_TILE = 0;
	public static final int LARGE_TILE = 1;

	boolean isSet;

	public static final Comparator<Tile> ROW_ORDER = new RowOrder();
	public static final Comparator<Tile> COL_ORDER = new ColOrder();

	// the image for the tile
	private final static Image smallSize = new ImageIcon(
			"images\\Blank_S_Tile.jpg").getImage();
	public final static int WIDTH_SMALL = smallSize.getWidth(null);
	public final static int HEIGHT_SMALL = smallSize.getHeight(null);

	private final static Image bigSize = new ImageIcon("images\\Blank_Tile.jpg")
			.getImage();
	public final static int WIDTH = bigSize.getWidth(null);
	public final static int HEIGHT = bigSize.getHeight(null);

	protected Image bigTile;
	protected Image littleTile;

	// the values of the letters in Scrabble
	protected static final int[] LETTER_VALUES = { 1, 3, 3, 2, 1, 4, 2, 4, 1,
			8, 5, 1, 3, 1, 1, 3, 10, 1, 1, 1, 1, 4, 4, 8, 4, 10, 0 };

	/**
	 * Constructs a Tile object with a given index in the deck that defaults to
	 * still being in the pouch (not played and not visible) as well as sized to
	 * be large and not a blank
	 * 
	 * @param letter the letter on the Tile
	 * @param number the index of the Tile in the Tile Deck
	 */
	public Tile(char letter)
	{
		super(0, 0, 0, 0);

		isPlayed = false;
		this.letter = letter;

		// Special case for a blank tile
		if (letter == ' ')
		{
			isSet = false;
			score = 0;

			// Load up the appropriate large image file for this tile
			String imageFileName = "images\\Blank_Tile.jpg";
			bigTile = new ImageIcon(imageFileName).getImage();

			// Load up the appropriate small image file for this tile
			imageFileName = "images\\Blank_S_Tile.jpg";
			littleTile = new ImageIcon(imageFileName).getImage();
		}

		else
		{
			isSet = true;

			// Calculates the score of the tile given the letter on it
			int scoreIndex = letter - 'A';

			if (scoreIndex < 0)
				scoreIndex = 0;

			score = LETTER_VALUES[scoreIndex];

			// Load up the appropriate large image file for this tile
			String imageFileName = "images\\" + letter + "_Tile.jpg";
			bigTile = new ImageIcon(imageFileName).getImage();

			// Load up the appropriate small image file for this tile
			imageFileName = "images\\" + letter + "_S_Tile.jpg";
			littleTile = new ImageIcon(imageFileName).getImage();
		}

		// Set the size of the tile based on the image size
		setSize(bigSize.getWidth(null), bigSize.getHeight(null));
	}

	/**
	 * Returns the letter of this Tile
	 * 
	 * @return the letter
	 */
	public char getChar()
	{
		return letter;
	}

	/**
	 * Returns the score of this Tile
	 * 
	 * @return the score
	 */
	public int getScore()
	{
		return score;
	}

	/**
	 * Draws a Tile in a Graphics context
	 * 
	 * @param g Graphics to draw the card in
	 */
	public void draw(Graphics g)
	{
		// Draw the small version of the tile
		if (isShrinked)
		{
			g.drawImage(littleTile, x, y, null);

			// Draw a little 0 in the bottom corner if blank
		}

		// Draw the large version of the tile
		else
			g.drawImage(bigTile, x, y, null);

	}

	/**
	 * Moves a Tile from one point to another
	 * 
	 * @param initialPos the initial position of the Tile
	 * @param finalPos the position the Tile is being moved to
	 */
	public void move(Point initialPos, Point finalPos)
	{
		translate(finalPos.x - initialPos.x, finalPos.y - initialPos.y);
	}

	/**
	 * Returns the Tile information as a String containing the letter followed
	 * by its value
	 * 
	 * @return a string representation of a card
	 */
	public String toString()
	{
		if (isSet)
		{
			StringBuilder tileStr = new StringBuilder(2);

			tileStr.append(letter);
			tileStr.append(score);

			return tileStr.toString();
		}

		// For blank tiles
		else
		{
			String tileStr = "?0";
			return tileStr;
		}
	}

	/**
	 * Locks a Tile into place on the board
	 */
	public void lock()
	{
		isPlayed = true;
	}

	/**
	 * Returns whether the tile has already been played (is locked in)
	 */
	public boolean isPlayed()
	{
		return isPlayed;
	}

	/**
	 * Changes the size of the Tile according to the size value specified
	 * 
	 * @param size the size value
	 */
	public boolean changeSize(int size)
	{
		// If the size is 0, change the size of the Tile to be little
		if (size == SMALL_TILE && !isShrinked)
		{
			isShrinked = true;
			setSize(littleTile.getWidth(null), littleTile.getHeight(null));
			return true;
		}

		// If the size is 1, change the size of the Tile to be big
		else if (size == LARGE_TILE && isShrinked)
		{
			isShrinked = false;
			setSize(bigTile.getWidth(null), bigTile.getHeight(null));
			return true;
		}

		return false;
	}

	/**
	 * Swaps the locations of 2 tiles
	 * 
	 * @param tile the tile to swap
	 */
	public void swap(Tile tile)
	{
		// Temporarily store the location of the first tile
		int tempX = tile.x;
		int tempY = tile.y;

		// Swap the tile locations
		tile.x = this.x;
		tile.y = this.y;

		this.x = tempX;
		this.y = tempY;
	}

	/**
	 * Compares 2 tiles and returns the tile with the greater score
	 * 
	 * @param tile the tile to compare to
	 * @return 1 if this tile is greater than the other tile -1 if this tile is
	 *         less than the other tile 0 if these tiles are equal
	 */
	public int compareTo(Tile tile)
	{
		System.out.println("Using this");
		return this.score - tile.score;
	}

	/**
	 * Checks if two tiles are equal
	 * 
	 * @param tile the tile to compare to
	 * @return true if the other tile has the same score and letter as this tile
	 *         false otherwise
	 */
	public boolean equals(Tile tile)
	{
		return (this.x == tile.x && this.y == tile.y);
	}

	/**
	 * Checks if this tile is Blank
	 * 
	 * @return true if this tile is Blank false if this tile is not Blank
	 */
	public boolean isBlank()
	{
		return (score == 0);
	}

	/**
	 * Sets the location of the word on the board
	 * 
	 * @param row the row index of the tile
	 * @param column the column index of the tile
	 */
	public void setPos(int row, int column)
	{
		this.row = row;
		this.col = column;
	}

	/**
	 * Returns the row index of the tile
	 * 
	 * @return the row index
	 */
	public int getRow()
	{
		return row;
	}

	/**
	 * Returns the column index of the tile
	 * 
	 * @return the column index
	 */
	public int getCol()
	{
		return col;
	}

	/**
	 * An inner Comparator class that compares two Tiles by their row position
	 */
	private static class RowOrder implements Comparator<Tile>
	{
		/**
		 * Compares 2 tiles and returns the tile that's further left
		 * 
		 * @param first the tile to compare
		 * @param second the tile to compare
		 * @return 1 if this tile is further left than the other tile -1 if this
		 *         tile is further right than the other tile 0 if these tiles
		 *         have the same column position
		 */
		public int compare(Tile first, Tile second)
		{
			return (first.col - second.col);
		}
	}

	/**
	 * An inner Comparator class that compares two Tiles by their column
	 * position
	 */
	private static class ColOrder implements Comparator<Tile>
	{

		public int compare(Tile first, Tile second)
		{
			return (first.row - second.row);
		}
	}

	/**
	 * Checks if the tile is small
	 * 
	 * @return true if the tile shrunk and false if not
	 */
	public boolean isSmall()
	{
		return isShrinked;
	}

	/**
	 * Sets the tile so the given position is at the centre of the tile
	 * 
	 * @param pos the position to centre the tile at
	 */
	public void centreTile(Point pos)
	{
		// If the tile is small, move it towards the upper left direction as it
		// grows
		if (isShrinked)
		{
			x = pos.x - WIDTH_SMALL / 2;
			y = pos.y - HEIGHT_SMALL / 2;
		}

		// If the tile is large, move it towards the lower right direction as it
		// shrinks
		else
		{
			x = pos.x - WIDTH / 2;
			y = pos.y - HEIGHT / 2;
		}
	}

	/**
	 * Sets the letter on the blank tile
	 */
	public void set(char letter)
	{
		this.letter = letter;

		isSet = true;

		// Reloads the image of the tile
		String imageFileName = "images\\" + letter + "_Tile.jpg";
		bigTile = new ImageIcon(imageFileName).getImage();

		// Load up the appropriate small image file for this tile
		imageFileName = "images\\" + letter + "_0_Tile.jpg";
		littleTile = new ImageIcon(imageFileName).getImage();
	}

	/**
	 * Resets the Tile to be blank
	 */
	public void reset()
	{
		letter = ' ';
		isSet = false;

		// Load up the appropriate large image file for this tile
		String imageFileName = "images\\Blank_Tile.jpg";
		bigTile = new ImageIcon(imageFileName).getImage();

		// Load up the appropriate small image file for this tile
		imageFileName = "images\\Blank_S_Tile.jpg";
		littleTile = new ImageIcon(imageFileName).getImage();
	}

	public boolean isSameChar(Tile other)
	{
		return this.letter == other.letter;

	}
}