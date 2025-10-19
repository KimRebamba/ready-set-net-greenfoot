import greenfoot.*;
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
        // Load and scale the boundary image to fit game
        GreenfootImage boundaryImage = new GreenfootImage("images/boundary.png");
        boundaryImage.scale(40, 85);
        setImage(boundaryImage);
        
        // Track dimensions for collision detection later
        width = 80;
        height = 85;
        
        setRotation(0);
    }
    
    public void act()
    {
        // Static obstacle - no movement or behavior needed
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