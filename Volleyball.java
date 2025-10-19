import greenfoot.*;

public class Volleyball extends Actor {
    private double dx = 0;
    private double dy = 0;
    private final double GRAVITY = 0.4;
    private final double BOUNCE_DAMPING = 0.7;
    private final double AIR_RESISTANCE = 0.99;
    private final double NET_BOUNCE_STRENGTH = 1.2;
    private int lastNetHitFrame = -10;
    private static int frameCounter = 0;
    private final int GROUND_LEVEL_OFFSET = 27;
    private final int SPIKE_PROXIMITY = 80;
    private final int SPIKE_HEIGHT = 60;
    
    private GreenfootSound volleyballSound;
    private GreenfootSound volleyballSmash;
    private boolean soundEnabled = true;
    
    public double getVelocityX() {
        return dx;
    }

    public double getVelocityY() {
        return dy;
    }
    
    public void setVelocity(double x, double y) {
        dx = x;
        dy = y;
    }

    public void playSmashSound() {
        GreenfootSound smash = new GreenfootSound("volleyball_smash.wav");
        smash.play();
    }

    public Volleyball() {
        GreenfootImage img = new GreenfootImage("volleyball.png");
        img.scale(80, 80);
        setImage(img);
        
        try {
            volleyballSound = new GreenfootSound("basketball_bounce.wav");
            volleyballSmash = new GreenfootSound("volleyball_smash.wav");
        } catch (Throwable t) {
            System.out.println("Could not load volleyball sounds: " + t.getMessage());
            soundEnabled = false;
        }
    }
    
    public void act() {
        frameCounter++;
        applyPhysics();
        checkCollisions();
    }
    
    public void setInitialVelocity(double dx, double dy) {
        this.dx = dx;
        this.dy = dy;
    }
    
    private void applyPhysics() {
        dy += GRAVITY;

        // Cap upward velocity to prevent unrealistic rocket-like spikes
        if (dy < -15) dy = -15;

        dx *= AIR_RESISTANCE;
        setLocation(getX() + (int)dx, getY() + (int)dy);

        // Keep ball from going through ceiling
        int ceilingLimit = getImage().getHeight() / 2 + 50;
        if (getY() < ceilingLimit) {
            setLocation(getX(), ceilingLimit);
            dy = 0;
        }
    }
    
    private void checkCollisions() {
        // Handle ball bouncing off left and right walls
        if (getX() <= 0 || getX() >= getWorld().getWidth()) {
            dx = -dx * BOUNCE_DAMPING;
        }
        
        // Handle ball bouncing off ceiling
        if (getY() <= 0) {
            dy = Math.abs(dy) * BOUNCE_DAMPING;
        }
        
        // Handle ball bouncing off the ground
        int groundY = getWorld().getHeight() - GROUND_LEVEL_OFFSET;
        if (getY() >= groundY - getImage().getHeight() / 2) {
            setLocation(getX(), groundY - getImage().getHeight() / 2);
            dy = -Math.abs(dy) * BOUNCE_DAMPING;
            if (Math.abs(dy) < 1) dy = 0;
        }
        
        // Check for collision with the net
        VolleyballNet net = (VolleyballNet)getOneIntersectingObject(VolleyballNet.class);
        if (net != null && frameCounter - lastNetHitFrame > 5) {
            lastNetHitFrame = frameCounter;

            int ballLeft = getX() - getImage().getWidth() / 2;
            int ballRight = getX() + getImage().getWidth() / 2;
            int ballTop = getY() - getImage().getHeight() / 2;
            int ballBottom = getY() + getImage().getHeight() / 2;

            int netLeft = net.getX() - net.getImage().getWidth() / 2;
            int netRight = net.getX() + net.getImage().getWidth() / 2;
            int netTop = net.getY() - net.getImage().getHeight() / 2;
            int netBottom = net.getY() + net.getImage().getHeight() / 2;

            // Calculate how far the ball overlaps into the net on each side
            int overlapLeft = ballRight - netLeft;
            int overlapRight = netRight - ballLeft;
            int overlapTop = ballBottom - netTop;
            int overlapBottom = netBottom - ballTop;

            // Find the smallest overlap to determine collision direction
            int minOverlap = Math.min(Math.min(overlapLeft, overlapRight), Math.min(overlapTop, overlapBottom));

            // Bounce ball away from the direction of smallest overlap
            if (minOverlap == overlapLeft) {
                dx = -Math.abs(dx) * BOUNCE_DAMPING;
                setLocation(netLeft - getImage().getWidth() / 2 - 2, getY());
            } 
            else if (minOverlap == overlapRight) {
                dx = Math.abs(dx) * BOUNCE_DAMPING;
                setLocation(netRight + getImage().getWidth() / 2 + 2, getY());
            } 
            else if (minOverlap == overlapTop) {
                dy = -Math.abs(dy) * 0.9;
                setLocation(getX(), netTop - getImage().getHeight() / 2 - 3);
                if (Math.abs(dx) < 1) dx += (Greenfoot.getRandomNumber(2) - 0.5) * 2;
            } 
            else if (minOverlap == overlapBottom) {
                dy = Math.abs(dy) * BOUNCE_DAMPING;
                setLocation(getX(), netBottom + getImage().getHeight() / 2 + 2);
            }
        }

        // Check for collision with player
        VolleyballPlayer player = (VolleyballPlayer)getOneIntersectingObject(VolleyballPlayer.class);
        if (player != null) {
            double hitAngle;
            double hitSpeed = Math.sqrt(dx*dx + dy*dy) + 5;
            hitSpeed = Math.min(hitSpeed, 20);

            boolean isLeftPlayer = player.getX() < getWorld().getWidth() / 2;

            // Determine if this is a spike (ball hit from above with player underneath)
            int horizontalDistance = Math.abs(getX() - player.getX());
            int verticalDistance = player.getY() - getY();
            if (verticalDistance > 0 && verticalDistance < SPIKE_HEIGHT && horizontalDistance < SPIKE_PROXIMITY) {
                // Execute spike with high speed and downward angle
                double spikeSpeed = 25 + Greenfoot.getRandomNumber(10);
                double spikeAngle = isLeftPlayer ? Math.toRadians(-60) : Math.toRadians(-120);

                dx = Math.cos(spikeAngle) * spikeSpeed;
                dy = Math.sin(spikeAngle) * spikeSpeed;

                // Play smash sound for spike
                if (soundEnabled && volleyballSmash != null) {
                    try {
                        
                        volleyballSmash.play();
                    } catch (Throwable t) {
                        soundEnabled = false;
                    }
                }
            } 
            else {
                // Regular hit from the side or lower part of player
                hitAngle = Math.atan2(getY() - player.getY(), getX() - player.getX());
                hitAngle -= Math.PI / 12;

                dx = Math.cos(hitAngle) * hitSpeed;
                dy = Math.sin(hitAngle) * hitSpeed;

                // Play regular hit sound
                if (soundEnabled && volleyballSound != null) {
                    try {
                        
                        volleyballSound.play();
                    } catch (Throwable t) {
                        soundEnabled = false;
                    }
                }
            }

            // Move ball away from player to prevent it from getting stuck
            setLocation(
                getX() + (int)(Math.cos(Math.atan2(dy, dx)) * 10),
                getY() + (int)(Math.sin(Math.atan2(dy, dx)) * 10)
            );
        }
    }
}