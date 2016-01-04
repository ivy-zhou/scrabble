import java.util.ArrayList;

import javax.swing.JFrame;

/**
 * Creates a reference to a row of tiles on the board called a Word which may or
 * may not be in the dictionary. Used to keep track of each individual tile in
 * the word and to find the score of the word.
 * 
 * 
 */

public class Word
{
	public final static int VERTICAL_WORD = 1;
	public final static int HORIZONTAL_WORD = 2;

	private ArrayList<Tile> word;
	private Tile firstTile;

	private int score;
	private int type;

	/**
	 * Constructs a word
	 * 
	 * @param word the list of tiles making up the word
	 * @param type the alignment of the word (vertical or horizontal)
	 */
	public Word(ArrayList<Tile> word, int type)
	{
		this.type = type;
		this.word = new ArrayList<Tile>(word);
		this.firstTile = word.get(0);
	}

	/**
	 * Constructs a copy of a given word
	 * 
	 * @param newWord the word to copy
	 */
	public Word(Word newWord)
	{
		this.type = newWord.type;
		this.score = newWord.score;

		this.word = new ArrayList<Tile>();

		// Creates a copy of every tile
		for (Tile nextTile : newWord.word)
		{
			Tile tile = new Tile(nextTile.getChar());
			tile.setPos(nextTile.row, nextTile.col);
			this.word.add(tile);
		}

		this.firstTile = word.get(0);
	}

	/**
	 * Sets the score for a word
	 * 
	 * @param score the set of the score
	 */
	public void setScore(int score)
	{
		this.score = score;
	}

	/**
	 * Gives the score for a word
	 * 
	 * @return the word's score
	 */
	public int getScore()
	{
		return score;
	}

	/**
	 * Returns the string representation of this word
	 * 
	 * @return the word
	 */
	public String toString()
	{
		StringBuilder str = new StringBuilder();
		for (int i = 0; i < word.size(); i++)
		{
			str.append(word.get(i).getChar());
		}

		return str.toString();
	}

	/**
	 * Return the length of the word
	 * 
	 * @return the length of the word
	 */
	public int length()
	{
		return word.size();
	}

	/**
	 * Return the tile at index i
	 * 
	 * @param the index i
	 * @return the tile
	 */
	public Tile getTile(int i)
	{
		return word.get(i);
	}

	/**
	 * Returns the row index of the first letter
	 * 
	 * @return the row index of the first letter
	 */
	public int getFirstRow()
	{
		return firstTile.getRow();
	}

	/**
	 * Returns the column index of the first letter
	 * 
	 * @return the column index of the first letter
	 */
	public int getFirstCol()
	{
		return firstTile.getCol();
	}

	/**
	 * Returns the type of the word
	 * 
	 * @return the type of the word
	 */
	public int getType()
	{
		return type;
	}

	/**
	 * Returns the ArrayList of tiles representing this word
	 * @return	the ArrayList of tiles representing this word
	 */
	public ArrayList<Tile> getWord()
	{
		return word;
	}
}
