import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;

/**
 * The Dictionary class is responsible for all the words that exist in the
 * English language Contains methods that check if word is in the dictionary
 * 
 */
public class Dictionary
{
	private HashSet<String> allWords;
	private Scanner scanner;
	char key;

	/**
	 * Constructs a dictionary object that stores all the words in the scrabble
	 * dictionary as a HashSet
	 */
	public Dictionary()
	{
		allWords = new HashSet<String>();

		try
		{
			scanner = new Scanner(new File("wordlist.txt"));
			while (scanner.hasNext())
			{
				String nextWord = scanner.next();
				allWords.add(nextWord);
			}

		}
		catch (FileNotFoundException e)
		{
			System.out.println("TOO BAD.");
		}
	}

	/**
	 * Checks if a word belong or not to the dictionary
	 * 
	 * @return the boolean who corresponds at the membership of the word in
	 *         dictionary
	 */
	public boolean isWord(String word)
	{
		word = word.toLowerCase();
		if (allWords.contains(word))
		{
			return true;
		}
		return false;
	}
}