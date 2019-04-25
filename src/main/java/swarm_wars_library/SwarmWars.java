package swarm_wars_library;

import processing.core.PApplet;
import swarm_wars_library.engine.*;
import swarm_wars_library.network.*;
import java.util.*;

/*control which screen is active by setting/updating gameScreen var
0: initial screen
1: game screen - game object
2: Game screen - entity
3: game-over screen
*/

public class SwarmWars extends PApplet {

    // Player must be here so that event listeners can access it
    private Entity player;

    private Entity enemyPlayer;

    // Entity list that has all our game things.
    private ArrayList <Entity> entityList = new ArrayList < Entity > ();
    // Entity builder class
    private EntityBuilder eb = new EntityBuilder(this);

    private int MAXSCREENS = 3;
    private int gameScreen = 2;
    private int initScreenTimer = 120;
    private int numBots = 30;
    private int numTurrets = 5;
    private static int playerId;
    private int enemyId;
    private int frameNumber;

    // global comms channel any entity that has comms should set comms to this
    CommsGlobal comms = new CommsGlobal();

    public static int getPlayerId(){
        return playerId;
    }

    public void setup() {
        frameRate(60); // We will need to test how frameRate affects our network - slower FR = less messages per second


        /* GUIDE TO ADDING NEW THINGS
          Use the EntityBuilder, for example: player = eb.newPlayer()
          this creates new entity - and automatically sets alls it's components
          optional - if has comms. add a space for it in a CommsChannel and set it's comms to the global comms
          add the entity to the entityList
        */

        // set up comms before entities
        comms.add("PLAYER" + playerId, new CommsChannel(numBots + 1));
        comms.add("ENEMY", new CommsChannel(numTurrets)); // we will add 1 turret therefore we have 1 item in enemy comms channel

        if(playerId == 0){
            enemyId = 1;
        } else {
            enemyId = 0;
        }

        comms.add("PLAYER" + enemyId, new CommsChannel(numBots + 1));

        // add a player
        player = eb.newPlayer(playerId);
        player.setComms(comms);
        entityList.add(player);

        //add player bots
        for (int i = 0; i < numBots; i++) {
            Entity bot = eb.newBot(playerId);
            bot.setSwarmLogic();
            bot.setComms(comms);
            entityList.add(bot);
        }


        // TODO set playerId of enemy player to a variable, also above in comms

        // add a player
        enemyPlayer = eb.newPlayer(enemyId);
        enemyPlayer.setComms(comms);
        entityList.add(enemyPlayer);

        SwarmLogic.resetCounter();
        //add player bots
        for (int i = 0; i < numBots; i++) {
            Entity bot = eb.newBot(enemyId);
            bot.setSwarmLogic();
            bot.setComms(comms);
            entityList.add(bot);
        }


        // add Enemies
        for (int i = 0; i < numTurrets; i++){
            Entity turret = eb.newTurret(0);
            turret.setPosition(Math.random() * width +1, Math.random() * height + 1);
            turret.setComms(comms);
            entityList.add(turret);
        }

        // IMPORTANT to do at end of setup - sets all initial packets to current
        comms.update();

        // NETWORK setup - put empty package?
        frameNumber = 0;
        NetworkClientFunctions.cleanBuffer();
        NetworkClientFunctions.sendConnect(playerId);
        NetworkClientFunctions.sendSetup(playerId);

        NetworkClientFunctions.sendStart(playerId);
        NetworkClientFunctions.awaitStart();
        NetworkClientFunctions.sendOperation(playerId, frameNumber, player.input);
    }

    public void settings() {
        size(700, 900, "processing.awt.PGraphicsJava2D");
    }

    public void draw() {
        //display contents of the current screen
        if (gameScreen == 0) {
            initScreen();
        } else if (gameScreen == 1) {
            gameScreen();
        } else if (gameScreen == 2) {
            gameScreenEntity();
        } else {
            gameOverScreen();
        }
    }

    /*--------GAME SCREENS ----*/

    void initScreen() {
        background(0);
        textAlign(CENTER);
        text("welcome to\n\nSWARM WARS\n\n\nMove: WASD", width / 2, height / 2);

        //after timer, switch to game
        if (initScreenTimer-- < 0) {
            gameScreen = 1;
        }
    }

    void gameScreen() {
        background(25, 25, 76);
    }

    // >>>>>> MAIN GAME LOOP <<<<<<<<<<
    void gameScreenEntity() {
        background(25, 25, 76);

        // NETWORK need to get other players inputs from server
        // NETWORK need to set player inputs in this game

        Map<String, Object> messageIn = NetworkClientFunctions.getPackage(playerId, frameNumber++);
        if(messageIn.containsKey("W")) enemyPlayer.input.setMoveUp((Integer) messageIn.get("W"));
        if(messageIn.containsKey("A")) enemyPlayer.input.setMoveLeft((Integer) messageIn.get("A"));
        if(messageIn.containsKey("S")) enemyPlayer.input.setMoveDown((Integer) messageIn.get("S"));
        if(messageIn.containsKey("D")) enemyPlayer.input.setMoveRight((Integer) messageIn.get("D"));

        // update all game things
        for (int j = 0; j < entityList.size(); j++) {
            entityList.get(j).update();
        }

        // sets future comms to current for next loop
        comms.update();


        NetworkClientFunctions.sendOperation(playerId, frameNumber, player.input);
        // NetworkClientFunctions.threadSleep();
    }

    void gameOverScreen() {
        background(0, 0, 0);
    }

    void changeScreen(int k) {
        //TODO add more checks here, only change screens in certain cases
        if (k == 'n' || k == 'N') {
            gameScreen++;
            if (gameScreen > MAXSCREENS) {
                gameScreen = 0;
            }
        }
        //add pause screen on 'p'
    }


    public static void main(String[] passedArgs) {
        // TODO need check if these inputs are correct


        playerId = NetworkClientFunctions.getPlayerIdFromUser();

        new Thread(new Runnable() {
            public void run() {
                try {
                    GameClient.run();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();

        try {
            GameClient.countDownLatch.await();
            Thread.sleep(10000);
        } catch (Exception e) {
            System.out.println("FAILED");
            e.printStackTrace();
        }


        // processing main

        String[] appletArgs = new String[] {
                "swarm_wars_library.SwarmWars"
        };

        PApplet.main(appletArgs);
    }

    /* ------ EVENT LISTENERS ------ */
    public void keyPressed() {
        //changeScreen(keyCode);
        player.input.setMove(keyCode, 1);
    }

    public void keyReleased() {
        player.input.setMove(keyCode, 0);
    }
}
