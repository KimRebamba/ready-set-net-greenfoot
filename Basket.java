import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

public class Basket extends Actor
{
    private int hoopY;
    private GreenfootSound rimSound;
    private boolean soundEnabled = true;
    private Backboard backboard;
    
   
    private void playRimSound() {
        if (!soundEnabled) return;
        try {
            GreenfootSound s = new GreenfootSound("sounds/basket_rim.wav");
            s.setVolume(80);
            s.play();
        } catch (Exception e) {
            soundEnabled = false;
            System.out.println("Bounce sound failed: " + e.getMessage());
        }
    }
    
    public Basket()
    {
        GreenfootImage basketImage = new GreenfootImage("images/basket.png");
        basketImage.scale(80, 60);
        setImage(basketImage);
        hoopY = 0;
        
        try {
            rimSound = new GreenfootSound("sounds/basket_rim.wav");
        } catch (Throwable t) {
            System.out.println("Could not load rim sound: " + t.getMessage());
            soundEnabled = false;
            rimSound = null;
        }
    }
    
    public void act()
    {
        // Set the hoop Y position once the basket is added to the world
        if (hoopY == 0 && getWorld() != null)
        {
            hoopY = getY() + 20;
        }
    }
    
    public boolean checkScore(Basketball ball)
    {
        int ballX = ball.getX();
        int ballY = ball.getY();
        int prevBallY = ball.getPrevY();
        int basketX = getX();
        int basketY = getY();
        
        int rimHalfWidth = 35;
        int rimTop = basketY + 10;
        int rimBottom = basketY + 55;
        
        // Check if ball is horizontally aligned with the hoop
        boolean inHorizontalZone = Math.abs(ballX - basketX) < rimHalfWidth;
        
        // Check if ball passed through the rim opening from above
        boolean crossedRimFromAbove = (prevBallY < rimTop && ballY >= rimTop);
        
        // Check if ball is currently within the rim vertical zone
        boolean inVerticalZone = (ballY >= rimTop && ballY <= rimBottom);
        boolean movingDownward = ball.getVelocityY() > 0.5;
        
        // Score when ball moves downward through the hoop
        if (inHorizontalZone && movingDownward && (crossedRimFromAbove || inVerticalZone))
        {
            if (soundEnabled && rimSound != null)
            {
                try {
                    playRimSound();
                } catch (Throwable t) {
                    soundEnabled = false;
                }
            }
            return true;
        }
        return false;
    }
    
    public void setBackboard(Backboard backboard)
    {
        this.backboard = backboard;
    }
    
    public void moveToRandomLocation()
    {
        if (getWorld() != null && backboard != null)
        {
            backboard.moveToRandomLocation();
        }
    }
    
    public int getHoopY()
    {
        return hoopY;
    }
}