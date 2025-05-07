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
            CREATE TABLE IF NOT EXISTS %s (
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

    public String[] listTables() throws IOException {
        String sqlStatement = """
        SELECT table_name FROM information_schema.tables
        WHERE table_schema = 'public' AND table_type = 'BASE TABLE';
        """;

        HttpURLConnection reqConn = createConnection("/rest/v1/rpc/execute_sql", "POST");
        reqConn.setDoOutput(true);

        String reqBody = """
        {
          "sql": %s
        }
        """.formatted(escapeJsonString(sqlStatement));

        try (OutputStream reqOut = reqConn.getOutputStream()) {
            reqOut.write(reqBody.getBytes(StandardCharsets.UTF_8));
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

        // Parse JSON manually (since we avoid libraries)
        return extractTableNames(response.toString());
    }

    private String[] extractTableNames(String json) {
        // Example response: [{"table_name":"messages"},{"table_name":"users"}]
        json = json.trim();
        if (!json.startsWith("[") || !json.endsWith("]")) return new String[0];

        json = json.substring(1, json.length() - 1); // remove [ and ]
        if (json.trim().isEmpty()) return new String[0];

        String[] items = json.split("\\},\\{"); // split objects
        for (int i = 0; i < items.length; i++) {
            items[i] = items[i].replaceAll(".*\"table_name\":\"", "")
                    .replaceAll("\".*", "");
        }
        return items;
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
