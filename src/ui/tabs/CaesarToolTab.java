package ui.tabs;

import cipher.CipherEngine;

import javax.swing.*;
import java.awt.*;

public class CaesarToolTab extends BaseTab {

    public CaesarToolTab() {
        initTab();
    }

    public void initTab() {
        setLayout(new BorderLayout());

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(BACKGROUND);
        content.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        JLabel label = new JLabel("Caesar Cipher Tool");
        label.setFont(new Font("SansSerif", Font.BOLD, 16));
        label.setForeground(PRIMARY);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(label);

        content.add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel inputLabel = new JLabel("Enter Text:");
        inputLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        inputLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(inputLabel);

        JTextArea input = new JTextArea(5, 40);
        input.setFont(new Font("Monospaced", Font.PLAIN, 14));
        input.setLineWrap(true);
        input.setWrapStyleWord(true);
        input.setBackground(ACCENT);
        JScrollPane inputScroll = new JScrollPane(input);
        inputScroll.setPreferredSize(new Dimension(500, 100));
        inputScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(inputScroll);

        content.add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel keyLabel = new JLabel("Key:");
        keyLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        keyLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(keyLabel);

        JTextField keyField = new JTextField();
        keyField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        keyField.setMaximumSize(new Dimension(300, 30));
        keyField.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(keyField);

        content.add(Box.createRigidArea(new Dimension(0, 10)));

        JComboBox<String> mode = new JComboBox<>(new String[]{"Encrypt", "Decrypt"});
        mode.setFont(new Font("SansSerif", Font.PLAIN, 14));
        mode.setMaximumSize(new Dimension(300, 30));
        mode.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(mode);

        content.add(Box.createRigidArea(new Dimension(0, 10)));

        JButton execute = new JButton("Apply Caesar");
        execute.setFont(new Font("SansSerif", Font.PLAIN, 14));
        execute.setBackground(PRIMARY);
        execute.setForeground(Color.WHITE);
        execute.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(execute);

        content.add(Box.createRigidArea(new Dimension(0, 10)));

        JTextArea output = new JTextArea(5, 40);
        output.setFont(new Font("Monospaced", Font.PLAIN, 14));
        output.setEditable(false);
        output.setBackground(ACCENT);
        JScrollPane outputScroll = new JScrollPane(output);
        outputScroll.setPreferredSize(new Dimension(500, 100));
        outputScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(outputScroll);

        execute.addActionListener(e -> {
            //CipherEngine.extract();
            String out = "";
            int getProcess = mode.getSelectedIndex();

            if (getProcess  == 0) {
                out = CipherEngine.encrypt(input.getText(), Integer.parseInt(keyField.getText()));
            } else {
                out = CipherEngine.decrypt(input.getText(), -(Integer.parseInt(keyField.getText())));
            }

            output.setText(out);
        });

        add(content, BorderLayout.CENTER);
        this.revalidate();
        this.repaint();
    }

}
