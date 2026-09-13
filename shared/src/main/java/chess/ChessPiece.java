package chess;

import java.util.Collection;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        // Implement the Chess Piece class
        // Has color and type - (type will define specific move set)
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        // Simply return ChessPiece.TeamColor class attribute
        return null;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        // Return ChessPiece.PieceType class attribute
        return null;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        // Use specifics of ChessPiece.PieceType to determine which squares a piece can move to
        // Check possible squares to move to, to see if a piece is there already.
        return null;
    }
}
