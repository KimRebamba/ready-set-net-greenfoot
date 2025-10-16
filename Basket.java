import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Basket and backboard actor that moves to random locations when scored.
 * 
 * @author (your name)
 * @version (a version number or a date)
 */
public class Basket extends Actor
{
    private int hoopY; // Y position of the actual hoop opening
    private GreenfootSound rimSound;
    private boolean soundEnabled = true;
    private Backboard backboard;
    
    private void playRimSound() {
    if (!soundEnabled) return;

    try {
        GreenfootSound s = new GreenfootSound("sounds/basket_rim.wav");
        s.setVolume(80); // optional: avoid being too loud
        s.play();
    } catch (Exception e) {
        soundEnabled = false;
        System.out.println("Bounce sound failed: " + e.getMessage());
    }
}

    public Basket()
    {
        GreenfootImage basketImage = new GreenfootImage("images/basket.png");
        basketImage.scale(80, 60);
        setImage(basketImage);
        hoopY = 0; // Will be set properly when added to world
        
        // Initialize rim sound with error handling
        try {
            rimSound = new GreenfootSound("sounds/basket_rim.wav");
        } catch (Throwable t) {
            System.out.println("Could not load rim sound: " + t.getMessage());
            soundEnabled = false;
            rimSound = null;
        }
    }
    
    public void act()
    {
        // Initialize hoopY if not set yet
        if (hoopY == 0 && getWorld() != null)
        {
            hoopY = getY() + 20; // Hoop is slightly below the image center
        }
    }
    
    public boolean checkScore(Basketball ball)
{
    int ballX = ball.getX();
    int ballY = ball.getY();
    int basketX = getX();
    int basketY = getY();

    // ✅ Make scoring area larger and more reliable
    int rimHalfWidth = 35;    // widen the hitbox a bit
    int rimTop = basketY + 10;  // slightly lower start of rim
    int rimBottom = basketY + 55; // extend downward a bit

    boolean inHorizontalZone = Math.abs(ballX - basketX) < rimHalfWidth;
    boolean inVerticalZone = (ballY >= rimTop && ballY <= rimBottom);

    // ✅ Slightly relaxed velocity check — allow slow or downward motion
    boolean movingDownward = ball.getVelocityY() >= -1; // less strict

    // ✅ Add a “passed through rim” logic to catch frames that skip
    if (inHorizontalZone && inVerticalZone && movingDownward)
    {
        // Optional: make sure it wasn’t already counted just before
        if (soundEnabled && rimSound != null)
        {
            try {
                playRimSound();
            } catch (Throwable t) {
                soundEnabled = false;
            }
        }
        return true;
    }

    return false;
}

    
    public void setBackboard(Backboard backboard)
    {
        this.backboard = backboard;
    }
    
    public void moveToRandomLocation()
    {
        if (getWorld() != null && backboard != null)
        {
            backboard.moveToRandomLocation();
        }
    }
    
    public int getHoopY()
    {
        return hoopY;
    }
}
