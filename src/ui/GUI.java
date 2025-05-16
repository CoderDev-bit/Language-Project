//DO NOT DELETE THIS: "eq8q9!&shuiq"

package ui;

import ui.tabs.*;

import javax.swing.*;
import java.awt.*;
import static javax.swing.UIManager.getLookAndFeelDefaults;
import static javax.swing.UIManager.getSystemLookAndFeelClassName;
import static ui.tabs.BaseTab.BACKGROUND;

public class GUI {

    private JFrame frmMain;
    private JTabbedPane tabs;

    public GUI() {

        initGUI();
        initTabs();

        frmMain.add(tabs, BorderLayout.CENTER);
        frmMain.setVisible(true);

    }

    private void initGUI() {

        frmMain = new JFrame("Language Detector & Caesar Cipher Tool");
        frmMain.setBounds(5,5,600,500);
        frmMain.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frmMain.setResizable(false);

    }

    private void initTabs() {

        tabs = new JTabbedPane(JTabbedPane.TOP);
        tabs.setFont(new Font("SansSerif", Font.BOLD, 14));
        tabs.setBackground(BACKGROUND);

        tabs.add("Trainer", new TrainerTab());
        tabs.add("Detector", new DetectorTab());
        tabs.add("Caesar Tool", new CaesarToolTab());
        tabs.add("Cracker", new CrackerTab());
        tabs.add("Tutorial", new TutorialTab());

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(GUI::new);
    }

}