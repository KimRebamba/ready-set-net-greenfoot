import greenfoot.*;
public class VolleyballNet extends Actor {
    private GreenfootImage netImage;
    private int width;
    private int height;
    
    public VolleyballNet() {
        netImage = new GreenfootImage("volleyball_net.png");
        width = netImage.getWidth();
        height = netImage.getHeight();
        setImage(netImage);
    }
    
    public boolean isTouchingNet(Volleyball ball) {
        // Get ball and net positions
        int bx = ball.getX();
        int by = ball.getY();
        int nx = getX();
        int ny = getY();
        int halfW = width / 2;
        int halfH = height / 2;
        
        // Collision thickness for different net parts
        int sideThickness = 10; 
        int topThickness = 20;
        
        // Calculate pole and top positions
        int leftPoleX = nx - halfW;
        int rightPoleX = nx + halfW;
        int topY = ny - halfH;
        
        // Test collision with top band of net
        boolean hitTop = by > topY - topThickness && by < topY + topThickness && 
                         bx > nx - halfW && bx < nx + halfW;
        
        // Test collision with left pole
        boolean hitLeft = bx > leftPoleX - sideThickness && bx < leftPoleX + sideThickness && 
                          by > ny - halfH && by < ny + halfH;
        
        // Test collision with right pole
        boolean hitRight = bx > rightPoleX - sideThickness && bx < rightPoleX + sideThickness && 
                           by > ny - halfH && by < ny + halfH;
        
        return hitTop || hitLeft || hitRight;
    }
}