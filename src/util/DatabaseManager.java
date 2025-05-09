package util;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Objects;

public class DatabaseManager {

    private URL urlDatabase;
    private final String strDBKey;
    private final StringBuilder strLog = new StringBuilder();

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



    private void logEvent(String msg) {
        strLog.append("[").append(LocalDateTime.now()).append("] ").append(msg).append("\n");
    }

    public String getLog() {
        return strLog.toString();
    }
}
