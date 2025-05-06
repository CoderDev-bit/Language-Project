import java.net.HttpURLConnection;
import java.net.URL;
import java.net.MalformedURLException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class DatabaseManager {

    private final URL baseUrl;

    public DatabaseManager(String strDatabaseURL) throws MalformedURLException {
        this.baseUrl = new URL(strDatabaseURL);
    }

    public DatabaseManager(URL urlDatabase) {
        this.baseUrl = urlDatabase;
    }

    /**
     * Writes the given JSON string to the specified Firebase node.
     *
     * @param nodePath  the RTDB path (e.g. "data.json" or "users/user1.json")
     * @param jsonData  the JSON payload to send
     * @return HTTP response code (200 for success)
     * @throws Exception on network or I/O errors
     */
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
        return code;
    }

    /**
     * Reads the raw JSON payload from the specified Firebase node.
     *
     * @param nodePath  the RTDB path (e.g. "data.json" or "users.json")
     * @return the JSON string returned by Firebase
     * @throws Exception on network or I/O errors
     */
    public String readJson(String nodePath) throws Exception {
        URL nodeUrl = new URL(baseUrl, nodePath);
        HttpURLConnection conn = (HttpURLConnection) nodeUrl.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");

        int code = conn.getResponseCode();
        if (code != HttpURLConnection.HTTP_OK) {
            conn.disconnect();
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
        return result.toString();
    }
}
