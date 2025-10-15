import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Backboard actor that provides a surface for the ball to bounce off.
 * 
 * @author (your name)
 * @version (a version number or a date)
 */
public class Backboard extends Actor
{
    private Basket basket;
    
    public Backboard()
    {
        GreenfootImage backboardImage = new GreenfootImage("images/backboard.png");
        backboardImage.scale(15, 140);
        setImage(backboardImage);
    }
    
    public void act()
    {
        // Backboard doesn't need to do anything in act()
        // It just sits there and provides collision detection
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
            // Move to a random location, ensuring it's not too high or too low
            int newX = Greenfoot.getRandomNumber(getWorld().getWidth() - 150) + 75;
            int newY = Greenfoot.getRandomNumber(200) + 150; // Between 150 and 350 (more reasonable height)
            
            setLocation(newX, newY);
            
            // Also move the basket to be positioned relative to the backboard
            if (basket != null)
            {
                basket.setLocation(newX - 45, newY + 51); // Keeps same offset as original placement
            }
        }
    }
}
