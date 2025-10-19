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
        this.cpuMode = isCPU && !isLeft;
        this.difficulty = diff;
        initializeImages();
    }
    
    private void initializeImages() {
        // Load and scale player racket images
        if (isLeftPlayer) {
            racketNormal = new GreenfootImage("1p_racket.png");
            racketSwing = new GreenfootImage("1p_racket-forward.png");
        } else {
            racketNormal = new GreenfootImage("2p_racket.png");
            racketSwing = new GreenfootImage("2p_racket-forward.png");
            // Flip right player to face correct direction
            racketNormal.mirrorHorizontally();
            racketSwing.mirrorHorizontally();
        }
        
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
        // P1 uses WASD, P2 uses arrow keys (or CPU takes over for P2)
        if (isLeftPlayer) {
            if (Greenfoot.isKeyDown("a")) moveLeft();
            if (Greenfoot.isKeyDown("d")) moveRight();
            if (Greenfoot.isKeyDown("w")) jump();
            if (Greenfoot.isKeyDown("s")) fastFall();
        } else if (!cpuMode) {
            if (Greenfoot.isKeyDown("j")) moveLeft();
            if (Greenfoot.isKeyDown("l")) moveRight();
            if (Greenfoot.isKeyDown("i")) jump();
            if (Greenfoot.isKeyDown("k")) fastFall();
        } else {
            handleCPUMovement();
        }
        
        // Keep players from crossing the net
        int netX = getWorld().getWidth() / 2;
        if (isLeftPlayer && getX() > netX - 50) {
            setLocation(netX - 50, getY());
        } else if (!isLeftPlayer && getX() < netX + 50) {
            setLocation(netX + 50, getY());
        }
    }
    
    private void handleCPUMovement() {
        if (cpuJumpCooldown > 0) {
            cpuJumpCooldown--;
        }

        java.util.List<Shuttlecock> shuttles = getWorld().getObjects(Shuttlecock.class);
        if (shuttles.isEmpty()) return;
        Shuttlecock shuttle = shuttles.get(0);

        int shuttleX = shuttle.getX();
        int playerX = getX();
        int netX = getWorld().getWidth() / 2;

        // Don't move if shuttle is on opponent's side
        if (shuttleX < netX) return;

        // Adjust accuracy and reaction time based on difficulty
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

        // Add random error to make CPU less perfect
        int targetX = shuttleX + Greenfoot.getRandomNumber(deviation * 2) - deviation;

        // Move towards target with occasional delay (2/3 chance per frame)
        if (Greenfoot.getRandomNumber(3) > 0) {
            if (playerX < targetX) setLocation(playerX + speed, getY());
            else if (playerX > targetX) setLocation(playerX - speed, getY());
        }

        // Jump if shuttle is above and cooldown is ready
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
            verticalSpeed += 3;
        }
    }
    
    private void handleSwing() {
        boolean smashInput = false;

        // Check player input
        if (isLeftPlayer && Greenfoot.isKeyDown("e")) smashInput = true;
        else if (!isLeftPlayer && !cpuMode && Greenfoot.isKeyDown("u")) smashInput = true;

        // CPU swings when shuttle is nearby
        if (!isLeftPlayer && cpuMode) {
            java.util.List<Shuttlecock> shuttles = getWorld().getObjects(Shuttlecock.class);
            if (!shuttles.isEmpty()) {
                Shuttlecock shuttle = shuttles.get(0);
                int distanceX = Math.abs(getX() - shuttle.getX());
                int distanceY = Math.abs(getY() - shuttle.getY());
                if (distanceX < 80 && distanceY < 80) smashInput = true;
            }
        }

        // Start swing animation
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
        // Switch to swing image while swinging
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
        
        // Stop at ground level
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