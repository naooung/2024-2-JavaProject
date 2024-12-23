import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class EndPanel extends JPanel {
    private ImageIcon resultIcon;
    private ImageIcon gameclear = new ImageIcon("images/gameclear.png");
    private ImageIcon gameover = new ImageIcon("images/gameover.png");
    private JLabel resultLabel = new JLabel();
    private JButton restartButton;
    private JButton exitButton;
    public EndPanel(MainFrame mainFrame, int n, int finishTime) {
        setLayout(null);
        Font font = new Font("Arial", Font.BOLD, 50);
        // GAME OVER
        if (n == 0) {
            resultIcon = gameover;
            resultLabel.setText("PLAY AGAIN?");
        }
        // GAME CLEAR
        else if (n == 1) {
            resultIcon = gameclear;
            int minutes = 0;
            int seconds = 0;
            if (finishTime < 60) {
                seconds = finishTime;
            } else {
                minutes = finishTime / 60;
                seconds = finishTime % 60;
            }
            resultLabel.setText("GAME CLEAR  " + minutes + ":" + seconds);
        }
        resultLabel.setFont(font);
        resultLabel.setHorizontalAlignment(SwingConstants.CENTER);
        resultLabel.setBounds(100, 270, 600, 100);
        this.add(resultLabel);

        Font buttonFont = new Font("Arial", Font.BOLD, 20);
        restartButton = new JButton("RESTART");
        restartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainFrame.changePanel("StartPanel");
            }
        });
        restartButton.setBounds(50, 380, 300, 40);
        restartButton.setBackground(Color.WHITE);
        restartButton.setFont(buttonFont);
        this.add(restartButton);

        exitButton = new JButton("EXIT");
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        exitButton.setBounds(450, 380, 300, 40);
        exitButton.setBackground(Color.WHITE);
        exitButton.setFont(buttonFont);
        this.add(exitButton);
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(resultIcon.getImage(), 275, 30, 250, 250, this);
    }
}