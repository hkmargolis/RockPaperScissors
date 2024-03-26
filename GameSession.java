package com.example.demo2;

/**
 * The GameSession class tracks the rounds and the cumulative winner of the game.
 */
public class GameSession {
    //Variables
    private int round;
    private int winner;
    private int player1points;
    private int player2points;

    /**
     * Constructor initializes values for a new game by setting them all to 0.
     */
    public GameSession(){
        //initialize values for new game
        player1points = 0;
        player2points = 0;
        round = 0;
        winner = 0;
    }

    /**
     * This method increments the player 1 or player 2's points. The token (1 or 2) is the winner of the round.
     * If the round was a draw (the token is 0), it does nothing.
     * @param token int 0,1, or 2
     */
    public void incrementPlayerPoints(int token) {
        if(token == 1){
            this.player1points += 1;
        }
        if(token == 2){
            this.player2points += 1;
        }
    }

    /**
     * This method increments the round by 1.
     */
    public void incrementRoundStatus(){
        round++;
    }

    /**
     * This method checks to see if the game session has reached 5 rounds. If it has the game is over.
     * @return gameOver true if round == 5
     */
    public boolean gameOver(){
        if(round == 5){
            return true;
        }
        return false;
    }

    /**
     * This method calculates the cumulative winner based on the players' points. It then sets the winner to 0, 1, or 2,
     */
    public void calcWinner(){
        if(player1points > player2points){
            winner = 1;
        }
        else if (player2points > player1points){
            winner = 2;
        }
        else{winner = 0;} //it is a draw

    }

    /**
     * This method gets player 1's points.
     * @return player1points
     */
    public int getPlayer1points(){
        return player1points;
    }

    /**
     * This method gets player 2's points
     * @return player2points
     */
    public int getPlayer2points(){
        return player2points;
    }

    /**
     * This method gets the current round.
     * @return round
     */
    public int getRoundStatus(){
        return round;
    }

    /**
     * This method sets all of the GameSession variables to 0 for a new game.
     */
    public void resetGameSession(){
        round = 0;
        player1points = 0;
        player2points = 0;
        winner = 0;
    }

    /**
     * This method gets the cumulative winner.
     * @return winner
     */
    public int getWinner(){
        return winner;
    }

}

