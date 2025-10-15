import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.List;
import java.util.ArrayList;

/**
 * Basketball game world with time limit and scoring mechanics.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class MyWorld extends World
{
    private Basketball ball;
    private Basket basket;
    private Backboard backboard;
    private int score = 0;
    private ArrayList<Boundary> boundaries = new ArrayList<Boundary>();
    private int timeLeft = 120; // 60 seconds
    private long lastTime = System.currentTimeMillis();
    private boolean gameOver = false;
    private Arrow arrow;
    private GreenfootSound timeEndSound;
    private boolean soundEnabled = true;
    
    // Mouse tracking for shooting
    private boolean mousePressed = false;
    private int startX, startY;
    private long pressStartTime;

    /**
     * Constructor for objects of class MyWorld.
     * 
     */
    public MyWorld()
    {    
        // Create a new world with 800x600 cells with a cell size of 1x1 pixels.
        super(800, 600, 1);
        
        // Set background
        GreenfootImage bg = new GreenfootImage("images/bg.png");
        bg.scale(800, 600);
        setBackground(bg);
        
        // Add basketball
        ball = new Basketball();
        addObject(ball, 100, 500);
        
        // Add backboard and basket
        backboard = new Backboard();
        addObject(backboard, 770, 200);
        
        basket = new Basket();
        addObject(basket, 725, 251); // Basket hangs below backboard
        
        // Connect backboard and basket
        backboard.setBasket(basket);
        basket.setBackboard(backboard);
        
        // Add arrow for shot indication
        arrow = new Arrow();
        addObject(arrow, ball.getX(), ball.getY());
        arrow.setVisible(false);
        
        // Display initial score and time
        showText("Score: " + score, 100, 30);
        showText("Time: " + timeLeft, 700, 30);
        
        // Initialize time end sound with error handling
        try {
            timeEndSound = new GreenfootSound("sounds/time_end.wav");
        } catch (Throwable t) {
            System.out.println("Could not load time end sound: " + t.getMessage());
            soundEnabled = false;
            timeEndSound = null;
        }
    }
    
    public void act()
    {
        if (!gameOver)
        {
            updateTime();
            handleMouseInput();
            updateUI();
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
                
                // Play game end sound
                if (soundEnabled && timeEndSound != null)
                {
                    try {
                        timeEndSound.play();
                    } catch (Throwable t) {
                        // Sound failed to play, continue silently
                        soundEnabled = false;
                    }
                }
                
                showText("Game Over! Final Score: " + score, 400, 300);
                Greenfoot.stop();
            }
        }
    }
    
    private void handleMouseInput()
    {
        MouseInfo mouse = Greenfoot.getMouseInfo();
        if (mouse != null)
        {
            if (Greenfoot.mousePressed(null))
            {
                mousePressed = true;
                startX = mouse.getX();
                startY = mouse.getY();
                pressStartTime = System.currentTimeMillis();
                arrow.setVisible(true);
            }
            
            if (mousePressed)
            {
                // Update arrow position and angle
                int deltaX = mouse.getX() - startX;
                int deltaY = mouse.getY() - startY;
                double angle = Math.atan2(deltaY, deltaX);
                double power = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
                
                arrow.setRotation((int) Math.toDegrees(angle));
                arrow.setPower((int) power);
                arrow.setLocation(ball.getX(), ball.getY());
            }
            
            if (Greenfoot.mouseClicked(null) && mousePressed)
            {
                mousePressed = false;
                arrow.setVisible(false);
                
                // Calculate shot velocity
                int deltaX = mouse.getX() - startX;
                int deltaY = mouse.getY() - startY;
                double power = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
                power = Math.min(power, 200); // Limit max power
                
                ball.shoot(deltaX, deltaY, power);
            }
        }
    }
    
    private void updateUI()
    {
        showText("Score: " + score, 100, 30);
        showText("Time: " + timeLeft, 700, 30);
    }
    
    public void addScore()
    {
        score += 2; // 2 points per basket
        basket.moveToRandomLocation();
        
        // Add boundary when score reaches 10, then every 10 points
        if (score == 10 || (score > 10 && score % 10 == 0))
        {
            addBoundary();
        }
        
        // Move all existing boundaries to new positions after each score
        moveAllBoundaries();
    }
    
    private void addBoundary()
    {
        // Try to find a good position for the boundary
        int attempts = 0;
        int maxAttempts = 50;
        
        while (attempts < maxAttempts)
        {
            // Generate random position near the basket
            int basketX = basket.getX();
            int basketY = basket.getY();
            
            int offsetX = Greenfoot.getRandomNumber(200) - 100; // -100 to +100
            int offsetY = Greenfoot.getRandomNumber(150) - 75;  // -75 to +75
            
            int newX = Math.max(50, Math.min(getWidth() - 50, basketX + offsetX));
            int newY = Math.max(50, Math.min(getHeight() - 100, basketY + offsetY));
            
            // Check if position is valid using the same validation logic
            if (isValidBoundaryPosition(newX, newY, null))
            {
                Boundary newBoundary = new Boundary();
                addObject(newBoundary, newX, newY);
                boundaries.add(newBoundary);
                break;
            }
            
            attempts++;
        }
    }
    
    private void moveAllBoundaries()
    {
        for (Boundary boundary : boundaries)
        {
            moveBoundaryToNewPosition(boundary);
        }
    }
    
    private void moveBoundaryToNewPosition(Boundary boundary)
    {
        // Try to find a good position for the boundary
        int attempts = 0;
        int maxAttempts = 50;
        
        while (attempts < maxAttempts)
        {
            // Generate random position near the basket
            int basketX = basket.getX();
            int basketY = basket.getY();
            
            int offsetX = Greenfoot.getRandomNumber(200) - 100; // -100 to +100
            int offsetY = Greenfoot.getRandomNumber(150) - 75;  // -75 to +75
            
            int newX = Math.max(50, Math.min(getWidth() - 50, basketX + offsetX));
            int newY = Math.max(50, Math.min(getHeight() - 100, basketY + offsetY));
            
            // Check if position is valid (not overlapping with basket, backboard, or other boundaries)
            if (isValidBoundaryPosition(newX, newY, boundary))
            {
                boundary.setLocation(newX, newY);
                break;
            }
            
            attempts++;
        }
    }
    
    private boolean isValidBoundaryPosition(int x, int y, Boundary excludeBoundary)
    {
        // Check distance from basket (don't spawn too close)
        double distanceFromBasket = Math.sqrt((x - basket.getX()) * (x - basket.getX()) + 
                                             (y - basket.getY()) * (y - basket.getY()));
        if (distanceFromBasket < 100) // Increased minimum distance
        {
            return false;
        }
        
        // Check distance from backboard (don't spawn too close)
        double distanceFromBackboard = Math.sqrt((x - backboard.getX()) * (x - backboard.getX()) + 
                                                (y - backboard.getY()) * (y - backboard.getY()));
        if (distanceFromBackboard < 100) // Minimum distance from backboard
        {
            return false;
        }
        
        // Check overlap with other boundaries
        for (Boundary existingBoundary : boundaries)
        {
            if (existingBoundary != excludeBoundary)
            {
                double distance = Math.sqrt((x - existingBoundary.getX()) * (x - existingBoundary.getX()) + 
                                           (y - existingBoundary.getY()) * (y - existingBoundary.getY()));
                if (distance < 120) // Increased minimum distance between boundaries
                {
                    return false;
                }
            }
        }
        
        return true;
    }
    
    public boolean isGameOver()
    {
        return gameOver;
    }
}
