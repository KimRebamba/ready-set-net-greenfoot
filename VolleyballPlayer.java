import greenfoot.*;

public class VolleyballPlayer extends Actor { 
    private boolean isLeftPlayer; 
    private int verticalSpeed = 0; 
    private final int JUMP_STRENGTH = -27; 
    private final int GRAVITY = 1; 
    private final int MOVE_SPEED = 10; 
    private boolean isOnGround = true; 
     
    public VolleyballPlayer(boolean isLeft) { 
        this.isLeftPlayer = isLeft; 
        updateImage("volleyball-receive"); 
        if (isLeft) { 
            getImage().mirrorHorizontally(); 
        } 
    } 
     
    public void act() { 
        handleMovement(); 
        handleAnimation(); 
        applyGravity(); 
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
        // --- Player 2 controls (Arrow keys)
        else { 
            if (Greenfoot.isKeyDown("left")) { 
                newX -= MOVE_SPEED; 
            } 
            if (Greenfoot.isKeyDown("right")) { 
                newX += MOVE_SPEED; 
            } 
            if (Greenfoot.isKeyDown("down") && !isOnGround) { 
                verticalSpeed += 3; // drop faster
            } 
            if (Greenfoot.isKeyDown("up") && isOnGround) { 
                verticalSpeed = JUMP_STRENGTH; 
                isOnGround = false; 
            } 
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
     
    private void handleAnimation() {
    Volleyball ball = (Volleyball)getWorld().getObjects(Volleyball.class).get(0);

    boolean movingHorizontally = 
        (isLeftPlayer && (Greenfoot.isKeyDown("a") || Greenfoot.isKeyDown("d"))) ||
        (!isLeftPlayer && (Greenfoot.isKeyDown("left") || Greenfoot.isKeyDown("right")));

    // Spike if mid-air and moving toward the ball
    if (!isOnGround && movingHorizontally && ball != null) {
        boolean facingBall = (isLeftPlayer && ball.getX() > getX()) ||
                             (!isLeftPlayer && ball.getX() < getX());

        if (facingBall) {
            updateImage("volleyball-spike");
            return;
        }
    }

    // Spike if actually colliding while in the air
    //if (!isOnGround && getOneIntersectingObject(Volleyball.class) != null) {
        //updateImage("volleyball-spike");
        //return;
    //}

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
