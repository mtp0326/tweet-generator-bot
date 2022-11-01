package org.cis120;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;

/** Tests for TweetParser */
public class TweetParserTest {

    // A helper function to create a singleton list from a word
    private static List<String> singleton(String word) {
        List<String> l = new LinkedList<String>();
        l.add(word);
        return l;
    }

    // A helper function for creating lists of strings
    private static List<String> listOfArray(String[] words) {
        List<String> l = new LinkedList<String>();
        for (String s : words) {
            l.add(s);
        }
        return l;
    }

    // Cleaning and filtering tests -------------------------------------------
    @Test
    public void removeURLsTest() {
        assertEquals("abc . def.", TweetParser.removeURLs("abc http://www.cis.upenn.edu. def."));
        assertEquals("abc", TweetParser.removeURLs("abc"));
        assertEquals("abc ", TweetParser.removeURLs("abc http://www.cis.upenn.edu"));
        assertEquals("abc .", TweetParser.removeURLs("abc http://www.cis.upenn.edu."));
        assertEquals(" abc ", TweetParser.removeURLs("http:// abc http:ala34?#?"));
        assertEquals(" abc  def", TweetParser.removeURLs("http:// abc http:ala34?#? def"));
        assertEquals(" abc  def", TweetParser.removeURLs("https:// abc https``\":ala34?#? def"));
        assertEquals("abchttp", TweetParser.removeURLs("abchttp"));
    }

    @Test
    public void testCleanWord() {
        assertEquals("abc", TweetParser.cleanWord("abc"));
        assertEquals("abc", TweetParser.cleanWord("ABC"));
        assertNull(TweetParser.cleanWord("@abc"));
        assertEquals("ab'c", TweetParser.cleanWord("ab'c"));
    }

    @Test
    public void testExtractColumnGetsCorrectColumn() {
        assertEquals(
                " This is a tweet.",
                TweetParser.extractColumn(
                        "wrongColumn, wrong column, wrong column!, This is a tweet.", 3
                )
        );
    }

    @Test
    public void parseAndCleanSentenceNonEmptyFiltered() {
        List<String> sentence = TweetParser.parseAndCleanSentence("abc #@#F");
        List<String> expected = new LinkedList<String>();
        expected.add("abc");
        assertEquals(expected, sentence);
    }

    @Test
    public void testParseAndCleanTweetRemovesURLS1() {
        List<List<String>> sentences = TweetParser
                .parseAndCleanTweet("abc http://www.cis.upenn.edu");
        List<List<String>> expected = new LinkedList<List<String>>();
        expected.add(singleton("abc"));
        assertEquals(expected, sentences);
    }

    @Test
    public void testCsvDataToTrainingDataSimpleCSV() {
        StringReader sr = new StringReader(
                "0, The end should come here.\n" +
                        "1, This comes from data with no duplicate words!"
        );
        BufferedReader br = new BufferedReader(sr);
        List<List<String>> tweets = TweetParser.csvDataToTrainingData(br, 1);
        List<List<String>> expected = new LinkedList<List<String>>();
        expected.add(listOfArray("the end should come here".split(" ")));
        expected.add(listOfArray("this comes from data with no duplicate words".split(" ")));
        assertEquals(expected, tweets);
    }

    @Test
    public void testCsvDataToTweetsSimpleCSV() {
        StringReader sr = new StringReader(
                "0, The end should come here.\n" +
                        "1, This comes from data with no duplicate words!"
        );
        BufferedReader br = new BufferedReader(sr);
        List<String> tweets = TweetParser.csvDataToTweets(br, 1);
        List<String> expected = new LinkedList<String>();
        expected.add(" The end should come here.");
        expected.add(" This comes from data with no duplicate words!");
        assertEquals(expected, tweets);
    }

    /* **** ****** **** WRITE YOUR TESTS BELOW THIS LINE **** ****** **** */
    @Test
    public void testExtractIndex0() {
        assertEquals(
                "wrongColumn",
                TweetParser.extractColumn(
                        "wrongColumn, wrong column, wrong column!, This is a tweet.", 0
                )
        );
    }

    @Test
    public void testExtractOutOfBoundsUnderLimit() {
        assertEquals(
                null,
                TweetParser.extractColumn(
                        "wrongColumn, wrong column, wrong column!, This is a tweet.", -1
                )
        );
    }

    @Test
    public void testExtractOutOfBoundsOverLimit() {
        assertEquals(
                null,
                TweetParser.extractColumn(
                        "wrongColumn, wrong column, wrong column!, This is a tweet.", 4
                )
        );
    }

    @Test
    public void testCsvDataToTweetsOneSentence() {
        StringReader sr = new StringReader(
                "Hello."
        );
        BufferedReader br = new BufferedReader(sr);
        List<String> tweets = TweetParser.csvDataToTweets(br, 0);
        List<String> expected = new LinkedList<String>();
        expected.add("Hello.");
        assertEquals(expected, tweets);
    }

    @Test
    public void testParseAndCleanTweetRemoveNull() {
        List<List<String>> sentences = TweetParser
                .parseAndCleanTweet("abc #@#F");
        List<List<String>> expected = new LinkedList<List<String>>();
        expected.add(singleton("abc"));
        assertEquals(expected, sentences);
    }

    @Test
    public void testCsvDataToTrainingDataNullTweetIncludedCSV() {
        StringReader sr = new StringReader(
                "0, The end should come here.\n" +
                        "1, This comes #@#F data with no duplicate words!\n" +
                        "2, #@#F"
        );
        BufferedReader br = new BufferedReader(sr);
        List<List<String>> tweets = TweetParser.csvDataToTrainingData(br, 1);
        List<List<String>> expected = new LinkedList<List<String>>();
        expected.add(listOfArray("the end should come here".split(" ")));
        expected.add(listOfArray("this comes data with no duplicate words".split(" ")));
        assertEquals(expected, tweets);
    }

    @Test
    public void testCsvDataToTrainingDataEmptyNullTweetCSV() {
        StringReader sr = new StringReader(
                "0, The end should come here.\n" +
                        "1, This comes #@#F data with no duplicate words!\n" +
                        ""
        );
        BufferedReader br = new BufferedReader(sr);
        List<List<String>> tweets = TweetParser.csvDataToTrainingData(br, 0);
        List<List<String>> expected = new LinkedList<List<String>>();
        expected.add(listOfArray("0".split(" ")));
        expected.add(listOfArray("1".split(" ")));
        assertEquals(expected, tweets);
    }

    @Test
    public void testCsvDataToTrainingDataCountOutOFBoundsCSV() {
        StringReader sr = new StringReader(
                "0, The end should come here.\n" +
                        "1\n" +
                        "2, This comes #@#F data with no duplicate words!"
        );
        BufferedReader br = new BufferedReader(sr);
        List<List<String>> tweets = TweetParser.csvDataToTrainingData(br, 1);
        List<List<String>> expected = new LinkedList<List<String>>();
        expected.add(listOfArray("the end should come here".split(" ")));
        expected.add(listOfArray("this comes data with no duplicate words".split(" ")));
        assertEquals(expected, tweets);
    }

    @Test
    public void testParseAndCleanTweetBadWords() {
        String tweet = "#@#F";
        List<List<String>> expected = new LinkedList<>();
        assertEquals(expected, TweetParser.parseAndCleanTweet((tweet)));
    }

    @Test
    public void testCsvDataToTrainingDataOutOfBoundsColumnForSomeLine() {
        StringReader sr = new StringReader(
                "0, The end should come here, with me.\n" +
                        "1, This comes from data with no duplicate words!"
        );
        BufferedReader br = new BufferedReader(sr);
        List<List<String>> tweets = TweetParser.csvDataToTrainingData(br, 2);
        List<List<String>> expected = new LinkedList<List<String>>();
        expected.add(listOfArray("with me".split(" ")));
        assertEquals(expected, tweets);
    }

    @Test
    public void testCsvDataToTrainingDataOutOfBoundsAllColumn() {
        StringReader sr = new StringReader(
                "0, The end should come here\n" +
                        "1, This comes from data with no duplicate words!"
        );
        BufferedReader br = new BufferedReader(sr);
        List<List<String>> tweets = TweetParser.csvDataToTrainingData(br, 2);
        List<List<String>> expected = new LinkedList<List<String>>();
        assertEquals(expected, tweets);
    }
}