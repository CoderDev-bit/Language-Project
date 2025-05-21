/**************************************************************************
 * File name:
 * CaesarToolTab.java
 *
 * Description:
 * This file defines the CaesarToolTab class that extends BaseTab.
 * It provides a GUI for applying Caesar encryption or decryption
 * based on a user-defined key and alphabet.
 *
 * Author:
 * Shivam and Muhammad
 *
 * Date: May 20 2025
 *
 * Concepts:
 * Swing GUI construction
 * Caesar cipher encryption/decryption
 * Event handling with ActionListener
 ***************************************************************************/

package ui.tabs;

import cipher.CipherEngine;

import javax.swing.*;
import java.awt.*;

public class CaesarToolTab extends BaseTab {

    /**********************************************************************
     * Method name:
     * CaesarToolTab
     *
     * Description:
     * Constructor that initializes the tab UI by calling initTab
     *
     * Parameters:
     * None
     *
     * Return:
     * No return value (constructor)
     *********************************************************************/
    public CaesarToolTab() {
        initTab();
    } /* End of CaesarToolTab constructor */

    /**********************************************************************
     * Method name:
     * initTab
     *
     * Description:
     * Constructs the Caesar cipher tool interface with input fields for
     * text, key, alphabet, and mode (encrypt/decrypt). It also includes
     * a button to execute the cipher operation and a field to display
     * the output.
     *
     * Parameters:
     * None
     *
     * Return:
     * No return value
     *********************************************************************/
    public void initTab() {

        /*
         * Set the layout of this tab to BorderLayout
         */
        setLayout(new BorderLayout());

        /*
         * Create a vertically stacked content panel with padding
         */
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(BACKGROUND);
        content.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        /*
         * Add main title label
         */
        JLabel label = new JLabel("Caesar Cipher Tool");
        label.setFont(new Font("SansSerif", Font.BOLD, 16));
        label.setForeground(PRIMARY);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(label);

        content.add(Box.createRigidArea(new Dimension(0, 10)));

        /*
         * Add input label and text area
         */
        JLabel inputLabel = new JLabel("Enter Text:");
        inputLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        inputLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(inputLabel);

        JTextArea input = new JTextArea(5, 40);
        input.setFont(new Font("Monospaced", Font.PLAIN, 14));
        input.setLineWrap(true);
        input.setWrapStyleWord(true);
        input.setBackground(ACCENT);
        JScrollPane inputScroll = new JScrollPane(input);
        inputScroll.setPreferredSize(new Dimension(500, 100));
        inputScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(inputScroll);

        content.add(Box.createRigidArea(new Dimension(0, 10)));

        /*
         * Add key label and input field
         */
        JLabel keyLabel = new JLabel("Key:");
        keyLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        keyLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(keyLabel);

        JTextField keyField = new JTextField();
        keyField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        keyField.setMaximumSize(new Dimension(300, 30));
        keyField.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(keyField);

        content.add(Box.createRigidArea(new Dimension(0, 10)));

        /*
         * Add alphabet label and input field
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
         * Add mode dropdown (Encrypt/Decrypt)
         */
        JComboBox<String> mode = new JComboBox<>(
                new String[]{"Encrypt", "Decrypt"}
        );
        mode.setFont(new Font("SansSerif", Font.PLAIN, 14));
        mode.setMaximumSize(new Dimension(300, 30));
        mode.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(mode);

        content.add(Box.createRigidArea(new Dimension(0, 10)));

        /*
         * Add execution button
         */
        JButton execute = new JButton("Apply Caesar");
        execute.setFont(new Font("SansSerif", Font.PLAIN, 14));
        execute.setBackground(PRIMARY);
        execute.setForeground(Color.WHITE);
        execute.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(execute);

        content.add(Box.createRigidArea(new Dimension(0, 10)));

        /*
         * Add output area for cipher result
         */
        JTextArea output = new JTextArea(5, 40);
        output.setFont(new Font("Monospaced", Font.PLAIN, 14));
        output.setEditable(false);
        output.setBackground(ACCENT);
        JScrollPane outputScroll = new JScrollPane(output);
        outputScroll.setPreferredSize(new Dimension(500, 100));
        outputScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(outputScroll);

        /*
         * Add button event handler to apply Caesar cipher
         */
        execute.addActionListener(e -> {

            /*
             * Parse alphabet input
             */
            CipherEngine.extract(alphabetField.getText());

            String out = "";
            int getProcess = mode.getSelectedIndex();

            /*
             * Apply encryption or decryption based on mode selection
             */
            if (getProcess == 0) {
                out = CipherEngine.encrypt(
                        input.getText(), Integer.parseInt(keyField.getText())
                );
            } else {
                out = CipherEngine.decrypt(
                        input.getText(), Integer.parseInt(keyField.getText())
                );
            }

            /*
             * Display the cipher result
             */
            output.setText(out);
        });

        /*
         * Add all content to the center of this tab
         */
        add(content, BorderLayout.CENTER);

        /*
         * Refresh layout after initialization
         */
        this.revalidate();
        this.repaint();

    } /* End of initTab method */

} /* End of CaesarToolTab class */
