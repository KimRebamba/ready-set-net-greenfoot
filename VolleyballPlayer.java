import greenfoot.*;
public class VolleyballPlayer extends Actor { 
    private boolean isLeftPlayer; 
    private int verticalSpeed = 0; 
    private final int JUMP_STRENGTH = -27; 
    private final int GRAVITY = 1; 
    private final int MOVE_SPEED = 12; 
    private boolean isOnGround = true; 
    private boolean cpuMode;
    private String difficulty;
    private int cpuJumpCooldown = 0;
    private final int CPU_JUMP_COOLDOWN = 40;
     
    public VolleyballPlayer(boolean isLeft) { 
        this.isLeftPlayer = isLeft; 
        this.cpuMode = false;
        updateImage("volleyball-receive"); 
        if (isLeft) { 
            getImage().mirrorHorizontally(); 
        } 
    } 
    
    public VolleyballPlayer(boolean isLeft, boolean isCPU, String diff) {
        this.isLeftPlayer = isLeft;
        this.cpuMode = isCPU && !isLeft; // Only right player can be CPU
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

    boolean canSmash = !isOnGround && Math.abs(ball.getX() - getX()) < 80 && ball.getY() < getY() + 60;

    // Left player - hold E
    if (isLeftPlayer && canSmash && Greenfoot.isKeyDown("e")) {
        performSmash(ball, true);
    }
    // Right player - hold U
    else if (!isLeftPlayer && !cpuMode && canSmash && Greenfoot.isKeyDown("u")) {
        performSmash(ball, false);
    }
    // CPU auto-smash: only when ball is falling near head level and close horizontally
else if (cpuMode && canSmash) {
    double ballVY = ball.getVelocityY();
    double ballVX = ball.getVelocityX();

    boolean ballFalling = ballVY > 0; // going downward
    boolean horizontallyClose = Math.abs(ball.getX() - getX()) < 60;
    boolean ballAboveHead = ball.getY() < getY() - 40;
    boolean goodAngle = (isLeftPlayer && ballVX > 0) || (!isLeftPlayer && ballVX < 0);

    // Only smash if ball is actually descending near and facing right direction
    if (ballFalling && horizontallyClose && ballAboveHead && goodAngle) {
        // Add reaction chance based on difficulty
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
    // Make the ball go sharply downward with strong force
    double smashPowerX = leftSide ? 18 : -18;  // forward push
    double smashPowerY = 22;                   // downward push (positive = down in Greenfoot)

    // Optional: make stronger if high up
    if (getY() < getWorld().getHeight() / 2) {
        smashPowerY += 5;
    }

    ball.setVelocity(smashPowerX, smashPowerY);
   ball.playSmashSound();
    updateImage("volleyball-spike");
}


    private void handleMovement() { 
        int newX = getX(); 
        // --- Player 1 controls (A, D, W, S)
        if (isLeftPlayer) { 
            if (Greenfoot.isKeyDown("a")) { 
                newX -= MOVE_SPEED; 
            } 
            if (Greenfoot.isKeyDown("d")) { 
                newX += MOVE_SPEED; 
            } 
            if (Greenfoot.isKeyDown("s") && !isOnGround) { 
                verticalSpeed += 3; // drop faster
            } 
            if (Greenfoot.isKeyDown("w") && isOnGround) { 
                verticalSpeed = JUMP_STRENGTH; 
                isOnGround = false; 
            } 
        } 
        // --- Player 2 controls (Arrow keys) or CPU
        else if (!cpuMode) { 
            if (Greenfoot.isKeyDown("j")) { 
                newX -= MOVE_SPEED; 
            } 
            if (Greenfoot.isKeyDown("l")) { 
                newX += MOVE_SPEED; 
            } 
            if (Greenfoot.isKeyDown("k") && !isOnGround) { 
                verticalSpeed += 3; // drop faster
            } 
            if (Greenfoot.isKeyDown("i") && isOnGround) { 
                verticalSpeed = JUMP_STRENGTH; 
                isOnGround = false; 
            } 
        } else {
            // CPU movement
            handleCPUMovement();
            newX = getX();
        }
         
        // Prevent crossing the net 
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

    // Only act if the ball is on CPU side (right side)
    if (ballX < netX) return;

    // Difficulty-based parameters
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

    // read ball velocity (you should have getters in Volleyball)
    double ballVX = ball.getVelocityX();
    double ballVY = ball.getVelocityY();

    // choose an interception vertical level (somewhere above player's head)
    double interceptY = getY() - 60; // tweak if needed

    // physics constant used by Volleyball: GRAVITY ~ 0.4 (match your Volleyball class)
    double g = 0.4;

    // Solve 0.5*g*t^2 + ballVY*t + (ball.getY() - interceptY) = 0 for positive t
    double A = 0.5 * g;
    double B = ballVY;
    double C = ball.getY() - interceptY;

    double t = -1;
    double disc = B * B - 4 * A * C;
    if (disc >= 0 && Math.abs(A) > 1e-6) {
        double t1 = (-B + Math.sqrt(disc)) / (2 * A);
        double t2 = (-B - Math.sqrt(disc)) / (2 * A);
        // pick smallest positive root
        if (t1 > 0 && t2 > 0) t = Math.min(t1, t2);
        else if (t1 > 0) t = t1;
        else if (t2 > 0) t = t2;
    } else if (Math.abs(B) > 1e-6) {
        // linear fallback (if A is ~0); solve ballVY * t + (ballY - interceptY) = 0
        double tf = -C / B;
        if (tf > 0) t = tf;
    }

    // If we couldn't find a valid time, fallback to simple prediction
    if (t <= 0 || Double.isNaN(t) || Double.isInfinite(t)) {
        // simple prediction: time ~ distance/avg speed
        t = 20; // arbitrary fallback, keeps CPU moving instead of freezing
    }

    // Predict horizontal position when ball reaches interceptY
    int predictedX = (int) Math.round(ballX + ballVX * t);

    // Add randomness based on difficulty
    predictedX += Greenfoot.getRandomNumber(deviation * 2) - deviation;

    // Clamp target within CPU side bounds
    int leftBound = netX + 40;
    int rightBound = getWorld().getWidth() - 40;
    predictedX = Math.max(leftBound, Math.min(predictedX, rightBound));

    // Smooth target updates so CPU doesn't jitter
    if (cpuRecalcTimer <= 0) {
        cpuTargetX = predictedX;
        cpuRecalcTimer = recalcDelay;
    } else {
        // slowly blend toward predicted to reduce overcorrection
        cpuTargetX = (int) Math.round(0.85 * cpuTargetX + 0.15 * predictedX);
        cpuRecalcTimer--;
    }

    // Move horizontally toward target (use "speed" from difficulty)
    if (playerX < cpuTargetX - 6) {
        setLocation(Math.min(playerX + speed, cpuTargetX), getY());
    } else if (playerX > cpuTargetX + 6) {
        setLocation(Math.max(playerX - speed, cpuTargetX), getY());
    }

    // JUMP TIMING:
    // Estimate how long CPU's jump takes to get to useful hitting height.
    // Player jump physics: JUMP_STRENGTH and GRAVITY in VolleyballPlayer (JUMP_STRENGTH negative).
    // approximate time-to-peak = -JUMP_STRENGTH / playerGRAVITY; use half/portion as window.
    double playerGravity = 1.0; // matches your VolleyballPlayer.GRAVITY
    double timeToApex = - (double) JUMP_STRENGTH / playerGravity; // e.g. ~27 frames
    double desiredLead = Math.max(6, timeToApex * 0.6); // jump so player is near apex when ball arrives

    // Only jump if ball is coming (positive t) and within horizontal tolerance
    boolean horizontallyClose = Math.abs(predictedX - getX()) < 90; // widen/narrow to tune
    if (cpuJumpCooldown == 0 && isOnGround && horizontallyClose) {
        // If ball arrives in roughly desiredLead frames, attempt jump
        // allow a small window (±5 frames) to tolerate timing errors
        if (t > 2 && Math.abs(t - desiredLead) < 8) {
            if (Greenfoot.getRandomNumber(100) < jumpChance) {
                verticalSpeed = JUMP_STRENGTH;
                isOnGround = false;
                cpuJumpCooldown = CPU_JUMP_COOLDOWN;
            }
        }
        // fallback: if ball will arrive very soon (t<6) and we're close, do a last-second jump
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
            (cpuMode && getX() != 0); // CPU is considered moving if not static
        
        // Spike if mid-air and moving toward the ball
        if (!isOnGround && movingHorizontally && ball != null) {
            boolean facingBall = (isLeftPlayer && ball.getX() > getX()) ||
                                 (!isLeftPlayer && ball.getX() < getX());
            if (facingBall) {
                updateImage("volleyball-spike");
                return;
            }
        }
        
        // Receive when on ground or idle
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
         
        if (getY() >= getWorld().getHeight() - 53) { 
            setLocation(getX(), getWorld().getHeight() - 53); 
            verticalSpeed = 0; 
            isOnGround = true; 
        } 
    } 
}