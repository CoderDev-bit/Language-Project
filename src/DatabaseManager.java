import java.net.HttpURLConnection;
import java.net.URL;
import java.net.MalformedURLException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class DatabaseManager {

    private URL baseUrl;
    private StringBuilder strLog = new StringBuilder();

    public DatabaseManager(String strDatabaseURL) throws MalformedURLException {
        setBaseUrl(strDatabaseURL);
        updateLog("Initialized DatabaseManager with URL: " + strDatabaseURL);
    }

    public DatabaseManager(URL urlDatabase) {
        setBaseUrl(urlDatabase);
        updateLog("Initialized DatabaseManager with URL: " + urlDatabase.toString());
    }

    public void setBaseUrl(String baseUrl) {
        try {
            setBaseUrl(new URL(baseUrl));
            updateLog("Base URL set to: " + baseUrl);
        } catch (MalformedURLException e) {
            updateLog("Failed to set base URL: " + baseUrl);
            throw new RuntimeException(e);
        }
    }

    public void setBaseUrl(URL urlDatabase) {
        this.baseUrl = urlDatabase;
        updateLog("Base URL set to: " + urlDatabase.toString());
    }

    public URL getBaseUrl() {
        return baseUrl;
    }

    @Override
    public String toString() {
        return "Database URL: " + baseUrl.toString();
    }

    public int writeJson(String nodePath, String jsonData) throws Exception {
        URL nodeUrl = new URL(baseUrl, nodePath);
        HttpURLConnection conn = (HttpURLConnection) nodeUrl.openConnection();
        conn.setRequestMethod("POST"); // or "PUT" for overwrite
        conn.setRequestProperty("Content-Type", "application/json; utf-8");
        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonData.getBytes(StandardCharsets.UTF_8);
            os.write(input);
        }

        int code = conn.getResponseCode();
        conn.disconnect();
        updateLog("Wrote JSON to node '" + nodePath + "' with response code: " + code);
        return code;
    }

    public String readJson(String nodePath) throws Exception {
        URL nodeUrl = new URL(baseUrl, nodePath);
        HttpURLConnection conn = (HttpURLConnection) nodeUrl.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");

        int code = conn.getResponseCode();
        if (code != HttpURLConnection.HTTP_OK) {
            conn.disconnect();
            updateLog("Failed to read JSON from node '" + nodePath + "'. HTTP code: " + code);
            throw new RuntimeException("GET failed with HTTP code: " + code);
        }

        StringBuilder result = new StringBuilder();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                result.append(line);
            }
        }
        conn.disconnect();
        updateLog("Read JSON from node '" + nodePath + "' successfully.");
        return result.toString();
    }

    /**
     * Appends a log entry with a timestamp to strLog.
     *
     * @param message the log message
     */
    private void updateLog(String message) {
        strLog.append("[").append(java.time.LocalDateTime.now()).append("] ")
                .append(message).append("\n");
    }

    /**
     * Returns the current log as a string.
     */
    public String getLog() {
        return strLog.toString();
    }
}
