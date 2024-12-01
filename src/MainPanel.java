import javax.swing.*;
import java.awt.*;

class MainPanel extends JPanel {
    public MainPanel(MainFrame mainFrame) {
        setLayout(new BorderLayout());

        InfoPanel infoPanel = new InfoPanel(mainFrame); // 화면 전환을 위해 인자로 mainFrame 전달
        this.add(infoPanel, BorderLayout.NORTH);
        GamePanel gamePanel = new GamePanel(infoPanel);
        this.add(gamePanel, BorderLayout.CENTER);
    }
}
