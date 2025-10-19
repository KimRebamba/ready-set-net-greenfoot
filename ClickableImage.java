import greenfoot.*;

/**
 * A helper Actor that displays an image which enlarges slightly when hovered.
 */
public class ClickableImage extends Actor {
    // Stores the normal-sized version of the image
    private GreenfootImage normalImage;
    
    // Stores the enlarged version of the image for hover effect
    private GreenfootImage hoverImage;
    
    // Tracks whether the image is currently being hovered over
    private boolean isHovered = false;
    
    // Sound effect to play when the image is interacted with
    private GreenfootSound menuSound = new GreenfootSound("menu_sound.wav");
    
    /**
     * Constructor that initializes the clickable image with normal and hover states.
     * 
     * @param imagePath The file path to the image resource
     * @param width The desired width of the normal-sized image
     * @param height The desired height of the normal-sized image
     */
    public ClickableImage(String imagePath, int width, int height) {
        // Load and scale the image to normal size
        normalImage = new GreenfootImage(imagePath);
        normalImage.scale(width, height);
        
        // Load and scale the image 10% larger for the hover effect
        hoverImage = new GreenfootImage(imagePath);
        hoverImage.scale((int)(width * 1.1), (int)(height * 1.1));
        
        // Set the initial display to the normal-sized image
        setImage(normalImage);
    }
    
    /**
     * Updates the visual state of the image based on whether it is being hovered.
     * Switches between normal and enlarged images accordingly.
     * 
     * @param hovering true if the mouse is over this image, false otherwise
     */
    public void updateHover(boolean hovering) {
        // If hovering, switch to the enlarged image
        if (hovering) {
            setImage(hoverImage);
            isHovered = true;
        } 
        // If no longer hovering and was previously hovered, switch back to normal
        else if (!hovering && isHovered) {
            setImage(normalImage);
            isHovered = false;
        }
    }
}