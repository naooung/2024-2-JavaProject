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
    private Point dragStart;
    private Ingredient draggedIngredient = null;
    private Timer createCustomerTimer;
    private Timer changeConditionTimer;
    private boolean isHamburgerDragged = false;
    private ImageIcon hamburgerImage = new ImageIcon("images/hamburger.png");
    private int hamburgerX = -1;
    private int hamburgerY = -1;
    private int gameLevel = 0;
    private Fly fly;
    private Timer createFlyTimer;
    private Timer moveFlyTimer;
    private ImageIcon swatter = new ImageIcon("images/flyswatter.png");
    private int swatterX, swatterY;
    private boolean showSwatter = false;
    private ImageIcon pickleSlice = new ImageIcon("images/pickleslice.png");
    private int lettuceCount = 5;
    private int tomatoCount = 5;
    private int cheeseCount = 5;
    private int onionCount = 5;
    private int pattyCount = 5;


    public GamePanel(InfoPanel infoPanel) {
        this.infoPanel = infoPanel;

        setLayout(null);
        menu = new Menu();
        user = new User();
        customers = new Customer[3];

        JButton storageButton = new JButton(new ImageIcon("images/storage.png"));
        storageButton.setBorderPainted(false);
        storageButton.setContentAreaFilled(false);
        storageButton.setBounds(650, 5, 100, 100);
        storageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JCheckBox lettuceBox = new JCheckBox("양상추");
                JCheckBox tomatoBox = new JCheckBox("토마토");
                JCheckBox cheeseBox = new JCheckBox("치즈");
                JCheckBox onionBox = new JCheckBox("양파");
                JCheckBox pattyBox = new JCheckBox("고기패티");

                Object[] message = {"리필할 재료를 선택하세요:",
                        lettuceBox, tomatoBox, cheeseBox, onionBox, pattyBox};

                int option = JOptionPane.showConfirmDialog(null,
                        message, "창고 이동", JOptionPane.OK_CANCEL_OPTION);

                if (option == JOptionPane.OK_OPTION) {
                    ArrayList<Ingredient> selectedIngredients = new ArrayList<>();
                    if (lettuceBox.isSelected()) selectedIngredients.add(new Lettuce());
                    if (tomatoBox.isSelected()) selectedIngredients.add(new Tomato());
                    if (cheeseBox.isSelected()) selectedIngredients.add(new Cheese());
                    if (onionBox.isSelected()) selectedIngredients.add(new Onion());
                    if (pattyBox.isSelected()) selectedIngredients.add(new Patty());

                    MiniGameFrame miniGameFrame = new MiniGameFrame(GamePanel.this, selectedIngredients);
                    miniGameFrame.setVisible(true);
                }
            }
        });
        this.add(storageButton);

        // 10초마다 손님을 생성하는 타이머
        createCustomer(0);
        createCustomerTimer = new Timer(10000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                    for (int i = 0; i < customers.length; i++)
                        if (customers[i] == null) {
                            createCustomer(i);
                            break;
                        }
            }
        });
        createCustomerTimer.start();

        // 1초마다 level 상승 조정하고 손님의 상태를 갱신하는 타이머
        changeConditionTimer = new Timer(1000, e -> {
            if (infoPanel.gameTime.getTime() % 60 == 0) {
                gameLevel++;
            }
            // 손님 상태 갱신
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
        changeConditionTimer.start();

        // 40초마다 파리를 생성하는 타이머
        createFlyTimer = new Timer(40000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fly = new Fly();
            }
        });
        createFlyTimer.start();

        // 파리객체의 움직임 타이머
        moveFlyTimer = new Timer(50, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (fly != null && fly.isArrived()) {
                    JOptionPane.showMessageDialog(null,
                            "파리로 인한 손님의 컴플레인이 들어왔습니다. \n 평판 -10",
                            "파리", JOptionPane.ERROR_MESSAGE);
                    infoPanel.minusRaputation(10);
                    fly = null;
                } else if (fly != null)
                    repaint();
            }
        });
        moveFlyTimer.start();

        // 드래그 시작할 때 눌린 곳이 재료의 좌표가 맞는지 확인하여 드래그 중인 재료 설정
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                dragStart = e.getPoint();
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
                        int offsetX = dragStart.x - centerX;
                        int offsetY = dragStart.y - centerY;
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
                    isHamburgerDragged = true;
                    hamburgerX = e.getX();
                    hamburgerY = e.getY();
                    repaint();
                }

                if (fly != null && fly.isClicked(e.getX(), e.getY())) {
                    swatterX = e.getX();
                    swatterY = e.getY();
                    showSwatter = true;

                    // 일정 시간 후 파리채 숨기기
                    Timer swatterTimer = new Timer(150, event -> showSwatter = false);
                    swatterTimer.setRepeats(false);
                    swatterTimer.start();

                    fly = null; // 파리 제거
                    repaint();
                    JOptionPane.showMessageDialog(null,
                            "파리를 잡았습니다. \n 평판 +10",
                            "파리", JOptionPane.PLAIN_MESSAGE);
                    infoPanel.plusRaputation(10);
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
                                useIngredient(draggedIngredient);
                            }
                        }
                        menu.ingredients = ingredientList.toArray(new Ingredient[0]);
                    } else // 아니라면 원래 위치로 이동
                        draggedIngredient.setOriginXY();
                    draggedIngredient = null;
                    repaint();
                }

                if (isHamburgerDragged) {
                    isHamburgerDragged = false;

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
                    int dx = e.getX() - dragStart.x;
                    int dy = e.getY() - dragStart.y;
                    // 드래그할 재료의 새 위치 업데이트
                    draggedIngredient.setX(draggedIngredient.getX() + dx);
                    draggedIngredient.setY(draggedIngredient.getY() + dy);
                    // 클릭 위치 업데이트
                    dragStart = e.getPoint();
                    repaint();
                }
                if (isHamburgerDragged) {
                    hamburgerX = e.getX();
                    hamburgerY = e.getY();
                    repaint();
                }
            }
        });
    }

    public void createCustomer(int index) {
        int x = 100 + index * 200;
        int y = 300;
        int width = 150;
        int height = 150;

        customers[index] = new Customer(x, y, width, height, gameLevel);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(background.getImage(), 0, 0, getWidth(), getHeight(), this);
        // 모든 재료 그리기 (드래그 중인 재료는 제외)
        for (Ingredient ingredient : menu.ingredients) {
            if (ingredient != draggedIngredient) {
                g.drawImage(ingredient.getIcon().getImage(), ingredient.getX(), ingredient.getY(), null);
            }
            String countText = "";
            switch (ingredient.getName()) {
                case "양상추": countText = lettuceCount + " / 5"; break;
                case "토마토": countText = tomatoCount + " / 5"; break;
                case "치즈": countText = cheeseCount + " / 5"; break;
                case "양파": countText = onionCount + " / 5"; break;
                case "고기패티": countText = pattyCount + " / 5"; break;
            }
            // 재료 아이콘 아래에 텍스트 그리기
            g.drawString(countText, ingredient.getOriginX() + 10, ingredient.getOriginY() + ingredient.getIcon().getIconHeight() + 15);
        }

        // 드래그 중인 재료 그리기
        if (draggedIngredient != null) {
            if (draggedIngredient.getName().equals("피클")) {
                // 피클인 경우 pickleslice.png 사용
                g.drawImage(pickleSlice.getImage(), draggedIngredient.getX(), draggedIngredient.getY(), null);
            } else {
                // 다른 재료는 원래 아이콘 사용
                g.drawImage(draggedIngredient.getIcon().getImage(), draggedIngredient.getX(), draggedIngredient.getY(), null);
            }
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

        if (isHamburgerDragged && hamburgerX != -1 && hamburgerY != -1) {
            g.drawImage(hamburgerImage.getImage(), hamburgerX - 25, hamburgerY - 25, 50, 50, this);
        }

        if (fly != null)
            fly.drawFly(g);
        if (showSwatter)
            g.drawImage(swatter.getImage(), swatterX - 30, swatterY - 30, 60, 60, null);

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

    public void useIngredient(Ingredient i) {
        switch (i.getName()) {
            case "양상추": lettuceCount--; break;
            case "토마토": tomatoCount--; break;
            case "치즈": cheeseCount--; break;
            case "양파": onionCount--; break;
            case "고기패티": pattyCount--; break;
        }
    }

    public void refillIngredient(ArrayList<Ingredient> collectedIngredient) {
        for (Ingredient i : collectedIngredient) {
            switch (i.getName()) {
                case "양상추": if (lettuceCount + 1 <= 5) lettuceCount++; break;
                case "토마토": if (tomatoCount + 1 <= 5) tomatoCount++; break;
                case "치즈": if (cheeseCount + 1 <= 5) cheeseCount++; break;
                case "양파": if (onionCount + 1 <= 5) onionCount++; break;
                case "고기패티": if (pattyCount + 1 <= 5) pattyCount++; break;
            }
        }
    }
}
