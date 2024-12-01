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
    JButton customerButton;
    JButton submitButton;
    private Timer timer;
    private int waitingTime;

    public Customer() {
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

    public void randomOrder() {
        Random random = new Random();
        for (Ingredient ingredient : ingredients) {
            ingredient.setAdded(random.nextBoolean()); // 랜덤으로 추가 여부 설정
        }

        if (customerButton != null) {
            for (ActionListener listener : customerButton.getActionListeners())
                customerButton.removeActionListener(listener);

            customerButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JOptionPane.showConfirmDialog(null, Customer.this.toString(), "주문서", JOptionPane.PLAIN_MESSAGE);
                }
            });
        }
    }

    public void restartWaitingTime() {
        waitingTime = 0;
        this.timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                waitingTime += 1;
            }
        });
        timer.start();
    }

    public int getWaitingTime() { return waitingTime; }

    // 클릭하면 손님의 주문서를 보여주는 손님 버튼을 리턴하는 메소드
    public JButton getCustomerButton(int num) {
        customerButton = new JButton(new ImageIcon("images/customer.png"));
        customerButton.setBorderPainted(false);
        customerButton.setContentAreaFilled(false);
        customerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showConfirmDialog(null, Customer.this.toString(), "주문서", JOptionPane.PLAIN_MESSAGE);
            }
        });
        return customerButton;
    }

    // 해당 버튼의 위치를 설정하는 메소드
    public void setCustomerButtonLocation(int x, int y) {
        customerButton.setBounds(x, y, 150, 150);
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