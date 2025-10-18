import greenfoot.*;
public class BadmintonPlayer extends Actor {
    private boolean isLeftPlayer;
    private int verticalSpeed = 0;
    private final int JUMP_STRENGTH = -25;
    private final int GRAVITY = 1;
    private final int MOVE_SPEED = 10;
    private boolean isOnGround = true;
    private GreenfootImage racketNormal;
    private GreenfootImage racketSwing;
    private boolean isSwinging = false;
    private int swingCooldown = 0;
    private final int GROUND_OFFSET = 75;
    private final int SWING_DURATION = 30;
    private boolean cpuMode;
    private String difficulty;
    private int cpuJumpCooldown = 0;
    private final int CPU_JUMP_COOLDOWN = 50;
    
    public BadmintonPlayer(boolean isLeft) {
        this.isLeftPlayer = isLeft;
        initializeImages();
    }
    
    public BadmintonPlayer(boolean isLeft, boolean isCPU, String diff) {
        this.isLeftPlayer = isLeft;
        this.cpuMode = isCPU && !isLeft; // Only right player can be CPU
        this.difficulty = diff;
        initializeImages();
    }
    
    private void initializeImages() {
        // Load player-specific images
        if (isLeftPlayer) {
            racketNormal = new GreenfootImage("1p_racket.png");
            racketSwing = new GreenfootImage("1p_racket-forward.png");
        } else {
            racketNormal = new GreenfootImage("2p_racket.png");
            racketSwing = new GreenfootImage("2p_racket-forward.png");
            // Mirror right player images
            racketNormal.mirrorHorizontally();
            racketSwing.mirrorHorizontally();
        }
        
        // Scale images appropriately
        racketNormal.scale(80, 120);
        racketSwing.scale(80, 120);
        
        setImage(racketNormal);
    }
    
    public void act() {
        handleMovement();
        handleSwing();
        applyGravity();
        updateAnimation();
    }
    
    private void handleMovement() {
        // Left player always uses WASD
        if (isLeftPlayer) {
            if (Greenfoot.isKeyDown("a")) moveLeft();
            if (Greenfoot.isKeyDown("d")) moveRight();
            if (Greenfoot.isKeyDown("w")) jump();
            if (Greenfoot.isKeyDown("s")) fastFall();
        } else if (!cpuMode) {
            // Right player uses arrow keys (only if not CPU)
            if (Greenfoot.isKeyDown("j")) moveLeft();
            if (Greenfoot.isKeyDown("l")) moveRight();
            if (Greenfoot.isKeyDown("i")) jump();
            if (Greenfoot.isKeyDown("k")) fastFall();
        } else {
            // CPU movement
            handleCPUMovement();
        }
        
        // Prevent crossing the net
        int netX = getWorld().getWidth() / 2;
        if (isLeftPlayer && getX() > netX - 50) {
            setLocation(netX - 50, getY());
        } else if (!isLeftPlayer && getX() < netX + 50) {
            setLocation(netX + 50, getY());
        }
    }
    
    private void handleCPUMovement() {
    // Decrement jump cooldown
    if (cpuJumpCooldown > 0) {
        cpuJumpCooldown--;
    }

    // Find shuttle
    java.util.List<Shuttlecock> shuttles = getWorld().getObjects(Shuttlecock.class);
    if (shuttles.isEmpty()) return;
    Shuttlecock shuttle = shuttles.get(0);

    int shuttleX = shuttle.getX();
    int playerX = getX();
    int netX = getWorld().getWidth() / 2;

    // Only act if shuttle is on CPU side
    if (shuttleX < netX) return;

    // Difficulty-based parameters
    int deviation = 0;
    int speed = MOVE_SPEED;
    int jumpChance = 0;

    switch(difficulty.toLowerCase()) {
        case "easy":
            deviation = 100;
            speed = 5;
            jumpChance = 15;
            break;
        case "medium":
            deviation = 60;
            speed = 7;
            jumpChance = 40;
            break;
        case "hard":
            deviation = 25;
            speed = 10;
            jumpChance = 60;
            break;
        case "expert":
            deviation = 10;
            speed = 10;
            jumpChance = 75;
            break;
        case "impossible":
            deviation = 1;
            speed = 12;
            jumpChance = 90;
            break;
    }

    // Target position with random deviation
    int targetX = shuttleX + Greenfoot.getRandomNumber(deviation * 2) - deviation;

    // Move towards targetX but only every few frames (simulate reaction time)
    if (Greenfoot.getRandomNumber(3) > 0) { // 2/3 chance to move
        if (playerX < targetX) setLocation(playerX + speed, getY());
        else if (playerX > targetX) setLocation(playerX - speed, getY());
    }

    // Jumping logic
    if (cpuJumpCooldown == 0 && isOnGround && shuttle.getY() < getY() - 30) {
        if (Greenfoot.getRandomNumber(100) < jumpChance) {
            jump();
            cpuJumpCooldown = CPU_JUMP_COOLDOWN;
        }
    }
}

    
    private void moveLeft() {
        setLocation(getX() - MOVE_SPEED, getY());
    }
    
    private void moveRight() {
        setLocation(getX() + MOVE_SPEED, getY());
    }
    
    private void jump() {
        if (isOnGround) {
            verticalSpeed = JUMP_STRENGTH;
            isOnGround = false;
        }
    }
    
    private void fastFall() {
        if (!isOnGround) {
            verticalSpeed += 3; // Accelerate downward faster
        }
    }
    
   private void handleSwing() {
    boolean smashInput = false;

    // Player-controlled input
    if (isLeftPlayer && Greenfoot.isKeyDown("e")) smashInput = true;
    else if (!isLeftPlayer && !cpuMode && Greenfoot.isKeyDown("u")) smashInput = true;

    // CPU logic: swing if shuttle is close enough
    if (!isLeftPlayer && cpuMode) {
        java.util.List<Shuttlecock> shuttles = getWorld().getObjects(Shuttlecock.class);
        if (!shuttles.isEmpty()) {
            Shuttlecock shuttle = shuttles.get(0);
            int distanceX = Math.abs(getX() - shuttle.getX());
            int distanceY = Math.abs(getY() - shuttle.getY());
            if (distanceX < 80 && distanceY < 80) smashInput = true;
        }
    }

    // Trigger swing animation for both human and CPU
    if (smashInput) {
        isSwinging = true;
        swingCooldown = SWING_DURATION;
    }

    if (swingCooldown > 0) {
        swingCooldown--;
    } else {
        isSwinging = false;
    }
}
    
    private void updateAnimation() {
        if (isSwinging && swingCooldown > 0) {
            setImage(racketSwing);
        } else {
            setImage(racketNormal);
            isSwinging = false;
        }
    }
    
    private void applyGravity() {
        verticalSpeed += GRAVITY;
        int newY = getY() + verticalSpeed;
        
        // Ground collision
        if (newY >= getWorld().getHeight() - GROUND_OFFSET) {
            newY = getWorld().getHeight() - GROUND_OFFSET;
            verticalSpeed = 0;
            isOnGround = true;
        }
        
        setLocation(getX(), newY);
    }
    
    public boolean isSwinging() {
        return isSwinging;
    }
}