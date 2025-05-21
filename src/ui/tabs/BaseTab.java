/**************************************************************************
 * File name:
 * BaseTab.java
 *
 * Description:
 * This abstract class provides a base structure for all GUI tabs in the
 * application. It sets up default styling and initializes a language
 * model using a remote database connection.
 *
 * Author:
 * Shivam
 *
 * Date: May 20 2025
 *
 * Concepts:
 * Abstract GUI base class
 * Supabase-backed language model initialization
 * Swing layout and styling
 ***************************************************************************/

package ui.tabs;

import lang.LanguageModel;
import util.LanguageDatabaseManager;
import util.TextPreprocessor;

import javax.swing.JPanel;
import java.awt.Color;
import java.io.IOException;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;

public abstract class BaseTab extends JPanel {

    /* Background color for tab content */
    public static final Color BACKGROUND = new Color(245, 248, 255);

    /* Primary UI color used for headers and buttons */
    protected static final Color PRIMARY = new Color(50, 100, 200);

    /* Accent color for text areas and highlights */
    protected static final Color ACCENT = new Color(230, 230, 255);

    /* Shared language model instance for NLP-based features */
    protected LanguageModel model;

    /**********************************************************************
     * Method name:
     * BaseTab
     *
     * Description:
     * Constructor for the base tab. It sets background color and margins,
     * and attempts to connect to the remote Supabase language database.
     * If successful, it initializes the shared language model.
     *
     * Parameters:
     * None
     *
     * Return:
     * No return value (constructor)
     *********************************************************************/
    public BaseTab() {

        /*
         * Set the background color of this JPanel
         */
        this.setBackground(BACKGROUND);

        /*
         * Set outer padding (top, left, bottom, right)
         */
        this.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        /*
         * Attempt to initialize LanguageDatabaseManager and LanguageModel
         */
        try {
            LanguageDatabaseManager dbInstance =
                    LanguageDatabaseManager.getDatabaseManager(
                            "https://frhgfmnvkopdwpiorszb.supabase.co",
                            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFz"
                                    + "ZSIsInJlZiI6ImZyaGdmbW52a29wZHdwaW9yc3piIiwicm9sZSI6InNlcn"
                                    + "ZpY2Vfcm9sZSIsImlhdCI6MTc0NjQ5MzI0OCwiZXhwIjoyMDYyMDY5MjQ4"
                                    + "fQ.bu7u6Doh9PMWGzpeROtFDm8qnSr5gk56m3vIDllMs7E"
                    );

            System.out.println("LanguageDatabaseManager initialized.");

            /*
             * Initialize the shared language model
             */
            model = new LanguageModel(dbInstance);

        } catch (IOException | IllegalStateException e) {

            /*
             * Log and print errors if connection or initialization fails
             */
            System.err.println(
                    "Failed to initialize LanguageDatabaseManager: "
                            + e.getMessage()
            );
            e.printStackTrace();
            return;
        }

    } /* End of BaseTab constructor */

    /**********************************************************************
     * Method name:
     * initTab
     *
     * Description:
     * Abstract method that must be implemented by all subclasses to
     * define tab-specific layout and component initialization.
     *
     * Parameters:
     * None
     *
     * Return:
     * No return value
     *********************************************************************/
    public abstract void initTab();

} /* End of BaseTab class */
