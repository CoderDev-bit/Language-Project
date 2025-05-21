/**************************************************************************
 * File name:
 * DetectorTab.java
 *
 * Description:
 * This file defines the DetectorTab class, which represents a user interface tab
 * for language detection. It allows users to either upload text from a file
 * or paste text directly to detect its language.
 *
 * Author:
 * Shivam Patel
 *
 * Date: May 20 2025
 *
 * Concepts:
 * Swing GUI components (JPanel, JLabel, JComboBox, JButton, JTextArea, JScrollPane)
 * Event handling (ActionListener)
 * File input/output (though not fully implemented in the provided snippet)
 * Text preprocessing and language model integration
 *
 *************************************************************************/
package ui.tabs;

import util.TextPreprocessor;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DetectorTab extends BaseTab {

    /**************************************************************************
     * Method name:
     * DetectorTab
     *
     * Description:
     * Constructor for the DetectorTab class.
     * Initializes the user interface components of the tab.
     *
     * Author:
     * Shivam Patel
     *
     * Date: May 20 2025
     *
     *************************************************************************/
    public DetectorTab() {
        initTab();
    }

    /**************************************************************************
     * Method name:
     * initTab
     *
     * Description:
     * Initializes and lays out all the Swing components for the language detection tab.
     * This includes setting up input fields, buttons, and display areas.
     *
     * Author:
     * Shivam Patel
     *
     * Date: May 20 2025
     *
     *************************************************************************/
    public void initTab() {
        // Block comment: Set the layout manager for the main tab panel.
        setLayout(new BorderLayout());

        // Block comment: Create and configure the content panel for organizing components.
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(BACKGROUND);
        content.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        // Block comment: Create and configure the main title label.
        JLabel label = new JLabel("Detect Language");
        label.setFont(new Font("SansSerif", Font.BOLD, 16));
        label.setForeground(PRIMARY);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(label);

        // Block comment: Add vertical spacing.
        content.add(Box.createRigidArea(new Dimension(0, 10)));

        // Block comment: Create and configure the input mode selection dropdown.
        JComboBox<String> inputMode = new JComboBox<>(new String[]{"Upload from File", "Paste Text"});
        inputMode.setFont(new Font("SansSerif", Font.PLAIN, 14));
        inputMode.setMaximumSize(new Dimension(300, 30));
        inputMode.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(inputMode);

        // Block comment: Add vertical spacing.
        content.add(Box.createRigidArea(new Dimension(0, 10)));

        // Block comment: Create and configure the file selection button.
        JButton fileButton = new JButton("Select File");
        fileButton.setFont(new Font("SansSerif", Font.PLAIN, 14));
        fileButton.setBackground(PRIMARY);
        fileButton.setForeground(Color.WHITE);
        fileButton.setMaximumSize(new Dimension(200, 30));
        fileButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(fileButton);

        // Block comment: Create and configure the label for paste text input.
        JLabel pasteLabel = new JLabel("Paste text:");
        pasteLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        pasteLabel.setForeground(Color.DARK_GRAY);
        pasteLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        pasteLabel.setVisible(false); // Initially hidden
        content.add(pasteLabel);

        // Block comment: Create and configure the text area for manual text input.
        JTextArea txtManualInput = new JTextArea(10, 50);
        txtManualInput.setFont(new Font("Monospaced", Font.PLAIN, 14));
        txtManualInput.setLineWrap(true);
        txtManualInput.setWrapStyleWord(true);
        txtManualInput.setBackground(ACCENT);
        JScrollPane scrollPane = new JScrollPane(txtManualInput);
        scrollPane.setPreferredSize(new Dimension(500, 150));
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollPane.setVisible(false); // Initially hidden
        content.add(scrollPane);

        // Block comment: Add vertical spacing.
        content.add(Box.createRigidArea(new Dimension(0, 10)));

        // Block comment: Initialize the text area for displaying detected languages.
        final JTextArea txtDetectedLanguages = new JTextArea(5, 40);

        // Block comment: Create and configure the language detection button.
        JButton detectButton = new JButton("Detect Language");
        detectButton.setFont(new Font("SansSerif", Font.PLAIN, 14));
        detectButton.setBackground(PRIMARY);
        detectButton.setForeground(Color.WHITE);
        detectButton.setMaximumSize(new Dimension(200, 30));
        detectButton.setAlignmentX(Component.LEFT_ALIGNMENT);

        /**************************************************************************
         * Block comment:
         * ActionListener for the "Detect Language" button.
         * This block handles the logic for processing the input text,
         * calling the language model, and displaying the detection results.
         *
         *************************************************************************/
        detectButton.addActionListener(e -> {
            // Block comment: Preprocess the text from the manual input text area.
            String preparedText = TextPreprocessor.preprocess(txtManualInput.getText());
            try {
                // Block comment: Call the language model to analyze the prepared text.
                HashMap<String, Double> results = model.analyze(preparedText);

                // Block comment: Calculate the total score of all detected languages.
                double totalScore = 0.0;
                for (Double score : results.values()) {
                    totalScore += score;
                }

                // Block comment: Handle the case where no language is detected.
                if (totalScore == 0) {
                    txtDetectedLanguages.setText("No language detected.");
                    return;
                }

                // Block comment: Build the output string for detected languages and their percentages.
                StringBuilder output = new StringBuilder();

                for (Map.Entry<String, Double> entry : results.entrySet()) {
                    String language = entry.getKey();
                    Double rawScore = entry.getValue();

                    // Block comment: Calculate the percentage of each detected language.
                    double percentage = (rawScore / totalScore) * 100;

                    output.append(language)
                            .append(" (")
                            .append(String.format("%.4f", percentage))
                            .append("%)\n");
                }

                // Block comment: Display the detected languages in the results text area.
                txtDetectedLanguages.setText(output.toString());

            } catch (IOException ex) {
                // Block comment: Handle potential IOException during model analysis.
                throw new RuntimeException(ex);
            }

        });

        content.add(detectButton);

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

        // Block comment: Create and configure the text field for noise characters.
        JTextField noiseField = new JTextField();
        noiseField.setMaximumSize(new Dimension(300, 30));
        noiseField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        noiseField.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(noiseField);

        // Block comment: Add vertical spacing.
        content.add(Box.createRigidArea(new Dimension(0, 10)));

        // Block comment: Create and configure the label for results.
        JLabel resultLabel = new JLabel("Detected Language(s): ");
        resultLabel.setFont(new Font("SansSerif", Font.ITALIC, 14));
        resultLabel.setForeground(Color.DARK_GRAY);
        resultLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(resultLabel);

        // Block comment: Configure the display area for detected languages.
        txtDetectedLanguages.setFont(new Font("Monospaced", Font.PLAIN, 14));
        txtDetectedLanguages.setLineWrap(true);
        txtDetectedLanguages.setWrapStyleWord(true);
        txtDetectedLanguages.setEditable(false); // Make it read-only
        txtDetectedLanguages.setBackground(ACCENT);
        JScrollPane scrollPane1 = new JScrollPane(txtDetectedLanguages);
        scrollPane1.setPreferredSize(new Dimension(500, 100));
        scrollPane1.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(scrollPane1);

        /**************************************************************************
         * Block comment:
         * ActionListener for the input mode dropdown.
         * This block dynamically shows or hides the file selection button
         * or the text input area based on the user's selection.
         *
         *************************************************************************/
        inputMode.addActionListener(e -> {
            // Block comment: Check if "Upload from File" is selected.
            boolean isFile = inputMode.getSelectedIndex() == 0;
            fileButton.setVisible(isFile);
            scrollPane.setVisible(!isFile); // Show/hide text area scroll pane
            pasteLabel.setVisible(!isFile); // Show/hide paste label
        });

        // Block comment: Add the content panel to the northern region of the tab.
        add(content, BorderLayout.NORTH);
    }
}