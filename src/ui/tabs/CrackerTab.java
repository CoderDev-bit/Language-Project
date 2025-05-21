/**************************************************************************
 * File name:
 * CrackerTab.java
 *
 * Description:
 * This file defines the CrackerTab class which extends BaseTab.
 * It provides a graphical user interface for decrypting
 * Caesar-encrypted text using user-specified alphabet and language.
 *
 * Author:
 * Shivam and Muhammad
 *
 * Date: May 20 2025
 *
 * Concepts:
 * Java Swing UI components
 * Event-driven programming using ActionListener
 * Caesar cipher cracking using external CipherEngine
 ***************************************************************************/

package ui.tabs;

import cipher.CipherEngine;

import javax.swing.*;
import java.awt.*;

public class CrackerTab extends BaseTab {

    /**********************************************************************
     * Method name:
     * CrackerTab
     *
     * Description:
     * Constructor that initializes the tab UI by calling initTab
     *
     * Parameters:
     * None
     *
     * Restrictions:
     * No restrictions
     *
     * Return:
     * No return value (constructor)
     *********************************************************************/
    public CrackerTab() {
        initTab();
    } /* End of CrackerTab constructor */

    /**********************************************************************
     * Method name:
     * initTab
     *
     * Description:
     * This method constructs the UI for Caesar cipher cracking.
     * It adds input fields for cipher text, alphabet, and language,
     * as well as a button to trigger decryption and a field to
     * display the output.
     *
     * Parameters:
     * None
     *
     * Restrictions:
     * No restrictions
     *
     * Return:
     * No return value
     *********************************************************************/
    public void initTab() {

        /*
         * Set layout of this tab to BorderLayout
         */
        setLayout(new BorderLayout());

        /*
         * Create a content panel with vertical BoxLayout
         */
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(BACKGROUND);
        content.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        /*
         * Add label for instructions to paste cipher text
         */
        JLabel label = new JLabel(
                "Paste Caesar-encrypted text below:"
        );
        label.setFont(new Font("SansSerif", Font.BOLD, 16));
        label.setForeground(PRIMARY);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(label);

        content.add(Box.createRigidArea(new Dimension(0, 10)));

        /*
         * Add text area to receive Caesar cipher text
         */
        JTextArea cipherTextArea = new JTextArea(5, 40);
        cipherTextArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        cipherTextArea.setLineWrap(true);
        cipherTextArea.setWrapStyleWord(true);
        cipherTextArea.setBackground(ACCENT);
        JScrollPane cipherScroll = new JScrollPane(cipherTextArea);
        cipherScroll.setPreferredSize(new Dimension(500, 100));
        cipherScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(cipherScroll);

        content.add(Box.createRigidArea(new Dimension(0, 10)));

        /*
         * Add label and text field for custom alphabet
         */
        JLabel alphabetLabel = new JLabel(
                "Alphabet: (In uppercase seperated by spaces)"
        );
        alphabetLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        alphabetLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(alphabetLabel);

        JTextField alphabetField = new JTextField();
        alphabetField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        alphabetField.setMaximumSize(new Dimension(300, 30));
        alphabetField.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(alphabetField);

        content.add(Box.createRigidArea(new Dimension(0, 10)));

        /*
         * Add label and text field for language input
         */
        JLabel languageLabel = new JLabel(
                "Language: (write like \"english\")"
        );
        languageLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        languageLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(languageLabel);

        JTextField languageField = new JTextField();
        languageField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        languageField.setMaximumSize(new Dimension(300, 30));
        languageField.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(languageField);

        content.add(Box.createRigidArea(new Dimension(0, 10)));

        /*
         * Add button to trigger cracking of Caesar cipher
         */
        JButton crackButton = new JButton("Crack Caesar Cipher");
        crackButton.setFont(new Font("SansSerif", Font.PLAIN, 14));
        crackButton.setBackground(PRIMARY);
        crackButton.setForeground(Color.WHITE);
        crackButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(crackButton);

        content.add(Box.createRigidArea(new Dimension(0, 10)));

        /*
         * Add output area to display the decryption result
         */
        JTextArea crackedOutput = new JTextArea(5, 40);
        crackedOutput.setFont(new Font("Monospaced", Font.PLAIN, 14));
        crackedOutput.setEditable(false);
        crackedOutput.setBackground(ACCENT);
        JScrollPane outputScroll = new JScrollPane(crackedOutput);
        outputScroll.setPreferredSize(new Dimension(500, 100));
        outputScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(outputScroll);

        /*
         * Add event listener to crack button to trigger CipherEngine
         */
        crackButton.addActionListener(e -> {

            /*
             * Extract the alphabet using user input
             */
            CipherEngine.extract(alphabetField.getText());

            /*
             * Call the crack function with the cipher text and language
             */
            String out = CipherEngine.crack(
                    cipherTextArea.getText(), languageField.getText()
            );

            /*
             * Display result of Caesar cracking
             */
            crackedOutput.setText(
                    "The text has been shifted " + out +
                            " characters based on your alphabet."
            );
        });

        /*
         * Add the content panel to the center of the tab
         */
        add(content, BorderLayout.CENTER);

        /*
         * Refresh layout to reflect changes
         */
        this.revalidate();
        this.repaint();

    } /* End of initTab method */

} /* End of CrackerTab class */
