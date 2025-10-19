import greenfoot.*;

public class Basketball extends Actor
{
    private double velocityX = 0;
    private double velocityY = 0;
    private final double gravity = 0.3;
    private final double bounceDamping = 0.7;
    private final double friction = 0.98;
    private GreenfootSound bounceSound;
    private GreenfootSound scoreSound;
    private GreenfootSound backboardSound;
    private GreenfootSound smackSound;
    private boolean soundEnabled = true;
    
    private int prevX = 0;
    private int prevY = 0;
    
    public int getPrevY()
    {
        return prevY;
    }
    
    public Basketball()
    {
        GreenfootImage ballImage = new GreenfootImage("images/basketball.png");
        ballImage.scale(42, 42);
        setImage(ballImage);
        
        try {
            smackSound = new GreenfootSound("sounds/basketball_smack.wav");
            bounceSound = new GreenfootSound("sounds/basketball_bounce.wav");
            scoreSound = new GreenfootSound("sounds/basketball_score.wav");
            backboardSound = new GreenfootSound("sounds/basketball_backboard.wav");
        } catch (Throwable t) {
            System.out.println("Could not load one or more sounds: " + t.getMessage());
            soundEnabled = false;
        }
    }
    
    public void act()
    {
        applyPhysics();
        checkCollisions();
        checkBounds();
        
        // Store current position for next frame to detect crossing the rim
        prevX = getX();
        prevY = getY();
    }
    
    private void applyPhysics()
    {
        // (all physics code below is AI-generated since im dumb af like damn, ball physics)
        velocityY += gravity;
        velocityX *= friction;
        setLocation(getX() + (int) velocityX, getY() + (int) velocityY);
    }
    
    public int getRimY() {
        return getY() - 10;
    }

    private void checkCollisions()
    {
        // Hand collision - when player touches the ball
        Hand hand = (Hand) getOneIntersectingObject(Hand.class);
        if (hand != null)
        {
            if (soundEnabled && smackSound != null)
            {
                try {
                    if (smackSound.isPlaying()) smackSound.stop();
                    smackSound.play();
                } catch (Throwable t) {
                    soundEnabled = false;
                }
            }

            // (all the physics stuff below - AI magic)
            double dx = getX() - hand.getX();
            double dy = getY() - hand.getY();
            double distance = Math.sqrt(dx * dx + dy * dy);
            double minDistance = 35;

            if (distance < minDistance)
            {
                double nx = dx / distance;
                double ny = dy / distance;

                setLocation(
                    (int)(hand.getX() + nx * minDistance),
                    (int)(hand.getY() + ny * minDistance)
                );

                double dot = velocityX * nx + velocityY * ny;
                velocityX -= 2 * dot * nx;
                velocityY -= 2 * dot * ny;

                velocityX *= bounceDamping;
                velocityY *= bounceDamping;

                velocityX += hand.getVelocityX() * 0.5;
                velocityY += hand.getVelocityY() * 0.3;

                if (Math.abs(velocityX) < 0.3) velocityX = 0;
                if (Math.abs(velocityY) < 0.3) velocityY = 0;
            }
        }

        // Boundary collision - hit walls/obstacles
        Boundary boundary = (Boundary) getOneIntersectingObject(Boundary.class);
        if (boundary != null)
        {
            // (more physics - AI handled this)
            double dx = getX() - boundary.getX();
            double dy = getY() - boundary.getY();
            double distance = Math.sqrt(dx * dx + dy * dy);
            double minDistance = (boundary.getWidth() / 2.0 + 20);

            if (distance < minDistance)
            {
                double nx = dx / distance;
                double ny = dy / distance;

                setLocation(
                    (int)(boundary.getX() + nx * minDistance),
                    (int)(boundary.getY() + ny * minDistance)
                );

                double dot = velocityX * nx + velocityY * ny;
                velocityX -= 2 * dot * nx;
                velocityY -= 2 * dot * ny;

                velocityX *= bounceDamping;
                velocityY *= bounceDamping;

                if (Math.abs(velocityX) < 0.3) velocityX = 0;
                if (Math.abs(velocityY) < 0.3) velocityY = 0;

                if (soundEnabled && bounceSound != null)
                {
                    try { 
                        if (soundEnabled && bounceSound != null) bounceSound.play(); 
                    } catch (Throwable t) { 
                        soundEnabled = false; 
                    }
                }
            }
        }
        
        // Backboard collision - ball bounces off the backboard
        Backboard backboard = (Backboard) getOneIntersectingObject(Backboard.class);
        if (backboard != null)
        {
            // (physics collision detection - AI knows better than me)
            int ballLeft = getX() - 21;
            int ballRight = getX() + 21;
            int ballTop = getY() - 21;
            int ballBottom = getY() + 21;
            
            int bbLeft = backboard.getLeft();
            int bbRight = backboard.getRight();
            int bbTop = backboard.getTop();
            int bbBottom = backboard.getBottom();
            
            if (ballRight > bbLeft && ballLeft < bbRight && 
                ballBottom > bbTop && ballTop < bbBottom)
            {
                int overlapLeft = ballRight - bbLeft;
                int overlapRight = bbRight - ballLeft;
                int overlapTop = ballBottom - bbTop;
                int overlapBottom = bbBottom - ballTop;
                
                int minOverlap = Math.min(Math.min(overlapLeft, overlapRight), 
                                           Math.min(overlapTop, overlapBottom));
                
                // Hit from left or right side
                if (minOverlap == overlapLeft || minOverlap == overlapRight)
                {
                    velocityX = -velocityX * bounceDamping;
                    
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
                    
                    int push = (int) Math.max(4, Math.abs(velocityY) + 1);
                    if (getY() < backboard.getY())
                        setLocation(getX(), getY() - push);
                    else
                        setLocation(getX(), getY() + push);
                }
                
                if (Math.abs(velocityX) < 0.5) velocityX = 0;
                if (Math.abs(velocityY) < 0.5) velocityY = 0;
                
                if (Math.abs(velocityX) < 2 && velocityX != 0) 
                    velocityX = (velocityX > 0 ? 2 : -2);
                if (Math.abs(velocityY) < 2 && velocityY != 0) 
                    velocityY = (velocityY > 0 ? 2 : -2);
                
                if (soundEnabled && backboardSound != null &&
                    (Math.abs(velocityX) > 2 || Math.abs(velocityY) > 2))
                {
                    backboardSound.play();
                }
            }
        }

        // Basket collision - check for scoring or bounce off rim
        Basket basket = (Basket) getOneIntersectingObject(Basket.class);
        if (basket != null)
        {
            // Check if ball scored in the basket
            if (basket.checkScore(this) && velocityY > 0)
            {
                if (soundEnabled && scoreSound != null) scoreSound.play();
                BasketballWorld world = (BasketballWorld) getWorld();
                world.addScore();

                velocityX = 0;
                velocityY = 0;
                return;
            }
            else
            {
                double dx = getX() - basket.getX();
                double dy = getY() - basket.getY();

                // Don't bounce if ball is above rim and falling through
                if (dy < -20 && velocityY > 0)
                {
                    return;
                }

                // Bounce from below or side of basket
                if (dy > 0 || Math.abs(dx) > 25)
                {
                    if (dy > 0 && velocityY < 0)
                        velocityY = -velocityY * bounceDamping;
                    else if (Math.abs(dx) > 25)
                        velocityX = -velocityX * bounceDamping;

                    int push = (int) Math.max(3, Math.abs(velocityX) + 1);
                    if (getX() < basket.getX())
                        setLocation(getX() - push, getY());
                    else
                        setLocation(getX() + push, getY());

                    if (Math.abs(velocityX) < 0.3) velocityX = 0;
                    if (Math.abs(velocityY) < 0.3) velocityY = 0;

                    if (soundEnabled && bounceSound != null)
                    {
                        try {
                            if (soundEnabled && bounceSound != null) bounceSound.play();
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
        // Ceiling collision
        if (getY() <= 20)
        {
            setLocation(getX(), 20);
            velocityY = -velocityY * bounceDamping;
            velocityX *= bounceDamping;
            
            if (soundEnabled && bounceSound != null && Math.abs(velocityY) > 2)
            {
                try {
                   if (soundEnabled && bounceSound != null) bounceSound.play();
                } catch (Throwable t) {
                    soundEnabled = false;
                }
            }
        }
        
        // Floor collision
        int groundY = getWorld().getHeight() - 45;

        if (getY() >= groundY)
        {
            setLocation(getX(), groundY);
            velocityY = -velocityY * bounceDamping;
            velocityX *= bounceDamping;
            
            if (soundEnabled && bounceSound != null && Math.abs(velocityY) > 2)
            {
                try {
                    if (soundEnabled && bounceSound != null) bounceSound.play();
                } catch (Throwable t) {
                    soundEnabled = false;
                }
            }
            
            // Stop ball if it's barely moving
            if (Math.abs(velocityY) < 1 && Math.abs(velocityX) < 1)
            {
                velocityY = 0;
                velocityX = 0;
            }
        }
        
        // Left wall collision
        if (getX() <= 20)
        {
            setLocation(20, getY());
            velocityX = -velocityX * bounceDamping;
            
            if (soundEnabled && bounceSound != null && Math.abs(velocityX) > 2)
            {
                try {
                    bounceSound.play();
                } catch (Throwable t) {
                    soundEnabled = false;
                }
            }
        }
        
        // Right wall collision
        if (getX() >= getWorld().getWidth() - 20)
        {
            setLocation(getWorld().getWidth() - 20, getY());
            velocityX = -velocityX * bounceDamping;
            
            if (soundEnabled && bounceSound != null && Math.abs(velocityX) > 2)
            {
                try {
                    if (soundEnabled && bounceSound != null) bounceSound.play();
                } catch (Throwable t) {
                    soundEnabled = false;
                }
            }
        }
    }
    
    public void shoot(double deltaX, double deltaY, double power)
    {
        // Calculate shooting direction and apply power to launch ball
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

    public void addVelocity(double vx, double vy)
    {
        velocityX += vx;
        velocityY += vy;
    }
}