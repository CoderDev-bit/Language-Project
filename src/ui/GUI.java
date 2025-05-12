package ui;

import javax.swing.*;
import java.awt.*;

public class GUI extends JFrame {

    public GUI() {
        setTitle("Language Detector & Caesar Cipher Tool");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create tabbed pane
        JTabbedPane tabbedPane = new JTabbedPane();

        // Add tabs
        tabbedPane.add("Trainer", createTrainerTab());
        tabbedPane.add("Detector", createDetectorTab());
        tabbedPane.add("Caesar Tool", createCaesarToolTab());
        tabbedPane.add("Cracker", createCrackerTab());
        tabbedPane.add("Tutorial", createTutorialTab());

        add(tabbedPane);
        setVisible(true);
    }

    private JPanel createTrainerTab() {
        JPanel panel = new JPanel(new BorderLayout());
        JTextArea textArea = new JTextArea("Load and train language profiles here.");
        JButton loadButton = new JButton("Load Training File");
        panel.add(new JLabel("Trainer"), BorderLayout.NORTH);
        panel.add(new JScrollPane(textArea), BorderLayout.CENTER);
        panel.add(loadButton, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createDetectorTab() {
        JPanel panel = new JPanel(new BorderLayout());
        JTextArea inputArea = new JTextArea("Paste text here to detect language.");
        JButton detectButton = new JButton("Detect Language");
        JLabel resultLabel = new JLabel("Detected Language: ");
        panel.add(new JLabel("Language Detector"), BorderLayout.NORTH);
        panel.add(new JScrollPane(inputArea), BorderLayout.CENTER);
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(detectButton, BorderLayout.WEST);
        bottom.add(resultLabel, BorderLayout.CENTER);
        panel.add(bottom, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createCaesarToolTab() {
        JPanel panel = new JPanel(new GridLayout(5, 1));
        JTextArea input = new JTextArea("Enter text to encrypt/decrypt.");
        JTextField keyField = new JTextField("Key (e.g., 3)");
        JComboBox<String> mode = new JComboBox<>(new String[]{"Encrypt", "Decrypt"});
        JButton execute = new JButton("Apply Caesar");
        JTextArea output = new JTextArea("Result here...");

        panel.add(new JLabel("Caesar Cipher Tool"));
        panel.add(new JScrollPane(input));
        panel.add(keyField);
        panel.add(mode);
        panel.add(execute);
        panel.add(new JScrollPane(output));
        return panel;
    }

    private JPanel createCrackerTab() {
        JPanel panel = new JPanel(new BorderLayout());
        JTextArea cipherTextArea = new JTextArea("Paste encrypted Caesar text here.");
        JButton crackButton = new JButton("Crack Caesar Cipher");
        JTextArea crackedOutput = new JTextArea("Cracked text will appear here.");
        panel.add(new JLabel("Cracker"), BorderLayout.NORTH);
        panel.add(new JScrollPane(cipherTextArea), BorderLayout.CENTER);
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(crackButton, BorderLayout.NORTH);
        bottom.add(new JScrollPane(crackedOutput), BorderLayout.CENTER);
        panel.add(bottom, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createTutorialTab() {
        JPanel panel = new JPanel(new BorderLayout());
        JTextArea tutorialText = new JTextArea("Welcome to the Language Detector & Caesar Cipher Tool.\nUse the tabs to train, detect, encrypt, and crack.");
        tutorialText.setEditable(false);
        panel.add(new JLabel("Tutorial"), BorderLayout.NORTH);
        panel.add(new JScrollPane(tutorialText), BorderLayout.CENTER);
        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(GUI::new);
    }
}
