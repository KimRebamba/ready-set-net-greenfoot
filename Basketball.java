import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Basketball actor with physics including gravity, bouncing, and shooting mechanics.
 * 
 * @author (your name)
 * @version (a version number or a date)
 */
public class Basketball extends Actor
{
    private double velocityX = 0;
    private double velocityY = 0;
    private final double gravity = 0.3;
    private final double bounceDamping = 0.7;
    private final double friction = 0.98;
    private GreenfootSound bounceSound;
    private boolean soundEnabled = true;
    // Add these fields at the top of Basketball class (with other private variables)
private int prevX = 0;
private int prevY = 0;
// Add this getter method:
public int getPrevY()
{
    return prevY;
}
    public Basketball()
    {
        GreenfootImage ballImage = new GreenfootImage("images/basketball.png");
        ballImage.scale(42, 42);
        setImage(ballImage);
        
        // Initialize sound with error handling
        try {
            bounceSound = new GreenfootSound("sounds/bounce.wav");
        } catch (Throwable t) {
            System.out.println("Could not load bounce sound: " + t.getMessage());
            soundEnabled = false;
            bounceSound = null;
        }
    }
    
    public void act()
    {
        applyPhysics();
    checkCollisions();
    checkBounds();
    
    // Store current position for next frame
    prevX = getX();
    prevY = getY();
    }
    
    private void applyPhysics()
    {
        // Apply gravity
        velocityY += gravity;
        
        // Apply friction
        velocityX *= friction;
        
        // Update position
        setLocation(getX() + (int) velocityX, getY() + (int) velocityY);
    }
    
    public int getRimY() {
    return getY() - 10; // adjust based on where the hoop opening is on your image
}

    private void checkCollisions()
    {
        // --- Hand Collision ---
        Hand hand = (Hand) getOneIntersectingObject(Hand.class);
        if (hand != null)
        {
            // Calculate collision direction
            double dx = getX() - hand.getX();
            double dy = getY() - hand.getY();
            double distance = Math.sqrt(dx * dx + dy * dy);
            double minDistance = 35; // Combined radius of ball and hand

            if (distance < minDistance)
            {
                // Normalize direction
                double nx = dx / distance;
                double ny = dy / distance;

                // Push ball away from hand
                setLocation(
                    (int)(hand.getX() + nx * minDistance),
                    (int)(hand.getY() + ny * minDistance)
                );

                // Reflect velocity with some damping
                double dot = velocityX * nx + velocityY * ny;
                velocityX -= 2 * dot * nx;
                velocityY -= 2 * dot * ny;

                // Apply damping
                velocityX *= bounceDamping;
                velocityY *= bounceDamping;

                // Add some extra force based on hand movement
                velocityX += hand.getVelocityX() * 0.5;
                velocityY += hand.getVelocityY() * 0.3;

                // Small cutoff
                if (Math.abs(velocityX) < 0.3) velocityX = 0;
                if (Math.abs(velocityY) < 0.3) velocityY = 0;

                // Sound
                if (soundEnabled && bounceSound != null)
                {
                    try { playBounceSound(); } catch (Throwable t) { soundEnabled = false; }
                }
            }
        }
        
       // --- Boundary Collision ---
Boundary boundary = (Boundary) getOneIntersectingObject(Boundary.class);
if (boundary != null)
{
    double dx = getX() - boundary.getX();
    double dy = getY() - boundary.getY();
    double distance = Math.sqrt(dx * dx + dy * dy);
    double minDistance = (boundary.getWidth() / 2.0 + 20); // 20 = ball radius

    if (distance < minDistance)
    {
        // Normalize direction
        double nx = dx / distance;
        double ny = dy / distance;

        // Push ball out of boundary
        setLocation(
            (int)(boundary.getX() + nx * minDistance),
            (int)(boundary.getY() + ny * minDistance)
        );

        // Reflect velocity
        double dot = velocityX * nx + velocityY * ny;
        velocityX -= 2 * dot * nx;
        velocityY -= 2 * dot * ny;

        // Apply damping
        velocityX *= bounceDamping;
        velocityY *= bounceDamping;

        // Small cutoff
        if (Math.abs(velocityX) < 0.3) velocityX = 0;
        if (Math.abs(velocityY) < 0.3) velocityY = 0;

        // Sound
        if (soundEnabled && bounceSound != null)
        {
            try { playBounceSound(); } catch (Throwable t) { soundEnabled = false; }
        }
    }
}
        
        // --- Backboard Collision --- (REPLACE THE OLD SECTION WITH THIS)
Backboard backboard = (Backboard) getOneIntersectingObject(Backboard.class);
if (backboard != null)
{
    // ✅ Proper rectangular collision detection
    int ballLeft = getX() - 21;
    int ballRight = getX() + 21;
    int ballTop = getY() - 21;
    int ballBottom = getY() + 21;
    
    int bbLeft = backboard.getLeft();
    int bbRight = backboard.getRight();
    int bbTop = backboard.getTop();
    int bbBottom = backboard.getBottom();
    
    // Check if actually overlapping
    if (ballRight > bbLeft && ballLeft < bbRight && 
        ballBottom > bbTop && ballTop < bbBottom)
    {
        // Determine which side we hit
        int overlapLeft = ballRight - bbLeft;
        int overlapRight = bbRight - ballLeft;
        int overlapTop = ballBottom - bbTop;
        int overlapBottom = bbBottom - ballTop;
        
        int minOverlap = Math.min(Math.min(overlapLeft, overlapRight), 
                                   Math.min(overlapTop, overlapBottom));
        
        // Hit from left or right
        if (minOverlap == overlapLeft || minOverlap == overlapRight)
        {
            velocityX = -velocityX * bounceDamping;
            
            // Push ball out
            int push = (int) Math.max(4, Math.abs(velocityX) + 1);
            if (getX() < backboard.getX())
                setLocation(getX() - push, getY());
            else
                setLocation(getX() + push, getY());
        }
        // Hit from top or bottom
        else
        {
            velocityY = -velocityY * bounceDamping;
            
            // Push ball out
            int push = (int) Math.max(4, Math.abs(velocityY) + 1);
            if (getY() < backboard.getY())
                setLocation(getX(), getY() - push);
            else
                setLocation(getX(), getY() + push);
        }
        
        // Prevent tiny jittering
        if (Math.abs(velocityX) < 0.5) velocityX = 0;
        if (Math.abs(velocityY) < 0.5) velocityY = 0;
        
        // Minimum rebound
        if (Math.abs(velocityX) < 2 && velocityX != 0) 
            velocityX = (velocityX > 0 ? 2 : -2);
        if (Math.abs(velocityY) < 2 && velocityY != 0) 
            velocityY = (velocityY > 0 ? 2 : -2);
        
        // Sound
        if (soundEnabled && bounceSound != null && 
            (Math.abs(velocityX) > 2 || Math.abs(velocityY) > 2))
        {
            try {
                playBounceSound();
            } catch (Throwable t) {
                soundEnabled = false;
            }
        }
    }
}

  // --- Basket Collision ---
Basket basket = (Basket) getOneIntersectingObject(Basket.class);
if (basket != null)
{
    // Only score if the ball is moving downward through the rim area
if (basket.checkScore(this) && velocityY > 0)
{
    BasketballWorld world = (BasketballWorld) getWorld();
    world.addScore();

    // Reset ball
    //setLocation(100, 500);
    velocityX = 0;
    velocityY = 0;
    return;
}
    else
    {
        // Calculate relative position of the ball to the basket
        double dx = getX() - basket.getX();
        double dy = getY() - basket.getY();

        // --- CONDITIONS ---
        // Ignore collision if ball is ABOVE rim and moving downward (about to score)
        if (dy < -20 && velocityY > 0)
{
    return; // let it fall through naturally
}

        // --- SIDE OR UNDERNEATH COLLISION ---
        if (dy > 0 || Math.abs(dx) > 25)
        {
            // bounce from below or side
            if (dy > 0 && velocityY < 0) // coming upward
                velocityY = -velocityY * bounceDamping;
            else if (Math.abs(dx) > 25)
                velocityX = -velocityX * bounceDamping;

            // Push slightly away from rim to prevent sticking
            int push = (int) Math.max(3, Math.abs(velocityX) + 1);
            if (getX() < basket.getX())
                setLocation(getX() - push, getY());
            else
                setLocation(getX() + push, getY());

            // Damp tiny movements
            if (Math.abs(velocityX) < 0.3) velocityX = 0;
            if (Math.abs(velocityY) < 0.3) velocityY = 0;

            // Play bounce sound
            if (soundEnabled && bounceSound != null)
            {
                try {
                    playBounceSound();
                } catch (Throwable t) {
                    soundEnabled = false;
                }
            }
        }
    }
}
}

    
    private void checkBounds()
    {
        // Check ceiling collision
        if (getY() <= 20)
        {
            setLocation(getX(), 20);
            velocityY = -velocityY * bounceDamping;
            velocityX *= bounceDamping;
            
            // Play bounce sound if enabled
            if (soundEnabled && bounceSound != null && Math.abs(velocityY) > 2)
            {
                try {
                    playBounceSound();
                } catch (Throwable t) {
                    // Sound failed to play, continue silently
                    soundEnabled = false;
                }
            }
        }
        
        // Check floor collision (raised floor)
int groundY = getWorld().getHeight() - 45; // raise floor by 100 pixels

if (getY() >= groundY)
{
    setLocation(getX(), groundY);
    velocityY = -velocityY * bounceDamping;
    velocityX *= bounceDamping;
            // Play bounce sound if enabled
            if (soundEnabled && bounceSound != null && Math.abs(velocityY) > 2)
            {
                try {
                    playBounceSound();
                } catch (Throwable t) {
                    // Sound failed to play, continue silently
                    soundEnabled = false;
                }
            }
            
            // If ball is barely moving, stop it
            if (Math.abs(velocityY) < 1 && Math.abs(velocityX) < 1)
            {
                velocityY = 0;
                velocityX = 0;
            }
        }
        
        // Check wall collisions
        if (getX() <= 20)
        {
            setLocation(20, getY());
            velocityX = -velocityX * bounceDamping;
            
            // Play wall bounce sound
            if (soundEnabled && bounceSound != null && Math.abs(velocityX) > 2)
            {
                try {
                    bounceSound.play();
                } catch (Throwable t) {
                    // Sound failed to play, continue silently
                    soundEnabled = false;
                }
            }
        }
        if (getX() >= getWorld().getWidth() - 20)
        {
            setLocation(getWorld().getWidth() - 20, getY());
            velocityX = -velocityX * bounceDamping;
            
            // Play wall bounce sound
            if (soundEnabled && bounceSound != null && Math.abs(velocityX) > 2)
            {
                try {
                    playBounceSound();
                } catch (Throwable t) {
                    // Sound failed to play, continue silently
                    soundEnabled = false;
                }
            }
        }
    }
    
public void shoot(double deltaX, double deltaY, double power)
{
    // Normalize the direction and apply power (INVERTED)
    double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
    if (distance > 0)
    {
        velocityX = -(deltaX / distance) * (power / 10.0);
        velocityY = -(deltaY / distance) * (power / 10.0);
    }
}

    
    public double getVelocityX()
    {
        return velocityX;
    }
    
    public double getVelocityY()
    {
        return velocityY;
    }
    
    private void playBounceSound() {
    if (!soundEnabled) return;

    try {
        GreenfootSound s = new GreenfootSound("sounds/bounce.wav");
        s.play();
    } catch (Throwable t) {
        soundEnabled = false;
    }
}

public void addVelocity(double vx, double vy)
{
    velocityX += vx;
    velocityY += vy;
}
}
