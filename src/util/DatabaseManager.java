/**************************************************************************
 * File name:
 * DatabaseManager.java
 *
 * Description:
 * This file defines the abstract DatabaseManager class, which provides
 * a foundational layer for interacting with a Supabase database. It handles
 * common HTTP operations like inserting and reading rows, managing API keys,
 * and includes a robust logging mechanism for tracking events and errors.
 *
 * Author:
 * Shivam Patel
 *
 * Date: May 20 2025
 *
 * Concepts:
 * HTTP client for REST API communication
 * Supabase API integration
 * Request/response handling
 * Error management
 * Event logging (in-memory and to file)
 * Thread safety with locks for file operations
 * URL and URI manipulation
 *
 *************************************************************************/
package util;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DatabaseManager {

    protected URL urlDatabase;
    protected final String strDBKey;
    protected final StringBuilder strLog = new StringBuilder();

    protected boolean isEventLoggingEnabled = false; // Controls overall logging (memory and file)
    protected Path logFilePath = null; // Null means no file logging
    protected final Lock fileLogLock = new ReentrantLock(); // For thread-safe file writing

    /**************************************************************************
     * Method name:
     * DatabaseManager
     *
     * Description:
     * Constructor for the DatabaseManager. Initializes the database URL and API key.
     *
     * Parameters:
     * @param baseUrl The base URL of the Supabase project.
     * @param apiKey The API key for authenticating with the Supabase project.
     *
     * Throws:
     * IllegalArgumentException if baseUrl or apiKey are null, or if baseUrl is a malformed URL.
     *
     * Author:
     * Shivam Patel
     *
     * Date: May 20 2025
     *
     *************************************************************************/
    public DatabaseManager(String baseUrl, String apiKey) {
        try {
            this.urlDatabase = new URL(Objects.requireNonNull(baseUrl));
            this.strDBKey = Objects.requireNonNull(apiKey);
            logEvent("Initialized with URL = " + baseUrl);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid Supabase URL", e);
        }
    }

    /**************************************************************************
     * Method name:
     * setBaseUrl
     *
     * Description:
     * Sets the base URL for database operations using a String.
     *
     * Parameters:
     * @param baseUrl The new base URL as a String.
     *
     * Throws:
     * IllegalArgumentException if the provided URL string is malformed.
     *
     * Author:
     * Shivam Patel
     *
     * Date: May 20 2025
     *
     *************************************************************************/
    public void setBaseUrl(String baseUrl) {
        try {
            setBaseUrl(new URL(baseUrl));
        } catch (MalformedURLException e) {
            logEvent("Failed to set base URL: " + baseUrl);
            throw new IllegalArgumentException("Invalid base URL", e);
        }
    }

    /**************************************************************************
     * Method name:
     * setBaseUrl
     *
     * Description:
     * Sets the base URL for database operations using a URL object.
     *
     * Parameters:
     * @param newUrl The new base URL as a URL object.
     *
     * Author:
     * Shivam Patel
     *
     * Date: May 20 2025
     *
     *************************************************************************/
    public void setBaseUrl(URL newUrl) {
        this.urlDatabase = Objects.requireNonNull(newUrl);
        logEvent("Base URL set to: " + newUrl);
    }

    /**************************************************************************
     * Method name:
     * getBaseUrl
     *
     * Description:
     * Retrieves the current base URL configured for the database manager.
     *
     * Returns:
     * The current base URL as a URL object.
     *
     * Author:
     * Shivam Patel
     *
     * Date: May 20 2025
     *
     *************************************************************************/
    public URL getBaseUrl() {
        return urlDatabase;
    }

    /**************************************************************************
     * Method name:
     * insertRow
     *
     * Description:
     * Inserts a new row into the specified database table.
     *
     * Parameters:
     * @param table The name of the table to insert into.
     * @param json The JSON string representing the row data to be inserted.
     *
     * Returns:
     * The HTTP response code of the insert operation.
     *
     * Throws:
     * IOException if an I/O error occurs during the HTTP request.
     *
     * Author:
     * Shivam Patel
     *
     * Date: May 20 2025
     *
     *************************************************************************/
    public int insertRow(String table, String json) throws IOException {
        Objects.requireNonNull(table);
        Objects.requireNonNull(json);
        HttpURLConnection conn = createConnection("/rest/v1/" + table, "POST");
        conn.setDoOutput(true);
        try (OutputStream out = conn.getOutputStream()) {
            out.write(json.getBytes(StandardCharsets.UTF_8));
        }
        int code = conn.getResponseCode();
        logEvent(String.format("INSERT into '%s' → HTTP %d", table, code));
        conn.disconnect();
        return code;
    }

    /**************************************************************************
     * Method name:
     * readRows
     *
     * Description:
     * Reads rows from the specified database table, optionally applying a filter.
     *
     * Parameters:
     * @param table The name of the table to read from.
     * @param filter An optional filter string to apply to the read operation (e.g., "?select=*").
     *
     * Returns:
     * A String containing the JSON response from the database.
     *
     * Throws:
     * IOException if an I/O error occurs or the HTTP response code is not OK.
     *
     * Author:
     * Shivam Patel
     *
     * Date: May 20 2025
     *
     *************************************************************************/
    public String readRows(String table, String filter) throws IOException {
        Objects.requireNonNull(table);
        String path = "/rest/v1/" + table + filter;
        HttpURLConnection conn = createConnection(path, "GET");
        int code = conn.getResponseCode();
        if (code != HttpURLConnection.HTTP_OK) {
            logEvent(String.format("READ from '%s' failed → HTTP %d", table, code));
            conn.disconnect();
            throw new IOException("GET failed with HTTP code: " + code);
        }
        StringBuilder res = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) res.append(line);
        }
        logEvent(String.format("READ from '%s' succeeded", table));
        conn.disconnect();
        return res.toString();
    }

    /**************************************************************************
     * Method name:
     * createConnection
     *
     * Description:
     * Creates and configures an HttpURLConnection for a given path and HTTP method.
     * Sets common request headers, including Content-Type, Accept, apikey, and Authorization.
     * Handles method override for PATCH requests.
     *
     * Parameters:
     * @param path The relative path for the HTTP request (e.g., "/rest/v1/table_name").
     * @param method The HTTP method (e.g., "GET", "POST", "PATCH", "DELETE").
     *
     * Returns:
     * A configured HttpURLConnection object.
     *
     * Throws:
     * IOException if an I/O error occurs while opening the connection.
     *
     * Author:
     * Shivam Patel
     *
     * Date: May 20 2025
     *
     *************************************************************************/
    protected HttpURLConnection createConnection(String path, String method) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(urlDatabase, path).openConnection();

        if ("PATCH".equalsIgnoreCase(method)) {
            conn.setRequestMethod("POST");  // Fallback method
            conn.setRequestProperty("X-HTTP-Method-Override", "PATCH");
        } else {
            conn.setRequestMethod(method);  // For GET, POST, DELETE, etc.
        }

        conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty("apikey", strDBKey);
        conn.setRequestProperty("Authorization", "Bearer " + strDBKey);

        conn.setDoOutput(true);  // Needed if you're sending a body

        return conn;
    }


    /**
     * Toggles the overall logging state between OFF and MEMORY_ONLY.
     * If file logging is currently active, calling this method will disable both
     * file and memory logging. If logging is currently off, it enables memory-only logging.
     */
    protected void toggleLogging() {
        if (this.isEventLoggingEnabled) {
            // If logging is currently on (either memory or file), turn it off
            this.isEventLoggingEnabled = false;
            this.logFilePath = null; // Ensure file logging is also turned off
            // Optional: Log this event before disabling
            logEvent("Event logging disabled."); // This call will now respect the flag (won't log)
        } else {
            // If logging is currently off, enable memory-only logging
            this.isEventLoggingEnabled = true;
            this.logFilePath = null; // Ensure file logging is off
            // Optional: Log this event after enabling
            logEvent("Event logging enabled to memory."); // This call will now respect the flag (will log)
        }
    }

    /**
     * Toggles file logging to a specified text file.
     * If file logging is currently active for the given path, it disables file logging
     * (memory logging state remains unchanged). If file logging is off for this path,
     * it enables file logging to the path and ensures overall logging is enabled
     * (which also enables memory logging).
     * Creates the file and parent directories if they don't exist when enabling.
     *
     * @param strFilePath The path to the log file as a String. Can be null or empty to disable file logging.
     * @throws IOException If an I/O error occurs creating directories or accessing the file when enabling.
     */
    protected void toggleLogging(String strFilePath) throws IOException {
        if (strFilePath == null || strFilePath.trim().isEmpty()) {
            // If null or empty path, disable file logging
            if (this.logFilePath != null) {
                logEvent("File logging disabled."); // Log before unsetting path
            }
            this.logFilePath = null;
            // isEventLoggingEnabled remains unchanged - memory logging might still be active
        } else {
            Path newFilePath = Paths.get(strFilePath);
            if (this.logFilePath != null && this.logFilePath.equals(newFilePath)) {
                // If already logging to this file, toggle off file logging
                logEvent("File logging disabled for path: " + strFilePath); // Log before unsetting path
                this.logFilePath = null;
                // isEventLoggingEnabled remains unchanged - memory logging might still be active
            } else {
                // If not logging to this file, enable file logging to the new path
                Path parentDir = newFilePath.getParent();
                if (parentDir != null) { // <-- Add this null check!
                    Files.createDirectories(parentDir); // Ensure parent directories exist ONLY if there's a parent
                }

                if (!Files.exists(newFilePath)) {
                    Files.createFile(newFilePath); // Create the file if it doesn't exist
                }
                this.logFilePath = newFilePath;
                this.isEventLoggingEnabled = true; // Ensure overall logging is enabled for file logging
                logEvent("File logging enabled to path: " + strFilePath); // Log after setting path
            }
        }
    }

    /**************************************************************************
     * Method name:
     * logEvent
     *
     * Description:
     * Logs a formatted event message to both an in-memory string buffer and,
     * if configured, to a specified log file. Logging only occurs if
     * `isEventLoggingEnabled` is true. File logging is thread-safe.
     *
     * Parameters:
     * @param msg The message string to be logged.
     *
     * Author:
     * Shivam Patel
     *
     * Date: May 20 2025
     *
     *************************************************************************/
    protected void logEvent(String msg) {
        // Check if logging is enabled first
        if (!isEventLoggingEnabled) return; // Exit immediately if logging is not enabled

        String formattedMsg = "[" + LocalDateTime.now() + "] " + msg + "\n";

        // Always log to memory if overall logging is enabled
        strLog.append(formattedMsg);

        // Log to file only if a file path is set
        if (logFilePath != null) {
            fileLogLock.lock(); // Acquire lock for thread safety
            try (BufferedWriter writer = Files.newBufferedWriter(logFilePath, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
                writer.write(formattedMsg);
            } catch (IOException e) {
                // Handle file logging error - this error itself might not be logged
                // to avoid recursion or further errors. Print to standard error.
                System.err.println("Error writing to log file " + logFilePath + ": " + e.getMessage());
                // Optionally unset the log file path if writing fails repeatedly
                // this.logFilePath = null;
            } finally {
                fileLogLock.unlock(); // Release lock
            }
        }
    }

    /**************************************************************************
     * Method name:
     * createPostRequest
     *
     * Description:
     * Creates and configures an HttpURLConnection for a POST request, including
     * writing the provided JSON input string to the output stream of the connection.
     *
     * Parameters:
     * @param path The relative path for the POST request.
     * @param jsonInputString The JSON string to be sent in the request body.
     *
     * Returns:
     * A configured HttpURLConnection object ready to send the POST request.
     *
     * Throws:
     * IOException if an I/O error occurs during connection creation or writing the body.
     *
     * Author:
     * Shivam Patel
     *
     * Date: May 20 2025
     *
     *************************************************************************/
    protected HttpURLConnection createPostRequest(String path, String jsonInputString) throws IOException {
        HttpURLConnection conn = createConnection(path, "POST");
        conn.setDoOutput(true);
        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length);
        }
        return conn;
    }

    /**************************************************************************
     * Method name:
     * extractColumn
     *
     * Description:
     * Extracts a specific column (property) value from a JSON-like response string.
     * This method assumes a simple JSON structure and provides a basic extraction
     * mechanism. It might need refinement depending on the actual JSON response format.
     *
     * Parameters:
     * @param response The JSON-like string response from which to extract the column.
     * @param column The name of the column (property) to extract.
     *
     * Returns:
     * The string value of the extracted column.
     *
     * Throws:
     * IOException if the specified property is not found in the response.
     *
     * Author:
     * Shivam Patel
     *
     * Date: May 20 2025
     *
     *************************************************************************/
    protected String extractColumn(String response, String column) throws IOException {
        // Assuming the response is a valid JSON-like string and we're looking for the property in it
        // You can refine the way the property is extracted, depending on the format of the response

        // Example extraction (pseudo-code, adapt it to your specific response format)
        String[] rows = response.split("\\},\\{");
        for (String row : rows) {
            if (row.contains(column)) {
                // Extract the property from the row
                String[] columnParts = row.split(",");
                for (String part : columnParts) {
                    if (part.contains(column)) {
                        return part.split(":")[1].replace("\"", "").trim();
                    }
                }
            }
        }

        // If no property found
        throw new IOException("No such property found!");
    }

    /**************************************************************************
     * Method name:
     * handleErrorResponse
     *
     * Description:
     * Reads and prints the error stream from an HttpURLConnection if an HTTP error occurs.
     *
     * Parameters:
     * @param conn The HttpURLConnection object from which to read the error stream.
     *
     * Throws:
     * IOException if an I/O error occurs while reading the error stream.
     *
     * Author:
     * Shivam Patel
     *
     * Date: May 20 2025
     *
     *************************************************************************/
    protected void handleErrorResponse(HttpURLConnection conn) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "utf-8"))) {
            StringBuilder errorResponse = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                errorResponse.append(line.trim());
            }
            System.err.println("Error response: " + errorResponse.toString());
        }
    }

    /**************************************************************************
     * Method name:
     * validateInputs
     *
     * Description:
     * A helper method to validate an array of input strings.
     * It throws an IllegalArgumentException if any input string is null or empty after trimming.
     *
     * Parameters:
     * @param inputs A variable number of String arguments to be validated.
     *
     * Throws:
     * IllegalArgumentException if any input string is invalid.
     *
     * Author:
     * Shivam Patel
     *
     * Date: May 20 2025
     *
     *************************************************************************/
    protected void validateInputs(String... inputs) throws IllegalArgumentException {
        for (String input : inputs) {
            if (input == null || input.trim().isEmpty()) {
                throw new IllegalArgumentException("Invalid input: " + input);
            }
        }
    }

    /**************************************************************************
     * Method name:
     * processResponse
     *
     * Description:
     * Processes the HTTP response from a connection. If the response code indicates
     * success (HTTP_OK or HTTP_NO_CONTENT), it reads and prints the input stream.
     * Otherwise, it calls `handleErrorResponse` to print the error details.
     *
     * Parameters:
     * @param conn The HttpURLConnection object whose response is to be processed.
     *
     * Throws:
     * IOException if an I/O error occurs during response processing.
     *
     * Author:
     * Shivam Patel
     *
     * Date: May 20 2025
     *
     *************************************************************************/
    protected void processResponse(HttpURLConnection conn) throws IOException {
        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_NO_CONTENT) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                if (response.length() > 0) {
                    System.out.println("Response: " + response.toString());
                }
            }
        } else {
            handleErrorResponse(conn);
        }
    }

    /**************************************************************************
     * Method name:
     * getLog
     *
     * Description:
     * Retrieves the accumulated in-memory log of events as a String.
     *
     * Returns:
     * A String containing all logged events since the DatabaseManager was initialized
     * or the log was last cleared/reset.
     *
     * Author:
     * Shivam Patel
     *
     * Date: May 20 2025
     *
     *************************************************************************/
    public String getLog() {
        return strLog.toString();
    }
}