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
    private final int GROUND_LEVEL_OFFSET = 27; // raised ground (same as player)
      
    public Volleyball() {
        GreenfootImage img = new GreenfootImage("volleyball.png");
        img.scale(80, 80);
        setImage(img);
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

    // --- Limit how high the ball can go ---
    if (dy < -15) dy = -15; // cap upward velocity (prevents rocket spikes)

    dx *= AIR_RESISTANCE;
    setLocation(getX() + (int)dx, getY() + (int)dy);

    // --- Prevent the ball from touching or clipping the ceiling ---
    int ceilingLimit = getImage().getHeight() / 2 + 50; // adjust the 50 for lower or higher arc
    if (getY() < ceilingLimit) {
        setLocation(getX(), ceilingLimit);
        dy = 0; // stop upward motion
    }
}
    
    private void checkCollisions() {
        // Wall collisions
        if (getX() <= 0 || getX() >= getWorld().getWidth()) {
            dx = -dx * BOUNCE_DAMPING;
        }
        
        // Ceiling collision
        if (getY() <= 0) {
            dy = Math.abs(dy) * BOUNCE_DAMPING;
        }
        
        // --- Raised ground collision ---
        int groundY = getWorld().getHeight() - GROUND_LEVEL_OFFSET;
        if (getY() >= groundY - getImage().getHeight() / 2) {
            setLocation(getX(), groundY - getImage().getHeight() / 2);
            dy = -Math.abs(dy) * BOUNCE_DAMPING;
            if (Math.abs(dy) < 1) dy = 0;
        }
        
        // --- Net collision ---
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

            // Overlap distances
            int overlapLeft = ballRight - netLeft;
            int overlapRight = netRight - ballLeft;
            int overlapTop = ballBottom - netTop;
            int overlapBottom = netBottom - ballTop;

            int minOverlap = Math.min(Math.min(overlapLeft, overlapRight), Math.min(overlapTop, overlapBottom));

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
       // --- Player collision ---
VolleyballPlayer player = (VolleyballPlayer)getOneIntersectingObject(VolleyballPlayer.class);
if (player != null) {
    double hitAngle;
    double hitSpeed = Math.sqrt(dx*dx + dy*dy) + 5;
    hitSpeed = Math.min(hitSpeed, 20);

    boolean isLeftPlayer = player.getX() < getWorld().getWidth() / 2;

    // --- Spike detection (upper hit zone) ---
    if (getY() < player.getY() - player.getImage().getHeight() / 3) {
        // SPIKE TIME 💥
        double spikeSpeed = 25 + Greenfoot.getRandomNumber(10); // fast and brutal

        // Correct spike angle: always downward toward the opponent’s court
        double spikeAngle;
        if (isLeftPlayer) {
            spikeAngle = Math.toRadians(-60); // down-right
        } else {
            spikeAngle = Math.toRadians(-120); // down-left
        }

        dx = Math.cos(spikeAngle) * spikeSpeed;
        dy = Math.sin(spikeAngle) * spikeSpeed;

        // Greenfoot.playSound("spike.wav");
    } 
    else {
        // Normal hit (side or lower part)
        hitAngle = Math.atan2(getY() - player.getY(), getX() - player.getX());
        hitAngle -= Math.PI / 12;

        dx = Math.cos(hitAngle) * hitSpeed;
        dy = Math.sin(hitAngle) * hitSpeed;
    }

    // Small offset so it doesn't get stuck inside player
    setLocation(
        getX() + (int)(Math.cos(Math.atan2(dy, dx)) * 10),
        getY() + (int)(Math.sin(Math.atan2(dy, dx)) * 10)
    );
}


    }
}
