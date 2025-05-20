package util;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LanguageDatabaseManager extends DatabaseManager {

    public LanguageDatabaseManager(String baseUrl, String apiKey) {
        super(baseUrl, apiKey);
    }

    // Method to get word property
    public String getWordProperty(String language, String word, String property) throws IOException {
        validateInputs(language, word, property);

        String sanitizedLanguage = language.trim();
        String sanitizedWord = word.trim();

        String filter = String.format("?language=ilike.%s&word=ilike.%s", sanitizedLanguage, sanitizedWord);

        String response = readRows("words", filter);
        if (response != null && !response.trim().isEmpty()) {
            return extractColumn(response, property);
        }
        throw new IOException("Word property not found for " + word + " in language " + language);
    }

    // Method to get character property
    public String getCharacterProperty(String language, String character, String property) throws IOException {
        validateInputs(language, character, property);

        String sanitizedLanguage = language.trim();
        String sanitizedCharacter = character.trim();

        String filter = String.format("?language=ilike.%s&character=ilike.%s", sanitizedLanguage, sanitizedCharacter);

        String response = readRows("characters", filter);
        if (response != null && !response.trim().isEmpty()) {
            return extractColumn(response, property);
        }
        throw new IOException("Character property not found for " + character + " in language " + language);
    }

    // Method to increment word absolute frequency
    public void incrementWordAbsFreq(String language, String word, int incrementBy) throws IOException {
        String path = "/rest/v1/rpc/increment_word_abs_freq";
        String jsonInputString = String.format("{\"lang\":\"%s\", \"wrd\":\"%s\", \"increment_by\": %d}", language, word, incrementBy);

        HttpURLConnection conn = createPostRequest(path, jsonInputString);
        processResponse(conn);
    }

    // Method to increment character absolute frequency
    public void incrementCharacterAbsFreq(String language, String character, int incrementBy) throws IOException {
        String path = "/rest/v1/rpc/increment_character_abs_freq";
        String jsonInputString = String.format("{\"lang_input\":\"%s\", \"char_input\":\"%s\", \"increment_by\": %d}", language, character, incrementBy);

        HttpURLConnection conn = createPostRequest(path, jsonInputString);
        processResponse(conn);
    }

    // Method to update all words' percentage frequency
    public void updateAllWordsPercentFreq(String language) throws IOException {
        String path = "/rest/v1/rpc/update_all_words_percent_freq";
        String jsonInputString = String.format("{\"language_input\":\"%s\"}", language);

        HttpURLConnection conn = createPostRequest(path, jsonInputString);
        processResponse(conn);
    }

    // Method to update all characters' percentage frequency
    public void updateAllCharactersPercentFreq(String language) throws IOException {
        String path = "/rest/v1/rpc/update_all_characters_percent_freq";
        String jsonInputString = String.format("{\"language_input\":\"%s\"}", language);

        HttpURLConnection conn = createPostRequest(path, jsonInputString);
        processResponse(conn);
    }

    // Method to get most frequent character
    public String getMostFrequentChar(String language) throws IOException {
        String path = "/rest/v1/rpc/get_most_frequent_char";
        String jsonInputString = String.format("{\"language_input\":\"%s\"}", language);

        HttpURLConnection conn = createPostRequest(path, jsonInputString);
        int responseCode = conn.getResponseCode();
        System.out.println(responseCode);

        if (responseCode == HttpURLConnection.HTTP_OK) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                if (response.toString().equals("[]")) throw new IOException("No most frequent characters found");
                return extractCharacterFromResponse(response.toString());
            }
        } else {
            handleErrorResponse(conn);
            return null;
        }
    }


    public String[] getStoredLanguages() throws IOException {
        // RPC function path to call the get_languages() function
        String rpcPath = "/rest/v1/rpc/get_languages";


        // Call the RPC function using the POST method (instead of GET)
        HttpURLConnection conn = createConnection(rpcPath, "POST");

        // The connection does not require an input body for the 'get_languages' RPC, so we can leave it empty
        conn.setDoOutput(false);  // No body to send for this request

        // Get the response code
        int responseCode = conn.getResponseCode();

        if (responseCode != HttpURLConnection.HTTP_OK) {
            handleErrorResponse(conn);
            throw new IOException("GET failed with HTTP code: " + responseCode);
        }

        StringBuilder response = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        }

        // Close the connection
        conn.disconnect();

        // Parse the response to extract the list of unique languages using manual JSON parsing
        String[] languages = parseLanguagesFromResponse(response.toString());


        return languages;
    }

    private String[] parseLanguagesFromResponse(String response) throws IOException {
        // Trim leading and trailing spaces from the whole response string
        response = response.trim();

        // Check if the response starts and ends with square brackets
        if (!response.startsWith("[") || !response.endsWith("]")) {
            if ("[]".equals(response) || response.isEmpty()) {
                return new String[0];
            }
            // Handle cases where the response is not a JSON array as expected
        }

        String contentInsideBrackets;
        if (response.length() >= 2 && response.startsWith("[") && response.endsWith("]")) {
            contentInsideBrackets = response.substring(1, response.length() - 1).trim(); // Strip the outer brackets and trim
        } else if ("[]".equals(response)) {
            contentInsideBrackets = ""; // Empty array
        } else {
            // If it wasn't an array string (e.g. "{}"), this path might be taken.
            // Or if response was just "[" or "]" (invalid JSON).
            contentInsideBrackets = response; // Proceed with the content as is, may lead to empty items.
        }

        if (contentInsideBrackets.isEmpty()) {
            return new String[0];
        }

        // Use regex to find all individual JSON objects: matches content between {} non-greedily.
        List<String> objectStrings = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\{.*?\\}"); // Matches "{...}" non-greedily
        Matcher matcher = pattern.matcher(contentInsideBrackets);

        while (matcher.find()) {
            objectStrings.add(matcher.group());
        }

        String[] languages = new String[objectStrings.size()];

        for (int i = 0; i < objectStrings.size(); i++) {
            String currentObjectJson = objectStrings.get(i).trim(); // e.g., {"language":"english"}

            // Manually parse the "language" field from the currentObjectJson
            // This assumes the structure is always {"language":"value"}
            String key = "\"language\":";
            int keyIndex = currentObjectJson.indexOf(key);

            if (keyIndex != -1) {
                int valueStartIndex = keyIndex + key.length();
                // The value is a string, so it will be enclosed in quotes
                int openingQuoteIndex = currentObjectJson.indexOf('"', valueStartIndex);
                if (openingQuoteIndex != -1) {
                    int closingQuoteIndex = currentObjectJson.indexOf('"', openingQuoteIndex + 1);
                    if (closingQuoteIndex != -1) {
                        languages[i] = currentObjectJson.substring(openingQuoteIndex + 1, closingQuoteIndex);
                    } else {
                        languages[i] = ""; // Or some error indicator
                    }
                } else {
                    languages[i] = ""; // Or some error indicator
                }
            } else {
                languages[i] = ""; // Or some error indicator, or null
            }
        }

        return languages;
    }












    // Extract character from response string
    private String extractCharacterFromResponse(String responseString) {
        if (responseString == null) throw new NullPointerException("Argument responseString to method extractCharacterFromResponse is null");
        if (!responseString.isEmpty() && responseString.startsWith("[{") && responseString.endsWith("}]")) {
            int startIndex = responseString.indexOf("\"character\":\"") + "\"character\":\"".length();
            int endIndex = responseString.indexOf("\"", startIndex);
            if (startIndex != -1 && endIndex != -1) {
                return responseString.substring(startIndex, endIndex);
            }
        }
        return null;
    }

    // Helper method to get database manager (use environment variables)
    public static LanguageDatabaseManager getDatabaseManager(String url, String key) throws IOException {
        //url = System.getenv("SUPABASE_URL");
        //key = System.getenv("SUPABASE_API_KEY");

        if (url == null || key == null) {
            throw new IllegalStateException("Supabase URL or API Key is not set as environment variables.");
        }

        LanguageDatabaseManager db = new LanguageDatabaseManager(url, key);
        db.toggleLogging("log.txt");
        return db;
    }
}
