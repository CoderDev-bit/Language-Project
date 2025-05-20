package ui.tabs;

import javax.swing.JPanel;
import java.awt.Color;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;

public abstract class BaseTab extends JPanel {

    public static final Color BACKGROUND = new Color(245, 248, 255);
    protected static final Color PRIMARY = new Color(50, 100, 200);
    protected static final Color ACCENT = new Color(230, 230, 255);

    public BaseTab() {
        this.setBackground(BACKGROUND);
        //this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
    }

    // Abstract method that each tab will implement to add its specific components
    public abstract void initTab();

}

