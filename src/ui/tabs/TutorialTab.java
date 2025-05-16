package ui.tabs;

import javax.swing.*;
import java.awt.*;

public class TutorialTab extends BaseTab{

    private static final String strVideoLink = "https://www.youtube.com/watch?v=8yh9BPUBbbQ";

    public TutorialTab() {
        initTab();
    }

    public void initTab() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel label = new JLabel("How to Use This Tool");
        label.setFont(new Font("SansSerif", Font.BOLD, 16));
        label.setForeground(PRIMARY);

        String tutorialMessage =
                "Welcome!\n" +
                        "\n" +
                        "1. Use the Trainer tab to load language files.\n" +
                        "2. Use the Detector to identify text language.\n" +
                        "3. Encrypt/Decrypt text in the Caesar Tool tab.\n" +
                        "4. Crack unknown Caesar ciphers in Cracker.\n" +
                        "\n" +
                        "This tool supports custom alphabets.\n";

        JTextArea tutorialText = new JTextArea(tutorialMessage);
        tutorialText.setFont(new Font("SansSerif", Font.PLAIN, 14));
        tutorialText.setEditable(false);
        tutorialText.setLineWrap(true);
        tutorialText.setWrapStyleWord(true);
        tutorialText.setBackground(ACCENT);

        add(label, BorderLayout.NORTH);
        add(new JScrollPane(tutorialText), BorderLayout.CENTER);

    }

}
