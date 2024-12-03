import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class GameTime {
    private int time;
    private Timer timer;

    // timer를 시작하고 1초마다 label을 변경하는 메소드
    public void startTimer(JLabel timerLabel) {
        time = 0;
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                time++;
                if (time < 60)
                    timerLabel.setText("00:" + time);
                else {
                    int minutes = time / 60;
                    int seconds = time % 60;
                    timerLabel.setText(minutes + ":" + seconds);
                }
            }
        });
        timer.start();
    }

    // 게임을 일시정지하는 버튼을 반환하는 메소드
    public JButton getPauseButton() {

        JButton pauseButton = new JButton(new ImageIcon("images/pause.png"));
        pauseButton.setBorderPainted(false);
        pauseButton.setContentAreaFilled(false);

        pauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                timer.stop();
                int n = JOptionPane.showConfirmDialog
                        (null, "다시 시작하려면 누르세요.", "일시정지됨",
                                JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE);

                if (n == 0 || n == -1)
                    timer.start();
            }
        });
        return pauseButton;
    }

    public int getTime() {
        return time;
    }

    // 게임이 종료되었을 때 종료 시간을 반환하는 메소드
    public int finishTime() {
        timer.stop();
        return time;
    }
}