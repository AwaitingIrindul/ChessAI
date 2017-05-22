package com.nullprogram.chess.ai;

import com.nullprogram.chess.*;

import java.util.Map;

/**
 * Created by Irindul on 22/05/2017.
 */
public class Negamax implements Player {

    private static final int MATE = 300000;
    private static final int STALEMATE = 0;
    private final int QUIESCENCE_MIN = 2;
    private final int QUIESCENCE_MID = 5;
    private final int QUIESCENCE_MAX = 100;


    // Create hash that contains previously processed nodes.
    private final int TT_INIT = 2 << 20;
    private final int TT_MAX = TT_INIT;
    private final float TT_LOAD = 0.75f;
    private Map<Long, BoardInfo> transpositionTable;

    private boolean cutoff = false;

    private long nodeNb;
    private int minDepth = 2;
    private int maxDepth;

    private Board board;
    private BoardRepete boardCounter;
    long timeLimit = 200000;

    public Negamax(int maxDepth) {
        this.maxDepth = maxDepth;
        boardCounter = new BoardRepete();
    }

    @Override
    public Move takeTurn(Board board, Piece.Side side) {
        nodeNb = 0;
        this.board = board;

        transpositionTable = new TTMap<>(TT_INIT, TT_MAX, TT_LOAD);

        boardCounter.increment(board);

        MoveList moves = board.allMoves(side, true);
        Move best = moves.peek();


        //Iterative deepening

        long startTime = System.currentTimeMillis();
        long endTime = startTime + timeLimit;
        long currentTime;
        for (int depth = 0; depth < maxDepth; depth++) {
            currentTime = System.currentTimeMillis();
            if(currentTime >= endTime){
                break;
            }

            //Can be stopped before reaching full depth if running out of time
            Move uncheckMove = rootNegamax(moves, depth, Integer.MAX_VALUE, Integer.MIN_VALUE, endTime, side);

            if(uncheckMove != null){
                best = uncheckMove;
                moves.setFirst(best);
            }

            board.move(best);
            boardCounter.increment(board);
            board.undo();
            return best;
        }

        return null;

    }

    private Move rootNegamax(MoveList moves, int depth, int alpha, int beta, long endTime, Piece.Side side) {
        nodeNb++;

        //We extend search if one player is in check (to fasten checkmate)
        if(board.check()){
            depth++;
        }

        BoardInfo infos = transpositionTable.get(board.id());

        if(infos != null && infos.getDepth() >= depth){

            if(infos.getType() == BoardInfo.EXACT){
                //Better move
                return infos.getBestMove();
            }
            //Better bounds
            else if (infos.getType() == BoardInfo.LOWER && infos.getValue() > alpha){
                alpha = infos.getValue();
            } else if (infos.getValue() == BoardInfo.UPPER && infos.getValue() < beta) {
                beta = infos.getValue();
            }

            if(alpha >= beta){ //Alpha-beta pruning
                return  infos.getBestMove();
            }
        }

        int bestValue = Integer.MIN_VALUE;
        Move best = moves.peek();
        int value;
        long currentTime;



        for(Move move : moves ){
            currentTime = System.currentTimeMillis();
            if(currentTime >= endTime){
                cutoff = false;
                return null;
            }


            board.move(move);
            value = -negamax(depth -1, -beta, -alpha, Piece.opposite(side), endTime);
            board.undo();

            //If we have a better value :
            if(value >= bestValue){
                bestValue = value;
                best = move;
            }

            // If our max is greater than our lower bound, update our lower bound
            if(bestValue > alpha) {
                alpha = bestValue;
            }

            // Alpha-beta pruning
            if(bestValue >= beta) {
                break;
            }

        }

        updateTranspositionTable(alpha, beta, depth, bestValue, best);
        return best;

    }



    private int negamax(int depth, int alpha, int beta, Piece.Side side, long endTime) {
        nodeNb++;

        //We extend search if one player is in check (to fasten checkmate)
        if(board.check()){
            depth++;
        }

        BoardInfo infos = transpositionTable.get(board.id());

        if(infos != null && infos.getDepth() >= depth) {

            if (infos.getType() == BoardInfo.EXACT) {
                //Better move
                return infos.getValue();
            }
            //Better bounds
            else if (infos.getType() == BoardInfo.LOWER && infos.getValue() > alpha) {
                alpha = infos.getValue();
            } else if (infos.getValue() == BoardInfo.UPPER && infos.getValue() < beta) {
                beta = infos.getValue();
            }

            if (alpha >= beta) { //Alpha-beta pruning
                return infos.getValue();
            }
        }

        //We avoid repetitions
        if(boardCounter.isRepetition(board)){
            return STALEMATE;
        }


        //Base case
        if(depth == 0) {
            return evaluate();
            //TODO Quiescence search
        }

        MoveList moves = board.allMoves(side, true);

        if(moves.isEmpty()) {
            if(board.check()){
                return -MATE; //Negation because it's called with value = -negamax()
            } else {
                return STALEMATE; //Stalemate
            }
        } else {
            //TODO move ordering
            int bestValue = Integer.MIN_VALUE;
            int value;
            long currentTime;
            Move bestMove = moves.peek();
            value = Integer.MIN_VALUE;

            for (Move move : moves){
                if(move.getReplacement() != null)
                    System.out.println(move.getReplacement().toString());
                currentTime = System.currentTimeMillis();
                if(currentTime >= endTime){
                    return Integer.MIN_VALUE;
                }

                //See if there is a best value :
                board.move(move);
                value = -negamax(depth-1, -beta, -alpha, Piece.opposite(side), endTime);

                // If our max is greater than our lower bound, update our lower bound
                if(bestValue > alpha) {
                    alpha = bestValue;
                }

                // Alpha-beta pruning
                if(bestValue >= beta) {
                    break;
                }
            }

            updateTranspositionTable(alpha, beta, depth, bestValue, bestMove);

            return bestValue;
        }

    }

    private int evaluate() {
        if(board.check())
            return MATE;
        else return 0;
    }

    private void updateTranspositionTable(int alpha, int beta, int depth, int bestValue, Move best) {
        if(bestValue <= alpha) {
            updateTranspositionTable(bestValue, best, BoardInfo.LOWER, depth);
        }
        else if(bestValue >= beta) {
            updateTranspositionTable(bestValue, best, BoardInfo.UPPER, depth);
        }
        else {
            updateTranspositionTable(bestValue, best, BoardInfo.EXACT, depth);
        }
    }

    private void updateTranspositionTable(int bestValue, Move best, int type, int depth) {
        BoardInfo boardInfo = transpositionTable.get(board.id());
        if(boardInfo != null) {
            boardInfo.updateInfo(bestValue, best, type, depth);
        }
        else {
            transpositionTable.put(board.id(), new BoardInfo(bestValue, best, type, depth));
        }
    }


}
