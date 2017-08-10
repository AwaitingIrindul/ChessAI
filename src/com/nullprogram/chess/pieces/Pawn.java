package com.nullprogram.chess.pieces;

import com.nullprogram.chess.Board;
import com.nullprogram.chess.Move;
import com.nullprogram.chess.MoveList;
import com.nullprogram.chess.Piece;
import com.nullprogram.chess.Position;

/**
 * The Chess pawn.
 * <p>
 * This class describes the movement and capture behavior of the pawn
 * chess piece.
 */
public class Pawn extends Piece {

    /**
     * Serialization identifier.
     */
    private static final long serialVersionUID = 456095470L;

    /**
     * Create a new pawn on the given side.
     *
     * @param side piece owner
     */
    public Pawn(final Side side) {
        super(side, "Pawn");
    }

    @Override
    public final MoveList getMoves(final boolean check) {
        MoveList list = new MoveList(getBoard(), check);
        Position pos = getPosition();
        Board board = getBoard();
        int dir = direction();
        Position dest = new Position(pos, 0, 1 * dir);
        boolean upgradeable = false;

        Move first;
        boolean firstAvailable = false;     // true if front cell is available
        for (PieceThatCanBeUpgradedTo piece : PieceThatCanBeUpgradedTo.values()) {
            first = new Move(pos, dest);
            if (addUpgrade(first, piece)) {
                firstAvailable = list.addMove(first);
                upgradeable = true;
            } else break;
        }
        if (!upgradeable) {
            first = new Move(pos, dest);
            firstAvailable = list.addMove(first);
        }
        if (firstAvailable && !moved()) {
            list.addMove(new Move(pos, new Position(pos, 0, 2 * dir)));
        }

        Move captureLeft;
        if (upgradeable) {
            for (PieceThatCanBeUpgradedTo piece : PieceThatCanBeUpgradedTo.values()) {
                captureLeft = new Move(pos, new Position(pos, -1, 1 * dir));
                addUpgrade(captureLeft, piece);
                list.addCaptureOnly(captureLeft);
            }
        } else {
            captureLeft = new Move(pos, new Position(pos, -1, 1 * dir));
            list.addCaptureOnly(captureLeft);
        }

        Move captureRight;
        captureRight = new Move(pos, new Position(pos, 1, 1 * dir));
        if (upgradeable) {
            for (PieceThatCanBeUpgradedTo piece : PieceThatCanBeUpgradedTo.values()) {
                captureRight = new Move(pos, new Position(pos, 1, 1 * dir));
                addUpgrade(captureRight, piece);
                list.addCaptureOnly(captureRight);
            }
        } else {
            list.addCaptureOnly(captureRight);
        }

        return list;
    }


    /**
     * Add the upgrade actions to the given move if needed.
     *
     * @param move the move to be modified
     * @return true if upgradeable
     */
    private boolean addUpgrade(final Move move, PieceThatCanBeUpgradedTo replacement) {
        if (move.getDest().getY() != upgradeRow()) {
            return false;
        }
        move.setNext(new Move(move.getDest(), null)); // remove the pawn
        Move upgrade = new Move(null, move.getDest());

        upgrade.setReplacement(replacement);
        upgrade.setReplacementSide(getSide());
        move.getNext().setNext(upgrade);              // add a queen
        return true;
    }

    /**
     * Determine direction of this pawn's movement.
     *
     * @return direction for this pawn
     */
    private int direction() {
        if (getSide() == Side.WHITE) {
            return 1;
        } else {
            return -1;
        }
    }

    /**
     * Determine upgrade row.
     *
     * @return the upgrade row index.
     */
    private int upgradeRow() {
        if (getSide() == Side.BLACK) {
            return 0;
        } else {
            return getBoard().getHeight() - 1;
        }
    }
}
