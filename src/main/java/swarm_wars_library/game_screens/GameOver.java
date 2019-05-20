package swarm_wars_library.game_screens;

import processing.core.PApplet;
import processing.core.PImage;
import swarm_wars_library.SwarmWars;
import swarm_wars_library.map.Map;
import swarm_wars_library.physics.Vector2D;
import swarm_wars_library.swarm_select.TextButton;
import processing.core.PConstants;
import java.util.concurrent.*;

import swarm_wars_library.comms.CommsGlobal;


public class GameOver {

    public static GameOver instance = new GameOver();

    // Processing
    private PApplet sketch;
    //private PImage backgroundImage;
    private PImage background;
    private PImage gameOverLogo;
    private PImage brokenShipLogo;
    private PImage flames;
    private PImage dummy;

    // Game Screen
    private GAMESCREEN currentScreen;

    // Game Score
    private int myScore;
    private int enemyScore;

    // Replay Button
    private Vector2D dimReplayButton;
    private Vector2D locationReplayButton;
    private TextButton renderReplayButton;

    // Mouse
    private boolean mousePressed = false;

    // animated sprite info
    private PImage[] sprites;
    private final int spriteX = 4;
    private final int spriteY = 1;
    private final int totalSprites = spriteX * spriteY;
    private int currentSprite;
    private int spriteW;
    private int spriteH;
    private int index = 0;

    private int winningPlayer = 0;

    private Map map;


    private GameOver(){};

    public void setup(PApplet sketch) {
        this.sketch = sketch;
        background = sketch.loadImage("resources/images/background.png");
        brokenShipLogo = sketch.loadImage("resources/images/brokenShipLogo.png"); 
        gameOverLogo = sketch.loadImage("resources/images/gameoverLogo.png");
        flames = sketch.loadImage("resources/images/gameOverFlameSingle.png");
        dummy = sketch.loadImage("resources/images/dummy.png");

        this.myScore = 0;
        this.enemyScore = 0;

        this.setupReplayButton();

        // animated sprite setup
        sprites = new PImage[totalSprites];
        spriteW = flames.width/spriteX;;
        spriteH = flames.height/spriteY;
        currentSprite = ThreadLocalRandom.current().nextInt( 0 , totalSprites );
        index = 0;
        for (int x = 0; x < spriteX; x++){
            for (int y = 0; y < spriteY; y++) {
                sprites[index] = flames.get(x * spriteW, y * spriteH, spriteW, spriteH);
                index++;
            }
        }

        map = Map.getInstance();
    }

    public void update() {
        if(sketch == null) throw new Error("GameOver screen not setup with sketch");
        this.updateBackground();
        this.updateMousePressButton();
        this.updateReplayButton();
    }

    public static GameOver getInstance() {
        return instance;
    }

    //=========================================================================//
    // Button methods                                                          //
    //=========================================================================//

    private void setupReplayButton() {

        int buttonWidth = 150;
        int buttonHeight = 60;
        int windowWidth = sketch.width;
        int windowHeight = sketch.height;

        dimReplayButton = new Vector2D(buttonWidth, buttonHeight);
        locationReplayButton = new Vector2D(windowWidth / 2 - buttonWidth / 2
                , windowHeight / 2 - buttonHeight / 2);

        this.renderReplayButton = new TextButton(
                this.sketch
                , "REPLAY"
                , locationReplayButton
                , dimReplayButton
                , 40, 40, 40
        );
    }

    private void updateReplayButton() {
        this.sketch.textSize(25);
        this.renderReplayButton.update();
        this.sketch.textSize(12);
    }


    //=========================================================================//
    // Mouse methods                                                           //
    //=========================================================================//

    public void listenMousePressed(){
        this.mousePressed = true;
    }

    public void listenMouseReleased(){
        this.mousePressed = false;
    }

    private boolean checkMousePressButton(Vector2D location,
                                          Vector2D dimensions){
        if(this.mousePressed){
            if(this.sketch.mouseX >= location.getX() &&
                    this.sketch.mouseX <= location.getX() + dimensions.getX() &&
                    this.sketch.mouseY >= location.getY() &&
                    this.sketch.mouseY <= location.getY() + dimensions.getY()){
                return true;
            }
        }
        return false;
    }

    private void updateMousePressButton() {
        if(this.checkMousePressButton(this.locationReplayButton, this.dimReplayButton)) {
            this.currentScreen = GAMESCREEN.FSMUI;
        }
    }

    //=========================================================================//
    // Background methods                                                      //
    //=========================================================================//
    private void updateBackground(){



        // draw background stars
        //this.sketch.background(13, 30, 40);
        this.sketch.imageMode(PConstants.CORNERS);
        this.sketch.image(background, 0, 0, this.sketch.width, this.sketch.height);

        if(this.getWinningPlayer() == map.getPlayerId()){
            // this player is winner
            System.out.println("WINNER: This Player won...");
            this.updateBackgroundWinner();
        } else if(this.getWinningPlayer() == map.getEnemyId()){
            // this player lost
            System.out.println("WINNER: Enemy won...");
            this.updateBackgroundLoser();
        } else {
            System.out.println("WINNER: ERROR");
        }

    }

    private void updateBackgroundWinner() {
        // draw gameover & ship logos
        //this.sketch.image(this.gameOverLogo, this.sketch.width/4, 20,(this.sketch.width/4)*3, (this.sketch.height/8)*3);
        this.sketch.image(this.gameOverLogo, 0, 0, this.sketch.width, this.sketch.height);
        this.sketch.imageMode(PConstants.CENTER);
        this.sketch.image(this.dummy, (this.sketch.width/2), (this.sketch.height/2));


    }

    private void updateBackgroundLoser() {
        // draw gameover & ship logos
        //this.sketch.image(this.gameOverLogo, this.sketch.width/4, 20,(this.sketch.width/4)*3, (this.sketch.height/8)*3);
        this.sketch.image(this.gameOverLogo, 0, 0, this.sketch.width, this.sketch.height);
        this.sketch.imageMode(PConstants.CENTER);
        this.sketch.image(this.brokenShipLogo, (this.sketch.width/2)*2, (this.sketch.height/4)*3);

        // draw fire
        this.sketch.imageMode(PConstants.CORNERS);
        this.sketch.image(this.sprites[currentSprite], 0, this.sketch.height/3,
                this.sketch.width, this.sketch.height);
        currentSprite++;
        currentSprite %= totalSprites;
    }

    //=========================================================================//
    // Data methods                                                            //
    //=========================================================================//

    public GAMESCREEN getGameScreen() {
        return currentScreen;
    }

    public void resetCurrentScreen() {
        this.currentScreen = GAMESCREEN.GAMEOVER;
    }


    public int getWinningPlayer() {
        return winningPlayer;
    }

    public void setWinningPlayer(int winningPlayer) {
        this.winningPlayer = winningPlayer;
    }
}
