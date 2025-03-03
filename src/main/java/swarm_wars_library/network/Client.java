package swarm_wars_library.network;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Client {

    public static int id;

    public static void main(String[] args) throws Exception {
        System.out.println("Enter your id");
        Scanner scanner = new Scanner(System.in);
        id = scanner.nextInt();

        int version = 0;

        new Thread(new Runnable() {
            public void run() {
                try {
                    GameClient.run();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();

        int frame = 0;

        GameClient.countDownLatch.await();
//        System.out.println("Start sending messages");
        Thread.sleep(5000);
        Map<String, Object> m = new HashMap<String, Object>();

        int times = 0;

        int interval = 1000;

        while(times < 5) {
//            System.out.println("***************");
//            System.out.println("Round " + times + " begins");

            // Clean the whole buffer
            MessageHandlerMulti.refreshClientReceiveBuffer();

            // First sends a CONNECT package to server
            // CONNECT means the player is connected to the server, now setting up swarms
            m = new HashMap<>();
            m.put(Headers.TYPE, Constants.CONNECT);
            m.put(Headers.PLAYER, id);
            MessageHandlerMulti.putPackage(m);
//            System.out.println("Sent CONNECT");
            Thread.sleep(interval);

            // Then sends a SETUP package to server
            // SETUP means the swarms logic is ready, and is prepared to play the game
            m = new HashMap();
            m.put(Headers.TYPE, Constants.SETUP);
            m.put(Headers.PLAYER, id);
            MessageHandlerMulti.putPackage(m);
//            System.out.println("Sent SETUP");
            Thread.sleep(interval);

            // If this is player 0, try to start the game
            // If the game is started successfully
            // The server will broadcast a START package
            // In this package there is a random seed generated by the server
            while (!MessageHandlerMulti.gameStarted && id == 1) {
                m = new HashMap();
                m.put(Headers.TYPE, Constants.START);
                MessageHandlerMulti.putPackage(m);
//                System.out.println("Tried to send START, but game not ready");
                Thread.sleep(interval);
            }

            Map rev = null;

            // Then keeps sending OPERATION package to server
            while (!MessageHandlerMulti.gameStarted) {
//                System.out.println("Game not started yet");
                Thread.sleep(interval);
            }

            System.out.println("Game start");

            if (id == 2) {
                m = new HashMap();
                m.put(Headers.TYPE, Constants.UPDATE_TURRET);
                m.put(Headers.TURRET_ID, 0);
                m.put(Headers.TURRET_VERSION, version++);
                System.out.println(m.toString());
                MessageHandlerMulti.putPackage(m);
            }

            /*
            while (frame < 20) {
                m = new HashMap<String, Object>();
                m.put(Headers.TYPE, Constants.OPERATION);
                m.put(Headers.PLAYER, id);
                m.put(Headers.W, 0);
                m.put(Headers.A, 1);
                MessageHandlerMulti.putPackage(m);
                while (rev == null) {
//                    System.out.println("Player " + id + " trying to get package with frame number " + frame);
                    rev = MessageHandlerMulti.getPackage(id == 1? 2 : 1, frame);
                    if (rev == null) {
//                        System.out.println("Did not get wanted package, try again, main game waiting");
                    }
                    Thread.sleep(interval);
                }
                rev = null;
                frame++;
                Thread.sleep(interval);
            }*/
            /*
            m = new HashMap<String, Object>();
            m.put(Headers.TYPE, Constants.END);
            m.put(Headers.PLAYER, id);
            MessageHandlerMulti.putPackage(m);
//            System.out.println("Player " + id + " Game ends");

            times++;
            frame = 0;
            Thread.sleep(1000);
            */
        }
    }
}
