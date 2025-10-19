import greenfoot.*;

public class MenuWorld extends World {
    private enum MenuState {
    BLACK_SCREEN,
    SPLASH_1,
    SPLASH_2,
    MAIN,
    SPORT,
    DIFFICULTY
}
    private int blackScreenTimer = 5;
    private GreenfootImage bg;
    private MenuState currentState = MenuState.SPLASH_1;

    // Logo always top
    private Actor logoActor;

    // Menu button objects
    private ClickableImage onePButton, twoPButton;
    private ClickableImage basketballBtn, volleyballBtn, badmintonBtn;
    private ClickableImage easyBtn, mediumBtn, hardBtn, expertBtn, impossibleBtn;
    private ClickableImage noteButton;
    private boolean startedMusic = false;

    // Text overlays for menu screens
    private GreenfootImage pickSportImage, pickDifficultyImage;

    // Note modal popup
    private Actor noteActor;
    private boolean noteVisible = false;

    // Game settings selected by player
    private String selectedSport = null;
    private boolean isTwoPlayer = false;
    private int add_push_first = 20;
    
    // Track which button is currently being hovered
    private ClickableImage lastHoveredButton = null;

    // Timing and audio
    private int splashTimer = 0;
    private GreenfootSound bgMusic = new GreenfootSound("bg_loop.wav");
    
    // Pool of random backgrounds for menu screens
    private String[] backgroundImages = {"images/badminton_bg_normal.png", "images/bg.png", "images/bg2.png"};

    public MenuWorld() {
        super(1100, 600, 1);
        showBlackScreen();
    }

    // Display black screen before splash screens
    private void showBlackScreen() {
        GreenfootImage black = new GreenfootImage("images/blackscreen.png");
        black.scale(getWidth(), getHeight());
        setBackground(black);
        currentState = MenuState.BLACK_SCREEN;
        blackScreenTimer = 5;
    }

    // Show company production splash screen
    private void showFirstSplash() {
        GreenfootImage splash1 = new GreenfootImage("images/production_splash.png");
        splash1.scale(getWidth(), getHeight());
        setBackground(splash1);
        splashTimer = 40;
        currentState = MenuState.SPLASH_1;
    }

    // Show project splash screen
    private void showSecondSplash() {
        GreenfootImage splash2 = new GreenfootImage("images/project_splash.png");
        splash2.scale(getWidth(), getHeight());
        setBackground(splash2);
        splashTimer = 40;
        currentState = MenuState.SPLASH_2;
    }

    // Display main menu with 1P/2P mode selection
    private void showMainMenu() {
        clearObjects();
        currentState = MenuState.MAIN;
        selectedSport = null;
        isTwoPlayer = false;

        if (!bgMusic.isPlaying()) {
            bgMusic.playLoop();
        }

        onePButton = new ClickableImage("images/1p_menu.png", 130, 110);
        twoPButton = new ClickableImage("images/2p_menu.png", 140, 110);

        addObject(logoActor, getWidth() / 2 + 10, 165 + add_push_first);

        int centerX = getWidth() / 2;
        int y = 420 + add_push_first;
        int gap = 90;
        addObject(onePButton, centerX - gap, y);
        addObject(twoPButton, centerX + gap, y);
        
        // Info button always accessible
        noteButton = new ClickableImage("images/note_button.png", 50, 50);
        addObject(noteButton, getWidth() - 40, getHeight() - 40);
    }

    // Display sport selection menu
    private void showSportMenu() {
        clearObjects();
        currentState = MenuState.SPORT;

        pickSportImage = new GreenfootImage("images/pick_a_sport.png");
        pickSportImage.scale(300, 35);
        getBackground().drawImage(pickSportImage, getWidth() / 2 - pickSportImage.getWidth() / 2, 300 + add_push_first + 10);

        basketballBtn = new ClickableImage("images/basketball_menuLogo.png", 120, 120);
        volleyballBtn = new ClickableImage("images/volleyball_menuLogo.png", 120, 120);
        badmintonBtn = new ClickableImage("images/badminton_menuLogo.png", 120, 120);

        int y = 430;
        int gap = 200;
        int centerX = getWidth() / 2;

        addObject(basketballBtn, centerX - gap, y + add_push_first + 15);
        addObject(volleyballBtn, centerX, y + add_push_first + 15);
        addObject(badmintonBtn, centerX + gap, y + add_push_first + 15);

        addObject(logoActor, getWidth() / 2 + 10, 165 + add_push_first);
        
        noteButton = new ClickableImage("images/note_button.png", 50, 50);
        addObject(noteButton, getWidth() - 40, getHeight() - 30);
    }

    // Display difficulty selection menu (single player only)
    private void showDifficultyMenu() {
        clearObjects();
        currentState = MenuState.DIFFICULTY;

        pickDifficultyImage = new GreenfootImage("images/pick_a_difficulty.png");
        pickDifficultyImage.scale(300, 30);
        getBackground().drawImage(pickDifficultyImage, getWidth() / 2 - pickDifficultyImage.getWidth() / 2, 300 + add_push_first + 20);

        easyBtn = new ClickableImage("images/easy.png", 110, 35);
        mediumBtn = new ClickableImage("images/medium.png", 150, 35);
        hardBtn = new ClickableImage("images/hard.png", 110, 35);
        expertBtn = new ClickableImage("images/expert.png", 130, 35);
        impossibleBtn = new ClickableImage("images/impossible.png", 210, 35);

        int centerX = getWidth() / 2;
        int y = 410 + add_push_first;
        int gap = 160;

        addObject(easyBtn, (centerX - (gap * 2)) + 47, y);
        addObject(mediumBtn, (centerX - gap) + 20, y + 70);
        addObject(hardBtn, centerX, y);
        addObject(expertBtn, (centerX + gap) - 33, y + 70);
        addObject(impossibleBtn, (centerX + (gap * 2)) - 90, y);

        addObject(logoActor, getWidth() / 2 + 10, 165 + add_push_first);
        
        noteButton = new ClickableImage("images/note_button.png", 50, 50);
        addObject(noteButton, getWidth() - 40, getHeight() - 30);
    }

    // Update button hover states and play sound on hover transition
    private void handleHover() {
        MouseInfo mouse = Greenfoot.getMouseInfo();
        if (mouse == null) return;
        int mouseX = mouse.getX();
        int mouseY = mouse.getY();
        
        ClickableImage currentHoveredButton = null;
        
        for (ClickableImage img : getObjects(ClickableImage.class)) {
            int halfWidth = img.getImage().getWidth() / 2;
            int halfHeight = img.getImage().getHeight() / 2;
            int imgX = img.getX();
            int imgY = img.getY();

            boolean hovering = mouseX >= imgX - halfWidth && mouseX <= imgX + halfWidth &&
                               mouseY >= imgY - halfHeight && mouseY <= imgY + halfHeight;
            
            img.updateHover(hovering);
            
            if (hovering) {
                currentHoveredButton = img;
            }
        }
        
        // Play sound only when moving to a different button
        if (currentHoveredButton != null && currentHoveredButton != lastHoveredButton) {
            new GreenfootSound("menu_sound.wav").play();
            lastHoveredButton = currentHoveredButton;
        } else if (currentHoveredButton == null) {
            lastHoveredButton = null;
        }
    }

    // Process mouse clicks for menu navigation and game start
    private void handleClicks() {
        // Note button is always clickable
        if (noteButton != null && Greenfoot.mouseClicked(noteButton)) {
            toggleNote();
            return;
        }
        
        if (currentState == MenuState.MAIN) {
            if (Greenfoot.mouseClicked(onePButton)) {
                isTwoPlayer = false;
                showSportMenu();
            } else if (Greenfoot.mouseClicked(twoPButton)) {
                isTwoPlayer = true;
                showSportMenu();
            }
        } 
        else if (currentState == MenuState.SPORT) {
            if (Greenfoot.mouseClicked(basketballBtn)) selectedSport = "basketball";
            else if (Greenfoot.mouseClicked(volleyballBtn)) selectedSport = "volleyball";
            else if (Greenfoot.mouseClicked(badmintonBtn)) selectedSport = "badminton";
            else return;

            // 2P skips difficulty selection and starts on normal
            if (isTwoPlayer) startGame("normal");
            else showDifficultyMenu();
        } 
        else if (currentState == MenuState.DIFFICULTY) {
            if (Greenfoot.mouseClicked(easyBtn)) startGame("easy");
            else if (Greenfoot.mouseClicked(mediumBtn)) startGame("medium");
            else if (Greenfoot.mouseClicked(hardBtn)) startGame("hard");
            else if (Greenfoot.mouseClicked(expertBtn)) startGame("expert");
            else if (Greenfoot.mouseClicked(impossibleBtn)) startGame("impossible");
        }
    }

    // Handle escape key for navigation and closing modals
    private void handleEscape() {
        if (Greenfoot.isKeyDown("escape")) {
            if (noteVisible) {
                hideNote();
            } else if (currentState == MenuState.DIFFICULTY) {
                showSportMenu();
            } else if (currentState == MenuState.SPORT) {
                showMainMenu();
            }
        }
    }

    // Toggle note visibility
    private void toggleNote() {
        if (noteVisible) {
            hideNote();
        } else {
            showNote();
        }
    }

    // Create and display note modal
    private void showNote() {
        if (noteActor == null) {
            noteActor = new Actor() {
                {
                    GreenfootImage note = new GreenfootImage("images/note.png");
                    note.scale(1000, 330);
                    setImage(note);
                }
            };
        }
        addObject(noteActor, getWidth() / 2, getHeight() / 2);
        noteVisible = true;
    }

    // Remove note modal from display
    private void hideNote() {
        if (noteActor != null) {
            removeObject(noteActor);
            noteVisible = false;
        }
    }

    // Transition to game world with selected sport and difficulty
    private void startGame(String difficulty) {
        bgMusic.stop();

        if ("basketball".equals(selectedSport)) {
            if (isTwoPlayer) Greenfoot.setWorld(new BasketballWorld());
            else Greenfoot.setWorld(new BasketballWorld(true, difficulty));
        } 
        else if ("volleyball".equals(selectedSport)) {
            if (isTwoPlayer) Greenfoot.setWorld(new VolleyballWorld());
            else Greenfoot.setWorld(new VolleyballWorld(true, difficulty));
        } 
        else if ("badminton".equals(selectedSport)) {
            if (isTwoPlayer) Greenfoot.setWorld(new BadmintonWorld());
            else Greenfoot.setWorld(new BadmintonWorld(true, difficulty));
        }
    }

    // Reset menu screen with random background and recreate logo
    private void clearObjects() {
        removeObjects(getObjects(null));
        
        String randomBg = backgroundImages[(int)(Math.random() * backgroundImages.length)];
        bg = new GreenfootImage(randomBg);
        bg.scale(1100, 600);
        setBackground(bg);

        if (logoActor == null) {
            logoActor = new Actor() {
                {
                    GreenfootImage logo = new GreenfootImage("images/logo_3.png");
                    logo.scale(350, 230);
                    setImage(logo);
                }
            };
        }
        
        lastHoveredButton = null;
        noteVisible = false;
    }

    // Main game loop for menu state management
    public void act() {
        if (!startedMusic) {
            bgMusic.playLoop();
            startedMusic = true;
        }

        // Show black screen then transition to splash screens
        if (currentState == MenuState.BLACK_SCREEN) {
            if (--blackScreenTimer <= 0) {
                showFirstSplash();
            }
            return;
        }

        // Countdown splash screen timers
        if (currentState == MenuState.SPLASH_1 || currentState == MenuState.SPLASH_2) {
            if (--splashTimer <= 0) {
                if (currentState == MenuState.SPLASH_1) {
                    showSecondSplash();
                } else {
                    clearObjects();
                    logoActor = new Actor() {
                        {
                            GreenfootImage logo = new GreenfootImage("images/logo_3.png");
                            logo.scale(350, 230);
                            setImage(logo);
                        }
                    };
                    addObject(logoActor, getWidth() / 2 + 10, 160 + add_push_first);
                    showMainMenu();
                }
            }
            return;
        }

        // Handle user input for menu screens
        handleEscape();
        handleHover();
        handleClicks();
    }
}