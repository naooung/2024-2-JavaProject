import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class Menu {
    protected Ingredient[] ingredients;
    Lettuce luttuce;
    Tomato tomato;
    Cheese cheese;
    Onion onion;
    Patty patty;
    Ketchup ketchup;
    Mustard mustard;
    Pickle pickle;

    public Menu() {
        ingredients = new Ingredient[8];
        luttuce = new Lettuce();    ingredients[0] = luttuce;
        tomato = new Tomato();      ingredients[1] = tomato;
        cheese = new Cheese();      ingredients[2] = cheese;
        onion = new Onion();        ingredients[3] = onion;
        patty = new Patty();        ingredients[4] = patty;
        ketchup = new Ketchup();    ingredients[5] = ketchup;
        mustard = new Mustard();    ingredients[6] = mustard;
        pickle = new Pickle();      ingredients[7] = pickle;
    }

    // 재료 추가 여부를 문자열로 변환하는 메소드
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < ingredients.length; i++) {
            if (ingredients[i].getAdded())
                s.append(ingredients[i].getName() + ": O \n");
            else
                s.append(ingredients[i].getName() + ": X \n");
        }
        return s.toString();
    }

    // 재료 추가 여부 배열을 반환하는 메소드
    public boolean[] getAddedIngredients() {
        boolean[] addedIngredients = new boolean[8];

        for (int i = 0; i < ingredients.length; i++)
            addedIngredients[i] = ingredients[i].getAdded();

        return addedIngredients;
    }
}

class Customer extends Menu {
    private Timer timer;
    //추가 변수
    private int x, y, width, height;
    private Emotion emotion;

    public Customer(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.emotion = new Emotion();

        ingredients = new Ingredient[8];
        luttuce = new Lettuce();    ingredients[0] = luttuce;
        tomato = new Tomato();      ingredients[1] = tomato;
        cheese = new Cheese();      ingredients[2] = cheese;
        onion = new Onion();        ingredients[3] = onion;
        patty = new Patty();        ingredients[4] = patty;
        ketchup = new Ketchup();    ingredients[5] = ketchup;
        mustard = new Mustard();    ingredients[6] = mustard;
        pickle = new Pickle();      ingredients[7] = pickle;

        // 랜덤 주문서 생성
        randomOrder();
        // 대기 시간 초기화
        restartWaitingTime();
    }
    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public Emotion getEmotion() { return emotion; }

    public void randomOrder() {
        Random random = new Random();
        for (Ingredient ingredient : ingredients) {
            ingredient.setAdded(random.nextBoolean()); // 랜덤으로 추가 여부 설정
        }
    }
    // 10초마다 손님 게이지 상승
    public void restartWaitingTime() {
        this.timer = new Timer(10000, e -> {
            emotion.plusGauge();
        });
        timer.start();
    }

    public void stopTimer() {
        if (timer != null) timer.stop();
    }

    public boolean isClicked(int x, int y) {
        return x >= this.x && x <= this.x + width && y >= this.y && y <= this.y + height;
    }

    public int getCondition() {
        return emotion.getCurrentGauge();
    }
    // 인자로 받은 사용자의 재료리스트가 주문서와 같은지 비교하는 메소드
    public boolean isUserCorrect(ArrayList<Ingredient> user) {
        for (Ingredient order : this.ingredients) {
            if (order.getAdded() && !user.contains(order)) {
                return false;
            }
        }
        return true;
    }
}

class User extends Menu {
    ArrayList<Ingredient> addedIngredient;

    public User() {
        addedIngredient = new ArrayList<>();
    }

    public JButton getPlateButton() {

        JButton plateButton = new JButton(new ImageIcon("images/plate.png"));
        plateButton.setBorderPainted(false);
        plateButton.setContentAreaFilled(false);

        plateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showConfirmDialog(null, User.this.toString(), "재료 추가 목록", JOptionPane.PLAIN_MESSAGE);
            }
        });
        return plateButton;
    }
}