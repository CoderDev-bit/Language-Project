package ui.tabs;

import javax.swing.*;
import java.awt.*;

public class TutorialTab extends BaseTab{

    public TutorialTab() {
        initTab();
    }

    public void initTab() {
        setLayout(new BorderLayout());

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(BACKGROUND);
        content.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        JLabel label = new JLabel("How to Use This Tool");
        label.setFont(new Font("SansSerif", Font.BOLD, 16));
        label.setForeground(PRIMARY);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(label);

        content.add(Box.createRigidArea(new Dimension(0, 10)));

        String tutorialMessage =
                "Welcome!\n" +
                        "\n" +
                        "1. Use the Trainer tab to load language files.\n" +
                        "2. Use the Detector to identify text language.\n" +
                        "3. Encrypt/Decrypt text in the Caesar Tool tab.\n" +
                        "4. Crack unknown Caesar ciphers in Cracker.\n" +
                        "\n" +
                        "This tool supports custom alphabets.\n" +
                        "Video Tutorial Link: https://drive.google.com/file/d/1nMJ8-4YEKyl56_NO6ESrbPrAr9FakGlN/view?usp=sharing\n";

        JTextArea tutorialText = new JTextArea(tutorialMessage);
        tutorialText.setFont(new Font("SansSerif", Font.PLAIN, 14));
        tutorialText.setEditable(false);
        tutorialText.setLineWrap(true);
        tutorialText.setWrapStyleWord(true);
        tutorialText.setBackground(ACCENT);
        JScrollPane tutorialScroll = new JScrollPane(tutorialText);
        tutorialScroll.setPreferredSize(new Dimension(500, 200));
        tutorialScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(tutorialScroll);

        add(content, BorderLayout.CENTER);
        this.revalidate();
        this.repaint();
    }

}
