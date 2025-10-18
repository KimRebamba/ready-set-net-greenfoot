import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Menu world for selecting which game to play.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class MenuWorld extends World
{
    private GreenfootImage basketballButton;
    private GreenfootImage volleyballButton;
    private GreenfootImage badmintonButton;
    
    private int buttonWidth = 300;
    private int buttonHeight = 80;
    private int buttonY = 200;
    
    /**
     * Constructor for objects of class MenuWorld.
     */
    public MenuWorld()
    {    
        // Create a new world with 1100x600 cells with a cell size of 1x1 pixels.
        super(1100, 600, 1);
        
        // Set background
        GreenfootImage bg = new GreenfootImage("images/bg.png");
        bg.scale(1100, 600);
        setBackground(bg);
        
        // Draw title
        //drawTitle();
        
        // Draw menu buttons
        drawButtons();
    }
    
    private void drawTitle()
    {
        GreenfootImage bg = getBackground();
        Font titleFont = new Font("Arial", true, false, 60);
        bg.setFont(titleFont);
        bg.setColor(Color.WHITE);
        bg.drawString("BYTEBALL GAMES", 250, 100);
        
        // Add outline effect
        bg.setColor(Color.BLACK);
        bg.drawString("BYTEBALL GAMES", 248, 98);
        bg.drawString("BYTEBALL GAMES", 252, 102);
    }
    
    private void drawButtons()
    {
        GreenfootImage bg = getBackground();
        Font buttonFont = new Font("Arial", true, false, 32);
        bg.setFont(buttonFont);
        
        // Basketball button
        drawButton("BASKETBALL", 550, 200, new Color(255, 140, 0));
        
        // Volleyball button
        drawButton("VOLLEYBALL", 550, 320, new Color(0, 150, 255));
        
        // Badminton button
        drawButton("BADMINTON", 550, 440, new Color(0, 200, 0));
    }
    
    private void drawButton(String text, int centerX, int centerY, Color color)
    {
        GreenfootImage bg = getBackground();
        
        // Draw button background
        bg.setColor(color);
        bg.fillRect(centerX - buttonWidth/2, centerY - buttonHeight/2, buttonWidth, buttonHeight);
        
        // Draw button border
        bg.setColor(Color.BLACK);
        bg.drawRect(centerX - buttonWidth/2, centerY - buttonHeight/2, buttonWidth, buttonHeight);
        bg.drawRect(centerX - buttonWidth/2 + 1, centerY - buttonHeight/2 + 1, buttonWidth - 2, buttonHeight - 2);
        
        // Draw text
        Font buttonFont = new Font("Arial", true, false, 32);
        GreenfootImage textImg = new GreenfootImage(text, 32, Color.WHITE, new Color(0, 0, 0, 0));
        bg.drawImage(textImg, centerX - textImg.getWidth()/2, centerY - textImg.getHeight()/2);
    }
    
    public void act()
    {
        checkButtonClicks();
    }
    
    private void checkButtonClicks()
    {
        if (Greenfoot.mouseClicked(this))
        {
            MouseInfo mouse = Greenfoot.getMouseInfo();
            if (mouse != null)
            {
                int x = mouse.getX();
                int y = mouse.getY();
                
                // Check basketball button (centered at 550, 200)
                if (isInButton(x, y, 550, 200))
                {
                    Greenfoot.setWorld(new BasketballWorld());
                }
                // Check volleyball button (centered at 550, 320)
                else if (isInButton(x, y, 550, 320))
                {
                    Greenfoot.setWorld(new VolleyballWorld());
                }
                // Check badminton button (centered at 550, 440)
                else if (isInButton(x, y, 550, 440))
                {
                    Greenfoot.setWorld(new BadmintonWorld());
                }
            }
        }
    }
    
    private boolean isInButton(int mouseX, int mouseY, int buttonCenterX, int buttonCenterY)
    {
        return mouseX >= buttonCenterX - buttonWidth/2 && 
               mouseX <= buttonCenterX + buttonWidth/2 &&
               mouseY >= buttonCenterY - buttonHeight/2 && 
               mouseY <= buttonCenterY + buttonHeight/2;
    }
}
