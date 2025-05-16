package ui.tabs;

import javax.swing.*;
import java.awt.*;

public class TrainerTab extends BaseTab{
    public TrainerTab() {
        initTab();
    }

    public void initTab() {

        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel label = new JLabel("Train a language profile:");
        label.setFont(new Font("SansSerif", Font.BOLD, 16));
        label.setForeground(PRIMARY);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        add(label);

        // Input mode selection
        String[] options = {"Upload from File", "Paste Text"};
        JComboBox<String> inputMode = new JComboBox<>(options);
        inputMode.setMaximumSize(new Dimension(300, 30));
        inputMode.setFont(new Font("SansSerif", Font.PLAIN, 14));
        inputMode.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(inputMode);
        add(Box.createRigidArea(new Dimension(0, 10)));

        // File button
        JButton fileButton = new JButton("Select File");
        fileButton.setFont(new Font("SansSerif", Font.PLAIN, 14));
        fileButton.setBackground(PRIMARY);
        fileButton.setForeground(Color.WHITE);
        fileButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(fileButton);
        add(Box.createRigidArea(new Dimension(0, 10)));

        // TextArea for pasted text
        JTextArea textArea = new JTextArea(10, 50);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setBackground(ACCENT);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollPane.setVisible(false);
        add(scrollPane);
        add(Box.createRigidArea(new Dimension(0, 10)));

        // "Paste Text" button (or label)
        JButton pasteButton = new JButton("Train Text");
        pasteButton.setFont(new Font("SansSerif", Font.PLAIN, 14));
        pasteButton.setBackground(PRIMARY);
        pasteButton.setForeground(Color.WHITE);
        pasteButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(pasteButton);
        add(Box.createRigidArea(new Dimension(0, 10)));

        // Add listener to toggle visibility
        inputMode.addActionListener(e -> {
            if (inputMode.getSelectedIndex() == 0) {
                fileButton.setVisible(true);
                scrollPane.setVisible(false);
                pasteButton.setVisible(false);
            } else {
                fileButton.setVisible(false);
                scrollPane.setVisible(true);
                pasteButton.setVisible(true);
            }
            revalidate();
            repaint();
        });

        // Add "Noise" input label and text field
        JLabel noiseLabel = new JLabel("Noise characters:");
        noiseLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        noiseLabel.setForeground(Color.DARK_GRAY);
        noiseLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        noiseLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        add(noiseLabel);

        JTextField noiseField = new JTextField();
        noiseField.setMaximumSize(new Dimension(300, 30));
        noiseField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        noiseField.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(noiseField);
    }
}
