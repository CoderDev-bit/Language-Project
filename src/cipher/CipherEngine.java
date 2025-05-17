package cipher;

import java.util.ArrayList;
import java.util.Scanner;

public class CipherEngine {
    //encrypt(Ciphers, key, message)

    public static final String SPECIAL = " ,.-;:'[]()|*&^%$#@!~`=+";
    public static ArrayList<Character> alphabets = new ArrayList<Character>();

    public static void extract(){
        Scanner sc = new Scanner(CipherEngine.class.getResourceAsStream("FrenchAlphabet.txt"));
        String[] temp;

        if (sc.hasNextLine()) {
            temp = sc.nextLine().trim().split(" ");

            if (temp != null) {
                for (String val : temp) {
                    alphabets.add(val.charAt(0));
                }
            }
        } else {
            System.out.println("-File Empty-");
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
                return "-Unknown Charecter Used-";
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

    /*Example of how we use the code:*/

    public static void main(String[] args){
        String message = "Je un chat.";
        extract();
        System.out.println(alphabets);
        String en = encrypt(message, 2);
        System.out.println(en);
        String dc = decrypt(en, 2);
        System.out.println(dc);

    }

}