package util;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Objects;

import java.nio.file.*;
import java.io.IOException;
import java.io.BufferedWriter;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DatabaseManager {

    private URL urlDatabase;
    private final String strDBKey;
    private final StringBuilder strLog = new StringBuilder();

    // no just file loggign yet
    private boolean isEventLoggingEnabled = false; // Controls overall logging (memory and file)
    private Path logFilePath = null; // Null means no file logging
    private final Lock fileLogLock = new ReentrantLock(); // For thread-safe file writing

    public DatabaseManager(String baseUrl, String apiKey) {
        try {
            this.urlDatabase = new URL(Objects.requireNonNull(baseUrl));
            this.strDBKey = Objects.requireNonNull(apiKey);
            logEvent("Initialized with URL = " + baseUrl);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid Supabase URL", e);
        }
    }

    public void setBaseUrl(String baseUrl) {
        try {
            setBaseUrl(new URL(baseUrl));
        } catch (MalformedURLException e) {
            logEvent("Failed to set base URL: " + baseUrl);
            throw new IllegalArgumentException("Invalid base URL", e);
        }
    }

    public void setBaseUrl(URL newUrl) {
        this.urlDatabase = Objects.requireNonNull(newUrl);
        logEvent("Base URL set to: " + newUrl);
    }

    public URL getBaseUrl() {
        return urlDatabase;
    }

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

    public String readRows(String table, String filter) throws IOException {
        Objects.requireNonNull(table);
        String path = "/rest/v1/" + table + (filter != null ? filter : "?select=*");
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

    public int createTable(String table) throws IOException {
        String sql = """
            CREATE TABLE IF NOT EXISTS "%s" (
                id BIGSERIAL PRIMARY KEY,
                user_id TEXT NOT NULL,
                message TEXT NOT NULL,
                created_at TIMESTAMPTZ DEFAULT now()
            );
        """.formatted(table);
        HttpURLConnection conn = createConnection("/rest/v1/rpc/execute_sql", "POST");
        conn.setDoOutput(true);
        String body = "{\"sql\": " + escapeJsonString(sql) + "}";
        try (OutputStream out = conn.getOutputStream()) {
            out.write(body.getBytes(StandardCharsets.UTF_8));
        }
        int code = conn.getResponseCode();
        logEvent(String.format("EXECUTE SQL to create table '%s' → HTTP %d", table, code));
        conn.disconnect();
        return code;
    }

    public String listTables() throws IOException {
        // The URL is hardcoded here, consider making this dynamic or using the base URL
        // if the RPC endpoint structure is consistent.
        String url = "https://frhgfmnvkopdwpiorszb.supabase.co/rest/v1/rpc/list_public_tables";

        HttpURLConnection reqConn = (HttpURLConnection) new URL(url).openConnection();
        reqConn.setRequestMethod("POST");
        reqConn.setRequestProperty("apikey", strDBKey);
        reqConn.setRequestProperty("Authorization", "Bearer " + strDBKey);
        reqConn.setRequestProperty("Content-Type", "application/json");
        reqConn.setRequestProperty("Accept", "application/json");
        reqConn.setDoOutput(true);

        // The function takes no input, but Supabase requires "{}" in the body
        try (OutputStream os = reqConn.getOutputStream()) {
            os.write("{}".getBytes(StandardCharsets.UTF_8));
        }

        int resCode = reqConn.getResponseCode();
        if (resCode != HttpURLConnection.HTTP_OK) {
            logEvent(String.format("LIST TABLES failed → HTTP %d", resCode));
            reqConn.disconnect();
            throw new IOException("Failed to list tables → HTTP " + resCode);
        }

        StringBuilder response = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(reqConn.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        }

        reqConn.disconnect();
        // Add log message here after successful operation
        logEvent("LIST TABLES succeeded.");

        return response.toString();
    }




    private String escapeJsonString(String input) {
        return "\"" + input.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "") + "\"";
    }

    private HttpURLConnection createConnection(String path, String method) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(urlDatabase, path).openConnection();
        conn.setRequestMethod(method);
        conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty("apikey", strDBKey);
        conn.setRequestProperty("Authorization", "Bearer " + strDBKey);
        return conn;
    }



    /**
     * Toggles the overall logging state between OFF and MEMORY_ONLY.
     * If file logging is currently active, calling this method will disable both
     * file and memory logging. If logging is currently off, it enables memory-only logging.
     */
    public void toggleLogging() {
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
    public void toggleLogging(String strFilePath) throws IOException {
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

    private void logEvent(String msg) {
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

    public String getLog() {
        return strLog.toString();
    }
}
