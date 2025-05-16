package ui.tabs;

import javax.swing.*;
import java.awt.*;

public class CaesarToolTab extends BaseTab {
    public CaesarToolTab() {
        initTab();
    }

    public void initTab() {

        JLabel label = new JLabel("Caesar Cipher Tool");
        label.setFont(new Font("SansSerif", Font.BOLD, 16));
        label.setForeground(PRIMARY);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextArea input = new JTextArea(5, 40);
        input.setFont(new Font("Monospaced", Font.PLAIN, 14));
        input.setLineWrap(true);
        input.setWrapStyleWord(true);
        input.setBackground(ACCENT);
        JScrollPane inputScroll = new JScrollPane(input);

        JTextField keyField = new JTextField();
        keyField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        keyField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JComboBox<String> mode = new JComboBox<>(new String[]{"Encrypt", "Decrypt"});
        mode.setFont(new Font("SansSerif", Font.PLAIN, 14));
        mode.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JButton execute = new JButton("Apply Caesar");
        execute.setFont(new Font("SansSerif", Font.PLAIN, 14));
        execute.setBackground(PRIMARY);
        execute.setForeground(Color.WHITE);

        JTextArea output = new JTextArea(5, 40);
        output.setFont(new Font("Monospaced", Font.PLAIN, 14));
        output.setEditable(false);
        output.setBackground(ACCENT);
        JScrollPane outputScroll = new JScrollPane(output);

        add(label);
        add(Box.createRigidArea(new Dimension(0, 10)));
        JLabel inputLabel = new JLabel("Enter Text:");
        inputLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        inputLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(inputLabel);
        inputScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(inputScroll);
        add(Box.createRigidArea(new Dimension(0, 10)));
        JLabel keyLabel = new JLabel("Key:");
        keyLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        keyLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(keyLabel);
        keyField.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(keyField);
        add(Box.createRigidArea(new Dimension(0, 10)));
        mode.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(mode);
        add(Box.createRigidArea(new Dimension(0, 10)));
        execute.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(execute);
        add(Box.createRigidArea(new Dimension(0, 10)));
        outputScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(outputScroll);


    }
}
