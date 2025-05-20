package ui.tabs;

import cipher.CipherEngine;

import javax.swing.*;
import java.awt.*;

public class CrackerTab extends BaseTab {

    public CrackerTab() {
        initTab();
    }

    public void initTab() {
        setLayout(new BorderLayout());

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(BACKGROUND);
        content.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        JLabel label = new JLabel("Paste Caesar-encrypted text below:");
        label.setFont(new Font("SansSerif", Font.BOLD, 16));
        label.setForeground(PRIMARY);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(label);

        content.add(Box.createRigidArea(new Dimension(0, 10)));

        JTextArea cipherTextArea = new JTextArea(5, 40);
        cipherTextArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        cipherTextArea.setLineWrap(true);
        cipherTextArea.setWrapStyleWord(true);
        cipherTextArea.setBackground(ACCENT);
        JScrollPane cipherScroll = new JScrollPane(cipherTextArea);
        cipherScroll.setPreferredSize(new Dimension(500, 100));
        cipherScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(cipherScroll);

        content.add(Box.createRigidArea(new Dimension(0, 10)));

        JButton crackButton = new JButton("Crack Caesar Cipher");
        crackButton.setFont(new Font("SansSerif", Font.PLAIN, 14));
        crackButton.setBackground(PRIMARY);
        crackButton.setForeground(Color.WHITE);
        crackButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(crackButton);
/*
        crackButton.addActionListener(e -> {
            //CipherEngine.extract();

            CipherEngine.

        });
 */
        content.add(Box.createRigidArea(new Dimension(0, 10)));

        JTextArea crackedOutput = new JTextArea(5, 40);
        crackedOutput.setFont(new Font("Monospaced", Font.PLAIN, 14));
        crackedOutput.setEditable(false);
        crackedOutput.setBackground(ACCENT);
        JScrollPane outputScroll = new JScrollPane(crackedOutput);
        outputScroll.setPreferredSize(new Dimension(500, 100));
        outputScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(outputScroll);

        add(content, BorderLayout.CENTER);
        this.revalidate();
        this.repaint();
    }

}
