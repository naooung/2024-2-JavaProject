import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.*;

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
    private MiniGameFrame miniGameFrame;
    private final int mazeSize = 19;
    private final int blockSize = 20;
    private int[][] maze = new int[mazeSize][mazeSize];
    private Player player;
    private Map<Point, Ingredient> ingredientMap = new HashMap<>();
    private ImageIcon home = new ImageIcon("images/restaurant.png");
    private ArrayList<Ingredient> neededIngredient;

    public MazePanel(MiniGameFrame miniGameFrame, ArrayList<Ingredient> neededIngredient) {
        this.miniGameFrame = miniGameFrame;
        this.neededIngredient = neededIngredient;

        createMaze();           // DFS를 이용한 미로 생성
        createRandomIngredients(); // 무작위 재료 배치

        player = new Player();

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int playerX = player.getX();
                int playerY = player.getY();

                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                        if (possibleToMove(playerX, playerY - 1)) player.setY(playerY - 1);
                        break;
                    case KeyEvent.VK_DOWN:
                        if (possibleToMove(playerX, playerY + 1)) player.setY(playerY + 1);
                        break;
                    case KeyEvent.VK_LEFT:
                        if (possibleToMove(playerX - 1, playerY)) player.setX(playerX - 1);
                        break;
                    case KeyEvent.VK_RIGHT:
                        if (possibleToMove(playerX + 1, playerY)) player.setX(playerX + 1);
                        break;
                }
                checkIngredient();
                checkHome();
                repaint();
            }
        });

        setFocusable(true);
        requestFocusInWindow();
    }

    public boolean possibleToMove(int x, int y) {
        return x > 0 && y > 0 && x < mazeSize && y < mazeSize && maze[y][x] == 0;
    }

    private void createMaze() {
        for (int i = 0; i < mazeSize; i++) {
            for (int j = 0; j < mazeSize; j++) {
                maze[i][j] = 1;
            }
        }
        maze[2][1] = 0; // 시작 위치
        dfs(1, 1);
    }

    private void dfs(int x, int y) {
        maze[y][x] = 0;
        int[][] directions = {{0, -2}, {0, 2}, {-2, 0}, {2, 0}};
        Random random = new Random();

        // 방향 무작위 섞기
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

    private void createRandomIngredients() {
        Random random = new Random();
        int totalIngredients = 10;
        ingredientMap.clear();

        // 필요한 재료가 10개보다 적으면 반복해서 채우기
        int neededSize = neededIngredient.size();
        Ingredient[] selectedIngredients = new Ingredient[totalIngredients];
        for (int i = 0; i < totalIngredients; i++) {
            selectedIngredients[i] = neededIngredient.get(i % neededSize);
        }

        // 중복되지 않는 위치에 재료 배치
        for (Ingredient ingredient : selectedIngredients) {
            while (true) {
                int x = random.nextInt(mazeSize / 2) * 2 + 1;
                int y = random.nextInt(mazeSize / 2) * 2 + 1;
                Point point = new Point(x, y);

                if (maze[y][x] == 0 && !ingredientMap.containsKey(point)) {
                    ingredient.setX(x);
                    ingredient.setY(y);
                    ingredientMap.put(point, ingredient);
                    break;
                }
            }
        }
    }

    private void checkIngredient() {
        Point playerPos = new Point(player.getX(), player.getY());
        if (ingredientMap.containsKey(playerPos)) {
            Ingredient ingredient = ingredientMap.get(playerPos);
            miniGameFrame.plusIngredient(ingredient);
            ingredientMap.remove(playerPos); // 재료 수집 후 제거
        }
    }

    private void checkHome() {
        if (player.getX() == 1 && player.getY() == 1) {
            miniGameFrame.exitFrame();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // 미로 그리기
        for (int y = 0; y < mazeSize; y++) {
            for (int x = 0; x < mazeSize; x++) {
                if (maze[y][x] == 1) {
                    g.setColor(Color.GRAY);
                    g.fillRect(x * blockSize, y * blockSize, blockSize, blockSize);
                }
            }
        }

        g.drawImage(home.getImage(), blockSize, blockSize, blockSize, blockSize, this);

        // 재료 그리기
        for (Map.Entry<Point, Ingredient> entry : ingredientMap.entrySet()) {
            Point point = entry.getKey();
            Ingredient ingredient = entry.getValue();
            g.drawImage(ingredient.getIcon().getImage(), point.x * blockSize, point.y * blockSize, blockSize, blockSize, this);
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