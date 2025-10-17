import greenfoot.*;

public class TextMessage extends Actor {
    private int timer = 90;

    public TextMessage(String text, int fontSize, Color color) {
        GreenfootImage img = new GreenfootImage(text, fontSize, color, new Color(0, 0, 0, 0));
        setImage(img);
    }

    public void act() {
        timer--;
        if (timer <= 0) getWorld().removeObject(this);
    }
}
