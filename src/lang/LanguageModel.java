package lang;

import util.LanguageDatabaseManager;

import java.io.IOException;
import java.io.UnsupportedEncodingException; // For URLEncoder, though not used directly in this file
import java.net.URLEncoder;                 // For URLEncoder, if you were to fix LDBM
import java.nio.charset.StandardCharsets;   // For URLEncoder
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class LanguageModel {

    private LanguageDatabaseManager db;

    // --- IMPORTANT: CONFIGURATION ---
    // If your database stores percentage frequencies under a different name,
    // change this constant to the correct property name.
    public static final String PERCENT_FREQ_PROPERTY_NAME = "%_freq";
    // --- END CONFIGURATION ---

    private static final boolean DETAILED_LOGGING_ENABLED = true; // Toggle for detailed logs

    public LanguageModel(LanguageDatabaseManager db) {
        if (db == null) {
            throw new IllegalArgumentException("LanguageDatabaseManager cannot be null.");
        }
        this.db = db;

    }

    private void log(String message) {
        if (DETAILED_LOGGING_ENABLED) {
            System.out.println("[LOG] " + message);
        }
    }

    private String mapCharacterToStringKey(char c) {
        switch (c) {
            case ' ': return "_SPACE_";
            case '\t': return "_TAB_";
            case '\n': return "_NEWLINE_";
            case '\r': return "_CARRIAGE_RETURN_";
            default:
                return String.valueOf(c);
        }
    }

    public void train(String strLanguageName, String strText, String[] wordSeparators) throws IOException {
        log("--- Training Started for Language: " + strLanguageName + " ---");
        if (strLanguageName == null || strLanguageName.trim().isEmpty()) {
            throw new IllegalArgumentException("Language name cannot be null or empty.");
        }
        if (strText == null) {
            strText = "";
        }
        if (wordSeparators == null) {
            throw new IllegalArgumentException("Word separators array cannot be null.");
        }

        String lang = strLanguageName.trim();
        String textToProcess = strText.toLowerCase();
        log("Training text (lowercase): \"" + textToProcess.replace("\n", "\\n").replace("\t", "\\t") + "\"");

        Map<String, Integer> wordFrequencies = new HashMap<>();
        String trainingWordSplitRegex;
        if (wordSeparators.length == 0) {
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
            if (sb.length() == 0) {
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

        Map<Character, Integer> charOriginalFrequencies = new HashMap<>();
        for (char character : textToProcess.toCharArray()) {
            charOriginalFrequencies.put(character, charOriginalFrequencies.getOrDefault(character, 0) + 1);
        }
        Map<String, Integer> mappedCharFrequenciesForLog = new HashMap<>();
        for(Map.Entry<Character, Integer> entry : charOriginalFrequencies.entrySet()){
            mappedCharFrequenciesForLog.put(mapCharacterToStringKey(entry.getKey()), entry.getValue());
        }
        log("Mapped Character Frequencies for '" + lang + "': " + mappedCharFrequenciesForLog);

        log("Incrementing word absolute frequencies for '" + lang + "'...");
        for (Map.Entry<String, Integer> entry : wordFrequencies.entrySet()) {
            try {
                this.db.incrementWordAbsFreq(lang, entry.getKey(), entry.getValue());
            } catch (IOException e) {
                System.err.println("[ERROR] Training: Failed to increment word freq for '" + entry.getKey() + "': " + e.getMessage());
            }
        }

        log("Incrementing character absolute frequencies (using mapped keys) for '" + lang + "'...");
        for (Map.Entry<Character, Integer> entry : charOriginalFrequencies.entrySet()) {
            char originalChar = entry.getKey();
            String charKey = mapCharacterToStringKey(originalChar);
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

        Map<Character, Integer> inputCharOriginalFreq = new HashMap<>();
        for (char character : textToAnalyze.toCharArray()) {
            inputCharOriginalFreq.put(character, inputCharOriginalFreq.getOrDefault(character, 0) + 1);
        }
        Map<String, Integer> inputMappedCharFreqForLog = new HashMap<>();
        for(Map.Entry<Character, Integer> entry : inputCharOriginalFreq.entrySet()){
            inputMappedCharFreqForLog.put(mapCharacterToStringKey(entry.getKey()), entry.getValue());
        }
        log("Analysis: Input Text Mapped Character Frequencies: " + inputMappedCharFreqForLog);

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
                            currentLangScore += countInInput * parsedFreq;
                        } catch (NumberFormatException nfe) {
                            System.err.println("[WARN] Analysis: Could not parse word frequency '" + freqStr + "' for word '" + word + "' in language '" + lang + "'.");
                        }
                    } else {
                        log("Frequency string for word '" + word + "' is null or empty for lang '" + lang + "'.");
                    }
                } catch (IOException e) { // This is where "No such property found!" or HTTP 400 would be caught
                    log("IOException while getting property for word '" + word + "' in '" + lang + "': " + e.getMessage() + ". Assuming 0 frequency.");
                }
            }
            log("Word-based score component for " + lang + ": " + currentLangScore);

            double charScoreComponentStart = currentLangScore;
            log("Calculating character-based score for " + lang + "...");
            for (Map.Entry<Character, Integer> entry : inputCharOriginalFreq.entrySet()) {
                char originalChar = entry.getKey();
                String charKey = mapCharacterToStringKey(originalChar);
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
                            currentLangScore += countInInput * parsedFreq;
                        } catch (NumberFormatException nfe) {
                            System.err.println("[WARN] Analysis: Could not parse char frequency '" + freqStr + "' for char key '" + charKey + "' (original: '" + originalChar + "') in language '" + lang + "'.");
                        }
                    } else {
                        log("Frequency string for char_key '" + charKey + "' is null or empty for lang '" + lang + "'.");
                    }
                } catch (IOException e) { // This is where "No such property found!" or HTTP 400 would be caught
                    log("IOException while getting property for char_key '" + charKey + "' in '" + lang + "': " + e.getMessage() + ". Assuming 0 frequency.");
                }
            }
            log("Character-based score component for " + lang + ": " + (currentLangScore - charScoreComponentStart));
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
            log("Analysis: TotalScoreSum is 0 or less. Setting all percentages to 0.0.");
            for (String langKey : languageRawScores.keySet()) {
                resultPercentages.put(langKey, 0.0);
            }
        }
        log("Analysis: Final result percentages: " + resultPercentages);
        log("--- Analysis Ended ---");
        return resultPercentages;
    }

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
            String[] separators = {" ", ",", ".", ";", ":", "!", "?", "(", ")", "\n", "\t", "-"};

            System.out.println("\nStarting training for English (via main)...");
            model.train("english", "Hello world, this is a sample text.\nHello again, world of wonders.\tTest tab.", separators);
            System.out.println("English training complete (via main).");

            System.out.println("\nStarting training for French (via main)...");
            model.train("french", "Bonjour le monde. Ceci est un texte d'exemple.\nBonjour encore, monde des merveilles.\tOnglet de test.", separators);
            System.out.println("French training complete (via main).");

            System.out.println("\nStarting training for Spanish (via main)...");
            model.train("spanish", "Hola mundo, este es un texto de ejemplo.\nHola de nuevo, mundo de maravillas.\tPestaña de prueba.", separators);
            System.out.println("Spanish training complete (via main).");

            System.out.println("\nStarting training for TestLang (simple) (via main)...");
            model.train("testlang", "aaa bbb aaa cc aaa bbb", new String[]{" "});
            System.out.println("TestLang training complete (via main).");

            String unknownText1 = "Hello, this feels like an English text.";
            HashMap<String, Double> analysisResult1 = model.analyze(unknownText1);
            System.out.println("\nAnalysis for '" + unknownText1 + "': " + analysisResult1);

            String unknownText2 = "Bonjour, ceci est une phrase en français.";
            HashMap<String, Double> analysisResult2 = model.analyze(unknownText2);
            System.out.println("Analysis for '" + unknownText2 + "': " + analysisResult2);

            String unknownText3 = "Hola, este es un texto en español.";
            HashMap<String, Double> analysisResult3 = model.analyze(unknownText3);
            System.out.println("Analysis for '" + unknownText3 + "': " + analysisResult3);

            String mixedText = "Hello world.\nBonjour le monde.\tHola mundo.";
            HashMap<String, Double> analysisResultMixed = model.analyze(mixedText);
            System.out.println("Analysis for '" + mixedText + "': " + analysisResultMixed);

            String shortGibberish = "asdf qwer zxcv";
            HashMap<String, Double> analysisResultGibberish = model.analyze(shortGibberish);
            System.out.println("Analysis for '" + shortGibberish + "': " + analysisResultGibberish);

            String textWithTabsAndNewlines = "This\thas\nvarious whitespace.";
            System.out.println("\nAnalyzing text with special whitespace: '" + textWithTabsAndNewlines.replace("\t", "\\t").replace("\n", "\\n") + "'");
            HashMap<String, Double> analysisWhitespace = model.analyze(textWithTabsAndNewlines);
            System.out.println("Analysis for text with special whitespace: " + analysisWhitespace);

            String testLangText = "aaa";
            System.out.println("\nAnalyzing text for TestLang: '" + testLangText + "'");
            HashMap<String, Double> analysisTestLang = model.analyze(testLangText);
            System.out.println("Analysis for TestLang text: " + analysisTestLang);

            String testLangTextMultiple = "aaa bbb aaa";
            System.out.println("\nAnalyzing text for TestLang (multiple): '" + testLangTextMultiple + "'");
            HashMap<String, Double> analysisTestLangMultiple = model.analyze(testLangTextMultiple);
            System.out.println("Analysis for TestLang text (multiple): " + analysisTestLangMultiple);

        } catch (IOException e) {
            System.err.println("\nAn error occurred during model training or analysis: " + e.getMessage());
            e.printStackTrace();
        }
    }
}