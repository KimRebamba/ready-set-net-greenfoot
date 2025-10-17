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
        int bx = ball.getX();
        int by = ball.getY();
        int nx = getX();
        int ny = getY();

        int halfW = width / 2;
        int halfH = height / 2;

        // thinner side collision (just the poles)
        int sideThickness = 10; 
        int topThickness = 20;  

        // Define each section of the net
        int leftPoleX = nx - halfW;
        int rightPoleX = nx + halfW;
        int topY = ny - halfH;

        // Check top band
        boolean hitTop = by > topY - topThickness && by < topY + topThickness && 
                         bx > nx - halfW && bx < nx + halfW;

        // Check left and right poles separately
        boolean hitLeft = bx > leftPoleX - sideThickness && bx < leftPoleX + sideThickness && 
                          by > ny - halfH && by < ny + halfH;

        boolean hitRight = bx > rightPoleX - sideThickness && bx < rightPoleX + sideThickness && 
                           by > ny - halfH && by < ny + halfH;

        return hitTop || hitLeft || hitRight;
    }
}
