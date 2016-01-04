import java.awt.Point;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JFrame;

/**
 * The class that is responsible for the players behavior, such as their rack
 * and their score
 * 
 * @version December 2013
 */
public class Player implements Comparable<Player>
{

	private String name;
	private int score;
	private ArrayList<Tile> rack;
	int computerType;
	Board board;
	private ArrayList<Word> words;
	private static HashSet<String> allWords;
	private static ArrayList<Tile> playedTiles;
	private int playerNo;

	public static final int EASY_COMPUTER = 1;
	public static final int HARD_COMPUTER = 2;

	/**
	 * Constructs a human player with a given rack and number
	 * 
	 * @param playerRack the rack of the player
	 * @param playerNo the player number
	 */
	public Player(ArrayList<Tile> playerRack, int playerNo)
	{
		// Sets the default name of each player
		this.playerNo = playerNo;
		if (playerNo == 1)
			name = "Player One";
		else
			name = "Player Two";

		// Sets up the rack and the score
		rack = playerRack;
		score = 0;
	}

	/**
	 * Constructs a CPU player with a given board, rack, difficulty, list of
	 * words on the board, list of played tiles on the board, and player number
	 * 
	 * @param board the Scrabble board
	 * @param playerRack the rack of this CPU player
	 * @param computerType the difficulty of this CPU player (easy/hard)
	 * @param words the list of words on the board
	 * @param playerNo the player no
	 */
	public Player(Board board, ArrayList<Tile> playerRack, int computerType,
			ArrayList<Word> words, ArrayList<Tile> playedTiles, int playerNo)
	{
		// Set the default name of each player
		name = "Player " + playerNo;
		this.playerNo = playerNo;

		// Set up other variables
		rack = playerRack;
		score = 0;
		this.board = board;
		this.words = words;
		this.computerType = computerType;

		// Initialize the list of all words and the played tiles
		allWords = new HashSet<String>();
		Player.playedTiles = playedTiles;
	}

	/**
	 * Returns to current score of the player
	 * 
	 * @return the score of the player at the given moment
	 */
	public int getScore()
	{
		return score;
	}

	/**
	 * Returns the rack of a given player
	 * 
	 * @return the current tiles on the rack
	 */
	public ArrayList<Tile> getRack()
	{
		return rack;
	}

	/**
	 * Adds a given score to the player's original score
	 * 
	 * @param newScore The score to be added on
	 */
	public void addScore(int newScore)
	{
		score += newScore;
	}

	/**
	 * Sets the name of the player
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Shuffles the rack
	 */
	public void shuffleRack()
	{
		// Shuffles the rack
		// Randomly swap the locations of 2 elements
		for (int nextTile = 0; nextTile < rack.size() / 2; nextTile++)
		{
			// Randomize a new position for the next Tile
			int newPos = (int) (Math.random() * rack.size());

			// Swap the Tiles
			rack.get(nextTile).swap(rack.get(newPos));
		}
	}

	/**
	 * Returns a string representation of this player
	 * 
	 * @return a string representation of this player
	 */
	public String toString()
	{
		if (playerNo == 1)
			return "Player One";
		else
			return "Player Two";
	}

	/**
	 * Returns this player's name and score as a String
	 * 
	 * @return this player's name and score as a String
	 */
	public String getPlayerData()
	{
		return name + " - Score: " + score;
	}

	/**
	 * Compares players by their score used for arranging the list of top scores
	 * from highest to lowest score
	 * 
	 * @param other the player to compare to
	 * @return a negative integer if the score of this player is higher than
	 *         that of the other player, 0 if the scores are equal, and a
	 *         positive integer otherwise
	 */
	public int compareTo(Player other)
	{
		return (other.score - this.score);
	}

	/**
	 * Adds all the unique variations of a given string recursively
	 * 
	 * @param str the string to find combinations for
	 * @param indexOfLength the length of the string
	 * @return
	 */
	public static HashSet<String> generateWords(StringBuilder str,
			int indexOfLength)
	{
		// If the length is 0, return the string
		if (indexOfLength <= 0)
		{
			allWords.add(str.toString());
		}

		else
		{
			// Recursively call this, placing all other chars at current first
			// position
			generateWords(str, indexOfLength - 1);
			int currentPos = str.length() - indexOfLength;
			for (int i = currentPos + 1; i < str.length(); i++)
			{
				// Swap all other chars with current first letter
				swap(str, currentPos, i);
				generateWords(str, indexOfLength - 1);
				swap(str, i, currentPos);
			}
		}

		return allWords;
	}

	/**
	 * Swaps two letters in a StringBuilder
	 * 
	 * @param str the StringBuilder to swap characters in
	 * @param first the index of the first letter to swap
	 * @param second the index of the second letter to swap
	 */
	public static void swap(StringBuilder str, int first, int second)
	{
		// Check if it's trying to swap out of bounds
		if (first < 0 || first >= str.length() || second < 0
				|| second >= str.length())
			return;

		char temp = str.charAt(first);
		str.setCharAt(first, str.charAt(second));
		str.setCharAt(second, temp);
	}

	/**
	 * Makes an easy move
	 * 
	 * @param validWords the list of valid words
	 * @return the Word which the CPU played
	 */
	public Word makeEasyMove(HashSet<String> validWords)
	{
		for (String nextWord : validWords)
		{
			// Find where to place the next word (tiles that you're building off
			// of)
			ArrayList<Tile> tilesToUse = findMissingTiles(nextWord, rack);
			for (Tile matchTile : tilesToUse)
			{
				int score = 0;
				int matchRow = matchTile.getRow();
				int matchCol = matchTile.getCol();

				ArrayList<Tile> tileList = new ArrayList<Tile>();
				int lettersBefore = nextWord.indexOf((matchTile.getChar()));
				int lettersAfter = nextWord.length() - lettersBefore - 1;

				int nextWordIndex = 0;

				// Place the word around the matching letter vertically
				int rowIndex = matchRow - lettersBefore;

				while (board.rowIsValid(rowIndex)
						&& rowIndex < matchRow + lettersAfter)
				{
					// Do not add a new tile for the tile on the board
					if (rowIndex != matchRow)
					{
						// If the positions around the tile are
						// empty
						if (board.getBoard()[rowIndex][matchCol] == null)
						{
							// Add a tile to the board
							Tile tileToAdd = getFromRack(nextWord
									.charAt(nextWordIndex));
							tileToAdd.setPos(rowIndex, matchCol);
							board.getBoard()[rowIndex][matchCol] = tileToAdd;
							tileList.add(tileToAdd);
						}
					}
					nextWordIndex++;
					rowIndex++;
				}

				// Check if the whole word was added successfully
				if (tileList.size() == nextWord.length() - 1)
				{
					// If it was added successfully, add to tile lists
					playedTiles.addAll(tileList);

					tileList.add(matchTile);

					// Create the new word to find the score of and add to board
					Word newWord = board.isAligned(tileList);

					tileList.remove(matchTile);

					if (newWord != null)
					{

						score = board.getTotalScore(newWord, tileList);

						// If the surrounding words are valid, return
						if (score > 0)
						{
							newWord.setScore(score);
							return newWord;
						}
					}
				}

				// If the word was not added successfully
				// Remove leftover tiles from the board
				if (tileList.size() > 0)
				{
					board.remove(tileList);
					playedTiles.removeAll(tileList);

					// Reset their locations back to rack
					rack.addAll(tileList);
					for (Tile next : tileList)
					{
						Point tileLocation = ScrabbleMain.findNextSpot(rack);
						if (tileLocation != null)
						{
							next.setLocation(tileLocation);
						}
					}
					tileList.clear();
				}

				// Reset the string index
				nextWordIndex = 0;
				score = 0;

				// Try placing the word around the matching letter
				// horizontally
				int colIndex = matchCol - lettersBefore;
				while (board.colIsValid(colIndex)
						&& colIndex < matchCol + lettersAfter)
				{

					// Do not add a new tile for the tile you're building around
					// on the board
					if (colIndex != matchCol)
					{
						// If the positions around the tile are
						// empty
						if (board.getBoard()[matchRow][colIndex] == null)
						{
							// Add a tile to the board
							Tile tileToAdd = getFromRack(nextWord
									.charAt(nextWordIndex));
							tileToAdd.setPos(matchRow, colIndex);
							board.getBoard()[matchRow][colIndex] = tileToAdd;
							tileList.add(tileToAdd);
						}

					}

					nextWordIndex++;
					colIndex++;
				}

				// Check if the whole word was added successfully
				if (tileList.size() == nextWord.length() - 1)
				{

					// If it was added successfully, add to tile lists
					playedTiles.addAll(tileList);

					tileList.add(matchTile);

					// Create the new word to find the score of and add to board
					Word newWord = board.isAligned(tileList);

					tileList.remove(matchTile);

					if (newWord != null)
					{

						score = board.getTotalScore(newWord, tileList);

						// If the surrounding words are valid, return
						if (score > 0)
						{
							newWord.setScore(score);
							return newWord;
						}
					}
				}

				// If the word was not added successfully
				// Remove leftover tiles from the board
				if (tileList.size() > 0)
				{
					board.remove(tileList);
					playedTiles.removeAll(tileList);

					// Reset their locations back to rack
					rack.addAll(tileList);

					for (Tile next : tileList)
					{
						Point tileLocation = ScrabbleMain.findNextSpot(rack);
						if (tileLocation != null)
						{
							next.setLocation(tileLocation);
						}

					}
					tileList.clear();
				}
			}
		}

		// If a word could not be placed
		return null;
	}

	/**
	 * Retrieves a tile with the given character from the rack
	 * 
	 * @param letterToFind the given character
	 * @return a tile with the given character from the rack
	 */
	public Tile getFromRack(char letterToFind)
	{
		int tileIndex = -1;
		for (int index = 0; index < rack.size(); index++)
		{
			if (rack.get(index).getChar() == letterToFind)
			{
				tileIndex = index;
			}
		}
		if (tileIndex == -1)
			return null;
		else
			return rack.remove(tileIndex);
	}

	/**
	 * Returns an arrayList of the possible tiles on the board needed by the
	 * word that the computer is placing
	 * 
	 * @param str the word being placed
	 * @param rack the computer's rack of tiles
	 * @return an arrayList of the possible tiles on the board needed by the
	 *         word that the computer is placing
	 */
	public ArrayList<Tile> findMissingTiles(String str, ArrayList<Tile> rack)
	{
		ArrayList<Tile> missingTiles = new ArrayList<Tile>();
		char missingLetter = ' ';

		// Create a tally of all the letters in the word needed
		int[] letterTally = new int[26];

		for (int i = 0; i < str.length(); i++)
		{
			letterTally[str.charAt(i) - 'A']++;
		}

		// Subtract all the letters that the rack has
		for (Tile rackTile : rack)
		{
			letterTally[rackTile.getChar() - 'A']--;
		}

		// Go through the tally and find the letter that has 1, which is the
		// one that the rack that does not have
		for (int i = 0; i < letterTally.length; i++)
		{
			if (letterTally[i] == 1)
				missingLetter = (char) ('A' + i);
		}

		// // Find the letter needed by the word
		// for (int i = 0; i < str.length(); i++)
		// {
		// boolean letterFound = false;
		// for (Tile rackTile : rack)
		// {
		// if (str.charAt(i) == rackTile.getChar())
		// letterFound = true;
		// }
		// if (!letterFound)
		// missingLetter = str.charAt(i);
		// }

		// If there were no letters missing, we can use any one letter in the
		// word that's on the board
		ArrayList<Character> missingLetters = new ArrayList<Character>();
		if (missingLetter == ' ')
		{
			for (int i = 0; i < str.length(); i++)
				missingLetters.add(str.charAt(i));
		}
		else
			missingLetters.add(missingLetter);

		// Go through the tiles on the board and add tiles that are that letter
		// to the missing tiles
		for (Tile next : playedTiles)
		{
			for (char missing : missingLetters)
				if (next.getChar() == missing)
					missingTiles.add(next);
		}

		return missingTiles;
	}

	/**
	 * Finds all of the possible words with the rack and a given character
	 * 
	 * @param letter the given character
	 * @return all of the possible words with the rack and a given character
	 */
	public ArrayList<String> getMoves(char letter)
	{
		StringBuilder str = new StringBuilder();
		StringBuilder checkStr = new StringBuilder();
		int size = rack.size() + 1;

		// Go through the whole word
		if (letter == ' ')
			size--;

		else
			str.append(letter);

		for (Tile tile : rack)
		{
			if (!tile.isBlank())
				str.append(tile.getChar());
			else
			{
				str.append("S");
				tile.set('S');
			}
		}

		allWords.addAll(generateWords(str, size));

		// Finds all possible words with the length of the rack - 1,
		// usually 7
		// Remove from 1 as you can only remove characters from the rack,
		// not the board
		for (int remove = 1; remove < size; remove++)
		{
			char currentChar = str.charAt(remove);
			str.deleteCharAt(remove);
			generateWords(str, size - 1);
			str.insert(remove, currentChar);
		}

		// Finds all possible words with 2 letters taken out from the
		// combination (6 letter words)
		for (int i = 1; i < size - 1; i++)
			for (int j = i + 1; j < size; j++)
			{
				checkStr.append(str.substring(0, i));
				checkStr.append(str.substring(i + 1, j));
				checkStr.append(str.substring(j + 1));
				generateWords(checkStr, size - 2);
				clear(checkStr);
			}

		// Finds all the possible words with 3 letters taken out from
		// the combination (5 letter words)
		for (int i = 1; i < size - 2; i++)
			for (int j = i + 1; j < size - 1; j++)
				for (int k = j + 1; k < size; k++)
				{
					checkStr.append(str.substring(0, i));
					checkStr.append(str.substring(i + 1, j));
					checkStr.append(str.substring(j + 1, k));
					checkStr.append(str.substring(k + 1));
					generateWords(checkStr, size - 3);
					clear(checkStr);
				}

		// Finds all the possible 4 letter words
		for (int i = 1; i < size - 3; i++)
			for (int j = i + 1; j < size - 2; j++)
				for (int k = j + 1; k < size - 1; k++)
					for (int l = k + 1; l < size; l++)
					{
						checkStr.append(str.substring(0, i));
						checkStr.append(str.substring(i + 1, j));
						checkStr.append(str.substring(j + 1, k));
						checkStr.append(str.substring(k + 1, l));
						checkStr.append(str.substring(l));

						generateWords(checkStr, size - 4);
						clear(checkStr);
					}

		// Finds all the possible 3 letter words
		for (int i = 1; i < size - 2; i++)
			for (int j = i + 1; j < size - 1; j++)
				for (int k = j + 1; k < size; k++)
				{
					checkStr.append(str.substring(0, i));
					checkStr.append(str.substring(i + 1, j));
					checkStr.append(str.substring(k + 1));
					generateWords(checkStr, size - 5);
					clear(checkStr);
				}

		// Find all possible 2 letter words
		for (int i = 1; i < size - 1; i++)
			for (int j = i + 1; j < size; j++)
			{
				checkStr.append(str.charAt(i));
				checkStr.append(str.charAt(j));
				generateWords(checkStr, size - 6);
				clear(checkStr);
			}

		// Find the score of all the valid words that are found
		ArrayList<String> validWords = new ArrayList<String>();
		for (String nextWord : allWords)
		{
			// Check if the words generated are valid
			if (board.dictionary.isWord(nextWord))
				validWords.add(nextWord);
		}

		allWords.clear();

		return validWords;
	}

	/**
	 * Clears a StringBuilder
	 * 
	 * @param str the StringBuilder to clear
	 */
	private void clear(StringBuilder str)
	{
		for (int i = str.length() - 1; i >= 0; i--)
			str.deleteCharAt(i);
	}

	/**
	 * Makes a move for the CPU player
	 * 
	 * @return the word that was played
	 */
	public Word makeMove()
	{
		// Create a set to store valid words in
		HashSet<String> validWords = new HashSet<String>();

		// If it's the first turn
		if (playedTiles.isEmpty())
		{
			// Generate valid words with just the letters on the rack
			validWords.addAll(getMoves(' '));

			// Play the first possible valid word
			ArrayList<Tile> tiles = new ArrayList<Tile>();
			for (String nextWord : validWords)
			{
				for (int i = 0; i < nextWord.length(); i++)
				{
					Tile nextTile = getFromRack(nextWord.charAt(i));
					nextTile.setPos(7, 7 + i);
					tiles.add(nextTile);
				}

				Word newWord = new Word(tiles, Word.HORIZONTAL_WORD);
				newWord.setScore (board.getScore (newWord, tiles));
				playedTiles.addAll(tiles);
				return newWord;
			}
		}

		// Else if it's not the first turn
		else
		{
			// Generate all valid words
			for (Tile playedTile : playedTiles)
			{
				validWords.addAll(getMoves(playedTile.getChar()));
			}
		}

		// If the computer is easy, make an easy move
		if (computerType == EASY_COMPUTER)
		{
			return makeEasyMove(validWords);
		}

		// If the computer is hard, make a hard move
		else if (computerType == HARD_COMPUTER)
		{
			return makeHardMove(validWords);
		}

		// If no move could be made return null
		return null;
	}

	/**
	 * Makes a hard move
	 * 
	 * @param validWords the list of possible words to play
	 * @return the word played
	 */
	public Word makeHardMove(HashSet<String> validWords)
	{
		// Store the best word found and where to build off the word
		// (missingTile)
		String bestStr = null;
		int bestScore = 0;
		Tile missingTile = null;
		char missingLetter = ' ';
		int tilesToAdd = -1;
		int indexOfMissing = -1;
		int bestWordType = 0;
		int bestWordRow = -1;
		int bestWordCol = -1;

		for (String nextWord : validWords)
		{
			// Find where to place the next word (tiles that you're building off
			// of)
			ArrayList<Tile> tilesToUse = findMissingTiles(nextWord, rack);
			for (Tile matchTile : tilesToUse)
			{
				int score = 0;
				int matchRow = matchTile.getRow();
				int matchCol = matchTile.getCol();

				ArrayList<Tile> tileList = new ArrayList<Tile>();
				int lettersBefore = nextWord.indexOf((matchTile.getChar()));
				int lettersAfter = nextWord.length() - lettersBefore - 1;

				int nextWordIndex = 0;

				System.out.println("Rack: " + rack);
				System.out.println("nextWord: " + nextWord);
				System.out.println("matchTile: " + matchTile);

				// Place the word around the matching letter vertically
				int rowIndex = matchRow - lettersBefore;

				while (board.rowIsValid(rowIndex)
						&& rowIndex <= matchRow + lettersAfter)
				{
					// Do not add a new tile for the tile on the board
					if (rowIndex != matchRow)
					{
						// If the positions around the tile are
						// empty
						if (board.getBoard()[rowIndex][matchCol] == null)
						{
							// Add a tile to the board
							Tile tileToAdd = getFromRack(nextWord
									.charAt(nextWordIndex));
							tileToAdd.setPos(rowIndex, matchCol);
							board.getBoard()[rowIndex][matchCol] = tileToAdd;
							tileList.add(tileToAdd);
						}
					}

					nextWordIndex++;
					rowIndex++;
				}

				// Check if the whole word was added successfully
				if (tileList.size() == nextWord.length() - 1)
				{
					// If it was added successfully, add to tile lists
					tileList.add(matchTile);

					// Create the new word to find the score of and add to board
					Word newWord = board.isAligned(tileList);

					tileList.remove(matchTile);

					if (newWord != null)
					{
						score = board.getTotalScore(newWord, tileList);

						// If this word beats the best word, store the word
						if (score > bestScore)
						{
							bestScore = score;
							bestStr = newWord.toString();
							missingTile = matchTile;
							missingLetter = missingTile.getChar();
							tilesToAdd = tileList.size();
							indexOfMissing = lettersBefore;
							bestWordType = newWord.getType();
							bestWordRow = newWord.getTile(0).getRow();
							bestWordCol = newWord.getTile(0).getCol();
						}
					}
				}

				// Remove leftover tiles from the board
				if (tileList.size() > 0)
				{
					board.remove(tileList);

					// Reset their locations back to rack
					rack.addAll(tileList);
					for (Tile next : tileList)
					{
						Point tileLocation = ScrabbleMain.findNextSpot(rack);
						if (tileLocation != null)
						{
							next.setLocation(tileLocation);
						}
					}
					tileList.clear();
				}

				// Reset the string index
				nextWordIndex = 0;
				score = 0;

				// Try placing the word around the matching letter
				// horizontally
				int colIndex = matchCol - lettersBefore;
				while (board.colIsValid(colIndex)
						&& colIndex <= matchCol + lettersAfter)
				{

					// Do not add a new tile for the tile you're building around
					// on the board
					if (colIndex != matchCol)
					{
						// If the positions around the tile are
						// empty
						if (board.getBoard()[matchRow][colIndex] == null)
						{
							// Add a tile to the board
							Tile tileToAdd = getFromRack(nextWord
									.charAt(nextWordIndex));
							tileToAdd.setPos(matchRow, colIndex);
							board.getBoard()[matchRow][colIndex] = tileToAdd;
							tileList.add(tileToAdd);
						}

					}

					nextWordIndex++;
					colIndex++;
				}

				// Check if the whole word was added successfully
				if (tileList.size() == nextWord.length() - 1)
				{

					// If it was added successfully, add to tile lists
					tileList.add(matchTile);

					// Create the new word to find the score of and add to board
					Word newWord = board.isAligned(tileList);

					tileList.remove(matchTile);

					if (newWord != null)
					{

						score = board.getTotalScore(newWord, tileList);

						// If the score of this word beats the previous best
						// score, then store the word
						if (score > bestScore)
						{
							bestScore = score;
							bestStr = newWord.toString();
							missingTile = matchTile;
							missingLetter = missingTile.getChar();
							tilesToAdd = tileList.size();
							indexOfMissing = lettersBefore;
							bestWordType = newWord.getType();
							bestWordRow = newWord.getTile(0).getRow();
							bestWordCol = newWord.getTile(0).getCol();
						}
					}
				}

				// Remove leftover tiles from the board
				if (tileList.size() > 0)
				{
					board.remove(tileList);

					// Reset their locations back to rack
					rack.addAll(tileList);

					for (Tile next : tileList)
					{
						Point tileLocation = ScrabbleMain.findNextSpot(rack);
						if (tileLocation != null)
						{
							next.setLocation(tileLocation);
						}

					}
					tileList.clear();
				}
			}
		}

		// Play the best word found
		if (bestStr != null)
		{
			System.out.println("Final best word: " + bestStr);
			System.out.println("Word score: " + bestScore);
			System.out.println("Missing letter: " + missingLetter);
			System.out.println("Rack: " + rack);
			System.out.println("Index of missing: " + indexOfMissing);
			System.out.println("Comparison: " + bestStr.length() + " "
					+ tilesToAdd);

			ArrayList<Tile> bestTiles = new ArrayList<Tile>();

			// Find all the tiles of the word
			for (int i = 0; i < bestStr.length(); i++)
			{
				if (i != indexOfMissing)
				{
					Tile tileToAdd = getFromRack(bestStr.charAt(i));
					bestTiles.add(tileToAdd);
				}
				else
					bestTiles.add(missingTile);
			}

			// Add the best word to the board
			int i = 0;

			// If the word is horizontal, start from the row and column of the
			// first tile of the best word and add the word to the right
			if (bestWordType == Word.HORIZONTAL_WORD)
			{
				for (int col = bestWordCol; col < bestWordCol
						+ bestStr.length(); col++)
				{
					if (col - bestWordCol != indexOfMissing)
					{
						Tile nextTile = bestTiles.get(i);

						if (nextTile != null)
						{
							board.getBoard()[bestWordRow][col] = nextTile;
							nextTile.setPos(bestWordRow, col);
						}
						else
							bestTiles
									.set(i, board.getBoard()[bestWordRow][col]);

					}
					i++;
				}
			}

			// If the word is vertical, start from the row and column of the
			// first tile of the best word and add the word going down
			else
			{
				for (int row = bestWordRow; row < bestWordRow
						+ bestStr.length(); row++)
				{
					if (row - bestWordRow != indexOfMissing)
					{
						Tile nextTile = bestTiles.get(i);

						if (nextTile != null)
						{
							board.getBoard()[row][bestWordCol] = nextTile;
							nextTile.setPos(row, bestWordCol);
						}
						else
							bestTiles
									.set(i, board.getBoard()[row][bestWordCol]);

					}
					i++;
				}
			}

			Word wordToPlay = new Word(bestTiles, bestWordType);
			wordToPlay.setScore(bestScore);

			System.out.println("Word to play: " + wordToPlay);

			// Add the best Word to the lists
			playedTiles.addAll(bestTiles);

			return wordToPlay;
		}

		return null;
	}
}
