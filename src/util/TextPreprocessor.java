/**************************************************************************
 * File name:
 * TextPreprocessor.java
 *
 * Description:
 * This file provides utility methods for preprocessing text data.
 * It includes functionalities for converting text to lowercase,
 * removing emojis, Roman numerals, normalizing punctuation, and
 * optionally removing custom noise words, all aimed at preparing text
 * for language analysis or other NLP tasks.
 *
 * Author:
 * Shivam Patel
 *
 * Date: May 20 2025
 *
 * Concepts:
 * Regular expressions for pattern matching and replacement (emojis, Roman numerals, punctuation)
 * String manipulation (toLowerCase, replaceAll, trim, split, join)
 * Data structures for efficient lookup (HashSet for noise words)
 * Overloaded methods for flexible usage
 *
 *************************************************************************/
package util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TextPreprocessor {

    /**************************************************************************
     * Field name:
     * EMOJI_PATTERN
     *
     * Description:
     * A regular expression pattern used to detect and match various Unicode emoji ranges.
     * This pattern helps in identifying and subsequently removing emojis from text.
     *
     * Author:
     * Shivam Patel
     *
     * Date: May 20 2025
     *
     *************************************************************************/
    private static final Pattern EMOJI_PATTERN = Pattern.compile(
            "[" +
                    "\u2600-\u26FF" +        // Miscellaneous symbols
                    "\u2700-\u27BF" +        // Dingbats
                    "\\x{1F300}-\\x{1F5FF}" + // Misc Symbols and Pictographs  (Note: double backslash for string literal)
                    "\\x{1F600}-\\x{1F64F}" + // Emoticons
                    "\\x{1F680}-\\x{1F6FF}" + // Transport and Map
                    "\\x{1F1E0}-\\x{1F1FF}" + // Flags (iOS)
                    // Add more ranges if needed
                    "]+");

    /**************************************************************************
     * Field name:
     * ROMAN_NUMERAL_PATTERN
     *
     * Description:
     * A regular expression pattern designed to identify standalone Roman numerals
     * (e.g., "IV", "mcmxcix"). It uses word boundaries to ensure whole Roman numerals
     * are matched and not parts of other words.
     *
     * Author:
     * Shivam Patel
     *
     * Date: May 20 2025
     *
     *************************************************************************/
    private static final Pattern ROMAN_NUMERAL_PATTERN = Pattern.compile(
            "\\b(m{0,3})(cm|cd|d?c{0,3})(xc|xl|l?x{0,3})(ix|iv|v?i{0,3})\\b");

    /**************************************************************************
     * Field name:
     * PUNCT_TO_REPLACE_WITH_SPACE
     *
     * Description:
     * A regular expression pattern that defines a set of common punctuation characters
     * that should be replaced with a single space during text preprocessing.
     * This pattern deliberately excludes apostrophes and hyphens, allowing them to
     * be handled by the language model's word tokenization.
     *
     * Author:
     * Shivam Patel
     *
     * Date: May 20 2025
     *
     *************************************************************************/
    private static final Pattern PUNCT_TO_REPLACE_WITH_SPACE = Pattern.compile(
            "[.,;:!?()\"“”‘’«»–—{}\\[\\]<>]+" // Added more common quoting/bracketing
    );


    /**************************************************************************
     * Method name:
     * preprocess
     *
     * Description:
     * Primary text pre-processing method.
     * Performs default cleaning (lowercase, emoji removal, Roman numeral removal,
     * normalization of common "hard" punctuation to spaces, and whitespace normalization).
     * Then, optionally removes a list of custom-defined noise words.
     * Apostrophes and hyphens within potential words are generally preserved by default cleaning,
     * allowing LanguageModel's tokenizer to handle them based on its wordSeparators.
     *
     * Parameters:
     * @param rawText The original text string.
     * @param customNoiseWords An array of specific words (case will be ignored) to remove.
     * Pass null or an empty array to skip this step.
     *
     * Returns:
     * The pre-processed text string.
     *
     * Author:
     * Shivam Patel
     *
     * Date: May 20 2025
     *
     *************************************************************************/
    public static String preprocess(String rawText, String[] customNoiseWords) {
        if (rawText == null) {
            return "";
        }

        // 1. Convert to Lowercase
        String text = rawText.toLowerCase();

        // 2. Default: Remove Emojis (replace with a space to avoid merging words)
        text = EMOJI_PATTERN.matcher(text).replaceAll(" ");

        // 3. Default: Remove Roman Numerals (replace with a space)
        text = ROMAN_NUMERAL_PATTERN.matcher(text).replaceAll(" ");

        // 4. Default: Normalize "hard" Punctuation to spaces
        // This helps in separating words cleanly before custom noise word removal.
        // Apostrophes and hyphens are deliberately left mostly untouched here.
        text = PUNCT_TO_REPLACE_WITH_SPACE.matcher(text).replaceAll(" ");

        // 5. Default: Normalize all types of whitespace (newline, tab, etc.) to a single space
        text = text.replaceAll("[\n\t\r]+", " "); // Convert newlines, tabs, CR to spaces
        text = text.replaceAll("\\s+", " ").trim();   // Consolidate multiple spaces and trim

        // 6. Remove custom noise words (if any provided)
        if (customNoiseWords != null && customNoiseWords.length > 0) {
            // Split into words based on current spaces.
            // The previous steps should have made space the primary delimiter.
            List<String> words = new ArrayList<>(Arrays.asList(text.split(" ")));

            Set<String> noiseSet = new HashSet<>();
            for (String noise : customNoiseWords) {
                if (noise != null) {
                    noiseSet.add(noise.toLowerCase()); // Ensure noise words are compared in lowercase
                }
            }

            words.removeIf(word -> noiseSet.contains(word) || word.isEmpty()); // Also remove empty strings from splitting

            text = String.join(" ", words);
            // Re-normalize spaces in case removing words left multiple spaces or if the list became empty
            text = text.replaceAll("\\s+", " ").trim();
        }

        return text;
    }

    /**************************************************************************
     * Method name:
     * preprocess
     *
     * Description:
     * Overloaded method to perform only default cleaning (emojis, Roman numerals,
     * specific punctuation to spaces, whitespace normalization).
     * It calls the primary `preprocess` method with a null value for custom noise words.
     *
     * Parameters:
     * @param rawText The original text string.
     *
     * Returns:
     * The pre-processed text string with default cleaning.
     *
     * Author:
     * Shivam Patel
     *
     * Date: May 20 2025
     *
     *************************************************************************/
    public static String preprocess(String rawText) {
        return preprocess(rawText, null);
    }

    /**************************************************************************
     * Method name:
     * main
     *
     * Description:
     * The main method for demonstrating the functionality of the TextPreprocessor.
     * It includes various sample texts to show how emojis, Roman numerals,
     * punctuation, whitespace, and custom noise words are handled during preprocessing.
     *
     * Parameters:
     * @param args Command line arguments (not used in this demonstration).
     *
     * Author:
     * Shivam Patel
     *
     * Date: May 20 2025
     *
     *************************************************************************/
    public static void main(String[] args) {
        String sampleText1 = "Chapter IV: THE ADVENTURES of Huckleberry Finn ( 😊 )\n" +
                "This is a test, with some punctuation!! And emojis 👍. \n" +
                "Also a Roman numeral mcmxcix. And another: VII.\n" +
                "Some 'quotes' and \"more quotes\". A word-hyphenated. Don't stop.\n" +
                "RareWord1 and commonWord. Another RareWord1.";

        System.out.println("Original Text:\n" + sampleText1);

        String defaultCleaned = TextPreprocessor.preprocess(sampleText1);
        System.out.println("\nDefault Cleaned Text:\n" + defaultCleaned);

        String[] noise = {"rareword1", "another"}; // Note: must be lowercase for custom list
        String customCleaned = TextPreprocessor.preprocess(sampleText1, noise);
        System.out.println("\nCustom Cleaned Text (removing 'rareword1', 'another'):\n" + customCleaned);

        String textWithOnlyHyphens = "state-of-the-art technology re-evaluate";
        System.out.println("\nOriginal with hyphens: " + textWithOnlyHyphens);
        System.out.println("Default cleaned (hyphens preserved): " + TextPreprocessor.preprocess(textWithOnlyHyphens));

        String textWithApostrophes = "it's dylan's book o'connor";
        System.out.println("\nOriginal with apostrophes: " + textWithApostrophes);
        System.out.println("Default cleaned (apostrophes preserved): " + TextPreprocessor.preprocess(textWithApostrophes));
    }
}