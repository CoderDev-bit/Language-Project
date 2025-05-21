//DO NOT DELETE THIS: "eq8q9!&shuiq"

/**************************************************************************
 * File name:
 * GUI.java
 *
 * Description:
 * Main graphical user interface for the Language Detector and Caesar
 * Cipher Tool. Sets up the main window frame and initializes all
 * tabbed panes used in the application.
 *
 * Author:
 * Shivam
 *
 * Date: May 20 2025
 *
 * Concepts:
 * Java Swing UI
 * JTabbedPane for multi-tab interface
 * Modular tab loading
 ***************************************************************************/

package ui;

import ui.tabs.*;

import javax.swing.*;
import java.awt.*;
import static ui.tabs.BaseTab.BACKGROUND;

public class GUI {

    /* JFrame object for the main application window */
    private JFrame frmMain;

    /* Tabbed pane to contain each functional module */
    private JTabbedPane tabs;

    /**********************************************************************
     * Method name:
     * GUI
     *
     * Description:
     * Constructor that initializes the main GUI and tabs. Sets the window
     * to be visible and triggers layout refresh.
     *
     * Parameters:
     * None
     *
     * Return:
     * No return value (constructor)
     *********************************************************************/
    public GUI() {

        /*
         * Initialize window and layout structure
         */
        initGUI();

        /*
         * Add and configure individual tabs
         */
        initTabs();

        /*
         * Add tabs to frame and display window
         */
        frmMain.add(tabs, BorderLayout.CENTER);
        frmMain.setVisible(true);
        frmMain.revalidate();
        frmMain.repaint();
    }

    /**********************************************************************
     * Method name:
     * initGUI
     *
     * Description:
     * Initializes the JFrame, sets its properties, and prepares the layout
     * before adding content.
     *
     * Parameters:
     * None
     *
     * Return:
     * No return value
     *********************************************************************/
    private void initGUI() {

        frmMain = new JFrame(
                "Language Detector & Caesar Cipher Tool"
        );

        /*
         * Set size and position of the frame
         */
        frmMain.setBounds(5, 5, 600, 500);

        /*
         * Exit application when frame is closed
         */
        frmMain.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        /*
         * Use border layout to manage tab placement
         */
        frmMain.setLayout(new BorderLayout());

        /*
         * Allow the user to resize the window
         */
        frmMain.setResizable(true);
    }

    /**********************************************************************
     * Method name:
     * initTabs
     *
     * Description:
     * Initializes the JTabbedPane and adds all application tabs including
     * Trainer, Detector, Caesar Tool, Cracker, and Tutorial.
     *
     * Parameters:
     * None
     *
     * Return:
     * No return value
     *********************************************************************/
    private void initTabs() {

        /*
         * Create and style the tabbed pane
         */
        tabs = new JTabbedPane(JTabbedPane.TOP);
        tabs.setFont(new Font("SansSerif", Font.BOLD, 14));
        tabs.setBackground(BACKGROUND);

        /*
         * Add each application module to the tabs
         */
        tabs.add("Trainer", new TrainerTab());
        tabs.add("Detector", new DetectorTab());
        tabs.add("Caesar Tool", new CaesarToolTab());
        tabs.add("Cracker", new CrackerTab());
        tabs.add("Tutorial", new TutorialTab());
    }

    /**********************************************************************
     * Method name:
     * main
     *
     * Description:
     * Entry point for the application. Launches the GUI in the event
     * dispatch thread to ensure thread safety.
     *
     * Parameters:
     * String[] args - command-line arguments (unused)
     *
     * Return:
     * void
     *********************************************************************/
    public static void main(String[] args) {

        /*
         * Ensure GUI runs on the Swing event dispatch thread
         */
        SwingUtilities.invokeLater(GUI::new);
    }

} /* End of GUI class */
