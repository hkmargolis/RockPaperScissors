package com.example.demo2;

import java.io.*;
import java.net.*;
import java.util.Date;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

/**
 * The RPSServer Class extends Application and implements RPSConstants. It activates a server with sockets for clients,
 * opens the gui, creates a new GameLogic object and GameSession object and runs the game. It takes in a MoveType enum
 * from each player and determines who the winner is for the round and if the game session is over determines the final winner.
 *
 */

public class RPSServer extends Application implements RPSConstants {
    //Variables
    private int sessionNo = 1; // Number a session
    private GameLogic logic;
    private GameSession gameSession;

    @Override // Override the start method in the Application class
    public void start(Stage primaryStage) {
        TextArea taLog = new TextArea();
        logic = new GameLogic(); //GameLogic calculates the winner for each round
        gameSession = new GameSession(); //GameSession keeps track of how many rounds their has been and the final winner

        // Create a scene and place it in the stage
        Scene scene = new Scene(new ScrollPane(taLog), 450, 200);
        primaryStage.setTitle("RPSServer"); // Set the stage title
        primaryStage.setScene(scene); // Place the scene in the stage
        primaryStage.show(); // Display the stage

        //creation of a new thread comes directly form TicTacToe demo
        new Thread(() -> {
            try {
                // Create a server socket
                ServerSocket serverSocket = new ServerSocket(8000);
                Platform.runLater(() -> taLog.appendText(new Date() +
                        ": Server started at socket 8000\n"));

                // Ready to create a session for every two players
                while (true) {
                    Platform.runLater(() -> taLog.appendText(new Date() +
                            ": Wait for players to join session " + sessionNo + '\n'));

                    // Connect to player 1
                    Socket player1 = serverSocket.accept();

                    Platform.runLater(() -> {
                        taLog.appendText(new Date() + ": Player 1 joined session "
                                + sessionNo + '\n');
                        taLog.appendText("Player 1's IP address" +
                                player1.getInetAddress().getHostAddress() + '\n');
                    });

                    // Notify that the player is Player 1
                    new DataOutputStream(
                            player1.getOutputStream()).writeInt(PLAYER1);

                    // Connect to player 2
                    Socket player2 = serverSocket.accept();

                    Platform.runLater(() -> {
                        taLog.appendText(new Date() +
                                ": Player 2 joined session " + sessionNo + '\n');
                        taLog.appendText("Player 2's IP address" +
                                player2.getInetAddress().getHostAddress() + '\n');
                    });

                    // Notify that the player is Player 2
                    new DataOutputStream(
                            player2.getOutputStream()).writeInt(PLAYER2);

                    // Display this session and increment session number
                    Platform.runLater(() ->
                            taLog.appendText(new Date() +
                                    ": Start a thread for session " + sessionNo++ + '\n'));

                    // Launch a new thread for this session of two players
                    new Thread(new HandleASession(player1, player2)).start();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    // Inner Class .... define the thread class for handling a new session for two players
    class HandleASession implements Runnable, RPSConstants {
        //Variables
        private Socket player1;
        private Socket player2;
        private DataInputStream fromPlayer1;
        private DataOutputStream toPlayer1;
        private DataInputStream fromPlayer2;
        private DataOutputStream toPlayer2;
        private int  player1MoveType= 10; //This initializes the variable, but the value is arbitrary
        private int player2MoveType = 11; //This initializes the variable, but the value is arbitrary
        private boolean continueToPlay = true;

        /**
         * Construct a thread
         */
        public HandleASession(Socket player1, Socket player2) {
            this.player1 = player1;
            this.player2 = player2;
        }

        /**
         * Implement the run() method for the thread
         */
        public void run() {
            try {

                // Create data input and output streams
                DataInputStream fromPlayer1 = new DataInputStream(
                        player1.getInputStream());
                DataOutputStream toPlayer1 = new DataOutputStream(
                        player1.getOutputStream());
                DataInputStream fromPlayer2 = new DataInputStream(
                        player2.getInputStream());
                DataOutputStream toPlayer2 = new DataOutputStream(
                        player2.getOutputStream());

                //Write anything to notify player 1 to start
                toPlayer1.writeInt(10);
                toPlayer2.writeInt(10);

                // Continuously serve the players and determine and report the game status to the players
                //System.out.println("before while loop");
                while (continueToPlay) {
                    //System.out.println("top of while loop");

                    // Receive a move from player 1
                    player1MoveType = fromPlayer1.readInt();
                    logic.setPlayer1MoveType((player1MoveType));
                    //System.out.println("Player1: " + player1MoveType);

                    // Receive a move from player 2
                    player2MoveType = fromPlayer2.readInt();
                    logic.setPlayer2MoveType(player2MoveType);
                    //System.out.println("Player2: " + player2MoveType);

                    //Determine round winner
                    logic.calcRoundWinner();
                    int roundWinner = logic.getRoundWinner();
                    //System.out.println("Round winner: " + roundWinner);

                    //Increment player points based on winner
                    gameSession.incrementPlayerPoints(roundWinner);

                    //Calculate the cumulative winner
                    gameSession.calcWinner();

                    //Increment the round status
                    gameSession.incrementRoundStatus();

                    // update players with the game status
                    //case 1: game not over, round was a draw
                    if (!gameSession.gameOver() && roundWinner == 0) {
                        toPlayer1.writeInt(ROUND_WAS_DRAW);
                        toPlayer2.writeInt(ROUND_WAS_DRAW);
                        //System.out.println("round was draw");
                    }
                    //case 2: game not over, player 1 won round
                    else if (!gameSession.gameOver() && roundWinner == 1) {
                        toPlayer1.writeInt(PLAYER1_WON_ROUND);
                        toPlayer2.writeInt((PLAYER1_WON_ROUND));
                        //System.out.println("player 1 won round");

                    }
                    //case 3: game not over, player 2 won round
                    else if (!gameSession.gameOver() && roundWinner == 2) {
                        toPlayer1.writeInt(PLAYER2_WON_ROUND);
                        toPlayer2.writeInt(PLAYER2_WON_ROUND);
                        //System.out.println("player 2 won round");

                    }
                    //case 4: game over (round 5 completed), game session was a draw
                    else if (gameSession.gameOver() && isWinner(0)){
                        toPlayer1.writeInt(DRAW);
                        toPlayer2.writeInt(DRAW);
                        continueToPlay = false;
                        //System.out.println("game over, draw");
                    }
                    //case 5: game over (round 5 completed), player 1 won game session
                    else if(gameSession.gameOver() && isWinner(1)){
                        toPlayer1.writeInt(PLAYER1_WON);
                        toPlayer2.writeInt((PLAYER1_WON));
                        continueToPlay = false;
                        //System.out.println("game over, player 1 won");
                    }
                    //case 6: game over (round 5 completed), player 2 won game session
                    else if(gameSession.gameOver() && isWinner(2)){
                        toPlayer1.writeInt(PLAYER2_WON);
                        toPlayer2.writeInt(PLAYER2_WON);
                        continueToPlay = false;
                        //System.out.println("game over, player 2 won");

                    }
                    //case 7: player has requested new game
                    else if(fromPlayer1.readInt() == NEW_GAME || fromPlayer2.readInt() == NEW_GAME){
                        continueToPlay = true;
                        gameSession.resetGameSession();
                        logic.resetGameLogic();
                        //System.out.println("reset game session and game logic variables");
                        run();
                    }
                    //case 8: player has quit the game
                    else if(fromPlayer1.readInt() == QUIT_GAME || fromPlayer2.read() == QUIT_GAME){
                        continueToPlay = false;
                        System.exit(0);
                    }
                }//exit while loop b/c continueToPlay == false

                //listen for player to start a new game
                if(fromPlayer1.readInt() == NEW_GAME || fromPlayer2.readInt() == NEW_GAME) {
                    continueToPlay = true;
                    gameSession.resetGameSession();
                    logic.resetGameLogic();
                    //System.out.println("reset game session and game logic variables");
                    run();
                }
                //listen for player to quit
                else if(fromPlayer1.readInt() == QUIT_GAME || fromPlayer2.read() == QUIT_GAME){
                    System.exit(0);
                }

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        /**
         * Determine if the player with the specified token wins
         * @param token 1 or 2
         * @return winner if token == winner, returns true
         */
        private boolean isWinner(int token) {
            int winner = gameSession.getWinner();
            if (winner == token) {
                return true;
            }
            return false;

        }
    }

    /**
     * The main method is only needed for the IDE with limited
     * JavaFX support. Not needed for running from the command line.
     */
    public static void main(String[] args) {
        launch(args);
    }
}

