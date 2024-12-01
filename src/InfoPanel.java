import javax.swing.*;
import java.awt.*;

class InfoPanel extends JPanel {
    private MainFrame mainFrame;
    private JLabel completedOrderLabel;
    private int completedOrder = 0;
    private JLabel raputationLabel;
    private int raputation = 100;
    private GameTime gameTime;
    private JLabel timerLabel;

    public InfoPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.setLayout(new BorderLayout());
        this.setBackground(Color.WHITE);
        Font font = new Font(Font.DIALOG, Font.BOLD, 17);

        JPanel westPanel = new JPanel();
        westPanel.setBackground(Color.WHITE);
        westPanel.setLayout(new GridLayout(2, 1));
        completedOrderLabel = new JLabel("완성한 주문: " + completedOrder + " / 10");
        completedOrderLabel.setFont(font);
        westPanel.add(completedOrderLabel);
        raputationLabel = new JLabel("평판: " + raputation + " / 100");
        raputationLabel.setFont(font);
        westPanel.add(raputationLabel);
        this.add(westPanel, BorderLayout.WEST);

        timerLabel = new JLabel("소요시간: 0초");
        timerLabel.setFont(font);
        timerLabel.setHorizontalAlignment(JLabel.CENTER);
        gameTime = new GameTime();
        gameTime.startTimer(timerLabel);
        this.add(timerLabel, BorderLayout.CENTER);

        this.add(gameTime.getPauseButton(), BorderLayout.EAST);
    }

    public void plusCompletedOrder() {
        completedOrder++;
        completedOrderLabel.setText("완성한 주문: " + completedOrder + " / 10");
        if (completedOrder == 10) {
            endGame(1); // 10개 완성시 GAME CLEAR 처리
        }
    }
    public int getCompletedOrder() { return completedOrder; }
    public void plusRaputation(int n) {
        if (raputation + n <= 100) {
            this.raputation += n;
            raputationLabel.setText("평판: " + raputation + " / 100");
        }
    }
    public void minusRaputation(int n) {
        if (raputation - n > 0) {
            this.raputation -= n;
            raputationLabel.setText("평판: " + raputation + " / 100");
        }
        else if (raputation - n <= 0) {
            endGame(0); // GAME OVER 처리
        }
    }
    public int getRaputation() { return raputation; }

    public void endGame(int n) {
        int finishTime = gameTime.finishTime();
        EndPanel endPanel = new EndPanel(mainFrame, n, finishTime);
        mainFrame.addPanel(endPanel, "EndPanel");
        mainFrame.switchPanel("EndPanel");
    }
}