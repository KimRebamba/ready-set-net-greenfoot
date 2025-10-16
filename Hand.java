import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Defense hand that can block or hit the basketball.
 * Controls: W (bounce), A (left), S (down), D (right), Space (jump higher)
 */
public class Hand extends Actor
{
    private double velocityX = 0;
    private double velocityY = 0;
    private final double gravity = 0.2;
    private final double bounceDamping = 0.8;
    private final double friction = 0.95;
    private final double moveSpeed = 7.0;
    private final double jumpPower = 15.0;
    private final double bouncePower = 4.0;
    private boolean onGround = false;
    private int groundY = 0;
    private boolean facingLeft = true;

    public Hand()
    {
        GreenfootImage handImage = new GreenfootImage("images/hand.png");
        handImage.scale(70, 70);
        setImage(handImage);
    }

    public void act()
    {
        handleInput();
        applyPhysics();
        checkGround();
        checkBounds();
        mirrorToBall();
        hitBall(); // ⚡ new feature
        
}

    private void handleInput()
    {
      if (Greenfoot.isKeyDown("a") || Greenfoot.isKeyDown("left")) {
    velocityX = -moveSpeed;
}
if (Greenfoot.isKeyDown("d") || Greenfoot.isKeyDown("right")) {
    velocityX = moveSpeed;
}
if ((Greenfoot.isKeyDown("s") || Greenfoot.isKeyDown("down")) && !onGround) {
    velocityY += 1.0;
}
        if ((Greenfoot.isKeyDown("space") || Greenfoot.isKeyDown("w") || Greenfoot.isKeyDown("up")) && onGround)
        {
            velocityY = -jumpPower;
            onGround = false;
        }
    }

    private void applyPhysics()
    {
        if (!onGround)
            velocityY += gravity;

        velocityX *= friction;
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
        if (getY() <= 25)
        {
            setLocation(getX(), 25);
            velocityY = 0;
        }
    }

    private void mirrorToBall()
    {
        java.util.List<Basketball> balls = getWorld().getObjects(Basketball.class);
        if (balls.isEmpty()) return;

        Basketball ball = balls.get(0);
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

    // ⚡ NEW: Make the hand send the ball flying when touched
    private void hitBall()
    {
        Basketball ball = (Basketball) getOneIntersectingObject(Basketball.class);
        if (ball != null)
        {
            // Determine hit direction
            double dx = ball.getX() - getX();
            double dy = ball.getY() - getY();

            // Normalize the direction
            double length = Math.sqrt(dx * dx + dy * dy);
            if (length == 0) length = 1; // avoid divide-by-zero
            dx /= length;
            dy /= length;

            // Power of the hit — tweak for balance
            double power = 20.0;

            // Apply force to the ball
            ball.addVelocity(dx * power, dy * power * -0.6); // sends it flying up

            // Optional: small recoil to hand
            velocityX -= dx * 2;
            velocityY -= dy * 2;
        }
    }

    public double getVelocityX() { return velocityX; }
    public double getVelocityY() { return velocityY; }
    public boolean isOnGround() { return onGround; }
}
