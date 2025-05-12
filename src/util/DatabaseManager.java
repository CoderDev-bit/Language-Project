package util;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DatabaseManager {
    private URL urlDatabase;
    private final String strDBKey;
    private final StringBuilder strLog = new StringBuilder();

    // Logging fields
    private boolean isEventLoggingEnabled = false;
    private Path logFilePath = null;
    private final Lock fileLogLock = new ReentrantLock();

    // SQL templates for common operations
    public static final String SQL_INSERT_ROW_TEMPLATE   = "INSERT INTO \"%s\" (%s) VALUES (%s);";
    public static final String SQL_READ_ROWS_TEMPLATE    = "SELECT * FROM \"%s\"%s;";
    public static final String SQL_CREATE_TABLE_TEMPLATE = "CREATE TABLE IF NOT EXISTS \"%s\" (id BIGSERIAL PRIMARY KEY, %s);";
    public static final String SQL_LIST_TABLES           = "SELECT table_name FROM information_schema.tables WHERE table_schema = 'public';";

    /**
     * Initialize with Supabase base URL and API key.
     */
    public DatabaseManager(String baseUrl, String apiKey) {
        try {
            this.urlDatabase = new URL(Objects.requireNonNull(baseUrl));
            this.strDBKey = Objects.requireNonNull(apiKey);
            logEvent("Initialized with URL = " + baseUrl);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid Supabase URL", e);
        }
    }

    /**
     * Execute INSERT/UPDATE/DELETE/DDL SQL via Supabase RPC.
     * @param sql the SQL statement to execute
     * @return HTTP response code
     */
    public int executeUpdate(String sql) throws IOException {
        String payload = "{\"sql\":\"" + sql.replace("\"", "\\\"") + "\"}";
        HttpURLConnection conn = createConnection("/rest/v1/rpc/execute_sql", "POST");
        conn.setDoOutput(true);
        try (OutputStream out = conn.getOutputStream()) {
            out.write(payload.getBytes(StandardCharsets.UTF_8));
        }
        int code = conn.getResponseCode();
        logEvent(String.format("EXECUTE_UPDATE HTTP %d; SQL: %s", code, sql));
        conn.disconnect();
        return code;
    }

    /**
     * Execute SELECT SQL via Supabase RPC and return JSON result.
     * @param sql the SELECT statement to execute
     * @return JSON string of query result
     */
    public String executeQuery(String sql) throws IOException {
        String payload = "{\"sql\":\"" + sql.replace("\"", "\\\"") + "\"}";
        HttpURLConnection conn = createConnection("/rest/v1/rpc/execute_sql", "POST");
        conn.setDoOutput(true);
        try (OutputStream out = conn.getOutputStream()) {
            out.write(payload.getBytes(StandardCharsets.UTF_8));
        }
        int code = conn.getResponseCode();
        if (code != HttpURLConnection.HTTP_OK && code != HttpURLConnection.HTTP_NO_CONTENT) {
            logEvent(String.format("EXECUTE_QUERY failed HTTP %d; SQL: %s", code, sql));
            conn.disconnect();
            throw new IOException("Query failed with HTTP code: " + code);
        }
        StringBuilder res = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                res.append(line);
            }
        }
        logEvent(String.format("EXECUTE_QUERY succeeded; SQL: %s", sql));
        conn.disconnect();
        return res.toString();
    }

    /**
     * List all public tables via Supabase RPC (uses specialized connection setup).
     */
    public String listTables() throws IOException {
        HttpURLConnection conn = createConnection("/rest/v1/rpc/list_public_tables", "POST");
        conn.setDoOutput(true);
        try (OutputStream out = conn.getOutputStream()) {
            out.write("{}".getBytes(StandardCharsets.UTF_8));
        }
        int code = conn.getResponseCode();
        if (code != HttpURLConnection.HTTP_OK) {
            logEvent(String.format("LIST_TABLES failed HTTP %d", code));
            conn.disconnect();
            throw new IOException("Failed to list tables HTTP " + code);
        }
        StringBuilder response = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        }
        logEvent("LIST_TABLES succeeded.");
        conn.disconnect();
        return response.toString();
    }


    /**
     * Helper to create a standard supabase REST connection.
     */
    private HttpURLConnection createConnection(String path, String method) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(urlDatabase, path).openConnection();
        conn.setRequestMethod(method);
        conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty("apikey", strDBKey);
        conn.setRequestProperty("Authorization", "Bearer " + strDBKey);
        return conn;
    }

    // Simplified logging methods
    private void logEvent(String msg) {
        if (!isEventLoggingEnabled) return;
        String entry = "[" + LocalDateTime.now() + "] " + msg + "\n";
        strLog.append(entry);
        if (logFilePath != null) {
            fileLogLock.lock();
            try (BufferedWriter w = Files.newBufferedWriter(logFilePath, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
                w.write(entry);
            } catch (IOException ignored) {
            } finally {
                fileLogLock.unlock();
            }
        }
    }
    public String getLog() { return strLog.toString(); }

    /**
     * Demonstration main method to test basic operations.
     */
    public static void main(String[] args) {
        final String BASE_URL = "https://frhgfmnvkopdwpiorszb.supabase.co";
        final String API_KEY  = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImZyaGdmbW52a29wZHdwaW9yc3piIiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTc0NjQ5MzI0OCwiZXhwIjoyMDYyMDY5MjQ4fQ.bu7u6Doh9PMWGzpeROtFDm8qnSr5gk56m3vIDllMs7E";
        final String TABLE    = "demo_table";

        try {
            DatabaseManager db = new DatabaseManager(BASE_URL, API_KEY);

            // 1. Create table
            String cols = "data TEXT NOT NULL";
            String sqlCreate = String.format(SQL_CREATE_TABLE_TEMPLATE, TABLE, cols);
            System.out.println("Create table response: " + db.executeUpdate(sqlCreate));

            // 2. Insert a row
            String sqlInsert = String.format(SQL_INSERT_ROW_TEMPLATE, TABLE, "data", "'Hello, World!'"
            );
            System.out.println("Insert row response: " + db.executeUpdate(sqlInsert));

            // 3. Read rows
            String sqlSelect = String.format(SQL_READ_ROWS_TEMPLATE, TABLE, "");
            String rows = db.executeQuery(sqlSelect);
            System.out.println("Read rows result: " + rows);

            // 4. List tables
            String tableList = db.listTables();
            String result = db.executeQuery("SELECT * FROM table_list;");



            System.out.println("List tables: " + result);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
