import greenfoot.*;

/**
 * Defense hand that can block or hit the basketball.
 * Controls: W (bounce), A (left), S (down), D (right), Space (jump higher)
 * Can also operate in CPU mode for AI defense
 */
public class Hand extends Actor
{
    private double velocityX = 0;
    private double velocityY = 0;
    private final double gravity = 0.8;
    private final double bounceDamping = 0.8;
    private final double friction = 0.90;
    private final double moveSpeed = 7.0;
    private final double jumpPower = 27.0;
    private final double bouncePower = 4.0;
    private boolean onGround = false;
    private int groundY = 0;
    private boolean facingLeft = true;
    
    // CPU mode
    private boolean cpuMode = false;
    private String difficulty = "medium";
    private int cpuReactionDelay = 0;

    public Hand()
    {
        this(false, "medium");
    }
    
    public Hand(boolean isCPU, String diff)
    {
        this.cpuMode = isCPU;
        this.difficulty = diff;
        
        GreenfootImage handImage = new GreenfootImage("images/hand.png");
        handImage.scale(70, 70);
        setImage(handImage);
    }

    public void act()
    {
        // Route to appropriate input handler
        if (cpuMode) {
            handleCPUInput();
        } else {
            handleInput();
        }
        
        // Update position and collision
        applyPhysics();
        checkGround();
        checkBounds();
        mirrorToBall();
        hitBall();
    }
    
    private void handleCPUInput()
    {
        // Skip action if still in reaction delay period
        if (cpuReactionDelay > 0) {
            cpuReactionDelay--;
            return;
        }
        
        if (getWorld() == null) return;
        
        java.util.List<Basketball> balls = getWorld().getObjects(Basketball.class);
        if (balls.isEmpty()) return;
        
        Basketball ball = balls.get(0);
        
        // Set difficulty parameters
        int reactionTime = 0;
        int moveSpeedCPU = 0;
        int jumpChance = 0;
        
        switch (difficulty.toLowerCase()) {
            case "easy":
                reactionTime = 40;
                moveSpeedCPU = 3;
                jumpChance = 20;
                break;
            case "medium":
                reactionTime = 25;
                moveSpeedCPU = 5;
                jumpChance = 45;
                break;
            case "hard":
                reactionTime = 15;
                moveSpeedCPU = 7;
                jumpChance = 65;
                break;
            case "expert":
                reactionTime = 8;
                moveSpeedCPU = 8;
                jumpChance = 80;
                break;
            case "impossible":
                reactionTime = 0;
                moveSpeedCPU = 10;
                jumpChance = 95;
                break;
        }
        
        // Track ball position and move toward it
        int ballX = ball.getX();
        int handX = getX();
        
        if (ballX < handX - 10) {
            velocityX = -moveSpeedCPU;
        } else if (ballX > handX + 10) {
            velocityX = moveSpeedCPU;
        } else {
            velocityX = 0;
        }
        
        // Predict ball trajectory and jump if needed
        int ballY = ball.getY();
        int handY = getY();
        double ballVelocityY = ball.getVelocityY();
        
        // Calculate where ball will be shortly
        double predictedY = ballY + ballVelocityY * 5;
        
        // Attempt to intercept the ball
        if (ballVelocityY > 0 && predictedY > handY - 80 && onGround) {
            if (Greenfoot.getRandomNumber(100) < jumpChance) {
                velocityY = -jumpPower;
                onGround = false;
                cpuReactionDelay = reactionTime;
            }
        }
    }

    private void handleInput()
    {
        // Horizontal movement
        if (Greenfoot.isKeyDown("a") || Greenfoot.isKeyDown("left")) {
            velocityX = -moveSpeed;
        }
        if (Greenfoot.isKeyDown("d") || Greenfoot.isKeyDown("right")) {
            velocityX = moveSpeed;
        }
        
        // Downward acceleration when airborne
        if ((Greenfoot.isKeyDown("s") || Greenfoot.isKeyDown("down")) && !onGround) {
            velocityY += 1.0;
        }
        
        // Jump action
        if ((Greenfoot.isKeyDown("space") || Greenfoot.isKeyDown("w") || Greenfoot.isKeyDown("up")) && onGround)
        {
            velocityY = -jumpPower;
            onGround = false;
        }
    }

    private void applyPhysics()
    {
        // Apply gravity while in air
        if (!onGround)
            velocityY += gravity;

        // Reduce horizontal velocity over time
        velocityX *= friction;
        
        // Update position
        setLocation(getX() + (int) velocityX, getY() + (int) velocityY);
    }

    private void checkGround()
    {
        int groundY = getWorld().getHeight() - 45;

        if (getY() >= groundY)
        {
            setLocation(getX(), groundY);
            velocityY = 0;
            onGround = true;
            this.groundY = getY();
        }
        else
        {
            onGround = false;
        }
    }

    private void checkBounds()
    {
        // Keep hand within left/right bounds
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
        
        // Bounce off ceiling
        if (getY() <= 25)
        {
            setLocation(getX(), 25);
            velocityY = Math.abs(velocityY) * 0.5;
        }
    }

    private void mirrorToBall()
    {
        java.util.List<Basketball> balls = getWorld().getObjects(Basketball.class);
        if (balls.isEmpty()) return;

        Basketball ball = balls.get(0);
        // Flip hand sprite to face the ball
        boolean ballOnRight = ball.getX() > getX();
        GreenfootImage img = getImage();

        if (ballOnRight && facingLeft)
        {
            img.mirrorHorizontally();
            facingLeft = false;
            setImage(img);
        }
        else if (!ballOnRight && !facingLeft)
        {
            img.mirrorHorizontally();
            facingLeft = true;
            setImage(img);
        }
    }

    private void hitBall()
    {
        Basketball ball = (Basketball) getOneIntersectingObject(Basketball.class);
        if (ball != null)
        {
            // Calculate direction from hand to ball
            double dx = ball.getX() - getX();
            double dy = ball.getY() - getY();

            // Convert to unit vector
            double length = Math.sqrt(dx * dx + dy * dy);
            if (length == 0) length = 1;
            dx /= length;
            dy /= length;

            // Impact force strength
            double power = 20.0;

            // Hit ball away from hand
            ball.addVelocity(dx * power, dy * power * -0.6);

            // Give hand recoil
            velocityX -= dx * 2;
            velocityY -= dy * 2;
        }
    }

    public double getVelocityX() { return velocityX; }
    public double getVelocityY() { return velocityY; }
    public boolean isOnGround() { return onGround; }
}