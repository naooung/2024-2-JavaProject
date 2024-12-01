import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MainFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel panel;
    public MainFrame() {
        setTitle("Final Project");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        cardLayout = new CardLayout();
        panel = new JPanel(cardLayout);

        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension screenSize = kit.getScreenSize();
        setLocation((screenSize.width - this.getWidth()) / 2, (screenSize.height - this.getHeight()) / 2);
        setResizable(true);

        StartPanel startPanel = new StartPanel(this);
        panel.add(startPanel, "StartPanel");

        this.add(panel);
        setResizable(false);
        setVisible(true);
    }
    public void addPanel(JPanel newPanel, String name) {
        panel.add(newPanel, name);
    }

    public void switchPanel(String name) {
        cardLayout.show(panel, name);
    }

    public static void main(String[] args) {
        MainFrame main = new MainFrame();
    }
}