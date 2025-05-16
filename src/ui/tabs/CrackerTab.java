package ui.tabs;

import javax.swing.*;
import java.awt.*;

public class CrackerTab extends BaseTab {

    public CrackerTab() {
        initTab();
    }

    public void initTab() {

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel label = new JLabel("Paste Caesar-encrypted text below:");
        label.setFont(new Font("SansSerif", Font.BOLD, 16));
        label.setForeground(PRIMARY);

        JTextArea cipherTextArea = new JTextArea();
        cipherTextArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        cipherTextArea.setLineWrap(true);
        cipherTextArea.setWrapStyleWord(true);
        cipherTextArea.setBackground(ACCENT);

        JButton crackButton = new JButton("Crack Caesar Cipher");
        crackButton.setFont(new Font("SansSerif", Font.PLAIN, 14));
        crackButton.setBackground(PRIMARY);
        crackButton.setForeground(Color.WHITE);

        JTextArea crackedOutput = new JTextArea();
        crackedOutput.setFont(new Font("Monospaced", Font.PLAIN, 14));
        crackedOutput.setEditable(false);
        crackedOutput.setBackground(ACCENT);

        add(label, BorderLayout.NORTH);
        add(new JScrollPane(cipherTextArea), BorderLayout.CENTER);
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBackground(BACKGROUND);
        bottom.add(crackButton, BorderLayout.NORTH);
        bottom.add(new JScrollPane(crackedOutput), BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);



    }

}
