import greenfoot.*;
public class Shuttlecock extends Actor {
    private double dx = 0;
    private double dy = 0;
    private final double GRAVITY = 0.3;
    private final double DRAG = 0.985;
    private final double BOUNCE_DAMPING = 0.6;
    private final int GROUND_LEVEL_OFFSET = 35;
    private final double HIT_DISTANCE_MULTIPLIER = 1.5; // Adjust this to make hits go further/shorter
    private double rotation = 0;
    private GreenfootImage baseImage;
    private boolean facingRight = true;
    private GreenfootSound racketHitSound = new GreenfootSound("racket_sound.wav");
private GreenfootSound racketSmashSound = new GreenfootSound("racket_smash.wav");


    public Shuttlecock() {
        baseImage = new GreenfootImage("shuttlecock.png");
        baseImage.scale(35, 35);
        setImage(baseImage);
        dx = Greenfoot.getRandomNumber(2) == 0 ? -5 : 5;
        dy = -8;
    }
    
    public void act() {
        applyPhysics();
        updateRotation();
        checkCollisions();
    }
    
    private void applyPhysics() {
        dy += GRAVITY;
        
        // Limit how high the shuttlecock can go (cap upward velocity)
        if (dy < -12) dy = -12;
        
        dx *= DRAG;
        dy *= DRAG;
        
        setLocation(getX() + (int)dx, getY() + (int)dy);
        
        // Prevent ball from touching the ceiling
        int ceilingLimit = getImage().getHeight() / 2 + 40;
        if (getY() < ceilingLimit) {
            setLocation(getX(), ceilingLimit);
            dy = 0;
        }
        
        if (dx > 0 && !facingRight) {
            facingRight = true;
            updateImageFacing();
        } else if (dx < 0 && facingRight) {
            facingRight = false;
            updateImageFacing();
        }
    }
    
    private void updateImageFacing() {
        GreenfootImage img = new GreenfootImage(baseImage);
        if (!facingRight) {
            img.mirrorHorizontally();
        }
        setImage(img);
    }

    private void updateRotation() {
        double speed = Math.sqrt(dx * dx + dy * dy);
        if (speed > 0.5) {
            double angle = Math.atan2(dy, dx);
            rotation = Math.toDegrees(angle);
        }
        
        GreenfootImage rotatedImage = new GreenfootImage(baseImage);
        rotatedImage.rotate((int)rotation);
        setImage(rotatedImage);
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
        
        // Net collision
        BadmintonNet net = (BadmintonNet)getOneIntersectingObject(BadmintonNet.class);
        if (net != null) {
            dx = -dx * BOUNCE_DAMPING;
            setLocation(getX() + (dx > 0 ? 10 : -10), getY());
        }
        
        BadmintonPlayer player = (BadmintonPlayer)getOneIntersectingObject(BadmintonPlayer.class);
if (player != null) {
    double relativeX = getX() - player.getX();
    double relativeY = getY() - player.getY();
    
    if (player.isSwinging()) {
        // 🔊 Smash hit sound
        if (racketSmashSound.isPlaying()) racketSmashSound.stop();
        racketSmashSound.play();
        
        // Active swing - stronger hit with directional control
        double impactAngle = Math.atan2(relativeY, relativeX);
                
                double hitSpeed = 15.0;
                double verticalModifier = 1.0;
                
                // Adjust trajectory based on where shuttle hits the racket
                if (relativeY < -20) {  // Shuttle below racket - smash downward
                    verticalModifier = 1.8;
                    hitSpeed *= 1.2;  // Faster smash
                    dy = Math.abs(hitSpeed * verticalModifier);  // Force downward
                } else if (relativeY > 20) {  // Shuttle above racket - hit upward
                    verticalModifier = 0.5;
                    hitSpeed *= 1.3;  // Faster shot
                    dy = -Math.abs(hitSpeed * verticalModifier);  // Force upward
                } else {  // Middle of racket
                    hitSpeed *= 1.1;
                    dy = Math.sin(impactAngle) * hitSpeed * verticalModifier - 8;
                }
                
                // Calculate horizontal velocity
                dx = Math.cos(impactAngle) * hitSpeed * HIT_DISTANCE_MULTIPLIER;
                
                // Ensure proper direction based on player side
                int netX = getWorld().getWidth() / 2;
                if (player.getX() < netX) {
                    dx = Math.abs(dx);  // Left player hits right
                } else {
                    dx = -Math.abs(dx); // Right player hits left
                }
                
                // Move shuttle away from racket
                setLocation(getX() + (int)(dx > 0 ? 25 : -25), 
                           getY() + (int)(dy > 0 ? 25 : -25));
            } else {
        // 🔊 Normal racket hit sound
        if (racketHitSound.isPlaying()) racketHitSound.stop();
        racketHitSound.play();
        
        // Stationary racket - sends shuttle across with arc
        int netX = getWorld().getWidth() / 2;;
                
                if (relativeY < -15) {  // Top of racket - upward arc
                    dy = -10.0;  // Strong upward velocity
                    if (player.getX() < netX) {
                        dx = 10.0;  // Left player sends right (increased from 8)
                    } else {
                        dx = -10.0;  // Right player sends left (increased from -8)
                    }
                } else if (relativeY > 15) {  // Bottom of racket - lower arc
                    dy = -5.0;  // Moderate upward velocity
                    if (player.getX() < netX) {
                        dx = 10.0;  // (increased from 8)
                    } else {
                        dx = -10.0;  // (increased from -8)
                    }
                } else {  // Middle of racket - medium arc
                    dy = -8.0;
                    if (player.getX() < netX) {
                        dx = 10.0;  // (increased from 8)
                    } else {
                        dx = -10.0;  // (increased from -8)
                    }
                }
                
                // Move shuttle away from racket
                setLocation(getX() + (int)(dx > 0 ? 15 : -15), 
                           getY() - 10);
            }
        }
        
        // Ground collision
        int groundY = getWorld().getHeight() - GROUND_LEVEL_OFFSET;
        if (getY() >= groundY) {
            setLocation(getX(), groundY);
            dy = 0;
            dx = 0;
        }
    }
    
    public void setInitialVelocity(double dx, double dy) {
    this.dx = dx;
    this.dy = dy;
}
}