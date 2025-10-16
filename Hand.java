import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Defense hand that can block the basketball.
 * Controls: W (bounce), A (left), S (down), D (right), Space (jump higher)
 * 
 * @author (your name)
 * @version (a version number or a date)
 */
public class Hand extends Actor
{
    private double velocityX = 0;
    private double velocityY = 0;
    private final double gravity = 0.2;
    private final double bounceDamping = 0.8;
    private final double friction = 0.95;
    private final double moveSpeed = 3.0;
    private final double jumpPower = 8.0;
    private final double bouncePower = 2.0;
    private boolean onGround = false;
    private int groundY = 0;
    
    public Hand()
    {
        GreenfootImage handImage = new GreenfootImage("images/hand.png");
        handImage.scale(50, 50);
        setImage(handImage);
    }
    
    public void act()
    {
        handleInput();
        applyPhysics();
        checkGround();
        checkBounds();
    }
    
    private void handleInput()
    {
        // W - Bounce (small upward movement)
        if (Greenfoot.isKeyDown("w"))
        {
            if (onGround)
            {
                velocityY = -bouncePower;
                onGround = false;
            }
        }
        
        // A - Move left
        if (Greenfoot.isKeyDown("a"))
        {
            velocityX = -moveSpeed;
        }
        
        // S - Move down (if not on ground)
        if (Greenfoot.isKeyDown("s"))
        {
            if (!onGround)
            {
                velocityY += 1.0; // Accelerate downward
            }
        }
        
        // D - Move right
        if (Greenfoot.isKeyDown("d"))
        {
            velocityX = moveSpeed;
        }
        
        // Space - Jump higher
        if (Greenfoot.isKeyDown("space"))
        {
            if (onGround)
            {
                velocityY = -jumpPower;
                onGround = false;
            }
        }
    }
    
    private void applyPhysics()
    {
        // Apply gravity
        if (!onGround)
        {
            velocityY += gravity;
        }
        
        // Apply friction to horizontal movement
        velocityX *= friction;
        
        // Update position
        setLocation(getX() + (int) velocityX, getY() + (int) velocityY);
    }
    
    private void checkGround()
    {
        // Check if hand is on the ground (bottom of the world)
        if (getY() >= getWorld().getHeight() - 25)
        {
            setLocation(getX(), getWorld().getHeight() - 25);
            velocityY = 0;
            onGround = true;
            groundY = getY();
        }
        else
        {
            onGround = false;
        }
    }
    
    private void checkBounds()
    {
        // Check wall collisions
        if (getX() <= 25)
        {
            setLocation(25, getY());
            velocityX = 0;
        }
        if (getX() >= getWorld().getWidth() - 25)
        {
            setLocation(getWorld().getWidth() - 25, getY());
            velocityX = 0;
        }
        
        // Check ceiling collision
        if (getY() <= 25)
        {
            setLocation(getX(), 25);
            velocityY = 0;
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
    
    public boolean isOnGround()
    {
        return onGround;
    }
}
