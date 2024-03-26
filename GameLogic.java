package com.example.demo2;

/**
 * The GameLogic class stores the current players' moves for a round and calculates the winner of the round.
 */
public class GameLogic {
    //Variables
    private MoveType player1MoveType;
    private MoveType player2MoveType;
    private int roundWinner;

    /**
     * The constructor initializes the variables for the start of a new game.
     */
    public GameLogic(){
        player1MoveType = null; //client has not entered a move yet
        player2MoveType = null;
        roundWinner = 0; //0 represents a draw

    }

    /**
     * This method calculates the winner of the round from the MoveTypes saved for player 1 and 2.
     * It sets the roundWinner variables.
     */
    public void calcRoundWinner(){
        int winner;

        if(player1MoveType.equals(MoveType.ROCK) && player2MoveType.equals(MoveType.PAPER)){
            winner = 2;
        }
        else if(player1MoveType.equals(MoveType.ROCK) && player2MoveType.equals(MoveType.SCISSORS)){
            winner = 1;
        }
        else if(player1MoveType.equals(MoveType.PAPER) && player2MoveType.equals(MoveType.ROCK)){
            winner = 1;
        }
        else if(player1MoveType.equals(MoveType.PAPER) && player2MoveType.equals(MoveType.SCISSORS)){
            winner = 2;
        }
        else if(player1MoveType.equals(MoveType.SCISSORS) && player2MoveType.equals(MoveType.PAPER)){
            winner = 1;
        }
        else if(player1MoveType.equals(MoveType.SCISSORS) && player2MoveType.equals(MoveType.ROCK)){
            winner = 2;
        }
        else{winner = 0; } //it is a draw

        this.roundWinner = winner;

    }
    /**
     * This method sets player 1's MoveType and is used by the server.
     * @param moveType ROCK, PAPER, or SCISSORS
     */
    public void setPlayer1MoveType(int moveType){
        if(moveType == 1){
            this.player1MoveType = MoveType.ROCK;
        }
        else if(moveType == 2){
            this.player1MoveType = MoveType.PAPER;
        }
        else if(moveType == 3){
            this.player1MoveType = MoveType.SCISSORS;
        }
        else{this.player1MoveType = null;}
    }

    /**This method sets player 2's MoveType and is used by the server.
     * @param moveType ROCK, PAPER, or SCISSORS
     */
    public void setPlayer2MoveType(int moveType){
        if(moveType == 1){
            this.player2MoveType = MoveType.ROCK;
        }
        else if(moveType == 2){
            this.player2MoveType = MoveType.PAPER;
        }
        else if(moveType == 3){
            this.player2MoveType = MoveType.SCISSORS;
        }
        else{this.player2MoveType = null;}
    }

    /**
     * This method gets player 1's MoveType
     * @return player1MoveType ROCK, PAPER, SCISSORS
     */
    public MoveType getPlayer1MoveType(){
        return player1MoveType;
    }

    /**
     * This method gets player 2's MoveType
     * @return player2MoveType ROCK, PAPER, SCISSORS
     */
    public MoveType getPlayer2MoveType(){
        return player2MoveType;
    }

    /**
     * This method gets the winner of the round.
     * @return roundWinner 0,1,2
     */
    public int getRoundWinner(){
        return roundWinner;
    }

    /**
     * This method sets the GameLogic variables for a new game
     */
    public void resetGameLogic(){
        player1MoveType = null;
        player2MoveType = null;
        roundWinner = 0;
    }
}

