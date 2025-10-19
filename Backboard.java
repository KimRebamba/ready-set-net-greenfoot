import greenfoot.*;

public class Backboard extends Actor
{
    private Basket basket;
    private final int WIDTH = 15;
    private final int HEIGHT = 140;
    private final int BALL_RADIUS = 21; // half of 42
    
    public Backboard()
    {
        GreenfootImage backboardImage = new GreenfootImage("images/backboard.png");
        backboardImage.scale(WIDTH, HEIGHT);
        setImage(backboardImage);
    }
    
    public void act()
    {
        // Backboard is stationary, no updates needed
    }
    
    public void setBasket(Basket basket)
    {
        this.basket = basket;
    }
    
    public Basket getBasket()
    {
        return basket;
    }
    
    public void moveToRandomLocation()
    {
        if (getWorld() != null)
        {
            int newX = Greenfoot.getRandomNumber(getWorld().getWidth() - 150) + 75;
            int newY = Greenfoot.getRandomNumber(200) + 150;
            
            setLocation(newX, newY);
            
            // Move the basket along with the backboard
            if (basket != null)
            {
                basket.setLocation(newX - 45, newY + 51);
            }
        }
    }
    
    // Returns the left edge of the backboard
    public int getLeft()
    {
        return getX() - WIDTH / 2;
    }
    
    // Returns the right edge of the backboard
    public int getRight()
    {
        return getX() + WIDTH / 2;
    }
    
    // Returns the top edge of the backboard
    public int getTop()
    {
        return getY() - HEIGHT / 2;
    }
    
    // Returns the bottom edge of the backboard
    public int getBottom()
    {
        return getY() + HEIGHT / 2;
    }
}