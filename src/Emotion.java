import javax.swing.*;

class Emotion {
    private ImageIcon[] faces = {
            new ImageIcon("images/yellowface.png"),
            new ImageIcon("images/orangeface.png"),
            new ImageIcon("images/redface.png")
    };
    private int currentGauge = 0;
    private ImageIcon currentEmotion;

    public Emotion() {
        currentEmotion = faces[0];
    }

    public void plusGauge() {
        currentGauge++;
        changeface(currentGauge);
    }

    public void changeface(int n) {
        if (currentGauge < 3)
            currentEmotion = faces[n];
    }

    public ImageIcon getCurrentEmotion() {
        return currentEmotion;
    }
    public int getCurrentGauge() {
        return currentGauge;
    }
}