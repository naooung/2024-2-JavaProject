import javax.swing.*;

class Emotion {
    private ImageIcon[] faces = {
            new ImageIcon("images/yellowface.png"),
            new ImageIcon("images/orangeface.png"),
            new ImageIcon("images/redface.png")
    };
    private int gauge = 0;
    private ImageIcon currentEmotion;
    GamePanel gamePanel;

    public Emotion(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        currentEmotion = faces[0];
    }

    public int getGauge() {
        return gauge;
    }
    public void checkTime(int time) {
        if (time == 60)
            plusGauge(25);
        else if (time == 30)
            plusGauge(25);
    }

    public void plusGauge(int n) {
        gauge += n;
        if (gauge >= 50)
            changeface(2);
        else if (gauge >= 25)
            changeface(1);
        else
            changeface(0);
    }

    public void changeface(int n) {
        currentEmotion = faces[n];
    }

    public ImageIcon getCurrentEmotion() {
        return currentEmotion;
    }
}