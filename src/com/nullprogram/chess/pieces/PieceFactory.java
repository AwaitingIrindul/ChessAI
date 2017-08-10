package com.nullprogram.chess.pieces;

import com.nullprogram.chess.Piece;

/**
 * Creates pieces based on their name strings.
 */
public final class PieceFactory {

    /**
     * Hidden constructor.
     */
    private PieceFactory() {
    }

    /**
     * Create a new piece by name. Only for upgrade.
     *
     * @param name name of the piece
     * @param side side for the new piece
     * @return the new piece
     */
    public static Piece create(final Piece.PieceThatCanBeUpgradedTo name, final Piece.Side side) {
        Piece piece = null;
        switch (name) {
            case ROOK:
                piece = new Rook(side);
                break;
            case KNIGHT:
                piece = new Knight(side);
                break;
            case BISHOP:
                piece = new Bishop(side);
                break;
        }
        return piece;
    }
}
