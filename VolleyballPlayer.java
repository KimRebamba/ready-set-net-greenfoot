import greenfoot.*;
public class VolleyballPlayer extends Actor { 
    private boolean isLeftPlayer; 
    private int verticalSpeed = 0; 
    private final int JUMP_STRENGTH = -28; 
    private final int GRAVITY = 1; 
    private final int MOVE_SPEED = 12; 
    private boolean isOnGround = true; 
    private boolean cpuMode;
    private String difficulty;
    private int cpuJumpCooldown = 0;
    private final int CPU_JUMP_COOLDOWN = 40;
    private final int SPIKE_PROXIMITY = 80;
    private final int SPIKE_HEIGHT = 60;
     
    public VolleyballPlayer(boolean isLeft) { 
        this.isLeftPlayer = isLeft; 
        this.cpuMode = false;
        updateImage("volleyball-receive"); 
        if (isLeft) { 
            getImage().mirrorHorizontally(); 
        } 
    } 
    
    // Constructor for CPU opponent
    public VolleyballPlayer(boolean isLeft, boolean isCPU, String diff) {
        this.isLeftPlayer = isLeft;
        this.cpuMode = isCPU && !isLeft;
        this.difficulty = diff;
        updateImage("volleyball-receive");
        if (isLeft) {
            getImage().mirrorHorizontally();
        }
    }
     
    public void act() { 
        handleMovement(); 
        handleAnimation();
        handleSmash();
        applyGravity(); 
    } 
     
    private void handleSmash() {
        java.util.List<Volleyball> balls = getWorld().getObjects(Volleyball.class);
        if (balls.isEmpty()) return;
        Volleyball ball = balls.get(0);

        int horizontalDistance = Math.abs(ball.getX() - getX());
        int verticalDistance = getY() - ball.getY();
        
        // Check if player is in position to spike (airborne, close to ball, above it)
        boolean canSmash = !isOnGround && horizontalDistance < SPIKE_PROXIMITY && 
                           verticalDistance > 0 && verticalDistance < SPIKE_HEIGHT;

        if (isLeftPlayer && canSmash && Greenfoot.isKeyDown("e")) {
            performSmash(ball, true);
        }
        else if (!isLeftPlayer && !cpuMode && canSmash && Greenfoot.isKeyDown("u")) {
            performSmash(ball, false);
        }
        // CPU decides to spike automatically based on difficulty
        else if (cpuMode && canSmash) {
            double ballVY = ball.getVelocityY();
            double ballVX = ball.getVelocityX();

            boolean ballFalling = ballVY > 0;
            boolean horizontallyClose = Math.abs(ball.getX() - getX()) < 60;
            boolean ballAboveHead = ball.getY() < getY() - 40;
            boolean goodAngle = (isLeftPlayer && ballVX > 0) || (!isLeftPlayer && ballVX < 0);

            if (ballFalling && horizontallyClose && ballAboveHead && goodAngle) {
                int smashChance;
                switch (difficulty.toLowerCase()) {
                    case "easy": smashChance = 25; break;
                    case "medium": smashChance = 45; break;
                    case "hard": smashChance = 70; break;
                    case "expert": smashChance = 85; break;
                    case "impossible": smashChance = 100; break;
                    default: smashChance = 50; break;
                }

                if (Greenfoot.getRandomNumber(100) < smashChance) {
                    performSmash(ball, false);
                }
            }
        }
    }

    private void performSmash(Volleyball ball, boolean leftSide) {
        // Push ball down and forward for spike attack
        double smashPowerX = leftSide ? 18 : -18;
        double smashPowerY = 22;

        // Extra power when spiking from high up
        if (getY() < getWorld().getHeight() / 2) {
            smashPowerY += 5;
        }

        ball.setVelocity(smashPowerX, smashPowerY);
        ball.playSmashSound();
        updateImage("volleyball-spike");
    }


    private void handleMovement() { 
        int newX = getX(); 
        
        if (isLeftPlayer) { 
            // Left player: A/D to move, W to jump, S to fall faster
            if (Greenfoot.isKeyDown("a")) { 
                newX -= MOVE_SPEED; 
            } 
            if (Greenfoot.isKeyDown("d")) { 
                newX += MOVE_SPEED; 
            } 
            if (Greenfoot.isKeyDown("s") && !isOnGround) { 
                verticalSpeed += 3;
            } 
            if (Greenfoot.isKeyDown("w") && isOnGround) { 
                verticalSpeed = JUMP_STRENGTH; 
                isOnGround = false; 
            } 
        } 
        else if (!cpuMode) { 
            // Right player: J/L to move, I to jump, K to fall faster
            if (Greenfoot.isKeyDown("j")) { 
                newX -= MOVE_SPEED; 
            } 
            if (Greenfoot.isKeyDown("l")) { 
                newX += MOVE_SPEED; 
            } 
            if (Greenfoot.isKeyDown("k") && !isOnGround) { 
                verticalSpeed += 3;
            } 
            if (Greenfoot.isKeyDown("i") && isOnGround) { 
                verticalSpeed = JUMP_STRENGTH; 
                isOnGround = false; 
            } 
        } else {
            // CPU opponent uses AI movement
            handleCPUMovement();
            newX = getX();
        }
         
        // Keep players on their side of the net
        if (isLeftPlayer && newX > getWorld().getWidth()/2 - 50) { 
            newX = getWorld().getWidth()/2 - 50; 
        } 
        if (!isLeftPlayer && newX < getWorld().getWidth()/2 + 50) { 
            newX = getWorld().getWidth()/2 + 50; 
        } 
         
        setLocation(newX, getY()); 
    }
    
    private int cpuRecalcTimer = 0;
    private int cpuTargetX = 0;

    private void handleCPUMovement() {
        if (cpuJumpCooldown > 0) cpuJumpCooldown--;

        java.util.List<Volleyball> balls = getWorld().getObjects(Volleyball.class);
        if (balls.isEmpty()) return;
        Volleyball ball = balls.get(0);

        int ballX = ball.getX();
        int playerX = getX();
        int netX = getWorld().getWidth() / 2;

        // Ignore ball if it's on the other side
        if (ballX < netX) return;

        // Adjust CPU behavior based on difficulty
        int deviation = 0;
        int speed = MOVE_SPEED;
        int jumpChance = 0;
        int recalcDelay = 15;

        switch (difficulty.toLowerCase()) {
            case "easy":
                deviation = 120; speed = 6;  jumpChance = 20;  recalcDelay = 25; break;
            case "medium":
                deviation = 80;  speed = 9;  jumpChance = 45;  recalcDelay = 20; break;
            case "hard":
                deviation = 40;  speed = 12; jumpChance = 65;  recalcDelay = 15; break;
            case "expert":
                deviation = 15;  speed = 12; jumpChance = 80;  recalcDelay = 10; break;
            case "impossible":
                deviation = 5;   speed = 14; jumpChance = 95;  recalcDelay = 5;  break;
        }

        double ballVX = ball.getVelocityX();
        double ballVY = ball.getVelocityY();

        // Predict where CPU should intercept the ball (above head level)
        double interceptY = getY() - 60;

        // Solve trajectory equation to find when ball reaches interceptY
        double g = 0.4;
        double A = 0.5 * g;
        double B = ballVY;
        double C = ball.getY() - interceptY;

        double t = -1;
        double disc = B * B - 4 * A * C;
        if (disc >= 0 && Math.abs(A) > 1e-6) {
            double t1 = (-B + Math.sqrt(disc)) / (2 * A);
            double t2 = (-B - Math.sqrt(disc)) / (2 * A);
            if (t1 > 0 && t2 > 0) t = Math.min(t1, t2);
            else if (t1 > 0) t = t1;
            else if (t2 > 0) t = t2;
        } else if (Math.abs(B) > 1e-6) {
            double tf = -C / B;
            if (tf > 0) t = tf;
        }

        // Fallback estimate if physics calculation fails
        if (t <= 0 || Double.isNaN(t) || Double.isInfinite(t)) {
            t = 20;
        }

        // Calculate where ball will be horizontally at that time
        int predictedX = (int) Math.round(ballX + ballVX * t);

        // Add mistake variance based on difficulty
        predictedX += Greenfoot.getRandomNumber(deviation * 2) - deviation;

        // Stay within court bounds
        int leftBound = netX + 40;
        int rightBound = getWorld().getWidth() - 40;
        predictedX = Math.max(leftBound, Math.min(predictedX, rightBound));

        // Smooth target updates to avoid jittery movement
        if (cpuRecalcTimer <= 0) {
            cpuTargetX = predictedX;
            cpuRecalcTimer = recalcDelay;
        } else {
            cpuTargetX = (int) Math.round(0.85 * cpuTargetX + 0.15 * predictedX);
            cpuRecalcTimer--;
        }

        // Move toward target position at difficulty-based speed
        if (playerX < cpuTargetX - 6) {
            setLocation(Math.min(playerX + speed, cpuTargetX), getY());
        } else if (playerX > cpuTargetX + 6) {
            setLocation(Math.max(playerX - speed, cpuTargetX), getY());
        }

        // Calculate jump timing to meet ball at peak height
        double playerGravity = 1.0;
        double timeToApex = - (double) JUMP_STRENGTH / playerGravity;
        double desiredLead = Math.max(6, timeToApex * 0.6);

        boolean horizontallyClose = Math.abs(predictedX - getX()) < 90;
        if (cpuJumpCooldown == 0 && isOnGround && horizontallyClose) {
            // Jump if ball arrival matches our desired timing
            if (t > 2 && Math.abs(t - desiredLead) < 8) {
                if (Greenfoot.getRandomNumber(100) < jumpChance) {
                    verticalSpeed = JUMP_STRENGTH;
                    isOnGround = false;
                    cpuJumpCooldown = CPU_JUMP_COOLDOWN;
                }
            }
            // Emergency jump if ball arrives very soon
            else if (t <= 6 && t > 0 && horizontallyClose) {
                if (Greenfoot.getRandomNumber(100) < Math.max(30, jumpChance/2)) {
                    verticalSpeed = JUMP_STRENGTH;
                    isOnGround = false;
                    cpuJumpCooldown = CPU_JUMP_COOLDOWN;
                }
            }
        }
    }

     
    private void handleAnimation() {
        java.util.List<Volleyball> balls = getWorld().getObjects(Volleyball.class);
        if (balls.isEmpty()) return;
        
        Volleyball ball = balls.get(0);
        boolean movingHorizontally = 
            (isLeftPlayer && (Greenfoot.isKeyDown("a") || Greenfoot.isKeyDown("d"))) ||
            (!isLeftPlayer && !cpuMode && (Greenfoot.isKeyDown("left") || Greenfoot.isKeyDown("right"))) ||
            (cpuMode && getX() != 0);
        
        // Show spike animation when airborne and facing the ball
        if (!isOnGround && movingHorizontally && ball != null) {
            boolean facingBall = (isLeftPlayer && ball.getX() > getX()) ||
                                 (!isLeftPlayer && ball.getX() < getX());
            if (facingBall) {
                updateImage("volleyball-spike");
                return;
            }
        }
        
        // Default to ready position when on ground
        if (isOnGround) {
            updateImage("volleyball-receive");
        }
    }
     
    private void updateImage(String imageName) { 
        GreenfootImage img = new GreenfootImage(imageName + ".png"); 
        img.scale(65, 65); 
        if (isLeftPlayer) img.mirrorHorizontally(); 
        setImage(img); 
    } 
     
    private void applyGravity() { 
        verticalSpeed += GRAVITY; 
        setLocation(getX(), getY() + verticalSpeed); 
         
        // Snap to ground level
        if (getY() >= getWorld().getHeight() - 53) { 
            setLocation(getX(), getWorld().getHeight() - 53); 
            verticalSpeed = 0; 
            isOnGround = true; 
        } 
    } 
}