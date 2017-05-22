package com.nullprogram.chess.ai;

import com.nullprogram.chess.*;
import com.nullprogram.chess.pieces.*;

import java.util.*;

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

    private Map<Class, Double> values = new HashMap<>();

    private Board board;
    private Piece.Side side;
    private BoardRepete boardCounter;
    long timeLimit = 10000;
    int infinity = 10000000;

    public Negamax(int maxDepth) {
        this.maxDepth = maxDepth;
        boardCounter = new BoardRepete();
       // createValues();

    }

    private void createValues() {
        values.put((new Pawn(side)).getClass(),1.0);
        values.put((new Knight(side)).getClass(),3.0);
        values.put((new Bishop(side)).getClass(),3.0);
        values.put((new Rook(side)).getClass(),5.0);
        values.put((new King(side)).getClass(),1000.0);
    }

    @Override
    public Move takeTurn(Board board, Piece.Side side) {
        this.side = side;
        createValues();
        nodeNb = 0;
        this.board = board;

        transpositionTable = new TTMap<>(TT_INIT, TT_MAX, TT_LOAD);

        boardCounter.increment(board);

        MoveList moves = generateOrderedMoves(side);
        moves.sort(moveComparator);
        Move best = moves.first();



        //Iterative deepening

        long startTime = System.currentTimeMillis();
        long endTime = startTime + timeLimit;
        long currentTime;
        for (int depth = 1; depth < maxDepth; ++depth) {

            currentTime = System.currentTimeMillis();
            if(currentTime >= endTime){
                break;
            }

            //Can be stopped before reaching full depth if running out of time
            Move uncheckMove = rootNegamax(moves, depth, -infinity, infinity, endTime, Piece.opposite(side));

            if(uncheckMove != null){
                best = uncheckMove;
                moves.setFirst(best);
            }


        }
        board.move(best);
        boardCounter.increment(board);
        board.undo();
        return best;

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
        Move best = moves.first();
        int value;
        long currentTime;

        for(Move move : moves){
            currentTime = System.currentTimeMillis();
            if(currentTime >= endTime){
                System.out.println("Time's up");

                cutoff = false;
                return null;
            }


            board.move(move);
            value = -negamax(depth -1, -beta, -alpha, side, endTime);
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
            if(alpha >= beta) {
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
           // System.out.println("Repet");
            
            return STALEMATE;
        }


        //Base case
        if(depth == 0) {
            return (int)evaluate(board);
            //TODO Quiescence search
        }

        MoveList moves = generateOrderedMoves(side);

        if(moves.isEmpty()) {
            if(board.check()){
                return -MATE; //Negation because it's called with value = -negamax()
            } else {
                return STALEMATE; //Stalemate
            }
        } else {

            // Add killer moves to front of list
            orderMoves(moves, infos);


            int bestValue = Integer.MIN_VALUE;
            int value;
            long currentTime;
            Move bestMove = moves.first();

            for (Move move : moves){
                if(move.getReplacement() != null)
                    System.out.println(move.getReplacement().toString());
                currentTime = System.currentTimeMillis();
                if(currentTime >= endTime){
                    System.out.println("Time's up");

                    return Integer.MIN_VALUE;
                }

                //See if there is a best value :
                board.move(move);
                value = -negamax(depth-1, -beta, -alpha, Piece.opposite(side), endTime);
                board.undo();

                if(value > bestValue) {
                    bestValue = value;
                    bestMove = move;
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

            updateTranspositionTable(alpha, beta, depth, bestValue, bestMove);

            return bestValue;
        }

    }


    private MoveList generateOrderedMoves(Piece.Side side) {
        MoveList psmoves = board.allMoves(side, true);
        MoveList moves = new MoveList(board);

        Set<Move> setmoves = new HashSet<Move>(256);

        for(Move m : psmoves) {
            if(setmoves.add(m)) {
                if(m.getCaptured() != null || m.getReplacement() != null) {
                    moves.setFirst(m);
                }
                else {
                    moves.add(m);
                }
            }
        }
        return moves;
    }

    private void orderMoves(MoveList moves, BoardInfo infos) {
        if(infos != null) {
            if(infos.getSecondBestMove() != null) {
                moves.setFirst(infos.getSecondBestMove());
                moves.setFirst(infos.getSecondBestMove());
            }
            moves.setFirst(infos.getBestMove());
            moves.setFirst(infos.getBestMove());
        }
    }

    private double evaluate(final Board b) {
        double material = materialValue(b);
        double kingSafety = kingInsafetyValue(b);
        double mobility = mobilityValue(b);
        return material * 1.0 +
                kingSafety * 0.15 +
                mobility * 0.01;
    }

    /**
     * Add up the material value of the board only.
     *
     * @param b board to be evaluated
     * @return  material value of the board
     */
    private double materialValue(final Board b) {
        double value = 0;
        for (int y = 0; y < b.getHeight(); y++) {
            for (int x = 0; x < b.getWidth(); x++) {
                Position pos = new Position(x, y);
                Piece p = b.getPiece(pos);
                if (p != null) {
                    value += values.get(p.getClass()) * p.getSide().value();
                }
            }
        }
        return value * side.value();
    }

    /**
     * Determine the safety of each king. Higher is worse.
     *
     * @param b board to be evaluated
     * @return  king insafety score
     */
    private double kingInsafetyValue(final Board b) {
        return kingInsafetyValue(b, Piece.opposite(side)) -
                kingInsafetyValue(b, side);
    }

    /**
     * Helper function: determine safety of a single king.
     *
     * @param b board to be evaluated
     * @param s side of king to be checked
     * @return king insafety score
     */
    private double kingInsafetyValue(final Board b, final Piece.Side s) {
        /* Trace lines away from the king and count the spaces. */
        Position king = b.findKing(s);
        if (king == null) {
            /* Weird, but may happen during evaluation. */
            return Double.POSITIVE_INFINITY;
        }
        MoveList list = new MoveList(b, false);
        /* Take advantage of the Rook and Bishop code. */
        Rook.getMoves(b.getPiece(king), list);
        Bishop.getMoves(b.getPiece(king), list);
        return list.size();
    }

    /**
     * Mobility score for this board.
     *
     * @param b board to be evaluated
     * @return  score for this board
     */
    private double mobilityValue(final Board b) {
        return b.allMoves(side, false).size() -
                b.allMoves(Piece.opposite(side), false).size();
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

    private Comparator<Move> moveComparator = new Comparator<Move>() {
        public int compare (Move move1, Move move2) {
            int score1, score2;

            board.move(move1);
            score1 = (int) evaluate(board);
            board.undo();

            board.move(move2);
            score2 = (int)evaluate(board);
            board.undo();

            return score1 - score2;
        }
    };


}
