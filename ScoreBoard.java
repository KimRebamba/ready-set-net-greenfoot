import greenfoot.*;

public class ScoreBoard extends Actor {
    private GreenfootImage img;

    public ScoreBoard() {
        img = new GreenfootImage(400, 100);
        setImage(img);
        update(0, 0);
    }

    public void update(int leftScore, int rightScore) {
        img.clear();
        String text = "LEFT: " + leftScore + "   |   RIGHT: " + rightScore;
        drawCenteredOutlinedText(img, text, Color.WHITE, Color.BLACK, 36);
    }

    private void drawCenteredOutlinedText(GreenfootImage surface, String text, Color mainColor, Color outlineColor, int size) {
        GreenfootImage txt = new GreenfootImage(text, size, mainColor, new Color(0, 0, 0, 0));
        GreenfootImage outline = new GreenfootImage(text, size, outlineColor, new Color(0, 0, 0, 0));
        GreenfootImage combined = new GreenfootImage(txt.getWidth() + 4, txt.getHeight() + 4);

        // Create outline
        for (int dx = -2; dx <= 2; dx++)
            for (int dy = -2; dy <= 2; dy++)
                combined.drawImage(outline, dx + 2, dy + 2);

        combined.drawImage(txt, 2, 2);

        // Center horizontally and vertically
        int x = (surface.getWidth() - combined.getWidth()) / 2;
        int y = (surface.getHeight() - combined.getHeight()) / 2;

        surface.drawImage(combined, x + 7, y);
    }
}
