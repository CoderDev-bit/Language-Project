import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Objects;

public class DatabaseManager {

    private URL baseUrl;
    private final String supabaseKey;
    private final StringBuilder log = new StringBuilder();

    public DatabaseManager(String baseUrlString, String supabaseKey) {
        try {
            this.baseUrl = new URL(Objects.requireNonNull(baseUrlString, "Base URL cannot be null"));
            this.supabaseKey = Objects.requireNonNull(supabaseKey, "Supabase key cannot be null");
            updateLog("Initialized with URL=" + baseUrlString);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid Supabase URL", e);
        }
    }

    public void setBaseUrl(String baseUrlString) {
        try {
            setBaseUrl(new URL(baseUrlString));
        } catch (MalformedURLException e) {
            updateLog("Failed to set base URL: " + baseUrlString);
            throw new IllegalArgumentException("Invalid base URL", e);
        }
    }

    public void setBaseUrl(URL newUrl) {
        this.baseUrl = Objects.requireNonNull(newUrl, "Base URL cannot be null");
        updateLog("Base URL set to: " + newUrl);
    }

    public URL getBaseUrl() {
        return baseUrl;
    }

    public int insertRow(String table, String jsonPayload) throws IOException {
        Objects.requireNonNull(table, "Table name cannot be null");
        Objects.requireNonNull(jsonPayload, "JSON payload cannot be null");

        HttpURLConnection conn = createConnection("/rest/v1/" + table, "POST");
        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(jsonPayload.getBytes(StandardCharsets.UTF_8));
        }

        int responseCode = conn.getResponseCode();
        updateLog(String.format("INSERT into '%s' → HTTP %d", table, responseCode));
        conn.disconnect();
        return responseCode;
    }

    public String readRows(String table, String filters) throws IOException {
        Objects.requireNonNull(table, "Table name cannot be null");

        String path = "/rest/v1/" + table + (filters != null ? filters : "?select=*");
        HttpURLConnection conn = createConnection(path, "GET");

        int responseCode = conn.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            updateLog(String.format("READ from '%s' failed → HTTP %d", table, responseCode));
            conn.disconnect();
            throw new IOException("GET failed with HTTP code: " + responseCode);
        }

        StringBuilder response = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        }

        updateLog(String.format("READ from '%s' succeeded", table));
        conn.disconnect();
        return response.toString();
    }

    private HttpURLConnection createConnection(String path, String method) throws IOException {
        URL url = new URL(baseUrl, path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(method);
        conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty("apikey", supabaseKey);
        conn.setRequestProperty("Authorization", "Bearer " + supabaseKey);
        return conn;
    }

    /**
     * Creates a new table in Supabase with two text columns: user_id and message.
     * Requires your service_role key.
     */
    public int createTable(String projectRef, String serviceRoleKey,
                           String tableName) throws Exception {
        // Management endpoint
        URL url = new URL(
                "https://api.supabase.io/v1/projects/"
                        + projectRef
                        + "/database/tables"
        );
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
        conn.setRequestProperty("apikey", serviceRoleKey);
        conn.setRequestProperty("Authorization", "Bearer " + serviceRoleKey);
        conn.setDoOutput(true);

        // Define your new table schema here
        String body = """
    {
      "name": "%s",
      "columns": [
        { "name": "id",       "type": "bigint", "isIdentity": true, "isPrimaryKey": true },
        { "name": "user_id",  "type": "text",   "isNullable": false },
        { "name": "message",  "type": "text",   "isNullable": false },
        { "name": "created_at", "type": "timestamptz", "default": "now()" }
      ]
    }
    """.formatted(tableName);


        try (OutputStream os = conn.getOutputStream()) {
            os.write(body.getBytes(StandardCharsets.UTF_8));
        }

        int code = conn.getResponseCode();
        conn.disconnect();
        return code;  // 201 = created, 4xx/5xx = error
    }


    private void updateLog(String message) {
        log.append("[").append(LocalDateTime.now()).append("] ").append(message).append("\n");
    }

    public String getLog() {
        return log.toString();
    }
}
