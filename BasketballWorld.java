import greenfoot.*;
import java.util.List;
import java.util.ArrayList;

public class BasketballWorld extends World
{
    private Basketball ball;
    private Basket basket;
    private Backboard backboard;
    private Hand hand;
    private int score = 0;
    private ArrayList<Boundary> boundaries = new ArrayList<Boundary>();
    private int timeLeft = 120;
    private long lastTime = System.currentTimeMillis();
    private boolean gameOver = false;
    private boolean endGameDisplayed = false;
    private Arrow arrow;
    private GreenfootSound timeEndSound;
    private boolean soundEnabled = true;
    
    // CPU difficulty settings
    private boolean cpuMode = false;
    private String difficulty = "medium";
    private int cpuReactionDelay = 0;
    
    // Track mouse drag for power and direction
    private boolean mousePressed = false;
    private int startX, startY;
    private long pressStartTime;
    
    // Initial instructions screen
    private boolean showingInstructions = true;
    private int instructionTimer = 120;
    private GreenfootImage instructionsImage = new GreenfootImage("basketball_instructions.png");

    public BasketballWorld()
    {    
        this(false, "medium");
    }
    
    public BasketballWorld(boolean isCPU, String diff)
    {
        super(1100, 600, 1);
        
        this.cpuMode = isCPU;
        this.difficulty = diff;
        
        showInstructionsScreen();
    }
    
    private void showInstructionsScreen() {
        instructionsImage.scale(1100, 600);
        setBackground(instructionsImage);
        showingInstructions = true;
        instructionTimer = 120;
    }
    
    private void prepareGame() {
        // Load and set background image
        GreenfootImage bg = new GreenfootImage("images/bg.png");
        bg.scale(1100, 600);
        setBackground(bg);
        
        // Create basketball in starting position
        ball = new Basketball();
        addObject(ball, 100, 500);
        
        // Add defensive hand controlled by AI or player
        hand = new Hand(cpuMode, difficulty);
        addObject(hand, 400, 550);
        
        // Add backboard and basket on right side of court
        backboard = new Backboard();
        addObject(backboard, 1080, 200);
        
        basket = new Basket();
        addObject(basket, 1035, 251);
        
        // Link backboard and basket for collision detection
        backboard.setBasket(basket);
        basket.setBackboard(backboard);
        
        // Create arrow visual for aiming before release
        arrow = new Arrow();
        addObject(arrow, ball.getX(), ball.getY());
        arrow.setVisible(false);
        
        // Load end-of-game sound effect with fallback
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
        // Show instructions screen for 2 seconds before game starts
        if (showingInstructions) {
            instructionTimer--;
            if (instructionTimer <= 0) {
                showingInstructions = false;
                prepareGame();
            }
            return;
        }
        
        if (!gameOver)
        {
            updateTime();
            handleMouseInput();
            updateUI();
        }
        
        // R key resets the current game
        if (Greenfoot.isKeyDown("r"))
        {
            Greenfoot.setWorld(new BasketballWorld(cpuMode, difficulty));
        }
        
        // ESC key returns to main menu
        if (Greenfoot.isKeyDown("escape"))
        {
            Greenfoot.setWorld(new MenuWorld());
        }
    }
    
    private void updateTime()
    {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastTime >= 1000)
        {
            timeLeft--;
            lastTime = currentTime;
            
            // Game ends when timer reaches zero
            if (timeLeft <= 0)
            {
                gameOver = true;
                
                // Play end-of-game sound effect
                if (soundEnabled && timeEndSound != null)
                {
                    try {
                        timeEndSound.play();
                    } catch (Throwable t) {
                        soundEnabled = false;
                    }
                }
            }
        }
    }
    
    private void drawOutlinedText(String text, int centerX, int y, Color mainColor, Color outlineColor, int size)
    {
        // Create main text and outline text layers
        GreenfootImage img = new GreenfootImage(text, size, mainColor, new Color(0, 0, 0, 0));
        GreenfootImage outline = new GreenfootImage(text, size, outlineColor, new Color(0, 0, 0, 0));
        GreenfootImage combined = new GreenfootImage(img.getWidth() + 4, img.getHeight() + 4);

        // Draw outline around text for better visibility
        for (int dx = -2; dx <= 2; dx++)
            for (int dy = -2; dy <= 2; dy++)
                combined.drawImage(outline, dx + 2, dy + 2);

        combined.drawImage(img, 2, 2);
        int x = centerX - combined.getWidth() / 2;
        getBackground().drawImage(combined, x, y);
    }
    
    private void handleMouseInput()
    {
        MouseInfo mouse = Greenfoot.getMouseInfo();
        if (mouse != null)
        {
            // Start tracking mouse drag when button is pressed
            if (Greenfoot.mousePressed(null))
            {
                mousePressed = true;
                startX = mouse.getX();
                startY = mouse.getY();
                pressStartTime = System.currentTimeMillis();
                arrow.setVisible(true);
            }
            
            // Update arrow angle and power during drag
            if (mousePressed)
            {
                int deltaX = mouse.getX() - startX;
                int deltaY = mouse.getY() - startY;
                double angle = Math.atan2(deltaY, deltaX);
                double power = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
                
                arrow.setRotation((int) Math.toDegrees(angle));
                arrow.setPower((int) power);
                arrow.setLocation(ball.getX(), ball.getY());
            }
            
            // Release and shoot ball when mouse button is clicked
            if (Greenfoot.mouseClicked(null) && mousePressed)
            {
                mousePressed = false;
                arrow.setVisible(false);
                
                // Calculate velocity based on drag distance
                int deltaX = mouse.getX() - startX;
                int deltaY = mouse.getY() - startY;
                double power = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
                power = Math.min(power, 200);
                
                ball.shoot(deltaX, deltaY, power);
            }
        }
    }
    
    private void updateUI()
    {
        // Display final score and reset instructions once when game ends
        if (gameOver && !endGameDisplayed) {
            GreenfootImage bg = new GreenfootImage("images/bg.png");
            bg.scale(1100, 600);
            setBackground(bg);
            
            drawOutlinedText("GAME OVER", 550, 150, Color.YELLOW, Color.BLACK, 60);
            drawOutlinedText("Final Score: " + score, 550, 250, Color.WHITE, Color.BLACK, 50);
            drawOutlinedText("Press R to reset or ESC to return to menu", 550, 350, Color.WHITE, Color.BLACK, 30);
            
            endGameDisplayed = true;
            return;
        }
        
        if (gameOver) return;
        
        // Redraw background each frame and display current stats
        GreenfootImage bg = new GreenfootImage("images/bg.png");
        bg.scale(1100, 600);
        setBackground(bg);
        
        // Draw score and time remaining in top corners
        drawGameText("Score: ", score, 40, 20, Color.WHITE, Color.BLACK);
        drawGameText("Time: ", timeLeft, 950, 20, Color.RED, Color.BLACK);
        
        // Show difficulty setting if playing against CPU
        if (cpuMode) {
            drawGameText("Difficulty: " + difficulty.toUpperCase(), "", 40, 570, Color.WHITE, Color.BLACK);
        }
    }

    public void addScore()
    {
        score += 2;
        basket.moveToRandomLocation();
        
        // Add new obstacle boundaries at score milestones
        if (score == 10 || (score > 10 && score % 10 == 0))
        {
            addBoundary();
        }
        
        // Relocate existing boundaries after each basket
        moveAllBoundaries();
    }
    
    private void addBoundary()
    {
        // Attempt to find open space for new obstacle near basket
        int attempts = 0;
        int maxAttempts = 50;
        
        while (attempts < maxAttempts)
        {
            int basketX = basket.getX();
            int basketY = basket.getY();
            
            // Random position within range of basket
            int offsetX = Greenfoot.getRandomNumber(200) - 100;
            int offsetY = Greenfoot.getRandomNumber(150) - 75;
            
            int newX = Math.max(50, Math.min(getWidth() - 50, basketX + offsetX));
            int newY = Math.max(50, Math.min(getHeight() - 100, basketY + offsetY));
            
            // Only place if position doesn't overlap existing objects
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
        // Reposition all obstacles to new random locations
        for (Boundary boundary : boundaries)
        {
            moveBoundaryToNewPosition(boundary);
        }
    }
    
    private void moveBoundaryToNewPosition(Boundary boundary)
    {
        // Find valid new position for single boundary
        int attempts = 0;
        int maxAttempts = 50;
        
        while (attempts < maxAttempts)
        {
            int basketX = basket.getX();
            int basketY = basket.getY();
            
            int offsetX = Greenfoot.getRandomNumber(200) - 100;
            int offsetY = Greenfoot.getRandomNumber(150) - 75;
            
            int newX = Math.max(50, Math.min(getWidth() - 50, basketX + offsetX));
            int newY = Math.max(50, Math.min(getHeight() - 100, basketY + offsetY));
            
            // Avoid overlapping with basket, backboard, or other boundaries
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
        // Don't spawn obstacles too close to basket
        double distanceFromBasket = Math.sqrt((x - basket.getX()) * (x - basket.getX()) + 
                                             (y - basket.getY()) * (y - basket.getY()));
        if (distanceFromBasket < 100)
        {
            return false;
        }
        
        // Don't spawn obstacles too close to backboard
        double distanceFromBackboard = Math.sqrt((x - backboard.getX()) * (x - backboard.getX()) + 
                                                (y - backboard.getY()) * (y - backboard.getY()));
        if (distanceFromBackboard < 100)
        {
            return false;
        }
        
        // Check spacing between obstacles
        for (Boundary existingBoundary : boundaries)
        {
            if (existingBoundary != excludeBoundary)
            {
                double distance = Math.sqrt((x - existingBoundary.getX()) * (x - existingBoundary.getX()) + 
                                           (y - existingBoundary.getY()) * (y - existingBoundary.getY()));
                if (distance < 120)
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
    
    // Draw text with integer value and outline effect
    private void drawGameText(String label, int value, int x, int y, Color mainColor, Color outlineColor)
    {
        String text = label + value;
        Font font = new Font("Arial", true, false, 28);
        GreenfootImage img = new GreenfootImage(text, 28, mainColor, new Color(0, 0, 0, 0));

        GreenfootImage outline = new GreenfootImage(text, 28, outlineColor, new Color(0, 0, 0, 0));
        GreenfootImage combined = new GreenfootImage(img.getWidth() + 4, img.getHeight() + 4);
        for (int dx = -2; dx <= 2; dx++)
            for (int dy = -2; dy <= 2; dy++)
                combined.drawImage(outline, dx + 2, dy + 2);
        combined.drawImage(img, 2, 2);

        getBackground().drawImage(combined, x, y);
    }
    
    // Draw text with string value and outline effect
    private void drawGameText(String label, String value, int x, int y, Color mainColor, Color outlineColor)
    {
        String text = label + value;
        Font font = new Font("Arial", true, false, 20);
        GreenfootImage img = new GreenfootImage(text, 20, mainColor, new Color(0, 0, 0, 0));

        GreenfootImage outline = new GreenfootImage(text, 20, outlineColor, new Color(0, 0, 0, 0));
        GreenfootImage combined = new GreenfootImage(img.getWidth() + 4, img.getHeight() + 4);
        for (int dx = -2; dx <= 2; dx++)
            for (int dy = -2; dy <= 2; dy++)
                combined.drawImage(outline, dx + 2, dy + 2);
        combined.drawImage(img, 2, 2);

        getBackground().drawImage(combined, x, y);
    }
}