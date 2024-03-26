package com.example.demo2;

/**
 * The MoveType enum stores the values for the player's possible move types, ROCK, PAPER, or SCISSORS.
 */
public enum MoveType {
    ROCK(1), PAPER(2), SCISSORS(3);
    final int moveType;
    MoveType(int move){
        this.moveType = move;
    }
}
