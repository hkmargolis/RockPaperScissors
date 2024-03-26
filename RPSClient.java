package com.example.demo2;

import java.io.*;
import java.net.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * The RPSClient class extends Application and implements RPSConstants. It activates a client, gui, assigns the client a player token,
 * and sends the client's moves to the server.
 */
public class RPSClient extends Application
        implements RPSConstants {
    // Indicate the token for the player, set by the server
    private int myToken;
    // Indicate the token for the other player
    private int otherToken;
    //create ButtonPane
    private ButtonPane buttonPane;
    // Create and initialize a status label
    private Label lblStatus = new Label(); //updates the players with info about the game

    // Input and output streams from/to server
    private DataInputStream fromServer;
    private DataOutputStream toServer;
    // Saves the button selected by the player to send to the server
    private int buttonSelected;
    // Tracks the round status of the players for display purposes only
    private int p1roundStatus = 0;
    private int p2roundStatus = 0;

    // Informs the while loop whether the game is over or the players should continue
    private boolean continueToPlay = true;

    // Wait for the player to select ROCK, PAPER, or SCISSORS
    private boolean waiting = true;

    // Host name or ip
    private String host = "localhost";

    @Override // Override the start method in the Application class
    public void start(Stage primaryStage) {
        // Initialize ButtonPane object
        buttonPane = new ButtonPane();

        //Create BorderPane
        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(buttonPane);
        borderPane.setBottom(lblStatus);

        // Create a scene and place it in the stage
        Scene scene = new Scene(borderPane, 650,300);
        primaryStage.setResizable(false); //Not resizeable
        primaryStage.setTitle("RPSClient"); // Set the stage title
        primaryStage.setScene(scene); // Place the scene in the stage
        primaryStage.show(); // Display the stage

        //Set the 5 Button Actions...
        //1. ROCK BUTTON
        buttonPane.getRockButton().setOnAction(e -> {
            Platform.runLater(() -> {
                lblStatus.setText("Selected rock."); //inform the user which button they selected
            });
            buttonSelected = 1; //save value, int matches MoveType.ROCK(1)
            //System.out.println(buttonSelected);
            waiting = false; //set waiting to false, player has finished their move
        });
        //2. PAPER BUTTON
        buttonPane.getPaperButton().setOnAction(e -> {
            if(continueToPlay == true) {
                Platform.runLater(() -> {
                    lblStatus.setText("Selected paper.");
                });
                buttonSelected = 2;
                //System.out.println(buttonSelected);
                waiting = false;
            }
        });
        //3.SCISSORS BUTTON
        buttonPane.getScissorsButton().setOnAction(e -> {
            if(continueToPlay == true) {
                Platform.runLater(() -> {
                    lblStatus.setText("Selected scissors.");
                });
                buttonSelected = 3;
                //System.out.println(buttonSelected);
                waiting = false;
            }
        });
        //4.NEW GAME BUTTON
        buttonPane.getNewGameButton().setOnAction(e -> {
            waiting = true;
            continueToPlay = true;
            p1roundStatus = 0; //reset p1RoundStatus
            p2roundStatus = 0; //reset p2RoundStatus
            Platform.runLater(() ->
                    lblStatus.setText("New Game. Select ROCK, PAPER, or SCISSORS."));
            try {
                toServer.writeInt(NEW_GAME);
                connectToServer();
            } catch (IOException ex) {
                System.out.println("Error with New Game button");
                throw new RuntimeException(ex);
            }
        });
        //5.QUIT BUTTON
        buttonPane.getQuitButton().setOnAction(e -> {
            try {
                toServer.writeInt(QUIT_GAME);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            System.exit(0);
        });

        // Connect to the server
        connectToServer();
    }

    /**
     * This method creates the new sockets, new thread, sets the player tokens, and runs the game while continueToPlay == true.
     * It waits for the clients to select a button, sends the button info for each client to the server and receives the info
     * from the server for each client until the game session is over or a client quits.
     */
    private void connectToServer() {
        try {
            // Create a socket to connect to the server
            Socket socket = new Socket(host, 8000);

            // Create an input stream to receive data from the server
            fromServer = new DataInputStream(socket.getInputStream());

            // Create an output stream to send data to the server
            toServer = new DataOutputStream(socket.getOutputStream());
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

        // Control the game on a separate thread
        new Thread(() -> {
            try {

                // Get notification from the server
                int player = fromServer.readInt();

                // Am I player 1 or 2?
                if (player == PLAYER1) {
                    myToken = 1;
                    otherToken = 2;
                    Platform.runLater(() -> {
                        lblStatus.setText("Waiting for player 2 to join...");
                    });

                    // Receive startup notification from the server
                    fromServer.readInt(); // Whatever read is ignored

                    // The other player has joined
                    Platform.runLater(() ->
                            lblStatus.setText("Player 1: Select ROCK, PAPER, or SCISSORS."));
                }
                else if (player == PLAYER2) {
                    myToken = 2;
                    otherToken = 1;
                    Platform.runLater(() -> {
                        lblStatus.setText("Player 2: Select ROCK, PAPER, or SCISSORS.");
                    });
                }
                //once players have connected to server, game has started and player have the option to quit
                buttonPane.getQuitButton().setVisible(true);

                // Continue to play
                while (continueToPlay) {
                    if (player == PLAYER1) {
                        waitForPlayerAction(); // Wait for player 1 to move
                        sendMove(); // Send the move to the
                        receiveInfoFromServer();
                    }
                    else if (player == PLAYER2) {
                        receiveInfoFromServer();
                        waitForPlayerAction(); // Wait for player 2 to move
                        sendMove(); // Send player 2's move to the server
                    }
                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    /**
     * This method puts the thread to sleep while waiting for a player to select a button
     * and sets waiting to true so the next player can take their turn.
     * @throws InterruptedException
     */
    private void waitForPlayerAction() throws InterruptedException {
        while (waiting) {
            Thread.sleep(100);
        }

        waiting = true;
    }

    /** This method sends the saved button selected by a player to the server
     * @throws IOException
     * */
    private void sendMove() throws IOException {
        //to do: send the selected button
        toServer.writeInt(buttonSelected);
    }

    /**This method receives the info from the server (from RPSConstants). It prints new info for the players telling them
     * which round it is, the winner of the round and ultimately the winner of game.
     * @throws IOException
     * */
    private void receiveInfoFromServer() throws IOException {
        // Receive game status
        int status = fromServer.readInt();

        //case 1: player 1 won round, game continues
        if (status == PLAYER1_WON_ROUND){
            if (myToken == 1){
                //display player 1 as winner in client 1 gui
                Platform.runLater(() -> lblStatus.setText("You won round " + p1roundStatus + "! --> Select ROCK, PAPER, or SCISSORS."));
                p1roundStatus++;
            }
            else if (myToken == 2) {
                //display player 1 as winner in client 2 gui
                Platform.runLater(() ->
                        lblStatus.setText("Player 1 won round " + p2roundStatus + "! --> Select ROCK, PAPER, or SCISSORS."));
                p2roundStatus++;
            }
        }
        //case 2: player 2 won round, game continues
        else if(status == PLAYER2_WON_ROUND){
            if (myToken == 1){
                //display player 2 as winner in client 1 gui
                Platform.runLater(() -> lblStatus.setText("Player 2 won round " + p1roundStatus + "! --> Select ROCK, PAPER, or SCISSORS."));
                p1roundStatus++;
            }
            else if (myToken == 2) {
                //display player 2 as winner in client 2 gui
                Platform.runLater(() ->
                        lblStatus.setText("You won round " + p2roundStatus + "! --> Select ROCK, PAPER, or SCISSORS."));
                p2roundStatus++;
            }
        }
        //case 3: round was a draw, game continues
        else if(status == ROUND_WAS_DRAW){
            if (myToken == 1){
                //display draw in client 1 gui
                Platform.runLater(() -> lblStatus.setText("Round " + p1roundStatus + " was a draw! --> Select ROCK, PAPER, or SCISSORS."));
                p1roundStatus++;
            }
            else if (myToken == 2) {
                //display draw in client 2 gui
                Platform.runLater(() -> lblStatus.setText("Round " + p2roundStatus + " was a draw! --> Select ROCK, PAPER, or SCISSORS."));
                p2roundStatus++;
            }
        }
        //case 4: Player 1 won the game session, game over
        else if (status == PLAYER1_WON) {
            continueToPlay = false;
            if (myToken == 1){
                //display player 1 as winner in client 1 gui
                Platform.runLater(() -> lblStatus.setText("Game over. You won!"));
            }
            else if (myToken == 2) {
                //display player 1 as winner in client 2 gui
                Platform.runLater(() ->
                        lblStatus.setText("Game over. Player 1 has won!"));
            }
        }
        //case 5: Player 2 won the game session, game over
        else if (status == PLAYER2_WON) {
            continueToPlay = false;
            if (myToken == 1) {
                //display player 2 is winner in client 1 gui
                Platform.runLater(() -> lblStatus.setText("Game over. Player 2 has won!"));
            }
            else if (myToken == 2) {
                //display player 2 is winner in client 2 gui
                Platform.runLater(() ->
                        lblStatus.setText("Game over. You won!"));
            }
        }
        //case 6: game was a draw, game over
        else if (status == DRAW) {
            continueToPlay = false;
            if (myToken == 1) {
                //display game was a draw in the client 1 gui
                Platform.runLater(() -> lblStatus.setText("Game over. It's a draw."));
            }
            else if (myToken == 2) {
                //display game was a draw in the client 2 gui
                Platform.runLater(() -> lblStatus.setText("Game over. It's a draw."));
            }
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
