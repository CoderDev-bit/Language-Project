/**************************************************************************
 * File name:
 * Letter.java
 *
 * Description:
 * This file contains a class Letter that is used to represent a character
 * and its frequency. It implements the Comparable interface to allow
 * comparison based on frequency.
 *
 * Author:
 * Muhammad
 *
 * Date: May 20 2025
 *
 * Concepts:
 * Use of constructors and accessors
 * Use of Comparable interface
 * Use of overridden methods (toString, compareTo)
 ***************************************************************************/

package cipher;

public class Letter implements Comparable<Letter> {

    /*
     * frequency stores the number of times the letter appears
     * letter stores the character value associated with the frequency
     */
    private final int frequency;
    private final char letter;

    /**********************************************************************
     * Method name:
     * Letter
     *
     * Description:
     * This constructor initializes the character and its frequency
     *
     * Parameters:
     * char – the letter to be stored
     * int – the frequency of the letter
     *
     * Restrictions:
     * No restrictions
     *
     * Return:
     * No return value (constructor)
     *********************************************************************/
    public Letter(char letter, int frequency){
        this.letter = letter;
        this.frequency = frequency;
    } /* End of Letter constructor */

    /**********************************************************************
     * Method name:
     * Letter
     *
     * Description:
     * This constructor initializes the frequency only. The letter
     * is set to a space character by default.
     *
     * Parameters:
     * int – the frequency of the letter
     *
     * Restrictions:
     * No restrictions
     *
     * Return:
     * No return value (constructor)
     *********************************************************************/
    public Letter(int frequency){
        this.letter = ' ';
        this.frequency = frequency;
    } /* End of Letter constructor */

    /**********************************************************************
     * Method name:
     * getText
     *
     * Description:
     * This method returns the character stored in the object
     *
     * Parameters:
     * None
     *
     * Restrictions:
     * No restrictions
     *
     * Return:
     * a char that represents the stored letter
     *********************************************************************/
    public char getText(){
        return letter;
    } /* End of getText method */

    /**********************************************************************
     * Method name:
     * getFreq
     *
     * Description:
     * This method returns the frequency of the letter
     *
     * Parameters:
     * None
     *
     * Restrictions:
     * No restrictions
     *
     * Return:
     * an int that represents the frequency
     *********************************************************************/
    public int getFreq(){
        return frequency;
    } /* End of getFreq method */

    /**********************************************************************
     * Method name:
     * toString
     *
     * Description:
     * This method returns a string showing the letter and its frequency
     *
     * Parameters:
     * None
     *
     * Restrictions:
     * No restrictions
     *
     * Return:
     * a String representing the letter and its frequency
     *********************************************************************/
    @Override
    public String toString() {
        return "Char: " + letter + " Frequency: " + frequency;
    } /* End of toString method */

    /**********************************************************************
     * Method name:
     * compareTo
     *
     * Description:
     * This method compares the current Letter object to another
     * based on frequency
     *
     * Parameters:
     * Letter – another Letter object to compare against
     *
     * Restrictions:
     * No restrictions
     *
     * Return:
     * an int representing comparison result (-1, 0, or 1)
     *********************************************************************/
    public int compareTo(Letter text){

        /*
         * if the other Letter is null or has a smaller frequency,
         * return -1
         */
        if (text == null || frequency > text.getFreq()) {
            return -1;

            /*
             * if both Letter objects have the same frequency,
             * return 0
             */
        } else if (frequency == text.getFreq()) {
            return 0;

            /*
             * if the other Letter has a higher frequency,
             * return 1
             */
        } else {
            return 1;
        } /* End of if else block */

    } /* End of compareTo method */

} /* End of Letter class */
