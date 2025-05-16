package ui.tabs;

import ui.GUI;

import javax.swing.*;
import java.awt.*;

public class DetectorTab extends BaseTab {

    public DetectorTab() {
        initTab();
    }

    public void initTab() {
        //JPanel outerPanel = new JPanel();
       setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        JLabel label = new JLabel("Detect Language");
        label.setFont(new Font("SansSerif", Font.BOLD, 16));
        label.setForeground(PRIMARY);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        JComboBox<String> inputMode = new JComboBox<>(new String[]{"Upload from File", "Paste Text"});
        inputMode.setFont(new Font("SansSerif", Font.PLAIN, 14));
        inputMode.setMaximumSize(new Dimension(300, 30));
        inputMode.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton fileButton = new JButton("Select File");
        fileButton.setFont(new Font("SansSerif", Font.PLAIN, 14));
        fileButton.setBackground(PRIMARY);
        fileButton.setForeground(Color.WHITE);
        fileButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        fileButton.setVisible(true);

        JLabel pasteLabel = new JLabel("Paste text:");
        pasteLabel.setVisible(false);
        pasteLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        pasteLabel.setForeground(Color.DARK_GRAY);
        pasteLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextArea txtManualInput = new JTextArea(10, 50);
        txtManualInput.setFont(new Font("Monospaced", Font.PLAIN, 14));
        txtManualInput.setLineWrap(true);
        txtManualInput.setWrapStyleWord(true);
        txtManualInput.setBackground(ACCENT);
        JScrollPane scrollPane = new JScrollPane(txtManualInput);
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollPane.setVisible(false);

        // Detected languages text area
        JTextArea txtDetectedLanguages = new JTextArea(10, 10);
        txtDetectedLanguages.setFont(new Font("Monospaced", Font.PLAIN, 14));
        txtDetectedLanguages.setLineWrap(true);
        txtDetectedLanguages.setWrapStyleWord(true);
        txtDetectedLanguages.setEditable(false);
        txtDetectedLanguages.setBackground(ACCENT);
        JScrollPane scrollPane1 = new JScrollPane(txtDetectedLanguages);
        scrollPane1.setAlignmentX(Component.RIGHT_ALIGNMENT);
        scrollPane1.setVisible(false);


        JButton detectButton = new JButton("Detect Language");
        detectButton.setFont(new Font("SansSerif", Font.PLAIN, 14));
        detectButton.setBackground(PRIMARY);
        detectButton.setForeground(Color.WHITE);
        detectButton.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Add noise characters label and text field
        JLabel noiseLabel = new JLabel("Noise characters:");
        noiseLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        noiseLabel.setForeground(Color.DARK_GRAY);
        noiseLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        noiseLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JTextField noiseField = new JTextField();

        Dimension fieldSize = new Dimension(300, 30);
        noiseField.setMaximumSize(fieldSize);
        noiseField.setPreferredSize(fieldSize);

        noiseField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        noiseField.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel resultLabel = new JLabel("Detected Language(s): ");
        resultLabel.setFont(new Font("SansSerif", Font.ITALIC, 14));
        resultLabel.setForeground(Color.DARK_GRAY);
        resultLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        resultLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        inputMode.addActionListener(e -> {
            boolean isFile = inputMode.getSelectedIndex() == 0;
            fileButton.setVisible(isFile);
            scrollPane.setVisible(!isFile);
            pasteLabel.setVisible(!isFile);
        });

        panel.add(label);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(inputMode);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(fileButton);
        panel.add(pasteLabel);
        panel.add(scrollPane);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(detectButton);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Add noise label and text field components
        noiseLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0)); // ensure this stays
        panel.add(noiseLabel);

        noiseField.setMaximumSize(new Dimension(300, 30)); // ensure this stays
        panel.add(noiseField);

        panel.add(Box.createRigidArea(new Dimension(0, 10))); // keep consistent spacing after field

        panel.add(resultLabel);

        add(panel, BorderLayout.NORTH);

    }
}
