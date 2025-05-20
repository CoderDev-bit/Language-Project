package ui.tabs;

import ui.GUI;

import javax.swing.*;
import java.awt.*;

public class DetectorTab extends BaseTab {

    public DetectorTab() {
        initTab();
    }

    public void initTab() {
        setLayout(new BorderLayout());

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(BACKGROUND);
        content.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        JLabel label = new JLabel("Detect Language");
        label.setFont(new Font("SansSerif", Font.BOLD, 16));
        label.setForeground(PRIMARY);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(label);

        content.add(Box.createRigidArea(new Dimension(0, 10)));

        JComboBox<String> inputMode = new JComboBox<>(new String[]{"Upload from File", "Paste Text"});
        inputMode.setFont(new Font("SansSerif", Font.PLAIN, 14));
        inputMode.setMaximumSize(new Dimension(300, 30));
        inputMode.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(inputMode);

        content.add(Box.createRigidArea(new Dimension(0, 10)));

        JButton fileButton = new JButton("Select File");
        fileButton.setFont(new Font("SansSerif", Font.PLAIN, 14));
        fileButton.setBackground(PRIMARY);
        fileButton.setForeground(Color.WHITE);
        fileButton.setMaximumSize(new Dimension(200, 30));
        fileButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(fileButton);

        JLabel pasteLabel = new JLabel("Paste text:");
        pasteLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        pasteLabel.setForeground(Color.DARK_GRAY);
        pasteLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        pasteLabel.setVisible(false);
        content.add(pasteLabel);

        JTextArea txtManualInput = new JTextArea(10, 50);
        txtManualInput.setFont(new Font("Monospaced", Font.PLAIN, 14));
        txtManualInput.setLineWrap(true);
        txtManualInput.setWrapStyleWord(true);
        txtManualInput.setBackground(ACCENT);
        JScrollPane scrollPane = new JScrollPane(txtManualInput);
        scrollPane.setPreferredSize(new Dimension(500, 150));
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollPane.setVisible(false);
        content.add(scrollPane);

        content.add(Box.createRigidArea(new Dimension(0, 10)));

        JButton detectButton = new JButton("Detect Language");
        detectButton.setFont(new Font("SansSerif", Font.PLAIN, 14));
        detectButton.setBackground(PRIMARY);
        detectButton.setForeground(Color.WHITE);
        detectButton.setMaximumSize(new Dimension(200, 30));
        detectButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(detectButton);

        content.add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel noiseLabel = new JLabel("Noise characters:");
        noiseLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        noiseLabel.setForeground(Color.DARK_GRAY);
        noiseLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(noiseLabel);

        content.add(Box.createRigidArea(new Dimension(0, 5)));

        JTextField noiseField = new JTextField();
        noiseField.setMaximumSize(new Dimension(300, 30));
        noiseField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        noiseField.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(noiseField);

        content.add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel resultLabel = new JLabel("Detected Language(s): ");
        resultLabel.setFont(new Font("SansSerif", Font.ITALIC, 14));
        resultLabel.setForeground(Color.DARK_GRAY);
        resultLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(resultLabel);

        JTextArea txtDetectedLanguages = new JTextArea(5, 40);
        txtDetectedLanguages.setFont(new Font("Monospaced", Font.PLAIN, 14));
        txtDetectedLanguages.setLineWrap(true);
        txtDetectedLanguages.setWrapStyleWord(true);
        txtDetectedLanguages.setEditable(false);
        txtDetectedLanguages.setBackground(ACCENT);
        JScrollPane scrollPane1 = new JScrollPane(txtDetectedLanguages);
        scrollPane1.setPreferredSize(new Dimension(500, 100));
        scrollPane1.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(scrollPane1);

        inputMode.addActionListener(e -> {
            boolean isFile = inputMode.getSelectedIndex() == 0;
            fileButton.setVisible(isFile);
            scrollPane.setVisible(!isFile);
            pasteLabel.setVisible(!isFile);
        });

        add(content, BorderLayout.NORTH);

    }
}
