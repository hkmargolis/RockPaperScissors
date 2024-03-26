package com.example.demo2;

/**
 * The RPSConstants interface stores the integers passed between the clients ands servers that describe
 * the player client types and the round and final wind status, new game status and quit status.
 */
public interface RPSConstants {
    public static int PLAYER1 = 1; // Indicate player 1
    public static int PLAYER2 = 2; // Indicate player 2
    public static int PLAYER1_WON = 1; // Indicate player 1 won game session
    public static int PLAYER2_WON = 2; // Indicate player 2 won game session
    public static int DRAW = 3; // Indicate a draw at the end of game session

    //public static int CONTINUE = 4; // Indicate to continue
    public static int PLAYER1_WON_ROUND = 5; //Indicate player 1 won this round
    public static int PLAYER2_WON_ROUND = 6; //Indicate player 2 won this round
    public static int ROUND_WAS_DRAW = 7; //Indicate this round was a draw
    public static int NEW_GAME = 8; //Indicate a player wants to start a new game
    public static int QUIT_GAME = 9; //Indicate a player wants to quit the game
}
