package com.nullprogram.chess.ai;

import com.nullprogram.chess.Move;

/**
 * Created by Irindul on 22/05/2017.
 */
public class BoardInfo {
    public static final int LOWER = 0;
    public static final int EXACT = 1;
    public static final int UPPER = 2;

    private int value;
    private Move bestMove;
    private Move secondBestMove;
    private int type;
    private int depth;
    private int secondMoveDepth;

    public BoardInfo(int value, Move move, int type, int depth) {
        this.value = value;
        this.bestMove = move;
        this.type = type;
        this.depth = depth;
        this.secondMoveDepth = -1;
        this.secondBestMove = null;
    }

    public int getValue() {
        return value;
    }

    public Move getSecondBestMove() {
        return secondBestMove;
    }

    public Move getBestMove() {
        return bestMove;
    }

    public int getDepth() {
        return depth;
    }

    public int getType() {
        return type;
    }

    // Replace when we have a better value (bigger depth)
    // Only replace the second best move if it's different from
    // the best move.
    public void updateInfo(int value, Move move, int type, int depth) {

        // Replacing a better value!
        if(depth > this.depth) {
            this.value = value;
            this.type = type;


            if (!move.equals(this.bestMove)) {
                this.secondBestMove = this.bestMove;
                this.secondMoveDepth = this.depth;
            }

            this.bestMove = move;
            this.depth = depth;
        }
        else if (this.secondBestMove == null || depth >= this.secondMoveDepth) {
            if (!move.equals(this.bestMove)) {
                this.secondBestMove = move;
                this.secondMoveDepth = depth;
            }
        }
    }

}
