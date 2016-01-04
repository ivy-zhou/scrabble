import java.util.ArrayList;
import java.util.Collections;

/**
 * The board class is responsible for creating the board by storing the scoring
 * pattern for the board and calculating scores of words placed on the board
 * 
 * 
 */

public class Board
{
	// Static variables
	private final static int[][] BOARDMAP = {
			{ 4, 0, 0, 1, 0, 0, 0, 4, 0, 0, 0, 1, 0, 0, 4 },
			{ 0, 2, 0, 0, 0, 3, 0, 0, 0, 3, 0, 0, 0, 2, 0 },
			{ 0, 0, 2, 0, 0, 0, 1, 0, 1, 0, 0, 0, 2, 0, 0 },
			{ 1, 0, 0, 2, 0, 0, 0, 3, 0, 0, 0, 2, 0, 0, 1 },
			{ 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0 },
			{ 0, 3, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 3, 0 },
			{ 0, 0, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1, 0, 0 },
			{ 4, 0, 0, 3, 0, 0, 0, 2, 0, 0, 0, 3, 0, 0, 4 },
			{ 0, 0, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1, 0, 0 },
			{ 0, 3, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 3, 0 },
			{ 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0 },
			{ 1, 0, 0, 2, 0, 0, 0, 3, 0, 0, 0, 2, 0, 0, 1 },
			{ 0, 0, 2, 0, 0, 0, 1, 0, 1, 0, 0, 0, 2, 0, 0 },
			{ 0, 2, 0, 0, 0, 3, 0, 0, 0, 3, 0, 0, 0, 2, 0 },
			{ 4, 0, 0, 1, 0, 0, 0, 4, 0, 0, 0, 1, 0, 0, 4 } };

	private final static int DOUBLE_LETTER = 1;
	private final static int DOUBLE_WORD = 2;
	private final static int TRIPLE_LETTER = 3;
	private final static int TRIPLE_WORD = 4;

	public final int NO_COLS = 15;
	public final int NO_ROWS = 15;
	private Tile[][] board;
	public Dictionary dictionary;

	private Player playerOne;
	private Player playerTwo;

	private ArrayList<Word> wordsOnBoard;
	ArrayList<Tile> tilesOnBoard;

	private final static char BLANK = ' ';
	private final static int VERTICAL_WORD = 1;
	private final static int HORIZONTAL_WORD = 2;

	/**
	 * Constructs a Board object keeping track of the tiles played and their
	 * locations, and words played on the board
	 */
	public Board()
	{
		board = new Tile[NO_ROWS][NO_COLS];
		dictionary = new Dictionary();
		wordsOnBoard = new ArrayList<Word>();
		tilesOnBoard = new ArrayList<Tile>();
	}

	/**
	 * Returns the board containing the tiles being played
	 * 
	 * @return the board represented by a 2D array containing the tiles on the
	 *         board
	 */
	public Tile[][] getBoard()
	{
		return board;
	}

	/**
	 * Return the score of the tile at the given index based off its face value
	 * and special squares on the board that may double or triple the value of
	 * the letter
	 * 
	 * @param row the row of the tile on the board
	 * @param column the column of the tile on the board
	 * @return the score of the letter at the given location on the board
	 */
	public int getCharScore(int row, int column)
	{
		// Get the face value tile score
		int score = board[row][column].getScore();

		// Factor in multipliers
		if (BOARDMAP[row][column] == DOUBLE_LETTER)
			score *= 2;
		else if (BOARDMAP[row][column] == TRIPLE_LETTER)
			score *= 3;

		return score;
	}

	/**
	 * Finds only the score of a given word including multipliers, not factoring
	 * in the scores of surrounding words
	 * 
	 * @param word the word to get the score of
	 * @param turnTiles the tiles being played this turn by the current player
	 * @return the score of the word played (-1 if it's not in the dictionary)
	 */
	public int getScore(Word word, ArrayList<Tile> turnTiles)
	{

		// Find the type of the word, the row of the first tile, and the column
		// of the first tile
		int type = word.getType();
		int row = word.getFirstRow();
		int column = word.getFirstCol();

		int score = 0;
		int totalScore = 0;
		int multiplicator = 1;

		// Check whether the word can be added to the board, then check for
		// whether the word is vertical or horizontal
		if (dictionary.isWord(word.toString()))
		{
			if (type == VERTICAL_WORD)
			{
				// Go through each tile in the word
				for (int i = 0; i < word.length(); i++)
				{
					// Count special squares if the tile was played this turn
					Tile currentTile = board[row + i][column];
					if (turnTiles.contains(currentTile))
					{
						// Get the score of the tile
						score = getCharScore(row + i, column);

						// Keep track of the multipliers being applied to the
						// word
						if (BOARDMAP[row + i][column] == DOUBLE_WORD)
						{
							multiplicator *= 2;
						}
						else if (BOARDMAP[row + i][column] == TRIPLE_WORD)
						{
							multiplicator *= 3;
						}
					}
					// If the tile was already played previously, simply add its
					// face value
					else
						score = currentTile.getScore();

					// Add to total score of the word
					totalScore += score;
				}

				// Apply the multipliers to the total score of the word
				totalScore *= multiplicator;
			}

			else if (type == HORIZONTAL_WORD)
			{
				// Go through each tile in the word
				for (int i = 0; i < word.length(); i++)
				{
					// Count special squares if the tile was played this turn
					Tile currentTile = board[row][column + i];
					if (turnTiles.contains(currentTile))
					{
						// Get the score of the tile
						score = getCharScore(row, column + i);

						// Keep track of the multipliers being applied to the
						// word
						if (BOARDMAP[row][column + i] == DOUBLE_WORD)
						{
							multiplicator *= 2;

						}
						if (BOARDMAP[row][column + i] == TRIPLE_WORD)
						{
							multiplicator *= 3;

						}
					}

					// If the tile was not played this turn, get its normal
					// value
					else
						score = currentTile.getScore();

					// Add to the total score of the word
					totalScore += score;
				}

				// Apply the multipliers
				totalScore *= multiplicator;
			}

			return totalScore;
		}

		// Otherwise if it's not in the dictionary
		return -1;
	}

	/**
	 * Gets the score of the word based on the letter values without using
	 * multipliers (for finding the score of surrounding words)
	 * 
	 * @param word the word to get the score of
	 * @return the score of the word without special squares being accounted for
	 */
	public int getScoreWithoutMultipliers(Word word)
	{
		int totalScore = 0;

		// Go through the word and add up the total values of the tiles
		for (int tile = 0; tile < word.length(); tile++)
		{
			totalScore += word.getTile(tile).getScore();
		}
		return totalScore;
	}

	/**
	 * Gets the entire score of the word including the score of the word itself
	 * and the scores of the words surrounding it
	 * 
	 * @param word the word to get the score of
	 * @return the entire score of the word being played
	 */
	public int getTotalScore(Word word, ArrayList<Tile> turnTiles)
	{
		// Add the score of the played word and surrounding words
		int wordScore = getScore(word, turnTiles);
		int surroundingWordScore = checkSurroundingWords(word, turnTiles);

		// If at least one of the words is not valid, this turn gets no points
		if (wordScore == -1 || surroundingWordScore == -1)
			return 0;

		// Otherwise add the two up and return
		else
			return wordScore + surroundingWordScore;
	}

	/**
	 * Finds the row of connecting tiles on the board called a Word based off of
	 * the row and column of the first tile and a given direction
	 * 
	 * @param row the row index of the first letter of the word
	 * @param column the column index of the first letter of the word
	 * @param type the alignment of the word (horizontal or vertical)
	 * @return a reference to the word on the board
	 */
	public Word buildWord(int row, int column, int type)
	{
		ArrayList<Tile> word = new ArrayList<Tile>();
		int currentRow = row;
		int currentCol = column;

		// Check in different directions depending on the type of the word
		if (type == HORIZONTAL_WORD)
		{
			// Go down the row of tiles until we reach the end and add to the
			// list of tiles we want to include in the word
			while (rowIsValid(currentRow) && colIsValid(currentCol)
					&& board[currentRow][currentCol] != null)
			{
				word.add(board[currentRow][currentCol]);
				currentCol++;
			}

			// Create the word object based on the tiles we found
			Word newWord = new Word(word, HORIZONTAL_WORD);

			return newWord;
		}

		else if (type == VERTICAL_WORD)
		{
			// Find the full word starting from the first tile and keep track of
			// the tiles being included in the word
			while (rowIsValid(currentRow) && colIsValid(currentCol)
					&& board[currentRow][currentCol] != null)
			{
				word.add(board[currentRow][currentCol]);
				currentRow++;
			}

			// Create a new word based on the tiles we found
			Word newWord = new Word(word, VERTICAL_WORD);
			return newWord;
		}

		return null;
	}

	/**
	 * Adds a group of tiles as a word to the list of played words on the board
	 * 
	 * @param wordToAdd the word to add to the list
	 */
	public void addWord(Word wordToAdd)
	{
		wordsOnBoard.add(wordToAdd);
	}

	/**
	 * Checks if the row is on the board
	 * 
	 * @param row, the row being checked if it is a valid row
	 * @return true if the row is in bounds, false if not
	 */
	public boolean rowIsValid(int row)
	{
		return (row < NO_ROWS && row >= 0);
	}

	/**
	 * Checks if the column is on the board
	 * 
	 * @param column the column being checked if it is a valid column
	 * @return true if the column is in bounds, false if not
	 */
	public boolean colIsValid(int column)
	{
		return (column < NO_COLS && column >= 0);
	}

	/**
	 * Gets the total score of the words surrounding the given word
	 * 
	 * @param word the word to check the surrounding words of
	 * @param turnTiles the tiles being played this turn to check words being
	 *            formed off them
	 * @return the total score of the surrounding words (if at least one is not
	 *         in the dictionary, return 0)
	 */

	public int checkSurroundingWords(Word word, ArrayList<Tile> turnTiles)
	{
		int type = word.getType();
		int row = word.getFirstRow();
		int column = word.getFirstCol();

		// Keeps track of the score for the current surrounding word
		int directionScore = 0;

		// Keeps track of the total surrounding word score
		int totalScore = 0;

		if (type == HORIZONTAL_WORD)
		{
			// For each tile in the current word that were played this turn,
			// check for vertical words connecting to it
			for (Tile next : turnTiles)
			{
				int currentCol = next.getCol();
				int rowCheck = row;
				directionScore = 0;

				// Find the first letter of the vertical word and build the
				// whole word
				while (rowIsValid(rowCheck - 1)
						&& board[rowCheck - 1][currentCol] != null)
				{
					rowCheck--;
				}
				Word verticalWord = buildWord(rowCheck, currentCol,
						VERTICAL_WORD);

				// If there is a vertical word, check the entire word to see
				// if it's in the dictionary
				if (verticalWord.length() > 1)
				{
					// If not, return -1
					if (!dictionary.isWord(verticalWord.toString()))
						return -1;

					// Otherwise find the score of this word
					else
					{
						directionScore = getScoreWithoutMultipliers(verticalWord);

						// If this turn tile is in a special square, apply to
						// the total score of the word
						int tileRow = next.getRow();
						int tileCol = next.getCol();

						if (BOARDMAP[tileRow][tileCol] == DOUBLE_LETTER)
							directionScore += next.getScore();
						else if (BOARDMAP[tileRow][tileCol] == DOUBLE_WORD)
							directionScore *= 2;
						else if (BOARDMAP[tileRow][tileCol] == TRIPLE_LETTER)
							directionScore += 2 * next.getScore();
						else if (BOARDMAP[tileRow][tileCol] == TRIPLE_WORD)
							directionScore *= 3;
					}
				}

				// Add to the total surrounding words score
				totalScore += directionScore;
			}
		}

		// If the word is vertical
		if (type == VERTICAL_WORD)
		{
			// For each tile in the current word that were played this turn,
			// check for horizontal words connecting to it
			for (Tile next : turnTiles)
			{
				int currentRow = next.getRow();
				int colCheck = column;
				directionScore = 0;

				// Find the first letter of the horizontal word and build the
				// word
				while (colIsValid(colCheck - 1)
						&& board[currentRow][colCheck - 1] != null)
				{
					colCheck--;
				}

				Word horizontalWord = buildWord(currentRow, colCheck,
						HORIZONTAL_WORD);

				// If there's a horizontal word and it's not in the
				// dictionary, return -1
				if (horizontalWord.length() > 1)
				{
					if (!dictionary.isWord(horizontalWord.toString()))
						return -1;

					// Otherwise find the score of this word
					else
					{
						directionScore = getScoreWithoutMultipliers(horizontalWord);

						// If the turn tile is in a special square, apply to the
						// word
						int tileRow = next.getRow();
						int tileCol = next.getCol();

						if (BOARDMAP[tileRow][tileCol] == DOUBLE_LETTER)
							directionScore += next.getScore();
						else if (BOARDMAP[tileRow][tileCol] == DOUBLE_WORD)
							directionScore *= 2;
						else if (BOARDMAP[tileRow][tileCol] == TRIPLE_LETTER)
							directionScore += 2 * next.getScore();
						else if (BOARDMAP[tileRow][tileCol] == TRIPLE_WORD)
							directionScore *= 3;
					}
				}

				// Add to the total surrounding words score
				totalScore += directionScore;
			}
		}

		// After all the words have been checked
		return totalScore;
	}

	/**
	 * Clears the board of tiles
	 */
	public void clear()
	{
		for (int i = 0; i < NO_ROWS; i++)
			for (int j = 0; j < NO_COLS; j++)
				board[i][j] = null;
	}

	/**
	 * Checks whether at least one of the tiles being played is connected to a
	 * tile that was previously played on the board
	 * 
	 * @param turnTiles the tiles being played this turn
	 * @return 1 if the word is vertical, 2 if the word is horizontal, 0 if the
	 *         word is not connected to previously played tiles
	 */
	public int isConnected(ArrayList<Tile> turnTiles)
	{
		// Go through the tiles played this turn
		for (Tile next : turnTiles)
		{
			int row = next.getRow();
			int col = next.getCol();

			// Check above, below, left and right of the current tile for played
			// tiles not in the turn tiles, while checking to see if the rows
			// and columns being checked are valid

			// The returns are generally meant for letters added in the same
			// direction as a previous word (building off of a previous word)

			// If there are tiles above or below, the word is vertical
			if ((rowIsValid(row + 1) && board[row + 1][col] != null && !turnTiles
					.contains(board[row + 1][col]))
					|| (rowIsValid(row - 1) && board[row - 1][col] != null && !turnTiles
							.contains(board[row - 1][col])))
			{
				return VERTICAL_WORD;
			}

			// If there are tiles left or right, the word is horizontal
			if ((colIsValid(col + 1) && board[row][col + 1] != null && !turnTiles
					.contains(board[row][col + 1]))
					|| (colIsValid(col - 1) && board[row][col - 1] != null && !turnTiles
							.contains(board[row][col - 1])))
			{
				return HORIZONTAL_WORD;
			}
		}

		// If neither, the word is not connected
		return 0;
	}

	/**
	 * Checks if the positions of the played tiles are in a row
	 * 
	 * @param turnTiles the tiles played this turn
	 * @return the word that was played, null if the tile positions were not
	 *         aligned
	 */
	public Word isAligned(ArrayList<Tile> turnTiles)
	{
		// If there is nothing in turnTiles, it cannot be aligned
		if (turnTiles.size() == 0)
			return null;

		// Check if the tiles are in the same row or column
		boolean isSameRow = true;
		boolean isSameCol = true;

		// Set up some indexes
		int currentRow = turnTiles.get(0).getRow();
		int currentCol = turnTiles.get(0).getCol();

		// Create an arrayList to store the tiles in
		ArrayList<Tile> word = new ArrayList<Tile>();

		// If there is only one tile, check if it's touching any other tile
		if (turnTiles.size() == 1)
		{
			int type = isConnected(turnTiles);
			if (type == VERTICAL_WORD)
			{
				isSameCol = true;
				isSameRow = false;
			}
			else if (type == HORIZONTAL_WORD)
			{
				isSameCol = false;
				isSameRow = true;
			}
		}

		// Go through the tiles and if one column or row does not match, it's
		// not in the same column or row
		if (turnTiles.size() > 1)
		{
			for (Tile tile : turnTiles)
			{
				if (tile.getRow() != currentRow)
					isSameRow = false;
				if (tile.getCol() != currentCol)
					isSameCol = false;
			}
		}

		// If the word could be horizontal
		if (isSameRow)
		{
			// Sort the tiles from left to right
			Collections.sort(turnTiles, Tile.ROW_ORDER);

			// Look for the leftmost horizontal letter
			while (colIsValid(currentCol)
					&& board[currentRow][currentCol] != null)
			{
				currentCol--;
			}

			// Move back one character since we subtract one too many
			currentCol++;

			// Keep track of the turnTiles used
			int turnTilesUsed = 0;

			// Add all of the word tiles in a row
			while (colIsValid(currentCol)
					&& board[currentRow][currentCol] != null)
			{
				// If we haven't used up the turnTiles yet
				if (turnTilesUsed < turnTiles.size()
						&& currentCol == turnTiles.get(turnTilesUsed).getCol())
					turnTilesUsed++;

				// Add the tile found to the list of tiles in the word
				word.add(board[currentRow][currentCol]);
				currentCol++;
			}

			// If the whole row of turnTiles was not found, there are invalid
			// gaps in them and the word is not playable
			if (turnTilesUsed != turnTiles.size())
				return null;

			// Look beneath last tile played for the rest of the word
			while (colIsValid(currentCol)
					&& board[currentRow][currentCol] != null)
			{
				word.add(board[currentRow][currentCol]);
				currentCol++;
			}

			Collections.sort(word, Tile.ROW_ORDER);

			// If the tiles are in a line, build a word
			Word newWord = new Word(word, HORIZONTAL_WORD);
			return newWord;
		}

		else if (isSameCol)
		{
			// Sort the tiles from up to down
			Collections.sort(turnTiles, Tile.COL_ORDER);

			// Look for the topmost vertical letter
			while (rowIsValid(currentRow)
					&& board[currentRow][currentCol] != null)
				currentRow--;

			// Move back one character
			currentRow++;

			int turnTilesUsed = 0;

			// Add all of the tiles in
			while (rowIsValid(currentRow)
					&& board[currentRow][currentCol] != null)
			{
				// If we haven't used up the turnTiles yet
				if (turnTilesUsed < turnTiles.size()
						&& currentRow == turnTiles.get(turnTilesUsed).getRow())
				{
					turnTilesUsed++;
				}

				// Add all of the tiles found
				word.add(board[currentRow][currentCol]);
				currentRow++;
			}

			// If the whole row of turnTiles was not found, there are invalid
			// gaps in them and the word is not playable
			if (turnTilesUsed != turnTiles.size())
			{
				return null;
			}

			// Look beneath last tile played for the rest of the word
			while (rowIsValid(currentRow)
					&& board[currentRow][currentCol] != null)
			{
				word.add(board[currentRow][currentCol]);
				currentRow++;
			}

			// Sort them again
			Collections.sort(word, Tile.COL_ORDER);

			// If the tiles are in a line, build a word
			Word newWord = new Word(word, VERTICAL_WORD);
			return newWord;
		}

		return null;
	}

	/**
	 * Gets the list of words played on the board
	 * 
	 * @return the words played
	 */
	public ArrayList<Word> getWordsOnBoard()
	{
		return wordsOnBoard;
	}

	/**
	 * Removes all the given tiles from the board
	 * 
	 * @param tileList the list of tiles to remove from the board
	 */
	public void remove(ArrayList<Tile> tileList)
	{
		for (Tile tile : tileList)
		{
			board[tile.getRow()][tile.getCol()] = null;
		}

	}
}