import javax.swing.*;
import java.awt.event.*;
import java.util.Objects;

public class Ingredient {
    private boolean added;
    protected String name;
    protected ImageIcon icon;
    protected int x, y;

    public String getName() {
        return name;
    }
    public void setAdded(boolean added) {
        this.added = added;
    }
    public boolean getAdded() {
        return this.added;
    }
    public void setOriginXY() { }
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
    public int getX() { return x; }
    public int getY() { return y; }
    public ImageIcon getIcon() { return icon; }

    @Override // 객체의 주소값 뿐만 아니라 이름으로도 비교하기 위해서 생성한 메소드
    public boolean equals(Object object) {
        if (this == object)
            return true;
        Ingredient ingredient = (Ingredient) object;
        return Objects.equals(this.getName(), ingredient.getName());
    }
}

class Lettuce extends Ingredient {
    public Lettuce() {
        this.name = "양상추";
        this.icon = new ImageIcon("images/lettuce.png");
        setOriginXY();
    }
    @Override
    public void setOriginXY() { this.x = 105; this.y = 170; }
}

class Tomato extends Ingredient {
    public Tomato() {
        this.name = "토마토";
        this.icon = new ImageIcon("images/tomato.png");
        setOriginXY();
    }
    @Override
    public void setOriginXY() { this.x = 180; this.y = 170; }
}

class Cheese extends Ingredient {
    public Cheese() {
        this.name = "치즈";
        this.icon = new ImageIcon("images/cheese.png");
        setOriginXY();
    }
    @Override
    public void setOriginXY() { this.x = 260; this.y = 170; }
}

class Onion extends Ingredient {
    public Onion() {
        this.name = "양파";
        this.icon = new ImageIcon("images/onion.png");
        setOriginXY();
    }
    @Override
    public void setOriginXY() { this.x = 340; this.y = 170; }
}

class Patty extends Ingredient {
    public Patty() {
        this.name = "고기패티";
        this.icon = new ImageIcon("images/patty.png");
        setOriginXY();
    }
    @Override
    public void setOriginXY() { this.x = 420; this.y = 170; }
}

class Ketchup extends Ingredient {
    public Ketchup() {
        this.name = "케찹";
        this.icon = new ImageIcon("images/ketchup.png");
        setOriginXY();
    }
    @Override
    public void setOriginXY() { this.x = 480; this.y = 158; }
}

class Mustard extends Ingredient {
    public Mustard() {
        this.name = "머스타드";
        this.icon = new ImageIcon("images/mustard.png");
        setOriginXY();
    }
    @Override
    public void setOriginXY() { this.x = 550; this.y = 165; }
}

class Pickle extends Ingredient {
    public Pickle() {
        this.name = "피클";
        this.icon = new ImageIcon("images/pickle.png");
        setOriginXY();
    }
    @Override
    public void setOriginXY() { this.x = 620; this.y = 160; }
}