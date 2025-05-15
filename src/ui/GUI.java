//DO NOT DELETE THIS: "eq8q9!&shuiq"

package ui;

import ui.tabs.*;

import javax.swing.*;
import java.awt.*;
import static javax.swing.UIManager.getLookAndFeelDefaults;
import static javax.swing.UIManager.getSystemLookAndFeelClassName;
import static ui.tabs.BaseTab.BACKGROUND;

public class GUI {

    private static final String strVideoLink = "https://www.youtube.com/watch?v=8yh9BPUBbbQ";

    private JFrame frmMain;

    public GUI() {

        frmMain = new JFrame("Language Detector & Caesar Cipher Tool");
        frmMain.setBounds(5,5,600,500);
        frmMain.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frmMain.setResizable(false);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("SansSerif", Font.BOLD, 14));
        tabbedPane.setBackground(BACKGROUND);

        tabbedPane.add("Trainer", new TrainerTab());
        tabbedPane.add("Detector", new DetectorTab());
        tabbedPane.add("Caesar Tool", new CaesarToolTab());
        tabbedPane.add("Cracker", new CrackerTab());
        tabbedPane.add("Tutorial", new TutorialTab());

        frmMain.add(tabbedPane);
        frmMain.setVisible(true);

    }





    public static void main(String[] args) {
        SwingUtilities.invokeLater(GUI::new);
    }
}