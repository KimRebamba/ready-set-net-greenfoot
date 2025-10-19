import greenfoot.*;

public class Arrow extends Actor
{
    private int power = 0;
    private boolean visible = false;
    
    public Arrow()
    {
        // Draw the arrow shaft as a white rectangle
        GreenfootImage arrowImage = new GreenfootImage(50, 10);
        arrowImage.setColor(Color.WHITE);
        arrowImage.fillRect(0, 4, 40, 2);
        
        // Draw the arrowhead as a white triangle
        int[] xPoints = {40, 50, 40};
        int[] yPoints = {0, 5, 10};
        arrowImage.fillPolygon(xPoints, yPoints, 3);
        
        setImage(arrowImage);
        setVisible(false);
    }
    
    public void setPower(int power)
    {
        this.power = power;
        
        // Scale arrow length between 20 and 80 pixels based on power value
        int length = Math.max(20, Math.min(80, power / 3));
        
        GreenfootImage arrowImage = new GreenfootImage(length + 10, 10);
        arrowImage.setColor(Color.WHITE);
        arrowImage.fillRect(0, 4, length, 2);
        
        // Draw the arrowhead at the end of the shaft
        int[] xPoints = {10, 0, 10};
        int[] yPoints = {0, 5, 10};
        arrowImage.fillPolygon(xPoints, yPoints, 3);
        arrowImage.fillRect(10, 4, length, 2);
    
        setImage(arrowImage);
    }
    
    public void setVisible(boolean visible)
    {
        this.visible = visible;
        // Hide the arrow by setting a transparent image
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
    
    private boolean facingLeft = false;  
    private boolean lastFacingLeft = false;
    
    public void setFacingLeft(boolean facingLeft)
    {
        this.facingLeft = facingLeft;
        updateDirection();
    }
    
    private void updateDirection()
    {
        // Mirror the arrow image when direction changes
        if (facingLeft != lastFacingLeft)
        {
            GreenfootImage img = getImage();
            img.mirrorHorizontally();
            setImage(img);
            lastFacingLeft = facingLeft;
        }
    }
}