//imports for the database stuff

import util.DatabaseManager;


public class Main {

    public static void main(String[] args) {
        try {
            // 1) Supabase project URL (no path/query)
            String url = "https://frhgfmnvkopdwpiorszb.supabase.co";
            // 2) Your anon or service_role key
            String key = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImZyaGdmbW52a29wZHdwaW9yc3piIiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTc0NjQ5MzI0OCwiZXhwIjoyMDYyMDY5MjQ4fQ.bu7u6Doh9PMWGzpeROtFDm8qnSr5gk56m3vIDllMs7E";

            // initialize manager
            DatabaseManager db = new DatabaseManager(url, key);

            // 3) Build JSON using your exact column names
            String payload = """
                    {
                      "user_id": "TFiwoFw",
                      "message": "Not really"
                    }
                    """;


            // 4) INSERT into Test
            int insertCode = db.insertRow("mytable", payload);
            System.out.println("INSERT HTTP code: " + insertCode);

            // 5) READ all rows back
            String allRows = db.readRows("mytable", "?select=*");
            System.out.println("ROWS JSON: " + allRows);

            // 6) Optional: print your internal log
            System.out.println("--- LOG ---\n" + db.getLog());

            String projectRef = "frhgfmnvkopdwpiorszb";
            String serviceRole = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImZyaGdmbW52a29wZHdwaW9yc3piIiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTc0NjQ5MzI0OCwiZXhwIjoyMDYyMDY5MjQ4fQ.bu7u6Doh9PMWGzpeROtFDm8qnSr5gk56m3vIDllMs7E";
            String newTableName = "myTable";

            //int code = db.createTable("myTable");
            //System.out.println("CREATE TABLE HTTP code: " + code);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
