import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

class GamePanel extends JPanel {
    private InfoPanel infoPanel;
    private ImageIcon background = new ImageIcon("images/background.png");
    private Menu menu;
    private Customer customers[];
    private Emotion emotions[];
    private JButton submitButtons[];
    private User user;
    private Point startdragged;
    private Ingredient draggedIngredient = null;

    public GamePanel(InfoPanel infoPanel) {
        this.infoPanel = infoPanel;

        setLayout(null);
        menu = new Menu();
        user = new User();
        customers = new Customer[3];
        emotions = new Emotion[3];
        submitButtons = new JButton[3];

        for (int i = 0; i < 3; i++) {
            customers[i] = new Customer();
            JButton customerButton = customers[i].getCustomerButton(i);
            submitButtons[i] = getSubmitButton(i);

            emotions[i] = new Emotion(this);
            int customerNumber = i;
            Timer timer = new Timer(1000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    emotions[customerNumber].checkTime(customers[customerNumber].getWaitingTime());
                    repaint();
                    // redface라면 평판 -20, customer과 face 갱신
                    if (emotions[customerNumber].getGauge() >= 50) {
                        infoPanel.minusRaputation(20);
                    }
                }
            });
            timer.start();

            switch (i) {
                case 0:
                    customers[i].setCustomerButtonLocation(100, 300);
                    setSubmitButtonLocation(i, 140, 265);
                    break;
                case 1:
                    customers[i].setCustomerButtonLocation(300, 300);
                    setSubmitButtonLocation(i, 340, 265);
                    break;
                case 2:
                    customers[i].setCustomerButtonLocation(500, 300);
                    setSubmitButtonLocation(i, 540, 265);
                    break;
            }
            add(customerButton);
            add(submitButtons[i]);
            setSubmitButton(i);
        }

        // 드래그 시작할 때 눌린 곳이 재료의 좌표가 맞는지 확인하여 드래그 중인 재료 설정
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                startdragged = e.getPoint();
                for (Ingredient ingredient : menu.ingredients) {
                    if (ingredient.getX() <= e.getX() &&
                            ingredient.getX() + ingredient.getIcon().getIconWidth() >= e.getX() &&
                            ingredient.getY() <= e.getY() &&
                            ingredient.getY() + ingredient.getIcon().getIconHeight() >= e.getY()) {
                        draggedIngredient = ingredient;

                        // 이미지의 중앙점 계산
                        int centerX = ingredient.getX() + ingredient.icon.getIconWidth() / 2;
                        int centerY = ingredient.getY() + ingredient.icon.getIconHeight() / 2;

                        // 드래그할 재료를 클릭한 위치의 중앙으로 이동
                        int offsetX = startdragged.x - centerX;
                        int offsetY = startdragged.y - centerY;

                        draggedIngredient.setX(ingredient.getX() + offsetX);
                        draggedIngredient.setY(ingredient.getY() + offsetY);
                        break;
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (draggedIngredient != null) {
                    // 드래그가 끝났을 때 범위 확인하여 제작 접시라면
                    if (e.getX() >= 370 && e.getX() <= 460 && e.getY() >= 105 && e.getY() <= 155) {
                        ArrayList<Ingredient> ingredientList = new ArrayList<>();
                        // 드래그되지 않은 재료만 배열로 변환하여 menu에 설정
                        for (Ingredient ingredient : menu.ingredients) {
                            if (ingredient != draggedIngredient) {
                                ingredientList.add(ingredient);
                            } else { // user에서 사용자가 추가한 재료 추가
                                user.addedIngredient.add(draggedIngredient);
                            }
                        }
                        menu.ingredients = ingredientList.toArray(new Ingredient[0]);
                    } else // 아니라면 원래 위치로 이동
                        draggedIngredient.setOriginXY();

                    draggedIngredient = null;
                    repaint();
                }
            }
        });

        // 드래그를 했을 때 시작점이 특정 재료라면 이동
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (draggedIngredient != null) {
                    // 현재 마우스 위치에 따른 이동 계산
                    int dx = e.getX() - startdragged.x;
                    int dy = e.getY() - startdragged.y;
                    // 드래그할 재료의 새 위치 업데이트
                    draggedIngredient.setX(draggedIngredient.getX() + dx);
                    draggedIngredient.setY(draggedIngredient.getY() + dy);
                    // 클릭 위치 업데이트
                    startdragged = e.getPoint();
                    repaint();
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(background.getImage(), 0, 0, getWidth(), getHeight(), this);
        for (int i = 0; i < menu.ingredients.length; i++) {
            Ingredient ingredient = menu.ingredients[i];
            g.drawImage(ingredient.icon.getImage(), ingredient.getX(), ingredient.getY(), null);
        }
    }

    public JButton getSubmitButton(int i) {
        JButton submitButton = new JButton(new ImageIcon("images/submit.png"));
        submitButton.setBorderPainted(false);
        submitButton.setContentAreaFilled(false);

        return submitButton;
    }
    private void setSubmitButton(int i) {
        // ActionListener 중복 방지: 기존 리스너 제거
        for (ActionListener listener : submitButtons[i].getActionListeners())
            submitButtons[i].removeActionListener(listener);

        // 새 ActionListener 등록
        submitButtons[i].addActionListener(e -> {
            boolean isCompleted = customers[i].isUserCorrect(user.addedIngredient);

            if (isCompleted) orderSuccess();
            else orderFailure();

            // 유저 상태 초기화
            menu = new Menu();
            user = new User();

            // 고객 상태 초기화 및 UI 갱신
            // 랜덤 주문과 고객 시간 갱신
            customers[i].randomOrder();
            customers[i].restartWaitingTime();
            revalidate();
            repaint();
        });
    }

    public void setSubmitButtonLocation(int i, int x, int y) {
        submitButtons[i].setBounds(x, y, 70, 70);
    }

    private void orderSuccess() {
        JOptionPane.showMessageDialog(null,
                "햄버거 제작 성공 \n 평판 +10",
                "성공", JOptionPane.PLAIN_MESSAGE);
        infoPanel.plusCompletedOrder();
        infoPanel.plusRaputation(10);
    }

    private void orderFailure() {
        JOptionPane.showMessageDialog(null,
                "잘못된 햄버거입니다! \n 평판 -10",
                "실패", JOptionPane.ERROR_MESSAGE);
        infoPanel.minusRaputation(10);
    }
}
