import greenfoot.*;

public class VolleyballWorld extends World {
    private VolleyballPlayer player1;
    private VolleyballPlayer player2;
    private Volleyball ball;
    private VolleyballNet net;
    private ScoreBoard scoreBoard;

    private int player1Score = 0;
    private int player2Score = 0;

    private boolean pointAwarded = false;
    private boolean isServingLeft = true;
    private int serveDelayTimer = 0;

    private boolean gameOver = false;
    private boolean gameStarted = false; 

    // Time to wait before accepting input on instruction screen
    private int startDelay = 30;

    // Current on-screen message and its display duration
    private int messageTimer = 0;
    private String activeMessage = null;

    private GreenfootSound whistleSound = new GreenfootSound("volleyball_whistle.wav");
    private GreenfootSound endSound = new GreenfootSound("whistle_end.wav");

    // CPU mode toggle and difficulty setting
    private boolean cpuMode = false;
    private String cpuDifficulty = "easy";

    // Manual game setup (for testing)
    public VolleyballWorld() {
        super(1100, 600, 1);
        showInstructions();
    }

    // Constructor with CPU mode and difficulty options
    public VolleyballWorld(boolean isCPUMode, String difficulty) {
        super(1100, 600, 1);
        cpuMode = isCPUMode;
        cpuDifficulty = difficulty;
        showInstructions();
    }
    
    private void showInstructions() {
        GreenfootImage instructions = new GreenfootImage("volleyball_instructions.png");
        instructions.scale(1100, 602);
        setBackground(instructions);
    }

    private void prepareGame() {
        whistleSound.play();

        // Set up game background
        GreenfootImage bg = new GreenfootImage("bg2.png");
        bg.scale(1100, 600);
        setBackground(bg);

        // Create and position net
        net = new VolleyballNet();
        GreenfootImage netImage = net.getImage();
        netImage.scale(20, 315);
        addObject(net, getWidth() / 2, getHeight() - 180);

        // Create both players (left is human, right is AI or second player)
        player1 = new VolleyballPlayer(true);
        player2 = new VolleyballPlayer(false, cpuMode, cpuDifficulty);
        addObject(player1, 200, getHeight() - 100);
        addObject(player2, 900, getHeight() - 100);

        // Spawn ball at center court
        spawnNewBallAtCenter();

        // Display scoreboard
        scoreBoard = new ScoreBoard();
        addObject(scoreBoard, getWidth() / 2, 50);
    }

    public void act() {
        // Show instruction screen until input is received or timer expires
        if (!gameStarted) {
            if (startDelay > 0) {
                startDelay--;
                return;
            }

            // Auto-start after 3 seconds (~120 frames)
            if (startDelay == -120) {
                gameStarted = true;
                prepareGame();
                return;
            }
            startDelay--;
            if (startDelay < -120) {
                gameStarted = true;
                prepareGame();
            }
            return;
        }

        // ESC returns to main menu
        if (Greenfoot.isKeyDown("escape")) {
            Greenfoot.setWorld(new MenuWorld());
            return;
        }

        // R restarts the current game
        if (Greenfoot.isKeyDown("r")) {
            Greenfoot.setWorld(new VolleyballWorld(cpuMode, cpuDifficulty));
            return;
        }

        // Stop processing when game ends
        if (gameOver) return;

        // Check if ball hit ground and award point
        if (ball != null) {
            checkScore();
        }

        // Wait after point before serving next ball
        if (serveDelayTimer > 0) {
            serveDelayTimer--;
            if (serveDelayTimer == 0 && !gameOver) {
                resetBall();
            }
        }

        // Hide temporary messages after delay
        if (messageTimer > 0) {
            messageTimer--;
            if (messageTimer == 0) {
                clearTextArea();
                scoreBoard.update(player1Score, player2Score);
                activeMessage = null;
            }
        }
    }

    private void checkScore() {
        if (ball == null) return;

        int groundY = getHeight() - 27;

        // Award point when ball hits ground
        if (!pointAwarded && ball.getY() >= groundY - ball.getImage().getHeight() / 2) {
            pointAwarded = true;

            // Determine which side failed to return the ball
            boolean leftLost = (ball.getX() < getWidth() / 2);
            if (leftLost) {
                player2Score++;
                isServingLeft = false;
            } else {
                player1Score++;
                isServingLeft = true;
            }

            scoreBoard.update(player1Score, player2Score);
            new GreenfootSound("volleyball_whistle.wav").play();

            // Check for match victory (first to 25 wins)
            if (player1Score >= 25 || player2Score >= 25) {
                clearTextArea();
                endGame();
                return;
            }

            showPointMessage(leftLost ? "Right side scores!" : "Left side scores!");
            serveDelayTimer = 100;
        }
    }

    private void endGame() {
        gameOver = true;
        endSound.play();
        String winner = (player1Score >= 25) ? "LEFT SIDE WINS!" : "RIGHT SIDE WINS!";
        drawOutlinedText(winner, getWidth() / 2, 100, Color.GREEN, Color.BLACK, 50);
        drawOutlinedText("Press ESC to return to menu or R to restart", getWidth() / 2, 170, Color.WHITE, Color.BLACK, 30);
    }

    private void resetBall() {
        if (gameOver) return;

        // Remove old ball and spawn new one at serving position
        if (ball != null) removeObject(ball);
        spawnNewBall(isServingLeft ? 200 : 900, getHeight() / 2 - 100);
        ball.setInitialVelocity(isServingLeft ? 4 : -4, -6);
        showServeMessage(isServingLeft ? "Left side serving!" : "Right side serving!");
        pointAwarded = false;
    }

    private void spawnNewBallAtCenter() {
        spawnNewBall(getWidth() / 2, getHeight() / 2 - 100);
    }

    private void spawnNewBall(int x, int y) {
        ball = new Volleyball();
        GreenfootImage ballImage = ball.getImage();
        ballImage.scale(60, 60);
        ball.setImage(ballImage);
        addObject(ball, x, y);
    }

    // Display scoring message temporarily
    private void showPointMessage(String text) {
        clearTextArea();
        drawOutlinedText(text, getWidth() / 2, 200, Color.RED, Color.BLACK, 40);
        activeMessage = text;
        messageTimer = 70;
    }

    // Display serving message temporarily
    private void showServeMessage(String text) {
        clearTextArea();
        drawOutlinedText(text, getWidth() / 2, 100, Color.YELLOW, Color.BLACK, 32);
        activeMessage = text;
        messageTimer = 100;
    }

    // Draw text with dark outline for readability
    private void drawOutlinedText(String text, int centerX, int y, Color mainColor, Color outlineColor, int size) {
        GreenfootImage img = new GreenfootImage(text, size, mainColor, new Color(0, 0, 0, 0));
        GreenfootImage outline = new GreenfootImage(text, size, outlineColor, new Color(0, 0, 0, 0));
        GreenfootImage combined = new GreenfootImage(img.getWidth() + 4, img.getHeight() + 4);

        // Draw outline around text
        for (int dx = -2; dx <= 2; dx++)
            for (int dy = -2; dy <= 2; dy++)
                combined.drawImage(outline, dx + 2, dy + 2);

        combined.drawImage(img, 2, 2);
        int x = centerX - combined.getWidth() / 2;
        getBackground().drawImage(combined, x, y);
    }

    // Refresh background and redraw score
    private void clearTextArea() {
        GreenfootImage bg = new GreenfootImage("bg2.png");
        bg.scale(1100, 600);
        getBackground().drawImage(bg, 0, 0);
        scoreBoard.update(player1Score, player2Score);
    }
}