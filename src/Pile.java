/**
 * Keeps track of a deck of Scrabble tiles and models deck behaviour
 * 
 * 
 */
public class Pile
{
	Tile[] deck; // All of the tiles in the Scrabble deck
	int topTile; // The index of the next tile to be picked out of the pouch

	// The distribution of tiles based on letter
	private static final int[] distribution = { 9, 2, 2, 4, 12, 2, 3, 2, 9, 1,
			1, 4, 2, 6, 8, 2, 1, 6, 4, 6, 4, 2, 2, 1, 2, 1, 2 };
	private static final char[] letters = { 'A', 'B', 'C', 'D', 'E', 'F', 'G',
			'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
			'U', 'V', 'W', 'X', 'Y', 'Z', ' ' };

	/**
	 * Creates a complete Scrabble deck sorted in alphabetical order with blanks
	 * at the end
	 */
	public Pile()
	{
		// Create an array to store the Tiles in
		deck = new Tile[100];

		int deckIndex = 0;

		// Fill the Pile
		for (int i = 0; i < distribution.length; i++)
		{
			int noOfLetter = distribution[i];

			// Create the necessary number of each letter
			while (noOfLetter > 0)
			{
				char letter = letters[i];
				Tile nextTile = new Tile(letter);
				deck[deckIndex] = nextTile;
				deckIndex++;
				noOfLetter--;
			}
		}

		// Set the topTile to be the last element in the array
		topTile = deck.length;
	}

	/**
	 * Returns the Pile information as a String of tiles, expressed as a
	 * character and it's value
	 */
	public String toString()
	{
		StringBuffer pileStr = new StringBuffer(deck.length * 3);

		// Convert each Tile in the Pile to a String
		for (int pileIndex = 0; pileIndex < deck.length - 1; pileIndex++)
		{
			// Add each converted String to the pileStr
			Tile nextTile = deck[pileIndex];
			pileStr.append(nextTile.toString());
			pileStr.append(" ");
		}

		return pileStr.toString();
	}

	/**
	 * Removes the top tile from the pile
	 * 
	 * @return the top tile
	 */
	public Tile deal()
	{
		// Check if there are Tiles left to deal
		if (topTile == 0)
		{
			return null;
		}

		// Set the topTile to the next Tile below it
		topTile--;

		// Return the Tile that used to be at the top of the tilePile
		return deck[topTile];

	}

	/**
	 * Returns the number of tiles left in the pile
	 * 
	 * @return the number of tiles left in the pile
	 */
	public int getNoOfTilesLeft()
	{
		return topTile;
	}

	/**
	 * Randomizes the position of Tiles in the Pile 
	 * Precondition: All tiles have been returned to the Pile
	 */
	public void shuffle()
	{
		// Randomly swap the positions of 2 elements
		for (int nextTile = 0; nextTile < topTile; nextTile++)
		{
			// Randomize a new position for the next Tile
			int newPos = (int) (Math.random() * topTile);

			// Swap the Tiles
			Tile temp = deck[nextTile];
			deck[nextTile] = deck[newPos];
			deck[newPos] = temp;
		}
	}

	/**
	 * Resets the top tile to position 100
	 */
	public void recall()
	{
		topTile = deck.length;
	}

	/**
	 * Resets the sizes of all the tiles to large
	 */
	public void resetSizes()
	{
		for (int tile = 0; tile < deck.length; tile++)
		{
			deck[tile].changeSize(1);
		}
	}

	/**
	 * Deals a tile of a given index from the pile
	 * @param tileIndex the index of the tile to deal
	 * @return the tile dealt
	 */
	public Tile dealTile(int tileIndex)
	{
		// Check if there are Tiles left to deal
		if (topTile == 0)
			return null;
		
		return deck[tileIndex];
	}

	/**
	 * Adds a tile to the deck at a given location
	 * @param tileToInsert the tile to add back to the pile
	 * @param insertIndex the index to insert the tile at
	 */
	public void insertTile(Tile tileToInsert, int insertIndex)
	{
		deck[insertIndex] = tileToInsert;
	}

	/**
	 * Finds the index of a given tile in the deck
	 * @param tileToFind the tile to get the location of
	 * @return the index of the tile
	 */
	public int getLocation(Tile tileToFind)
	{
		// Go through the deck to find the tile
		for (int i = 0; i < deck.length; i++)
		{
			if (deck[i].equals(tileToFind))
				return i;
		}
		return -1;
	}
}
