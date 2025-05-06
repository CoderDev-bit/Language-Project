package util;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Manages CRUD operations with a Supabase-backed database.
 */
public class DatabaseManager {

    private URL urlDatabase;
    private final String strDBKey;
    private final StringBuilder strLog = new StringBuilder();

    public DatabaseManager(String cfgBaseUrlString, String cfgSupabaseKey) {
        try {
            this.urlDatabase = new URL(Objects.requireNonNull(cfgBaseUrlString, "Base URL cannot be null"));
            this.strDBKey = Objects.requireNonNull(cfgSupabaseKey, "Supabase key cannot be null");
            logEvent("Initialized with URL = " + cfgBaseUrlString);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid Supabase URL", e);
        }
    }

    public void setBaseUrl(String cfgBaseUrlString) {
        try {
            setBaseUrl(new URL(cfgBaseUrlString));
        } catch (MalformedURLException e) {
            logEvent("Failed to set base URL: " + cfgBaseUrlString);
            throw new IllegalArgumentException("Invalid base URL", e);
        }
    }

    public void setBaseUrl(URL cfgNewUrl) {
        this.urlDatabase = Objects.requireNonNull(cfgNewUrl, "Base URL cannot be null");
        logEvent("Base URL set to: " + cfgNewUrl);
    }

    public URL getBaseUrl() {
        return urlDatabase;
    }

    /**
     * Inserts a row into a specified table using JSON payload.
     *
     * @param tblName      Table name.
     * @param reqJsonBody  JSON-formatted payload.
     * @return HTTP response code.
     * @throws IOException if the request fails.
     */
    public int insertRow(String tblName, String reqJsonBody) throws IOException {
        Objects.requireNonNull(tblName, "Table name cannot be null");
        Objects.requireNonNull(reqJsonBody, "JSON payload cannot be null");

        HttpURLConnection reqConn = createConnection("/rest/v1/" + tblName, "POST");
        reqConn.setDoOutput(true);

        try (OutputStream reqOut = reqConn.getOutputStream()) {
            reqOut.write(reqJsonBody.getBytes(StandardCharsets.UTF_8));
        }

        int resCode = reqConn.getResponseCode();
        logEvent(String.format("INSERT into '%s' → HTTP %d", tblName, resCode));
        reqConn.disconnect();

        return resCode;
    }

    /**
     * Reads rows from a specified table with optional filters.
     *
     * @param tblName   Table name.
     * @param reqFilter Optional query string filter.
     * @return JSON string of result.
     * @throws IOException if the request fails.
     */
    public String readRows(String tblName, String reqFilter) throws IOException {
        Objects.requireNonNull(tblName, "Table name cannot be null");

        String reqPath = "/rest/v1/" + tblName + (reqFilter != null ? reqFilter : "?select=*");
        HttpURLConnection reqConn = createConnection(reqPath, "GET");

        int resCode = reqConn.getResponseCode();
        if (resCode != HttpURLConnection.HTTP_OK) {
            logEvent(String.format("READ from '%s' failed → HTTP %d", tblName, resCode));
            reqConn.disconnect();
            throw new IOException("GET failed with HTTP code: " + resCode);
        }

        StringBuilder resJson = new StringBuilder();
        try (BufferedReader resReader = new BufferedReader(
                new InputStreamReader(reqConn.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = resReader.readLine()) != null) {
                resJson.append(line);
            }
        }

        logEvent(String.format("READ from '%s' succeeded", tblName));
        reqConn.disconnect();

        return resJson.toString();
    }

    /**
     * Creates a table using Supabase Management API.
     *
     * @param cfgProjectRef      Supabase project reference ID.
     * @param cfgServiceRoleKey  Service role API key.
     * @param tblName            Desired table name.
     * @return HTTP response code.
     * @throws IOException if creation fails.
     */
    public int createTable(String tblName) throws IOException {
        String sqlStatement = """
        CREATE TABLE IF NOT EXISTS %s (
            id BIGSERIAL PRIMARY KEY,
            user_id TEXT NOT NULL,
            message TEXT NOT NULL,
            created_at TIMESTAMPTZ DEFAULT now()
        );
        """.formatted(tblName);

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
        logEvent(String.format("EXECUTE SQL to create table '%s' → HTTP %d", tblName, resCode));
        reqConn.disconnect();

        return resCode;
    }

    // Escapes quotes/newlines for JSON compatibility
    private String escapeJsonString(String input) {
        return "\"" + input
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "") + "\"";
    }

    private HttpURLConnection createConnection(String reqPath, String reqMethod) throws IOException {
        URL reqUrl = new URL(urlDatabase, reqPath);
        HttpURLConnection conn = (HttpURLConnection) reqUrl.openConnection();
        conn.setRequestMethod(reqMethod);
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
