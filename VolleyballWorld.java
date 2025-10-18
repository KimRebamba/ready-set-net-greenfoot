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

    // Instruction screen delay
    private int startDelay = 30; // frames (~0.5s) before input is accepted

    // Message handling
    private int messageTimer = 0;
    private String activeMessage = null;

    private GreenfootSound whistleSound = new GreenfootSound("volleyball_whistle.wav");
    private GreenfootSound endSound = new GreenfootSound("whistle_end.wav");

    public VolleyballWorld() {
        super(1100, 600, 1);
        showInstructions();
    }

    private void showInstructions() {
        GreenfootImage instructions = new GreenfootImage("volleyball_instructions.png");
        instructions.scale(1100, 602);
        setBackground(instructions);
    }

    private void prepareGame() {
        whistleSound.play();

        // Background
        GreenfootImage bg = new GreenfootImage("bg2.png");
        bg.scale(1100, 600);
        setBackground(bg);

        // Net
        net = new VolleyballNet();
        GreenfootImage netImage = net.getImage();
        netImage.scale(20, 315);
        addObject(net, getWidth() / 2, getHeight() - 180);

        // Players
        player1 = new VolleyballPlayer(true);
        player2 = new VolleyballPlayer(false, false, "impossible");
        addObject(player1, 200, getHeight() - 100);
        addObject(player2, 900, getHeight() - 100);

        // Ball
        spawnNewBallAtCenter();

        // Scoreboard
        scoreBoard = new ScoreBoard();
        addObject(scoreBoard, getWidth() / 2, 50);
    }

    public void act() {
        // --- Instruction screen phase ---
    if (!gameStarted) {
        if (startDelay > 0) {
            startDelay--;
            return;
        }

        // Wait for 3 seconds (≈120 frames) or any input
        if (startDelay <= 0) {
            if ( startDelay == -120) {
                gameStarted = true;
                prepareGame();
                return;
            }
            startDelay--; // count down to -120
            if (startDelay < -120) { // auto-start after 3s
                gameStarted = true;
                prepareGame();
            }
        }
        return;
    }

        // ESC key → back to menu
        if (Greenfoot.isKeyDown("escape")) {
            Greenfoot.setWorld(new MenuWorld());
            return;
        }

        // Stop updates if game over
        if (gameOver) return;

        // Don’t check score if ball is gone (avoid null error)
        if (ball != null) {
            checkScore();
        }

        // Handle serve delay
        if (serveDelayTimer > 0) {
            serveDelayTimer--;
            if (serveDelayTimer == 0 && !gameOver) {
                resetBall();
            }
        }

        // Handle timed text disappearance
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

        if (!pointAwarded && ball.getY() >= groundY - ball.getImage().getHeight() / 2) {
            pointAwarded = true;

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
        drawOutlinedText("Press ESC to return to menu", getWidth() / 2, 170, Color.WHITE, Color.BLACK, 30);
    }

    private void resetBall() {
        if (gameOver) return;

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
        GreenfootImage bg = new GreenfootImage("bg2.png");
        bg.scale(1100, 600);
        getBackground().drawImage(bg, 0, 0);
        scoreBoard.update(player1Score, player2Score);
    }
}
