/**************************************************************************
 * File name:
 * CipherEngine.java
 *
 * Description:
 * This file provides encryption, decryption, and cracking methods for
 * Caesar-style ciphers using customizable alphabets and frequency analysis.
 * It interfaces with Supabase to retrieve character frequency data by language.
 *
 * Author:
 * Muhammad
 *
 * Date: May 20 2025
 *
 * Concepts:
 * Caesar cipher logic with custom alphabets
 * Frequency analysis for cracking encrypted messages
 * Use of ArrayList and HashSet
 * Interaction with external Supabase database
 ***************************************************************************/

package cipher;

import util.LanguageDatabaseManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;

public class CipherEngine {

    // Special characters allowed in the message, left unmodified during encryption
    public static final String SPECIAL = " ,.-;:'[]()|*&^%$#@!~`=+";

    // List of accepted characters for encryption and decryption
    public static ArrayList<Character> alphabets = new ArrayList<Character>();

    /**********************************************************************
     * Method name:
     * extract
     *
     * Description:
     * Extracts characters from a space-separated string and populates the
     * global 'alphabets' ArrayList for use in encryption and decryption.
     *
     * Parameters:
     * String alphabet – a string containing space-separated characters
     *
     * Restrictions:
     * Returns early if the string is null or empty
     *
     * Return:
     * None
     *********************************************************************/
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
    } /* End of extract method */

    /**********************************************************************
     * Method name:
     * encrypt
     *
     * Description:
     * Encrypts the input message using Caesar cipher logic and the provided key.
     *
     * Parameters:
     * String message – the message to be encrypted
     * int key – the number of positions to shift each character
     *
     * Restrictions:
     * Message must only contain characters from alphabets or SPECIAL
     *
     * Return:
     * Encrypted string or error message if format is invalid
     *********************************************************************/
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
    } /* End of encrypt method */

    /**********************************************************************
     * Method name:
     * shift
     *
     * Description:
     * Shifts a character by the key within the bounds of the alphabets list.
     * Preserves original case of the character.
     *
     * Parameters:
     * char chosn – the character to shift
     * int key – the number of positions to shift
     *
     * Return:
     * Shifted character with preserved case
     *********************************************************************/
    public static char shift(char chosn, int key){
        int index = alphabets.indexOf(Character.toUpperCase(chosn));
        int total = alphabets.size();

        int shift = (index + key + total) % total;
        return (Character.toUpperCase(chosn) == chosn)
                ? alphabets.get(shift)
                : Character.toLowerCase(alphabets.get(shift));
    } /* End of shift method */

    /**********************************************************************
     * Method name:
     * decrypt
     *
     * Description:
     * Decrypts the message by reversing the Caesar cipher shift.
     *
     * Parameters:
     * String message – the encrypted message
     * int key – the same key used for encryption
     *
     * Return:
     * Decrypted string
     *********************************************************************/
    public static String decrypt(String message, int key){
        String decrypted = encrypt(message, -key);
        return decrypted;
    } /* End of decrypt method */

    /**********************************************************************
     * Method name:
     * crack
     *
     * Description:
     * Attempts to deduce the Caesar cipher key using frequency analysis
     * comparing input message frequencies with a stored language profile.
     *
     * Parameters:
     * String message – encrypted message to crack
     * String language – name of the language to reference from database
     *
     * Return:
     * String representing key shift or error message
     *********************************************************************/
    public static String crack(String message, String language){
        if (message == null || message.isEmpty() || language == null || language.isEmpty()) {
            return "-Insufficient information-";
        }

        /* Getting frequency of all characters */
        HashSet<Letter> stored = getFrequencies(message);

        int checker = exceptions(stored);
        if (checker == 1) {
            return "-All letters same frequency so cannot do frequency analysis-";
        } else if (checker == 2) {
            return "-Unknown letter please update alphabet-";
        }

        char mostFrequent = findMostFrequent(stored);
        int shift = alphabets.indexOf(Character.toUpperCase(mostFrequent))
                - alphabets.indexOf(Character.toUpperCase(highestStored(language).charAt(0)));

        if (shift < 0) {
            shift += alphabets.size();
        }

        return "" + shift;
    } /* End of crack method */

    /**********************************************************************
     * Method name:
     * exceptions
     *
     * Description:
     * Checks for edge cases in frequency analysis
     *
     * Parameters:
     * HashSet<Letter> stored – character frequency set
     *
     * Return:
     * 1 if all frequencies are equal, 2 if unknown character, -1 otherwise
     *********************************************************************/
    public static int exceptions(HashSet<Letter> stored){
        int constant = 0;

        for (Letter temp: stored) {
            if (!(alphabets.contains(Character.toUpperCase(temp.getText())))
                    && (SPECIAL.indexOf(temp.getText()) == -1)) {
                return 2;
            }
            if (constant == 0) {
                constant = temp.getFreq();
            } else if (temp.getFreq() != constant) {
                return -1;
            }
        }

        return 1;
    } /* End of exceptions method */

    /**********************************************************************
     * Method name:
     * getFrequencies
     *
     * Description:
     * Calculates and returns the frequency of each character in a string
     *
     * Parameters:
     * String message – the message to analyze
     *
     * Return:
     * HashSet<Letter> – each letter with corresponding frequency
     *********************************************************************/
    public static HashSet<Letter> getFrequencies(String message){
        /* To store values */
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
    } /* End of getFrequencies method */

    /**********************************************************************
     * Method name:
     * findMostFrequent
     *
     * Description:
     * Finds the most frequently occurring character from a frequency set
     *
     * Parameters:
     * HashSet<Letter> stored – frequency set
     *
     * Return:
     * char – most frequent character
     *********************************************************************/
    public static char findMostFrequent(HashSet<Letter> stored) {
        Letter highest = new Letter(0);

        for (Letter k: stored) {
            if (k.compareTo(highest) < 0) {
                highest = k;
            }
        }
        return highest.getText();
    } /* End of findMostFrequent method */

    /**********************************************************************
     * Method name:
     * highestStored
     *
     * Description:
     * Retrieves the most frequent character for a language from Supabase
     *
     * Parameters:
     * String language – language identifier for database lookup
     *
     * Return:
     * String – character with highest frequency in the language
     *********************************************************************/
    public static String highestStored(String language){
        String temp = " ";

        LanguageDatabaseManager dbInstance = null;
        try {
            dbInstance = LanguageDatabaseManager.getDatabaseManager(
                    "https://frhgfmnvkopdwpiorszb.supabase.co",
                    "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImZyaGdmbW52a29wZHdwaW9yc3piIiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTc0NjQ5MzI0OCwiZXhwIjoyMDYyMDY5MjQ4fQ.bu7u6Doh9PMWGzpeROtFDm8qnSr5gk56m3vIDllMs7E"
            );
            System.out.println("LanguageDatabaseManager initialized.");

            temp = dbInstance.getMostFrequentChar(language);
        } catch (IOException | IllegalStateException e) {
            System.err.println("Failed to initialize LanguageDatabaseManager: " + e.getMessage());
            e.printStackTrace();
        }

        return temp;
    } /* End of highestStored method */

} /* End of CipherEngine class */
