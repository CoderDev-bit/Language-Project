/**************************************************************************
 * File name:
 * TrainerTab.java
 *
 * Description:
 * This file defines the TrainerTab class, which provides a user interface
 * for training new language profiles. Users can input text either by
 * uploading a file or pasting it directly, and then associate it with a
 * language name to create a new language model.
 *
 * Author:
 * Shivam Patel
 *
 * Date: May 20 2025
 *
 * Concepts:
 * Swing GUI components (JPanel, JLabel, JComboBox, JButton, JTextArea, JTextField, JScrollPane)
 * Event handling (ActionListener)
 * Language model training (via the 'model' object)
 * Text preprocessing
 *
 *************************************************************************/
package ui.tabs;

import util.TextPreprocessor;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class TrainerTab extends BaseTab {

    // Block comment: Defines an array of common separators used for text processing during training.
    private final static String[] commonSeparators = {" ", ",", ".", ";", ":", "!", "?", "(", ")", "\n", "\t", "-"};
    private JTextField languageField; // Block comment: Declares the text field for entering the language name.

    /**************************************************************************
     * Method name:
     * TrainerTab
     *
     * Description:
     * Constructor for the TrainerTab class.
     * Calls the initialization method to set up the tab's UI components.
     *
     * Author:
     * Shivam Patel
     *
     * Date: May 20 2025
     *
     *************************************************************************/
    public TrainerTab() {
        initTab();
    }

    /**************************************************************************
     * Method name:
     * initTab
     *
     * Description:
     * Initializes and arranges all Swing GUI components for the language trainer tab.
     * This includes input fields for language name, text input (file or paste),
     * and the "Train" button.
     *
     * Author:
     * Shivam Patel
     *
     * Date: May 20 2025
     *
     *************************************************************************/
    public void initTab() {
        // Block comment: Set the outer layout of the tab to BorderLayout.
        setLayout(new BorderLayout());

        // Block comment: Create an inner panel to hold content, using BoxLayout for vertical arrangement.
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(BACKGROUND);
        content.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        // Block comment: Create and configure the section title label.
        JLabel label = new JLabel("Train a language profile:");
        label.setFont(new Font("SansSerif", Font.BOLD, 16));
        label.setForeground(PRIMARY);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(label);

        // Block comment: Add vertical spacing.
        content.add(Box.createRigidArea(new Dimension(0, 10)));

        // Block comment: Create and configure the label for the language name input.
        JLabel languageLabel = new JLabel("Language Name:");
        languageLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        languageLabel.setForeground(Color.DARK_GRAY);
        languageLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(languageLabel);

        // Block comment: Add vertical spacing.
        content.add(Box.createRigidArea(new Dimension(0, 5)));

        // Block comment: Initialize and configure the text field for the language name.
        languageField = new JTextField();
        languageField.setMaximumSize(new Dimension(300, 30));
        languageField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        languageField.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(languageField);

        // Block comment: Add vertical spacing.
        content.add(Box.createRigidArea(new Dimension(0, 10)));

        // Block comment: Create and configure the combo box for selecting input mode (file or paste).
        JComboBox<String> inputMode = new JComboBox<>(new String[]{"Upload from File", "Paste Text"});
        inputMode.setMaximumSize(new Dimension(300, 30));
        inputMode.setFont(new Font("SansSerif", Font.PLAIN, 14));
        inputMode.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(inputMode);

        // Block comment: Add vertical spacing.
        content.add(Box.createRigidArea(new Dimension(0, 10)));

        // Block comment: Create and configure the button for selecting a file.
        JButton fileButton = new JButton("Select File");
        fileButton.setFont(new Font("SansSerif", Font.PLAIN, 14));
        fileButton.setBackground(PRIMARY);
        fileButton.setForeground(Color.WHITE);
        fileButton.setMaximumSize(new Dimension(200, 30));
        fileButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(fileButton);

        // Block comment: Add vertical spacing.
        content.add(Box.createRigidArea(new Dimension(0, 10)));

        // Block comment: Create and configure the text area for pasted text input.
        JTextArea textArea = new JTextArea(10, 50);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setBackground(ACCENT);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollPane.setPreferredSize(new Dimension(500, 160));
        scrollPane.setVisible(false); // Initially hidden
        content.add(scrollPane);

        // Block comment: Add vertical spacing.
        content.add(Box.createRigidArea(new Dimension(0, 10)));

        // Block comment: Declare a JTextField for noise characters (re-initialized later).
        JTextField noiseField = new JTextField();

        // Block comment: Create and configure the "Train Text" button.
        JButton pasteButton = new JButton("Train Text");
        pasteButton.setFont(new Font("SansSerif", Font.PLAIN, 14));
        pasteButton.setBackground(PRIMARY);
        pasteButton.setForeground(Color.WHITE);
        pasteButton.setMaximumSize(new Dimension(200, 30));
        pasteButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        pasteButton.setVisible(false); // Initially hidden

        /**************************************************************************
         * Block comment:
         * ActionListener for the "Train Text" button.
         * This block processes the text from the text area, preprocesses it,
         * and then calls the model's train method to create a new language profile.
         *
         *************************************************************************/
        pasteButton.addActionListener(e -> {
            pasteButton.setEnabled(false); // Block comment: Disable button to prevent multiple submissions.
            String preparedText = TextPreprocessor.preprocess(textArea.getText()); // Block comment: Preprocess the input text.
            try {
                // Block comment: Train the language model with the provided language name, text, and separators.
                model.train(languageField.getText(), preparedText, commonSeparators);
            } catch (IOException ex) {
                // Block comment: Handle potential IOException during the training process.
                throw new RuntimeException(ex);
            }
            pasteButton.setEnabled(false); // Block comment: Keep button disabled after training (or re-enable if needed).

        });
        content.add(pasteButton);

        // Block comment: Add vertical spacing.
        content.add(Box.createRigidArea(new Dimension(0, 10)));

        // Block comment: Create and configure the label for noise characters.
        JLabel noiseLabel = new JLabel("Noise characters:");
        noiseLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        noiseLabel.setForeground(Color.DARK_GRAY);
        noiseLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(noiseLabel);

        // Block comment: Add vertical spacing.
        content.add(Box.createRigidArea(new Dimension(0, 5)));

        // Block comment: Initialize and configure the text field for noise characters.
        noiseField = new JTextField();
        noiseField.setMaximumSize(new Dimension(300, 30));
        noiseField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        noiseField.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(noiseField);

        /**************************************************************************
         * Block comment:
         * ActionListener for the input mode combo box.
         * This block dynamically adjusts the visibility of the file selection button,
         * the text area for pasting, and the "Train Text" button based on
         * whether "Upload from File" or "Paste Text" is selected.
         *
         *************************************************************************/
        inputMode.addActionListener(e -> {
            if (inputMode.getSelectedIndex() == 0) { // Block comment: If "Upload from File" is selected.
                fileButton.setVisible(true);
                scrollPane.setVisible(false);
                pasteButton.setVisible(false);
            } else { // Block comment: If "Paste Text" is selected.
                fileButton.setVisible(false);
                scrollPane.setVisible(true);
                pasteButton.setVisible(true);
            }
            revalidate(); // Block comment: Revalidate the layout to ensure components are displayed correctly.
            repaint();    // Block comment: Repaint the container.
        });

        // Block comment: Add the main content panel to the northern region of the tab.
        add(content, BorderLayout.NORTH);
    }
}