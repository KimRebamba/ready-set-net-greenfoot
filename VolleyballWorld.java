import greenfoot.*;

public class VolleyballWorld extends World {
    private VolleyballPlayer player1;
    private VolleyballPlayer player2;
    private Volleyball ball;
    private VolleyballNet net;
    private ScoreBoard scoreBoard;

    private int player1Score = 24;
    private int player2Score = 0;

    private boolean pointAwarded = false;
    private boolean isServingLeft = true;
    private int serveDelayTimer = 0;

    private boolean gameOver = false;

    // Message handling
    private int messageTimer = 0;
    private String activeMessage = null;

    public VolleyballWorld() {
        super(1100, 600, 1);
        GreenfootImage bg = new GreenfootImage("bg2.png");
        bg.scale(1100, 600);
        setBackground(bg);
        prepareGame();
    }

    private void prepareGame() {
        // Net
        net = new VolleyballNet();
        GreenfootImage netImage = net.getImage();
        netImage.scale(20, 315);
        addObject(net, getWidth() / 2, getHeight() - 180);

        // Players
        player1 = new VolleyballPlayer(true);
        player2 = new VolleyballPlayer(false);
        addObject(player1, 200, getHeight() - 100);
        addObject(player2, 900, getHeight() - 100);

        // Ball
        spawnNewBallAtCenter();

        // Scoreboard
        scoreBoard = new ScoreBoard();
        addObject(scoreBoard, getWidth() / 2, 50);
    }

    public void act() {
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

        // Check for winner first before showing message
        if (player1Score >= 25 || player2Score >= 25) {
            clearTextArea();
            
            endGame();
            return; // stop here — no “Left/Right side scores!” message
        }

        // Only show this if the game isn’t over
        showPointMessage(leftLost ? "Right side scores!" : "Left side scores!");
        serveDelayTimer = 100;
    }
}


    private void endGame() {
    gameOver = true;

    String winner = (player1Score >= 25) ? "LEFT SIDE WINS!" : "RIGHT SIDE WINS!";
    drawOutlinedText(winner, getWidth() / 2, 100, Color.GREEN, Color.BLACK, 50); // was 250 → now 200
    drawOutlinedText("Press ESC to return to menu", getWidth() / 2, 170, Color.WHITE, Color.BLACK, 30); // was 320 → now 280
}


    private void resetBall() {
        
        if (gameOver) {
            return;
        }
        
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
