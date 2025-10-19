import greenfoot.*;
public class ScoreBoard extends Actor {
    private GreenfootImage img;
    
    public ScoreBoard() {
        img = new GreenfootImage(400, 100);
        setImage(img);
        update(0, 0);
    }
    
    // Update the scoreboard display with new scores
    public void update(int leftScore, int rightScore) {
        img.clear();
        String text = "LEFT: " + leftScore + "   |   RIGHT: " + rightScore;
        drawCenteredOutlinedText(img, text, Color.WHITE, Color.BLACK, 36);
    }
    
    // Draw text with an outline effect for better visibility
    private void drawCenteredOutlinedText(GreenfootImage surface, String text, Color mainColor, Color outlineColor, int size) {
        GreenfootImage txt = new GreenfootImage(text, size, mainColor, new Color(0, 0, 0, 0));
        GreenfootImage outline = new GreenfootImage(text, size, outlineColor, new Color(0, 0, 0, 0));
        GreenfootImage combined = new GreenfootImage(txt.getWidth() + 4, txt.getHeight() + 4);
        
        // Draw outline by layering the outline text at offsets
        for (int dx = -2; dx <= 2; dx++)
            for (int dy = -2; dy <= 2; dy++)
                combined.drawImage(outline, dx + 2, dy + 2);
        
        // Draw main text on top of the outline
        combined.drawImage(txt, 2, 2);
        
        // Center the combined text on the surface
        int x = (surface.getWidth() - combined.getWidth()) / 2;
        int y = (surface.getHeight() - combined.getHeight()) / 2;
        surface.drawImage(combined, x + 7, y);
    }
}