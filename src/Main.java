//imports for the database stuff
import javax.xml.crypto.Data;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class Main {

    public static void main(String[] args) {
        /*try {
            // Initialize the DatabaseManager with your Firebase Realtime Database base URL
            String baseUrl = "https://languageproject-67eb8-default-rtdb.europe-west1.firebasedatabase.app/";
            DatabaseManager dmTest = new DatabaseManager(baseUrl);

            // Write
            String payload = "{\"user\":\"G\",\"message\":\"Hello, world!\",\"timestamp\":\"2025-05-05T12:00\"}";
            int postCode = dmTest.writeJson("data.json", payload);
            System.out.println("WRITE HTTP code: " + postCode);

            // Read
            String json = dmTest.readJson("data.json");
            System.out.println("READ JSON payload: " + json);

            System.out.println(dmTest.getLog());

        } catch (Exception e) {
            e.printStackTrace();
        }*/

        //insertVal();

        /**/
        try {
            // 1) Supabase project URL (no path/query)
            String url = "https://frhgfmnvkopdwpiorszb.supabase.co";
            // 2) Your anon or service_role key
            String key = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImZyaGdmbW52a29wZHdwaW9yc3piIiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTc0NjQ5MzI0OCwiZXhwIjoyMDYyMDY5MjQ4fQ.bu7u6Doh9PMWGzpeROtFDm8qnSr5gk56m3vIDllMs7E";

            // initialize manager
            DatabaseManager1 db = new DatabaseManager1(url, key);

            // 3) Build JSON using your exact column names
            String payload = "{\n" +
                    "  \"user_id\": \"George\",\n" +
                    "  \"message\": \"Hello Supabase2eq2EQq3rq!\"\n" +
                    "}";


            // 4) INSERT into Test
            int insertCode = db.insertRow("Test", payload);
            System.out.println("INSERT HTTP code: " + insertCode);

            // 5) READ all rows back
            String allRows = db.readRows("Test", "?select=*");
            System.out.println("ROWS JSON: " + allRows);

            // 6) Optional: print your internal log
            System.out.println("--- LOG ---\n" + db.getLog());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void insertVal(){
        try {
            URL url = new URL("https://languageproject-67eb8-default-rtdb.europe-west1.firebasedatabase.app/data.json");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            // Customize your data here
            String message = "ENGLISH: Wrench";

            String jsonData = "{\"user\":\"G\",\"message\":\"" + message + "\",\"timestamp\":\"2025-04-25T14:30\"}";

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonData.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            System.out.println("Response Code : " + responseCode);

            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
                System.out.println("Response Data : " + response.toString());
            }

            connection.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
