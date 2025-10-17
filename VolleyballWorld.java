import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Volleyball game world - to be implemented.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class VolleyballWorld extends World
{
    private int score = 0;
    private int timeLeft = 120; // 120 seconds
    private long lastTime = System.currentTimeMillis();
    private boolean gameOver = false;

    /**
     * Constructor for objects of class VolleyballWorld.
     * 
     */
    public VolleyballWorld()
    {    
        // Create a new world with 1100x600 cells with a cell size of 1x1 pixels.
        super(1100, 600, 1);
        
        // Set background
        GreenfootImage bg = new GreenfootImage("images/bg2.png");
        bg.scale(1100, 600);
        setBackground(bg);
        
        // Display title
        showText("VOLLEYBALL GAME", 550, 100);
        showText("(To be implemented)", 550, 150);
        showText("Press ESC to return to menu", 550, 300);
        
        // TODO: Add volleyball net
        // TODO: Add volleyball ball
        // TODO: Add players
    }
    
    public void act()
    {
        if (!gameOver)
        {
            updateTime();
            // TODO: Add game logic here
        }
        
        // Check for ESC key to return to menu
        if (Greenfoot.isKeyDown("escape"))
        {
            Greenfoot.setWorld(new MenuWorld());
        }
    }
    
    private void updateTime()
    {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastTime >= 1000) // 1 second has passed
        {
            timeLeft--;
            lastTime = currentTime;
            
            if (timeLeft <= 0)
            {
                gameOver = true;
                showText("Game Over! Final Score: " + score, 550, 300);
                showText("Press ESC to return to menu", 550, 350);
            }
        }
    }
    
    public void addScore(int points)
    {
        score += points;
    }
    
    public boolean isGameOver()
    {
        return gameOver;
    }
}
