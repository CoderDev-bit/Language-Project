import java.net.HttpURLConnection;
import java.net.URL;
import java.net.MalformedURLException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class DatabaseManager1 {

    private  URL baseUrl;
    private  String supabaseKey;
    private  StringBuilder log = new StringBuilder();

    /**
     * @param baseUrlString  your Supabase project URL, e.g. "https://xyzcompany.supabase.co"
     * @param supabaseKey    your anon or service_role key from project settings
     */
    public DatabaseManager1(String baseUrlString, String supabaseKey) {
        try {
            this.baseUrl    = new URL(baseUrlString);
            this.supabaseKey = supabaseKey;
            updateLog("Initialized with URL=" + baseUrlString);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid Supabase URL", e);
        }
    }

    /** Inserts a row into the given table. Returns HTTP status. */
    public int insertRow(String table, String jsonPayload) throws Exception {
        // POST /rest/v1/<table>
        URL url = new URL(baseUrl, "/rest/v1/" + table);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
        conn.setRequestProperty("apikey", supabaseKey);
        conn.setRequestProperty("Authorization", "Bearer " + supabaseKey);
        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(jsonPayload.getBytes(StandardCharsets.UTF_8));
        }

        int code = conn.getResponseCode();
        updateLog("INSERT into '" + table + "' → HTTP " + code);
        conn.disconnect();
        return code;
    }

    /**
     * Reads rows from the given table.
     * @param table   the table name
     * @param filters any query string after select, e.g. "?select=*&id=eq.123"
     * @return raw JSON array of rows
     */
    public String readRows(String table, String filters) throws Exception {
        // GET /rest/v1/<table>?select=*
        String path = "/rest/v1/" + table + (filters != null ? filters : "?select=*");
        URL url = new URL(baseUrl, path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty("apikey", supabaseKey);
        conn.setRequestProperty("Authorization", "Bearer " + supabaseKey);

        int code = conn.getResponseCode();
        if (code != HttpURLConnection.HTTP_OK) {
            updateLog("READ from '" + table + "' failed HTTP " + code);
            conn.disconnect();
            throw new RuntimeException("GET failed with HTTP code: " + code);
        }

        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) sb.append(line);
        }
        conn.disconnect();
        updateLog("READ from '" + table + "' succeeded");
        return sb.toString();
    }

    private void updateLog(String msg) {
        log.append("[").append(java.time.LocalDateTime.now()).append("] ").append(msg).append("\n");
    }

    /** Returns an internal timestamped log. */
    public String getLog() {
        return log.toString();
    }


}
