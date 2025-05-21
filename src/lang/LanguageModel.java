/**************************************************************************
 * File name:
 * LanguageModel.java
 *
 * Description:
 * This file implements the LanguageModel class, which is responsible for
 * training language profiles based on text input and analyzing unknown text
 * to determine its most likely language. It interacts with a LanguageDatabaseManager
 * to store and retrieve language frequency data for words and characters.
 *
 * Author:
 * Shivam Patel
 *
 * Date: May 20 2025
 *
 * Concepts:
 * Language profile training (word and character frequencies)
 * Language analysis and scoring
 * Handling of word separators and noise characters
 * Database interaction for persistent storage of language data
 * Regular expressions for text processing
 *
 *************************************************************************/
package lang;

import util.LanguageDatabaseManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class LanguageModel {

    private LanguageDatabaseManager db;

    public static final String PERCENT_FREQ_PROPERTY_NAME = "%_freq";
    private static final boolean DETAILED_LOGGING_ENABLED = false;

    /**************************************************************************
     * Method name:
     * LanguageModel
     *
     * Description:
     * Constructor for the LanguageModel class.
     * Initializes the model with a LanguageDatabaseManager instance.
     *
     * Parameters:
     * @param db The LanguageDatabaseManager instance to be used for database operations.
     *
     * Throws:
     * IllegalArgumentException if the provided LanguageDatabaseManager is null.
     *
     * Author:
     * Shivam Patel
     *
     * Date: May 20 2025
     *
     *************************************************************************/
    public LanguageModel(LanguageDatabaseManager db) {
        if (db == null) {
            throw new IllegalArgumentException("LanguageDatabaseManager cannot be null.");
        }
        this.db = db;
    }

    /**************************************************************************
     * Method name:
     * log
     *
     * Description:
     * A private helper method for logging messages to the console if detailed logging
     * is enabled. This helps in debugging and understanding the flow of the model.
     *
     * Parameters:
     * @param message The string message to be logged.
     *
     * Author:
     * Shivam Patel
     *
     * Date: May 20 2025
     *
     *************************************************************************/
    private void log(String message) {
        if (DETAILED_LOGGING_ENABLED) {
            System.out.println("[LOG] " + message);
        }
    }

    /**************************************************************************
     * Method name:
     * mapCharacterToStringKey
     *
     * Description:
     * Maps special characters (like space, tab, newline) to unique string keys
     * for consistent storage in the database. Other characters are returned as their
     * string representation. Includes a warning for unusual whitespace characters
     * that might cause issues.
     *
     * Parameters:
     * @param c The character to be mapped.
     *
     * Returns:
     * A string key representation of the character.
     *
     * Author:
     * Shivam Patel
     *
     * Date: May 20 2025
     *
     *************************************************************************/
    private String mapCharacterToStringKey(char c) {
        switch (c) {
            case ' ': return "_SPACE_";
            case '\t': return "_TAB_";
            case '\n': return "_NEWLINE_";
            case '\r': return "_CARRIAGE_RETURN_";
            default:
                String val = String.valueOf(c);
                // This check is a safeguard. Most non-whitespace printable characters
                // will not trim to empty. If a character `c` is not one of the above
                // explicitly mapped ones, but String.valueOf(c) *still* trims to empty
                // (e.g., some unusual Unicode whitespace), it could fail DB validation
                // if not uniquely mapped. For now, assuming this switch covers common cases.
                if (!val.isEmpty() && val.trim().isEmpty()) {
                    log("[WARN] Character 'U+" + Integer.toHexString(c) + "' (string: \"" + val.replace("\n", "\\n").replace("\t", "\\t") + "\") " +
                            "is not explicitly mapped but its string representation trims to empty. " +
                            "This might cause database issues if it's stored. Consider mapping it if errors occur for this character type.");
                }
                return val;
        }
    }

    /**************************************************************************
     * Method name:
     * train
     *
     * Description:
     * Trains a language model based on the provided text for a specified language.
     * It calculates word and character frequencies, considering given word separators,
     * and stores these frequencies in the database. Characters defined as explicit
     * single-character word separators will be excluded from character frequency training.
     *
     * Parameters:
     * @param strLanguageName The name of the language to be trained.
     * @param strText The text content to be used for training.
     * @param wordSeparators An array of strings defining the word separators. If empty,
     * words are split by whitespace.
     *
     * Throws:
     * IllegalArgumentException if language name or word separators are null or invalid.
     * IOException if there is an error during database operations.
     *
     * Author:
     * Shivam Patel
     *
     * Date: May 20 2025
     *
     *************************************************************************/
    public void train(String strLanguageName, String strText, String[] wordSeparators) throws IOException {
        log("--- Training Started for Language: " + strLanguageName + " ---");
        if (strLanguageName == null || strLanguageName.trim().isEmpty()) {
            throw new IllegalArgumentException("Language name cannot be null or empty.");
        }
        if (strText == null) {
            strText = "";
        }
        if (wordSeparators == null) {
            throw new IllegalArgumentException("Word separators array cannot be null. Pass an empty array if no specific separators are defined beyond default splitting by whitespace for words.");
        }

        String lang = strLanguageName.trim();
        String textToProcess = strText.toLowerCase();
        log("Training text (lowercase): \"" + textToProcess.replace("\n", "\\n").replace("\t", "\\t") + "\"");

        Map<String, Integer> wordFrequencies = new HashMap<>();
        String trainingWordSplitRegex;
        if (wordSeparators.length == 0) {
            // If no separators are explicitly provided, default to splitting words by whitespace.
            // This also implies no characters are designated as *explicit* single-char separators for char exclusion.
            trainingWordSplitRegex = "\\s+";
        } else {
            StringBuilder sb = new StringBuilder();
            for (String sep : wordSeparators) {
                if (sep != null && !sep.isEmpty()) {
                    if (sb.length() > 0) {
                        sb.append("|");
                    }
                    sb.append(Pattern.quote(sep));
                }
            }
            if (sb.length() == 0) { // All separators in the array were null/empty
                trainingWordSplitRegex = "\\s+";
            } else {
                trainingWordSplitRegex = sb.toString();
            }
        }
        log("Training word split regex: '" + trainingWordSplitRegex + "'");

        String[] words = textToProcess.split(trainingWordSplitRegex);
        for (String word : words) {
            if (!word.isEmpty()) {
                wordFrequencies.put(word, wordFrequencies.getOrDefault(word, 0) + 1);
            }
        }
        log("Word Frequencies for '" + lang + "': " + wordFrequencies);

        // Block comment: Determine which single characters are explicit separators for this training run.
        Set<Character> explicitSingleCharSeparators = new HashSet<>();
        for (String sep : wordSeparators) {
            if (sep != null && sep.length() == 1) {
                explicitSingleCharSeparators.add(sep.charAt(0));
            }
        }
        log("Explicit single-character word separators for this training run (will be excluded from char training): " + explicitSingleCharSeparators);

        // Block comment: Character frequency calculation.
        // Only characters NOT in explicitSingleCharSeparators will be included.
        Map<Character, Integer> characterFrequenciesToStore = new HashMap<>();
        for (char character : textToProcess.toCharArray()) {
            if (explicitSingleCharSeparators.contains(character)) {
                log("Skipping character 'U+" + Integer.toHexString(character) + "' ('" + String.valueOf(character).replace("\n", "\\n").replace("\t", "\\t") + "') for character frequency training in '" + lang + "' because it's an explicit single-char wordSeparator for this run.");
                continue;
            }
            // This character is NOT an explicit separator for this run, so its frequency is tracked.
            // This includes space, tab, etc., if they were NOT in wordSeparators.
            characterFrequenciesToStore.put(character, characterFrequenciesToStore.getOrDefault(character, 0) + 1);
        }

        Map<String, Integer> mappedCharacterFrequenciesForLog = new HashMap<>();
        for(Map.Entry<Character,Integer> entry : characterFrequenciesToStore.entrySet()){
            mappedCharacterFrequenciesForLog.put(mapCharacterToStringKey(entry.getKey()), entry.getValue());
        }
        log("Character Frequencies to be stored (mapped keys, excluding explicit single-char separators) for '" + lang + "': " + mappedCharacterFrequenciesForLog);


        log("Incrementing word absolute frequencies for '" + lang + "'...");
        for (Map.Entry<String, Integer> entry : wordFrequencies.entrySet()) {
            try {
                this.db.incrementWordAbsFreq(lang, entry.getKey(), entry.getValue());
            } catch (IOException e) {
                System.err.println("[ERROR] Training: Failed to increment word freq for '" + entry.getKey() + "': " + e.getMessage());
            }
        }

        log("Incrementing character absolute frequencies for '" + lang + "'...");
        for (Map.Entry<Character, Integer> entry : characterFrequenciesToStore.entrySet()) {
            char originalChar = entry.getKey();
            String charKey = mapCharacterToStringKey(originalChar); // Map key for DB compatibility
            try {
                this.db.incrementCharacterAbsFreq(lang, charKey, entry.getValue());
            } catch (IOException e) {
                System.err.println("[ERROR] Training: Failed to increment char freq for '" + originalChar + "' (key: " + charKey + "): " + e.getMessage());
            }
        }

        log("Updating all words percent frequency for '" + lang + "' (expecting property: '" + PERCENT_FREQ_PROPERTY_NAME + "')...");
        try {
            this.db.updateAllWordsPercentFreq(lang);
            log("Successfully called updateAllWordsPercentFreq for " + lang);
        } catch (IOException e) {
            System.err.println("[ERROR] Training: Critical error updating all words percent frequency for language '" + lang + "': " + e.getMessage());
            throw e;
        }

        log("Updating all characters percent frequency for '" + lang + "' (expecting property: '" + PERCENT_FREQ_PROPERTY_NAME + "')...");
        try {
            this.db.updateAllCharactersPercentFreq(lang);
            log("Successfully called updateAllCharactersPercentFreq for " + lang);
        } catch (IOException e) {
            System.err.println("[ERROR] Training: Critical error updating all characters percent frequency for language '" + lang + "': " + e.getMessage());
            throw e;
        }
        log("--- Training Ended for Language: " + lang + " ---");
    }

    /**************************************************************************
     * Method name:
     * analyze
     *
     * Description:
     * Analyzes the given text to determine the most likely language(s) based on
     * stored language profiles. It calculates a raw score for each known language
     * by comparing the input text's word and character frequencies against the
     * trained profiles, and then converts these raw scores into percentages.
     *
     * Parameters:
     * @param strText The text to be analyzed for language detection.
     *
     * Returns:
     * A HashMap where keys are language names (String) and values are
     * the percentage likelihood (Double) that the input text belongs to that language.
     * Returns an empty map if no text is provided or no languages are stored.
     *
     * Throws:
     * IOException if there is an error during database operations (e.g., fetching stored languages).
     *
     * Author:
     * Shivam Patel
     *
     * Date: May 20 2025
     *
     *************************************************************************/
    public HashMap<String, Double> analyze(String strText) throws IOException {
        log("\n--- Analysis Started for Text: \"" + strText.replace("\n", "\\n").replace("\t", "\\t") + "\" ---");
        if (strText == null) {
            log("Analysis: Input text is null, returning empty map.");
            return new HashMap<>();
        }

        String textToAnalyze = strText.toLowerCase();
        log("Analysis: Text to analyze (lowercase): \"" + textToAnalyze.replace("\n", "\\n").replace("\t", "\\t") + "\"");

        Map<String, Integer> inputWordFreq = new HashMap<>();
        String analysisWordSplitPattern = "[\\s.,;:!?()\\-]+";
        log("Analysis: Word split pattern: '" + analysisWordSplitPattern + "'");
        String[] inputWords = textToAnalyze.split(analysisWordSplitPattern);
        for (String word : inputWords) {
            if (!word.isEmpty()) {
                inputWordFreq.put(word, inputWordFreq.getOrDefault(word, 0) + 1);
            }
        }
        log("Analysis: Input Text Word Frequencies: " + inputWordFreq);

        // For analysis, count all characters from input text.
        // Their mapped keys will be used for lookup. If a character was an
        // explicit separator for a specific language during its training,
        // the lookup for that char_key in that language's DB profile will naturally fail.
        Map<Character, Integer> inputCharOriginalFreq = new HashMap<>();
        for (char character : textToAnalyze.toCharArray()) {
            inputCharOriginalFreq.put(character, inputCharOriginalFreq.getOrDefault(character, 0) + 1);
        }
        Map<String, Integer> inputMappedCharFreqForLog = new HashMap<>();
        for(Map.Entry<Character, Integer> entry : inputCharOriginalFreq.entrySet()){
            inputMappedCharFreqForLog.put(mapCharacterToStringKey(entry.getKey()), entry.getValue());
        }
        log("Analysis: Input Text Mapped Character Frequencies (keys for lookup): " + inputMappedCharFreqForLog);

        String[] storedLanguages;
        try {
            log("Analysis: Fetching stored languages...");
            storedLanguages = this.db.getStoredLanguages();
            log("Analysis: Stored languages retrieved: " + (storedLanguages != null ? java.util.Arrays.toString(storedLanguages) : "null"));
        } catch (IOException e) {
            System.err.println("[ERROR] Analysis: Error fetching stored languages: " + e.getMessage());
            throw e;
        }

        if (storedLanguages == null || storedLanguages.length == 0) {
            log("Analysis: No stored languages found in DB. Returning empty map.");
            return new HashMap<>();
        }

        HashMap<String, Double> languageRawScores = new HashMap<>();
        double totalScoreSum = 0.0;

        log("Analysis: Using property name '" + PERCENT_FREQ_PROPERTY_NAME + "' for frequency lookup.");

        for (String lang : storedLanguages) {
            if (lang == null || lang.trim().isEmpty()) {
                log("Analysis: Skipping invalid language name from DB: '" + lang + "'");
                continue;
            }
            log("--- Scoring for Language: " + lang + " ---");
            double currentLangScore = 0.0;
            double wordScoreContribution = 0.0;
            double charScoreContribution = 0.0;

            log("Calculating word-based score for " + lang + "...");
            for (Map.Entry<String, Integer> entry : inputWordFreq.entrySet()) {
                String word = entry.getKey();
                int countInInput = entry.getValue();
                String freqStr = null;
                try {
                    log("Looking up word '" + word + "' for lang '" + lang + "', property '" + PERCENT_FREQ_PROPERTY_NAME + "'");
                    freqStr = this.db.getWordProperty(lang, word, PERCENT_FREQ_PROPERTY_NAME);
                    log("Retrieved freqStr for word '" + word + "' in '" + lang + "': '" + freqStr + "'");
                    if (freqStr != null && !freqStr.trim().isEmpty()) {
                        try {
                            double parsedFreq = Double.parseDouble(freqStr);
                            log("Parsed frequency for word '" + word + "': " + parsedFreq);
                            wordScoreContribution += countInInput * parsedFreq;
                        } catch (NumberFormatException nfe) {
                            System.err.println("[WARN] Analysis: Could not parse word frequency '" + freqStr + "' for word '" + word + "' in language '" + lang + "'.");
                        }
                    } else {
                        log("Frequency string for word '" + word + "' was null or empty for lang '" + lang + "' (before parsing).");
                    }
                } catch (IOException e) {
                    log("IOException while getting property for word '" + word + "' in '" + lang + "': " + e.getMessage() + ". Assuming 0 frequency contribution.");
                }
            }
            log("Word-based score component for " + lang + ": " + wordScoreContribution);

            log("Calculating character-based score for " + lang + "...");
            for (Map.Entry<Character, Integer> entry : inputCharOriginalFreq.entrySet()) {
                char originalChar = entry.getKey();
                String charKey = mapCharacterToStringKey(originalChar); // Map the input character to its DB key
                int countInInput = entry.getValue();
                String freqStr = null;
                try {
                    log("Looking up char_key '" + charKey + "' (original: '" + String.valueOf(originalChar).replace("\n", "\\n").replace("\t", "\\t") + "') for lang '" + lang + "', property '" + PERCENT_FREQ_PROPERTY_NAME + "'");
                    freqStr = this.db.getCharacterProperty(lang, charKey, PERCENT_FREQ_PROPERTY_NAME);
                    log("Retrieved freqStr for char_key '" + charKey + "' in '" + lang + "': '" + freqStr + "'");
                    if (freqStr != null && !freqStr.trim().isEmpty()) {
                        try {
                            double parsedFreq = Double.parseDouble(freqStr);
                            log("Parsed frequency for char_key '" + charKey + "': " + parsedFreq);
                            charScoreContribution += countInInput * parsedFreq;
                        } catch (NumberFormatException nfe) {
                            System.err.println("[WARN] Analysis: Could not parse char frequency '" + freqStr + "' for char key '" + charKey + "' (original: '" + originalChar + "') in language '" + lang + "'.");
                        }
                    } else {
                        log("Frequency string for char_key '" + charKey + "' was null or empty for lang '" + lang + "' (before parsing).");
                    }
                } catch (IOException e) {
                    log("IOException while getting property for char_key '" + charKey + "' in '" + lang + "': " + e.getMessage() + ". Assuming 0 frequency contribution.");
                }
            }
            log("Character-based score component for " + lang + ": " + charScoreContribution);

            currentLangScore = wordScoreContribution + charScoreContribution;
            log("Total raw score for " + lang + ": " + currentLangScore);
            languageRawScores.put(lang, currentLangScore);
            totalScoreSum += currentLangScore;
        }

        log("Analysis: All raw scores: " + languageRawScores);
        log("Analysis: Total raw score sum: " + totalScoreSum);

        HashMap<String, Double> resultPercentages = new HashMap<>();
        if (languageRawScores.isEmpty()){
            log("Analysis: No languages were scored, returning empty percentage map.");
            return resultPercentages;
        }

        if (totalScoreSum > 0) {
            for (Map.Entry<String, Double> entry : languageRawScores.entrySet()) {
                resultPercentages.put(entry.getKey(), (entry.getValue() / totalScoreSum) * 100.0);
            }
        } else {
            log("Analysis: TotalScoreSum is 0 or less. Setting all percentages to 0.0 for " + languageRawScores.size() + " language(s).");
            for (String langKey : languageRawScores.keySet()) {
                resultPercentages.put(langKey, 0.0);
            }
        }
        log("Analysis: Final result percentages: " + resultPercentages);
        log("--- Analysis Ended ---");
        return resultPercentages;
    }

    /**************************************************************************
     * Method name:
     * main
     *
     * Description:
     * The main method for demonstrating the LanguageModel's training and analysis
     * functionalities. It initializes a LanguageDatabaseManager, creates a
     * LanguageModel, and then performs various training and analysis operations
     * with different language texts and separator configurations.
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
        LanguageDatabaseManager dbInstance = null;
        try {
            dbInstance = LanguageDatabaseManager.getDatabaseManager(
                    "https://frhgfmnvkopdwpiorszb.supabase.co",
                    "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImZyaGdmbW52a29wZHdwaW9yc3piIiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTc0NjQ5MzI0OCwiZXhwIjoyMDYyMDY5MjQ4fQ.bu7u6Doh9PMWGzpeROtFDm8qnSr5gk56m3vIDllMs7E"
            );
            System.out.println("LanguageDatabaseManager initialized.");
        } catch (IOException | IllegalStateException e) {
            System.err.println("Failed to initialize LanguageDatabaseManager: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        LanguageModel model = new LanguageModel(dbInstance);
        System.out.println("LanguageModel instance created.");

        try {
            // Separators for English, French, Spanish will cause these single chars
            // (space, comma, period, etc.) to be EXCLUDED from character training for these languages.
            String[] commonSeparators = {" ", ",", ".", ";", ":", "!", "?", "(", ")", "\n", "\t", "-"};

            // For testlang, only space is a separator. So comma, period etc. WOULD be trained as chars if present in its text.
            String[] testLangSeparators = {" "};

            // For lang_alpha_space, NO separators are given in the array.
            // wordSeparators.length == 0 will make word splitting default to "\\s+".
            // For character training, NO character will be in `explicitSingleCharSeparators`.
            // Thus, ' ' (space) encountered in its training text WILL BE stored (as "_SPACE_").
            String[] noExplicitSeparators = {};


            System.out.println("\nStarting training for English (via main)...");
            model.train("english", "Half newton Hello world, this is a sample text.\nHello again, world of wonders.\tTest tab.", commonSeparators);
            System.out.println("English training complete (via main).");

            System.out.println("\nStarting training for French (via main)...");
            model.train("french", "Bonjour le monde. Ceci est un texte d'exemple.\nBonjour encore, monde des merveilles.\tOnglet de test.", commonSeparators);
            System.out.println("French training complete (via main).");

            System.out.println("\nStarting training for Spanish (via main)...");
            model.train("spanish", "Hola mundo, este es un texto de ejemplo.\nHola de nuevo, mundo de maravillas.\tPestaña de prueba.", commonSeparators);
            System.out.println("Spanish training complete (via main).");

            System.out.println("\nStarting training for TestLang (simple) (via main)...");
            model.train("testlang", "aaa bbb aaa cc aaa bbb", testLangSeparators);
            System.out.println("TestLang training complete (via main).");

            System.out.println("\nStarting training for lang_alpha_space (space is a char)...");
            model.train("lang_alpha_space", "word1 word2 space is char . comma,test", noExplicitSeparators);
            System.out.println("lang_alpha_space training complete.");


            String unknownText1 = "Hello, this feels like an English text.";
            HashMap<String, Double> analysisResult1 = model.analyze(unknownText1);
            System.out.println("\nAnalysis for '" + unknownText1 + "': " + analysisResult1);

            String alphaSpaceText = "word1 space"; // Contains ' ' which should be found if lang_alpha_space was trained correctly
            System.out.println("\nAnalyzing text for lang_alpha_space: '" + alphaSpaceText + "'");
            HashMap<String, Double> analysisAlphaSpace = model.analyze(alphaSpaceText);
            System.out.println("Analysis for lang_alpha_space text: " + analysisAlphaSpace);

            String commaTextForAlphaSpace = "word1, test"; // ',' should be a character for lang_alpha_space
            System.out.println("\nAnalyzing comma text for lang_alpha_space: '" + commaTextForAlphaSpace + "'");
            HashMap<String, Double> analysisCommaText = model.analyze(commaTextForAlphaSpace);
            System.out.println("Analysis for comma text for lang_alpha_space: " + analysisCommaText);


        } catch (IOException e) {
            System.err.println("\nAn error occurred during model training or analysis: " + e.getMessage());
            e.printStackTrace();
        }
    }
}