import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.List;
import java.util.ArrayList;

/**
 * Basketball game world with time limit and scoring mechanics.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class BasketballWorld extends World
{
    private Basketball ball;
    private Basket basket;
    private Backboard backboard;
    private Hand hand;
    private int score = 0;
    private ArrayList<Boundary> boundaries = new ArrayList<Boundary>();
    private int timeLeft = 120; // 120 seconds
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
     * Constructor for objects of class BasketballWorld.
     * 
     */
    public BasketballWorld()
    {    
        // Create a new world with 1100x600 cells with a cell size of 1x1 pixels.
        super(1100, 600, 1);
        
        // Set background
        GreenfootImage bg = new GreenfootImage("images/bg.png");
        bg.scale(1100, 600);
        setBackground(bg);
        
        // Add basketball
        ball = new Basketball();
        addObject(ball, 100, 500);
        
        // Add defense hand
        hand = new Hand();
        addObject(hand, 400, 550); // Position hand in the middle-bottom area
        
        // Add backboard and basket
        backboard = new Backboard();
        addObject(backboard, 1080, 200);
        
        basket = new Basket();
        addObject(basket, 1035, 251); // Basket hangs below backboard
        

        // Connect backboard and basket
        backboard.setBasket(basket);
        basket.setBackboard(backboard);
        
        // Add arrow for shot indication
        arrow = new Arrow();
        addObject(arrow, ball.getX(), ball.getY());
        arrow.setVisible(false);
        
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
                showText("Press ESC to return to menu", 400, 350);
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
        // Clear old text (by redrawing background each frame)
        GreenfootImage bg = new GreenfootImage("images/bg.png");
        bg.scale(1100, 600);
        setBackground(bg);
        
        // Draw new text
        drawGameText("Score: ", score, 40, 20, Color.WHITE, Color.BLACK);
        drawGameText("Time: ", timeLeft, 950, 20, Color.RED, Color.BLACK);
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
    
    private void drawGameText(String label, int value, int x, int y, Color mainColor, Color outlineColor)
    {
        String text = label + value;
        Font font = new Font("Arial", true, false, 28); // Bold, 28px
        GreenfootImage img = new GreenfootImage(text, 28, mainColor, new Color(0, 0, 0, 0));

        // Create outline
        GreenfootImage outline = new GreenfootImage(text, 28, outlineColor, new Color(0, 0, 0, 0));
        GreenfootImage combined = new GreenfootImage(img.getWidth() + 4, img.getHeight() + 4);
        for (int dx = -2; dx <= 2; dx++)
            for (int dy = -2; dy <= 2; dy++)
                combined.drawImage(outline, dx + 2, dy + 2);
        combined.drawImage(img, 2, 2);

        // Draw final image
        getBackground().drawImage(combined, x, y);
    }
}
