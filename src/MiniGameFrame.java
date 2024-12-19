import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Random;

public class MiniGameFrame extends JFrame {
    GamePanel gamePanel;
    ArrayList<Ingredient> neededIngredient; // 사용자가 선택한 재료
    ArrayList<Ingredient> collectedIngredient = new ArrayList<>(); // 사용자가 얻은 재료
    JLabel ingredientLabel;

    public MiniGameFrame(GamePanel gamePanel, ArrayList<Ingredient> neededIngredient) {
        this.neededIngredient = neededIngredient;
        this.gamePanel = gamePanel;

        setTitle("창고에서 필요한 재료를 가져오세요!");
        setSize(390, 440);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // 해당 프레임만 닫히게 설정
        setLayout(new BorderLayout());
        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension screenSize = kit.getScreenSize();
        setLocation((screenSize.width - this.getWidth()) / 2, (screenSize.height - this.getHeight()) / 2);

        // 현재까지 얻은 재료를 표시하는 패널
        JPanel currentPanel = new JPanel();
        ingredientLabel = new JLabel("획득한 재료: [없음]");
        currentPanel.add(ingredientLabel);
        this.add(currentPanel, BorderLayout.NORTH);

        // 실제 미로 게임이 진행되는 패널
        MazePanel mazePanel = new MazePanel(this, neededIngredient);
        this.add(mazePanel, BorderLayout.CENTER);
    }

    public void plusIngredient(Ingredient ingredient) { // 나중에 gamePanel의 값을 증가로 변경 필요
        collectedIngredient.add(ingredient);

        ingredientLabel.setText("획득한 재료: " + collectedIngredient.toString());
    }

    public void exitFrame() {
        gamePanel.refillIngredient(collectedIngredient);
        this.dispose();
    }
}

class MazePanel extends JPanel {
    MiniGameFrame miniGameFrame;
    private int mazeSize = 19;
    private int blockSize = 20;
    private int[][] maze = new int[mazeSize][mazeSize];
    ArrayList<Ingredient> neededIngredient; // 사용자가 선택한 재료
    Player player;
    // randomIngredient와 ingredientLocation의 인덱스는 짝지어져 있음
    Ingredient randomIngredient[] = new Ingredient[10];
    ArrayList<Point> ingredientLocations = new ArrayList<>();
    ImageIcon home = new ImageIcon("images/restaurant.png");
    public MazePanel(MiniGameFrame miniGameFrame, ArrayList<Ingredient> neededIngredient) {
        this.miniGameFrame = miniGameFrame;
        this.neededIngredient = neededIngredient;

        createMaze(); // dfs 미로 생성 함수
        createRandomIngredient(); // 재료 생성 함수

        player = new Player();
        addKeyListener(new KeyAdapter() { // 키보드를 눌렀을 플레이어의 이동을 처리하는 함수
            @Override
            public void keyPressed(KeyEvent e) {
                int playerX = player.getX();
                int playerY = player.getY();

                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                        if (possibleToMove(playerX, playerY - 1)) player.setY(playerY - 1); break;
                    case KeyEvent.VK_DOWN:
                        if (possibleToMove(playerX, playerY + 1)) player.setY(playerY + 1); break;
                    case KeyEvent.VK_LEFT:
                        if (possibleToMove(playerX - 1, playerY)) player.setX(playerX - 1); break;
                    case KeyEvent.VK_RIGHT:
                        if (possibleToMove(playerX + 1, playerY)) player.setX(playerX + 1); break;
                }
                checkIngredient(); // 재료와 충돌했는지 확인하는 함수
                checkHome(); // 다시 도착지에 도착했는지 확인하는 함수
                repaint();
            }
        });
        setFocusable(true);
        requestFocusInWindow();
    }
    public boolean possibleToMove(int x, int y) {
        // 캐릭터의 이동 위치가 프레임 범위를 넘어가지 않고 벽이 아닌지 확인하는 함수
        return x > 0 && y > 0 && x < mazeSize && y < mazeSize && maze[y][x] == 0;
    }

    public void createMaze() {
        for (int i = 0; i < mazeSize; i++) {
            for (int j = 0; j < mazeSize; j++) {
                maze[i][j] = 1;
            }
        }
        maze[2][1] = 0; //처음 시작 장소
        dfs(1, 1); // 레스토랑 위치
    }
    private void dfs(int x, int y) {
        maze[y][x] = 0;

        int[][] directions = {{0, -2}, {0, 2}, {-2, 0}, {2, 0}};

        Random random = new Random();

        // 방향을 무작위로 섞기
        for (int i = 0; i < directions.length; i++) {
            int randomIndex = random.nextInt(directions.length);
            int[] temp = directions[i];
            directions[i] = directions[randomIndex];
            directions[randomIndex] = temp;
        }

        for (int[] dir : directions) {
            int newX = x + dir[0];
            int newY = y + dir[1];

            if (newX > 0 && newX < mazeSize - 1 && newY > 0 && newY < mazeSize - 1 && maze[newY][newX] == 1) {
                maze[y + dir[1] / 2][x + dir[0] / 2] = 0; // 중간 벽 제거
                dfs(newX, newY);
            }
        }
    }

    public void createRandomIngredient() {
        Random random = new Random();
        int totalIngredients = 10;
        int ingredientCount = 0;

        while (ingredientCount < totalIngredients) {
            int x = random.nextInt(mazeSize / 2) * 2 + 1;
            int y = random.nextInt(mazeSize / 2) * 2 + 1;
            Point newPoint = new Point(x, y);

            // 중복 위치 확인 및 미로의 통로 확인
            if (maze[y][x] == 0 && !ingredientLocations.contains(newPoint)) {
                Ingredient ingredient = neededIngredient.get(random.nextInt(neededIngredient.size()));
                ingredient.setX(x);
                ingredient.setY(y);
                randomIngredient[ingredientCount] = ingredient;

                // 위치 저장
                ingredientLocations.add(newPoint);
                ingredientCount++;
            }
        }
    }

    public void checkIngredient() {
        int playerX = player.getX();
        int playerY = player.getY();

        for (int i = 0; i < ingredientLocations.size(); i++) {
            Point p = ingredientLocations.get(i);

            if (playerX == p.x && playerY == p.y) {
                Ingredient ingredient = randomIngredient[i];
                // 재료 수집 및 GamePanel에 반영
                miniGameFrame.plusIngredient(ingredient);

                // 제거 대신 가상의 위치로 좌표 변경
                ingredient.setX(-1);
                ingredient.setY(-1);
                ingredientLocations.set(i, new Point(-1, -1));
                break;
            }
        }
    }

    public void checkHome() {
        if (player.getX() == 1 && player.getY() == 1)
            miniGameFrame.exitFrame();
    }

    // 미로, 재료, 플레이어를 그리는 함수
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // 미로 그리기
        for (int y = 0; y < mazeSize; y++) {
            for (int x = 0; x < mazeSize; x++) {
                if (maze[y][x] == 1) {
                    g.setColor(Color.gray);
                    g.fillRect(x * blockSize, y * blockSize, blockSize, blockSize);
                }
            }
        }

        g.drawImage(home.getImage(), blockSize, blockSize, blockSize, blockSize, this);

        // 재료 그리기
        for (int i = 0; i < randomIngredient.length; i++) {
            Ingredient ingredient = randomIngredient[i];
            if (ingredient != null) {
                g.drawImage(ingredient.getIcon().getImage(), ingredient.getX() * blockSize, ingredient.getY() * blockSize, blockSize, blockSize, this);
            }
        }

        // 플레이어 그리기
        g.drawImage(player.playerImage.getImage(), player.getX() * blockSize, player.getY() * blockSize, blockSize, blockSize, this);
    }
}

 class Player {
    int x, y;
    ImageIcon playerImage = new ImageIcon("images/cart.png");

    public Player() {
        x = 1; y = 2;
    }
    void setX(int x) {
        this.x = x;
    }
    void setY(int y) {
        this.y = y;
    }
    int getX() { return x; }
    int getY() { return y; }
 }