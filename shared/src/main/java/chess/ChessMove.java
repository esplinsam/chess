package chess;

import java.util.Objects;

/**
 * Represents moving a chess piece on a chessboard
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessMove {
    public ChessPosition startPosition;
    public ChessPosition endPosition;
    public ChessPiece.PieceType promotionPiece;

    public ChessMove(ChessPosition startPosition, ChessPosition endPosition,
                     ChessPiece.PieceType promotionPiece) {
        // Processes move information of the chess piece
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.promotionPiece = promotionPiece;
    }

    /**
     * @return ChessPosition of starting location
     */
    public ChessPosition getStartPosition() {
        // simply return the actual value of ChessPiece.ChessPosition
        return startPosition;
    }

    /**
     * @return ChessPosition of ending location
     */
    public ChessPosition getEndPosition() {
        // Possibly get user input for a ChessPosition
        // and return that position
        return endPosition;
    }

    /**
     * Gets the type of piece to promote a pawn to if pawn promotion is part of this
     * chess move
     *
     * @return Type of piece to promote a pawn to, or null if no promotion
     */
    public ChessPiece.PieceType getPromotionPiece() {
        // defaults to null unless the piece is a pawn
        // and the EndPosition of the move is on the
        // opponents back rank, in which case, get
        // input for which piece to promote to
        return promotionPiece;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        // Quickly check to make sure the other object is also of the class ChessMove
        if (!(object instanceof ChessMove)) {
            return false;
        }

        ChessMove other = (ChessMove) object;

        // Compare their saved data directly
        return this.startPosition.equals(other.startPosition)
                && this.endPosition.equals(other.endPosition)
                && this.promotionPiece == other.promotionPiece;
    }

    @Override
    public int hashCode() {
        return Objects.hash(startPosition, endPosition, promotionPiece);
    }
}
