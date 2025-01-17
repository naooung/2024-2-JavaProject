import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class StartPanel extends JPanel {
    ImageIcon background = new ImageIcon("images/StartPanel_background.png");
    JButton buttons[] = new JButton[3];
    public StartPanel(MainFrame mainFrame) {
        setLayout(null);

        String s[] = {"PLAY", "HOW TO PLAY", "EXIT"};
        Font font = new Font("Arial", Font.BOLD, 20);
        int y = 270;

        for (int i = 0; i < buttons.length; i++) {
            buttons[i] = new JButton(s[i]);
            buttons[i].setFont(font);
            buttons[i].setBackground(Color.white);
            buttons[i].setForeground(new Color(0x663300));
            buttons[i].setBounds(570, y, 180, 40);
            y += 50;
            this.add(buttons[i]);
        }
        buttons[0].addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // mainPanel로 전환
                MainPanel mainPanel = new MainPanel(mainFrame);
                mainFrame.addPanel(mainPanel, "MainPanel");
                mainFrame.changePanel("MainPanel");
            }
        });

        buttons[1].addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ImageIcon helpIcon = new ImageIcon("images/help.png");

                // JOptionPane에 이미지 포함
                JOptionPane.showMessageDialog(
                        StartPanel.this,
                        "",
                        "햄버거 타이쿤 게임 방법",
                        JOptionPane.INFORMATION_MESSAGE,
                        helpIcon
                );
            }
        });

        buttons[2].addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(background.getImage(), 0, 0, getWidth(), getHeight(), this);
    }
}