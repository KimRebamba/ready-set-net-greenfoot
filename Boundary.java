import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Boundary obstacle that spawns as a challenge when score reaches 10.
 * 
 * @author (your name)
 * @version (a version number or a date)
 */
public class Boundary extends Actor
{
    private int width, height;
    
    public Boundary()
    {
        GreenfootImage boundaryImage = new GreenfootImage("images/boundary.png");
        boundaryImage.scale(40, 85);
        setImage(boundaryImage);
        
        // Store dimensions for collision detection
        width = 80;
        height = 85;
        
        setRotation(0);
       
    }
    
    public void act()
    {
        // Boundary doesn't need to do anything in act()
        // It just sits there as an obstacle
    }
    
    public int getWidth()
    {
        return width;
    }
    
    public int getHeight()
    {
        return height;
    }
}
