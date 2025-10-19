import greenfoot.*;
public class Shuttlecock extends Actor {
    private double dx = 0;
    private double dy = 0;
    private final double GRAVITY = 0.3;
    private final double DRAG = 0.985;
    private final double BOUNCE_DAMPING = 0.6;
    private final int GROUND_LEVEL_OFFSET = 35;
    private final double HIT_DISTANCE_MULTIPLIER = 1.5; // Controls hit distance strength
    private double rotation = 0;
    private GreenfootImage baseImage;
    private boolean facingRight = true;
    private GreenfootSound racketHitSound = new GreenfootSound("racket_sound.wav");
    private GreenfootSound racketSmashSound = new GreenfootSound("racket_smash.wav");

    public Shuttlecock() {
        baseImage = new GreenfootImage("shuttlecock.png");
        baseImage.scale(35, 35);
        setImage(baseImage);
        // Start with random horizontal direction and upward velocity
        dx = Greenfoot.getRandomNumber(2) == 0 ? -5 : 5;
        dy = -8;
    }
    
    public void act() {
        applyPhysics();
        updateRotation();
        checkCollisions();
    }
    
    private void applyPhysics() {
        // Apply gravity to vertical velocity
        dy += GRAVITY;
        
        // Cap maximum upward velocity
        if (dy < -12) dy = -12;
        
        // Apply air resistance
        dx *= DRAG;
        dy *= DRAG;
        
        setLocation(getX() + (int)dx, getY() + (int)dy);
        
        // Stop shuttlecock from going through ceiling
        int ceilingLimit = getImage().getHeight() / 2 + 40;
        if (getY() < ceilingLimit) {
            setLocation(getX(), ceilingLimit);
            dy = 0;
        }
        
        // Flip image based on direction of travel
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
        // Calculate speed and angle for rotation effect
        double speed = Math.sqrt(dx * dx + dy * dy);
        if (speed > 0.5) {
            double angle = Math.atan2(dy, dx);
            rotation = Math.toDegrees(angle);
        }
        
        // Rotate image based on movement direction
        GreenfootImage rotatedImage = new GreenfootImage(baseImage);
        rotatedImage.rotate((int)rotation);
        setImage(rotatedImage);
    }
    
    private void checkCollisions() {
        // Bounce off left and right walls
        if (getX() <= 0 || getX() >= getWorld().getWidth()) {
            dx = -dx * BOUNCE_DAMPING;
        }
        
        // Bounce off ceiling
        if (getY() <= 0) {
            dy = Math.abs(dy) * BOUNCE_DAMPING;
        }
        
        // Bounce off net
        BadmintonNet net = (BadmintonNet)getOneIntersectingObject(BadmintonNet.class);
        if (net != null) {
            dx = -dx * BOUNCE_DAMPING;
            setLocation(getX() + (dx > 0 ? 10 : -10), getY());
        }
        
        // Handle player racket collision
        BadmintonPlayer player = (BadmintonPlayer)getOneIntersectingObject(BadmintonPlayer.class);
        if (player != null) {
            double relativeX = getX() - player.getX();
            double relativeY = getY() - player.getY();
            
            if (player.isSwinging()) {
                // Play smash sound for active swing
                if (racketSmashSound.isPlaying()) racketSmashSound.stop();
                racketSmashSound.play();
                
                // Calculate impact angle for directional control
                double impactAngle = Math.atan2(relativeY, relativeX);
                
                double hitSpeed = 15.0;
                double verticalModifier = 1.0;
                
                // Different trajectory based on shuttle position on racket
                if (relativeY < -20) {
                    // Hit from below - smash downward
                    verticalModifier = 1.8;
                    hitSpeed *= 1.2;
                    dy = Math.abs(hitSpeed * verticalModifier);
                } else if (relativeY > 20) {
                    // Hit from above - lift upward
                    verticalModifier = 0.5;
                    hitSpeed *= 1.3;
                    dy = -Math.abs(hitSpeed * verticalModifier);
                } else {
                    // Center hit - medium arc
                    hitSpeed *= 1.1;
                    dy = Math.sin(impactAngle) * hitSpeed * verticalModifier - 8;
                }
                
                // Set horizontal velocity
                dx = Math.cos(impactAngle) * hitSpeed * HIT_DISTANCE_MULTIPLIER;
                
                // Ensure shuttle goes toward opposite side of net
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
                // Play normal sound for stationary racket
                if (racketHitSound.isPlaying()) racketHitSound.stop();
                racketHitSound.play();
                
                // Gentle arc when racket is not actively swinging
                int netX = getWorld().getWidth() / 2;
                
                if (relativeY < -15) {
                    // Top of racket - strong upward arc
                    dy = -10.0;
                    if (player.getX() < netX) {
                        dx = 10.0;
                    } else {
                        dx = -10.0;
                    }
                } else if (relativeY > 15) {
                    // Bottom of racket - lower arc
                    dy = -5.0;
                    if (player.getX() < netX) {
                        dx = 10.0;
                    } else {
                        dx = -10.0;
                    }
                } else {
                    // Middle of racket - balanced arc
                    dy = -8.0;
                    if (player.getX() < netX) {
                        dx = 10.0;
                    } else {
                        dx = -10.0;
                    }
                }
                
                // Move shuttle away from racket
                setLocation(getX() + (int)(dx > 0 ? 15 : -15), 
                           getY() - 10);
            }
        }
        
        // Stop at ground level
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