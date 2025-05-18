
import util.DatabaseManager1;

import java.io.IOException;


public class Main {

    public static void main(String[] args) {

    }

    private static DatabaseManager1 getDatabaseManager() throws IOException {
        String url = "https://frhgfmnvkopdwpiorszb.supabase.co";
        // 2) Your anon or service_role key
        String key = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImZyaGdmbW52a29wZHdwaW9yc3piIiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTc0NjQ5MzI0OCwiZXhwIjoyMDYyMDY5MjQ4fQ.bu7u6Doh9PMWGzpeROtFDm8qnSr5gk56m3vIDllMs7E";

        // initialize manager
        DatabaseManager1 db = new DatabaseManager1(url, key);

        db.toggleLogging("log.txt");
        return db;
    }
}
