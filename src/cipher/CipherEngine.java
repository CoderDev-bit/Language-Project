package cipher;

import util.LanguageDatabaseManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;

public class CipherEngine {
    //encrypt(Ciphers, key, message)

    public static final String SPECIAL = " ,.-;:'[]()|*&^%$#@!~`=+";
    public static ArrayList<Character> alphabets = new ArrayList<Character>();

    public static void extract(String alphabet){
        Scanner sc = new Scanner(alphabet);
        String[] temp;

        if (alphabet.isEmpty() || alphabet == null) {
            System.out.println("-Empty String-");
        } else {
            temp = sc.nextLine().trim().split(" ");

            for (String val : temp) {
                alphabets.add(val.charAt(0));
            }
        }
    }

    public static String encrypt(String message, int key){
        if (message == null || message.isEmpty()){
            return "-Invalid Format-";
        }

        char hold;
        char[] chars = message.toCharArray();
        String encrypted = "";

        for (char k: chars) {

            if (alphabets.contains(Character.toUpperCase(k))){
                hold = shift(k, key);
            } else if (SPECIAL.indexOf(k) >= 0) {
                hold = k;
            } else {
                return "-Unknown Character Used-";
            }

            encrypted += hold;
        }

        return encrypted;
    }

    public static char shift(char chosn, int key){
        int index = alphabets.indexOf(Character.toUpperCase(chosn));
        int total = alphabets.size();


        int shift = (index + key + total) % total;
        return (Character.toUpperCase(chosn) == chosn) ? alphabets.get(shift) : Character.toLowerCase(alphabets.get(shift));
    }

    public static String decrypt(String message, int key){
        String decrypted = encrypt(message, -key);
        return decrypted;
    }

    public static String crack(String message, String language){
        if (message == null || message.isEmpty() || language == null || language.isEmpty()) {
            return "-Insufficient information-";
        }

        /*Getting frequency of all charecters*/
        HashSet<Letter> stored = getFrequencies(message);

        int checker = exceptions(stored);
        if (checker == 1) {
            return "-All letters same frequency so cannot do frequency analysis-";
        } else if (checker == 2) {
            return "-Unknown letter please update alphabet-";
        }

        char mostFrequent = findMostFrequent(stored);
        int shift = alphabets.indexOf(Character.toUpperCase(mostFrequent)) - alphabets.indexOf(highestStored(language));

        if (shift < 0) {
            shift += alphabets.size();
        }

        return "" + shift;
    }

    public static int exceptions(HashSet<Letter> stored){
        int constant = 0;

        for (Letter temp: stored) {
            if (!(alphabets.contains(Character.toUpperCase(temp.getText()))) && (SPECIAL.indexOf(temp.getText()) == -1)) {
                return 2;
            }
            if (constant == 0) {
                constant = temp.getFreq();
            } else if (temp.getFreq() != constant) {
                return -1;
            }
        }

        return 1;
    }

    public static HashSet<Letter> getFrequencies(String message){
        /*To store values*/
        HashSet<Letter> temp = new HashSet<Letter>();

        for (char k: message.trim().replaceAll(" ", "").toUpperCase().toCharArray()) {
            boolean found = false;
            for (Letter l : temp) {
                if (l.getText() == k) {
                    Letter letter = new Letter(k, l.getFreq() + 1);
                    temp.remove(l);
                    temp.add(letter);
                    found = true;
                    break;
                }
            }
            if (!found) {
                Letter letter = new Letter(k, 1);
                temp.add(letter);
            }
        }

        return temp;
    }

    public static char findMostFrequent(HashSet<Letter> stored) {
        Letter highest = new Letter(0);

        for (Letter k: stored) {
            if (k.compareTo(highest) < 0) {
                highest = k;
            }
        }
        return highest.getText();
    }


    /*This is you Shivam --> U just gotta get the letter in the alphabet with most occurrences from database*/
    public static char highestStored(String language){
        String temp = " ";

        try {
            LanguageDatabaseManager db = LanguageDatabaseManager.getDatabaseManager("https://frhgfmnvkopdwpiorszb.supabase.co", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...");
            temp = db.getMostFrequentChar(language);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return temp.charAt(0);
    }

    /*Example of how we use the code:*/

    public static void main(String[] args){

        //for encryption and decryption
        String message = "Je un chat.";
        extract("A À Â B C Ç D E É È Ê Ë F G H I Î Ï J K L M N O Ô Œ P Q R S T U Ù Û Ü V W X Y Ÿ Z");
        System.out.println(alphabets);
        String en = encrypt(message, 2);
        System.out.println(en);
        String dc = decrypt(en, 2);
        System.out.println(dc);

        //for cracker
        message = "Lè ûô dîâù.";
        System.out.println(alphabets);
        String shift = crack(message, "french");
        System.out.println(shift);
    }

}