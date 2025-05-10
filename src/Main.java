//imports for the database stuff

import util.DatabaseManager;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;


public class Main {

    public static void main(String[] args) {
        try {
            // 1) Supabase project URL (no path/query)
            DatabaseManager db = getDatabaseManager();

            int code = db.createTable("mYTABle1");
            System.out.println("CREATE TABLE HTTP code: " + code);

            // 3) Build JSON using your exact column names
            String payload = """
                    {
                      "user_id": "THIS",
                      "message": "Not really)))"
                    }
                    """;


            // 4) INSERT into Test
            int insertCode = db.insertRow("mYTABle1", payload);
            System.out.println("INSERT HTTP code: " + insertCode);

            // 5) READ all rows back
            String allRows = db.readRows("mYTABle1", "?select=*");
            System.out.println("ROWS JSON: " + allRows);

            // 6) Optional: print your internal log
            System.out.println("--- LOG ---\n" + db.getLog());

            String test = db.listTables();
            System.out.println(test);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static DatabaseManager getDatabaseManager() throws IOException {
        String url = "https://frhgfmnvkopdwpiorszb.supabase.co";
        // 2) Your anon or service_role key
        String key = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImZyaGdmbW52a29wZHdwaW9yc3piIiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTc0NjQ5MzI0OCwiZXhwIjoyMDYyMDY5MjQ4fQ.bu7u6Doh9PMWGzpeROtFDm8qnSr5gk56m3vIDllMs7E";

        // initialize manager
        DatabaseManager db = new DatabaseManager(url, key);

        db.toggleLogging("log.txt");
        return db;
    }
}
