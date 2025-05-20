package ui.tabs;

import javax.swing.*;
import java.awt.*;

public class TrainerTab extends BaseTab{


    public TrainerTab() {
        initTab();
    }

    public void initTab() {
        // Outer layout with BorderLayout
        setLayout(new BorderLayout());

        // Inner panel with vertical BoxLayout
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(BACKGROUND);
        content.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        // Section title
        JLabel label = new JLabel("Train a language profile:");
        label.setFont(new Font("SansSerif", Font.BOLD, 16));
        label.setForeground(PRIMARY);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(label);

        content.add(Box.createRigidArea(new Dimension(0, 10)));

        // Input mode combo box
        JComboBox<String> inputMode = new JComboBox<>(new String[]{"Upload from File", "Paste Text"});
        inputMode.setMaximumSize(new Dimension(300, 30));
        inputMode.setFont(new Font("SansSerif", Font.PLAIN, 14));
        inputMode.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(inputMode);

        content.add(Box.createRigidArea(new Dimension(0, 10)));

        // File selection button
        JButton fileButton = new JButton("Select File");
        fileButton.setFont(new Font("SansSerif", Font.PLAIN, 14));
        fileButton.setBackground(PRIMARY);
        fileButton.setForeground(Color.WHITE);
        fileButton.setMaximumSize(new Dimension(200, 30));
        fileButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(fileButton);

        content.add(Box.createRigidArea(new Dimension(0, 10)));

        // Pasted text area
        JTextArea textArea = new JTextArea(10, 50);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setBackground(ACCENT);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollPane.setPreferredSize(new Dimension(500, 160));
        scrollPane.setVisible(false);
        content.add(scrollPane);

        content.add(Box.createRigidArea(new Dimension(0, 10)));

        // "Train" button
        JButton pasteButton = new JButton("Train Text");
        pasteButton.setFont(new Font("SansSerif", Font.PLAIN, 14));
        pasteButton.setBackground(PRIMARY);
        pasteButton.setForeground(Color.WHITE);
        pasteButton.setMaximumSize(new Dimension(200, 30));
        pasteButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        pasteButton.setVisible(false);
        content.add(pasteButton);

        content.add(Box.createRigidArea(new Dimension(0, 10)));

        // Noise label
        JLabel noiseLabel = new JLabel("Noise characters:");
        noiseLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        noiseLabel.setForeground(Color.DARK_GRAY);
        noiseLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(noiseLabel);

        content.add(Box.createRigidArea(new Dimension(0, 5)));

        // Noise field
        JTextField noiseField = new JTextField();
        noiseField.setMaximumSize(new Dimension(300, 30));
        noiseField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        noiseField.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(noiseField);

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

        // Add all to the main panel
        add(content, BorderLayout.NORTH);
    }
}
