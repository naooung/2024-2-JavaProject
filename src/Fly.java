import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

class Fly {
    private ImageIcon fly = new ImageIcon("images/fly.png");
    private int centerX = 415;
    private int centerY = 125;
    private int flyX, flyY;
    private double angle = 0;
    private double radius = 200;
    private Timer moveTimer;

    public Fly() {
        randomFlyLocation();
        startTimer();
    }

    public void randomFlyLocation() {
        Random random = new Random();

        int[] possibleX = {50, 750, 415, 415};   // 왼쪽, 오른쪽, 상단, 하단
        int[] possibleY = {125, 125, 50, 350};   // 왼쪽, 오른쪽, 상단, 하단

        int index = random.nextInt(possibleX.length);
        flyX = possibleX[index];
        flyY = possibleY[index];

        // 초기 각도 설정: 파리가 도착 지점을 향하도록 설정
        angle = Math.atan2(125 - flyY, 415 - flyX);
    }

    public void moveFly() {
        flyX = (int)(centerX + radius * Math.cos(angle));
        flyY = (int)(centerY + radius * Math.sin(angle));

        angle += Math.PI / 32; // 각도를 조금씩 증가 (시계 방향 회전)
        radius -= 2;           // 반지름을 줄여서 안쪽으로 이동

        if (radius <= 5)
            stopTimer();
    }

    public void drawFly(Graphics g) {
        g.drawImage(fly.getImage(), flyX - 15, flyY - 15, 30, 30, null);
    }

    private void startTimer() {
        moveTimer = new Timer(50, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                moveFly();
            }
        });
        moveTimer.start();
    }

    public void stopTimer() {
        moveTimer.stop();
    }

    public boolean isArrived() {
        return radius <= 5;
    }

    // 파리의 크기에 맞춰서 비교
    public boolean isClicked(int mouseX, int mouseY) {
        if (Math.abs(mouseX - flyX) < 15 && Math.abs(mouseY - flyY) < 15)
            return true;
        return false;
    }
}
