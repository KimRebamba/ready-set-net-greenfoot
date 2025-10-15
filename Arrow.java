import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Arrow actor that shows the direction and power of the shot.
 * 
 * @author (your name)
 * @version (a version number or a date)
 */
public class Arrow extends Actor
{
    private int power = 0;
    private boolean visible = false;
    
    public Arrow()
    {
        // Create a simple arrow shape
        GreenfootImage arrowImage = new GreenfootImage(50, 10);
        arrowImage.setColor(Color.WHITE);
        arrowImage.fillRect(0, 4, 40, 2);
        // Draw triangle arrowhead using fillPolygon
        int[] xPoints = {40, 50, 40};
        int[] yPoints = {0, 5, 10};
        arrowImage.fillPolygon(xPoints, yPoints, 3);
        
        setImage(arrowImage);
        setVisible(false);
    }
    
    public void setPower(int power)
    {
        this.power = power;
        
        // Update arrow length based on power
        int length = Math.max(20, Math.min(80, power / 3));
        
        GreenfootImage arrowImage = new GreenfootImage(length + 10, 10);
        arrowImage.setColor(Color.WHITE);
        arrowImage.fillRect(0, 4, length, 2);
        // Draw triangle arrowhead using fillPolygon
        int[] xPoints = {length, length + 10, length};
        int[] yPoints = {0, 5, 10};
        arrowImage.fillPolygon(xPoints, yPoints, 3);
        
        setImage(arrowImage);
    }
    
    public void setVisible(boolean visible)
    {
        this.visible = visible;
        if (!visible)
        {
            GreenfootImage transparent = new GreenfootImage(1, 1);
            transparent.setTransparency(0);
            setImage(transparent);
        }
    }
    
    public int getPower()
    {
        return power;
    }
    
    public boolean isVisible()
    {
        return visible;
    }
}
