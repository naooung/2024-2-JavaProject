import javax.swing.*;
import java.awt.*;

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

    public void changePanel(String name) {
        cardLayout.show(panel, name);
    }

    public static void main(String[] args) {
        MainFrame main = new MainFrame();
    }
}
class MainPanel extends JPanel {
    public MainPanel(MainFrame mainFrame) {
        setLayout(new BorderLayout());

        InfoPanel infoPanel = new InfoPanel(mainFrame); // 화면 전환을 위해 인자로 mainFrame 전달
        this.add(infoPanel, BorderLayout.NORTH);
        GamePanel gamePanel = new GamePanel(infoPanel);
        this.add(gamePanel, BorderLayout.CENTER);
    }
}