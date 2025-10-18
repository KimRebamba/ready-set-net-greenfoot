import greenfoot.*;
import greenfoot.Color;

public class BadmintonWorld extends World
{
    private BadmintonPlayer player1;
    private BadmintonPlayer player2;
    private Shuttlecock shuttle;
    private BadmintonNet net;
    private ScoreBoard scoreBoard;

    private int player1Score = 0;
    private int player2Score = 0;

    private boolean pointAwarded = false;
    private boolean isServingLeft = true;
    private int serveDelayTimer = 0;

    private boolean gameOver = false;

    // Message handling
    private int messageTimer = 0;
    private String activeMessage = null;
    
    // Background handling
    private int bgChangeTimer = 0;
    private final int BG_CHANGE_DURATION = 100;
    
    // CPU Mode
    private boolean cpuMode = false;
    private String cpuDifficulty = "impossible"; // easy, medium, hard, expert, impossible
    
    private GreenfootSound whistleSound = new GreenfootSound("whistle_sound.wav");
    private GreenfootSound whistleEndSound = new GreenfootSound("whistle_end.wav");

    // === NEW: Instructions screen handling ===
    private boolean showingInstructions = true;
    private int instructionTimer = 120; // 2 seconds at 60 fps
    private GreenfootImage instructionsImage = new GreenfootImage("badminton_instructions.png");
    // =========================================

    public BadmintonWorld() {
        super(1100, 600, 1);
        showInstructionsScreen(); // <-- show this first
    }
    
    public BadmintonWorld(boolean isCPUMode, String difficulty) {
        super(1100, 600, 1);
        this.cpuMode = isCPUMode;
        this.cpuDifficulty = difficulty;
        showInstructionsScreen(); // <-- show this first
    }

    // === NEW: Display instructions first ===
    private void showInstructionsScreen() {
        instructionsImage.scale(1100, 600);
        setBackground(instructionsImage);
        showingInstructions = true;
        instructionTimer = 120; // about 2 seconds
    }
    // =======================================

    private void prepareGame() {
        // Net
        net = new BadmintonNet();
        GreenfootImage netImage = net.getImage();
        netImage.scale(20, 280);
        addObject(net, getWidth() / 2, getHeight() - 155);

        // Players
        player1 = new BadmintonPlayer(true);
        
        if (cpuMode) {
            player2 = new BadmintonPlayer(false, true, cpuDifficulty);
        } else {
            player2 = new BadmintonPlayer(false);
        }
        
        addObject(player1, 200, getHeight() - 100);
        addObject(player2, 900, getHeight() - 100);

        // Shuttlecock
        spawnNewShuttleAtCenter();

        // Scoreboard
        scoreBoard = new ScoreBoard();
        addObject(scoreBoard, getWidth() / 2, 50);

        // Play starting whistle
        whistleSound.play();
    }

    public void act() {
        // === NEW: Handle instructions before game starts ===
        if (showingInstructions) {
            instructionTimer--;
            if (instructionTimer <= 0) {
                showingInstructions = false;
                returnToNormalBg();
                prepareGame();
            }
            return; // Stop everything until instructions are done
        }
        // ====================================================

        if (Greenfoot.isKeyDown("escape")) {
            Greenfoot.setWorld(new MenuWorld());
            return;
        }

        if (gameOver) return;

        if (shuttle != null) {
            checkScore();
        }

        if (serveDelayTimer > 0) {
            serveDelayTimer--;
            if (serveDelayTimer == 0 && !gameOver) {
                resetShuttle();
            }
        }

        if (messageTimer > 0) {
            messageTimer--;
            if (messageTimer == 0) {
                clearTextArea();
                scoreBoard.update(player1Score, player2Score);
                activeMessage = null;
            }
        }
        
        if (bgChangeTimer > 0) {
            bgChangeTimer--;
            if (bgChangeTimer == 0) {
                returnToNormalBg();
            }
        }
    }

    private void checkScore() {
        if (shuttle == null) return;

        int groundY = getHeight() - 27;

        if (!pointAwarded && shuttle.getY() >= groundY - shuttle.getImage().getHeight() / 2) {
            pointAwarded = true;

            // Play whistle for the point
            if (whistleSound.isPlaying()) whistleSound.stop();
            whistleSound.play();

            boolean leftLost = (shuttle.getX() < getWidth() / 2);
            if (leftLost) {
                player2Score++;
                isServingLeft = false;
            } else {
                player1Score++;
                isServingLeft = true;
            }

            scoreBoard.update(player1Score, player2Score);

            // Check for winner (first to 21 points)
            if (player1Score >= 21 || player2Score >= 21) {
                clearTextArea();
                endGame();
                return;
            }

            changeBgToCelebration();
            showPointMessage(leftLost ? "Right side scores!" : "Left side scores!");
            serveDelayTimer = 100;
        }
    }

    private void endGame() {
        gameOver = true;

        // Play end-game whistle
        if (whistleEndSound.isPlaying()) whistleEndSound.stop();
        whistleEndSound.play();

        String winner = (player1Score >= 21) ? "LEFT SIDE WINS!" : "RIGHT SIDE WINS!";
        drawOutlinedText(winner, getWidth() / 2, 100, Color.GREEN, Color.BLACK, 50);
        drawOutlinedText("Press ESC to return to menu", getWidth() / 2, 170, Color.WHITE, Color.BLACK, 30);
    }

    private void resetShuttle() {
        if (gameOver) return;

        if (shuttle != null) removeObject(shuttle);
        spawnNewShuttle(isServingLeft ? 200 : 900, getHeight() / 2 - 100);
        shuttle.setInitialVelocity(isServingLeft ? 4 : -4, -6);
        showServeMessage(isServingLeft ? "Left side serving!" : "Right side serving!");
        pointAwarded = false;
    }

    private void spawnNewShuttleAtCenter() {
        spawnNewShuttle(getWidth() / 2, getHeight() / 2 - 100);
    }

    private void spawnNewShuttle(int x, int y) {
        shuttle = new Shuttlecock();
        GreenfootImage img = shuttle.getImage();
        img.scale(35, 35);
        shuttle.setImage(img);
        addObject(shuttle, x, y);
    }

    // === BACKGROUND ===
    private void changeBgToCelebration() {
        GreenfootImage bg = new GreenfootImage("badminton_bg_celebration.png");
        bg.scale(1100, 600);
        setBackground(bg);
        bgChangeTimer = BG_CHANGE_DURATION;
    }

    private void returnToNormalBg() {
        GreenfootImage bg = new GreenfootImage("badminton_bg_normal.png");
        bg.scale(1100, 600);
        setBackground(bg);
    }

    // === MESSAGES ===
    private void showPointMessage(String text) {
        clearTextArea();
        drawOutlinedText(text, getWidth() / 2, 200, Color.RED, Color.BLACK, 40);
        activeMessage = text;
        messageTimer = 70;
    }

    private void showServeMessage(String text) {
        clearTextArea();
        drawOutlinedText(text, getWidth() / 2, 100, Color.YELLOW, Color.BLACK, 32);
        activeMessage = text;
        messageTimer = 100;
    }

    private void drawOutlinedText(String text, int centerX, int y, Color mainColor, Color outlineColor, int size) {
        GreenfootImage img = new GreenfootImage(text, size, mainColor, new Color(0, 0, 0, 0));
        GreenfootImage outline = new GreenfootImage(text, size, outlineColor, new Color(0, 0, 0, 0));
        GreenfootImage combined = new GreenfootImage(img.getWidth() + 4, img.getHeight() + 4);

        for (int dx = -2; dx <= 2; dx++)
            for (int dy = -2; dy <= 2; dy++)
                combined.drawImage(outline, dx + 2, dy + 2);

        combined.drawImage(img, 2, 2);
        int x = centerX - combined.getWidth() / 2;
        getBackground().drawImage(combined, x, y);
    }

    private void clearTextArea() {
        scoreBoard.update(player1Score, player2Score);
    }
}
