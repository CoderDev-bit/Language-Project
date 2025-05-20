package lang;

import util.LanguageDatabaseManager;

//import java.io.IO;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

public class LanguageModel {

    LanguageDatabaseManager db;

    public LanguageModel(LanguageDatabaseManager db) {



    }

    //GUI will remove the noise characters (emojis etc) and noise words (rarely used words). don't worry here
    public void train(String strLanguageName, String strText, String[] wordSeperators) {



    }

    public HashMap<String,Double> analyze(String strText) {

        return new HashMap<>();

    }

    public static void main(String[] args) {

        LanguageDatabaseManager db = null;  // Local variable in main

        try {
            db = LanguageDatabaseManager.getDatabaseManager("https://frhgfmnvkopdwpiorszb.supabase.co", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImZyaGdmbW52a29wZHdwaW9yc3piIiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTc0NjQ5MzI0OCwiZXhwIjoyMDYyMDY5MjQ4fQ.bu7u6Doh9PMWGzpeROtFDm8qnSr5gk56m3vIDllMs7E");
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            System.out.println(db.getWordProperty("english", "car", "abs_freq"));
            System.out.println(Arrays.stream(db.getStoredLanguages()).toList());
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Create an instance of LanguageModel with db
        LanguageModel test = new LanguageModel(db);
    }

}


