import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

class GamePanel extends JPanel {
    private InfoPanel infoPanel;
    private ImageIcon background = new ImageIcon("images/background.png");
    private Menu menu;
    private Customer customers[];
    private User user;
    private Point startdragged;
    private Ingredient draggedIngredient = null;
    // 추가한 변수
    private Timer customerGenerationTimer;
    private Timer changeEmotionTimer;
    private int customerCount = 0;
    private boolean isDragging = false;
    private ImageIcon hamburgerImage = new ImageIcon("images/hamburger.png");
    private int hamburgerX = -1;
    private int hamburgerY = -1;

    public GamePanel(InfoPanel infoPanel) {
        this.infoPanel = infoPanel;

        setLayout(null);
        menu = new Menu();
        user = new User();
        customers = new Customer[3];

        // 수정한 내용
        generateCustomer(0);
        // 손님 생성 타이머
        customerGenerationTimer = new Timer(10000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (customerCount < 3) {
                    for (int i = 0; i < customers.length; i++) {
                        if (customers[i] == null) {
                            generateCustomer(i);
                            break;
                        }
                    }
                }
            }
        });
        customerGenerationTimer.start();

        changeEmotionTimer = new Timer(1000, e -> {
            for (int i = 0; i < customers.length; i++) {
                if (customers[i] != null) {
                    if (customers[i].getCondition() < 3)
                        repaint();
                    else { // 손님이 매우 화나서 가버린 상태일 때
                        customers[i] = null;
                        infoPanel.minusRaputation(20);
                        continue;
                    }
                }
            }
        });
        changeEmotionTimer.start();

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
                for (Customer customer : customers) {
                    if (customer != null && customer.isClicked(e.getX(), e.getY())) {
                        // 손님을 클릭했을 때 주문서 표시
                        JOptionPane.showMessageDialog(null, customer.toString(), "주문서", JOptionPane.PLAIN_MESSAGE);
                        break;
                    }
                }
                // 제작 접시 범위 안에서 클릭했을 때 드래그 시작
                if (e.getX() >= 370 && e.getX() <= 460 && e.getY() >= 105 && e.getY() <= 155) {
                    isDragging = true;
                    hamburgerX = e.getX();
                    hamburgerY = e.getY();
                    repaint();
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

                if (isDragging) {
                    isDragging = false;

                    // 손님 위치와 비교
                    for (int i = 0; i < customers.length; i++) {
                        Customer customer = customers[i];
                        if (customer != null &&
                                e.getX() >= customer.getX() && e.getX() <= customer.getX() + customer.getWidth() &&
                                e.getY() >= customer.getY() && e.getY() <= customer.getY() + customer.getHeight()) {

                            // 손님의 주문과 유저 재료 비교
                            if (customer.isUserCorrect(user.addedIngredient)) {
                                orderSuccess();
                            } else {
                                orderFailure();
                            }
                            // 손님 상태 초기화
                            menu = new Menu();
                            customers[i] = null;
                            repaint();
                            return;
                        }
                    }
                    // 손님 위치가 아니면 햄버거 이미지를 초기화
                    hamburgerX = -1;
                    hamburgerY = -1;
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
                if (isDragging) {
                    // 드래그 도중 햄버거 이미지의 위치를 업데이트
                    hamburgerX = e.getX();
                    hamburgerY = e.getY();
                    repaint();
                }
            }
        });
    }
    public void generateCustomer(int index) {
        int x = 100 + index * 200; // 예제 위치
        int y = 300;
        int width = 150;
        int height = 150;

        customers[index] = new Customer(x, y, width, height);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(background.getImage(), 0, 0, getWidth(), getHeight(), this);
        for (int i = 0; i < menu.ingredients.length; i++) {
            Ingredient ingredient = menu.ingredients[i];
            g.drawImage(ingredient.icon.getImage(), ingredient.getX(), ingredient.getY(), null);
        }

        for (Customer customer : customers) {
            if (customer != null) {
                g.drawImage(new ImageIcon("images/customer.png").getImage(),
                        customer.getX(), customer.getY(),
                        customer.getWidth(), customer.getHeight(), this);

                Emotion emotion = customer.getEmotion();
                ImageIcon currentEmotion = emotion.getCurrentEmotion();
                if (currentEmotion != null) {
                    g.drawImage(currentEmotion.getImage(),
                            customer.getX() + 54, customer.getY() - 35, 40, 40, this);
                }
            }
        }

        if (isDragging && hamburgerX != -1 && hamburgerY != -1) {
            g.drawImage(hamburgerImage.getImage(), hamburgerX - 25, hamburgerY - 25, 50, 50, this);
        }
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
