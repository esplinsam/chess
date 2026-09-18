package chess;

import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    public ChessGame.TeamColor pieceColor;
    public ChessPiece.PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        // Implement the Chess Piece class
        // Has color and type - (type will define specific move set)
        this.pieceColor = pieceColor;
        this.type = type;
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
        return this.pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        // Return ChessPiece.PieceType class attribute
        return this.type;
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
    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (!(object instanceof ChessPiece)) {
            return false;
        }

        ChessPiece other = (ChessPiece) object;

        return this.pieceColor == other.pieceColor
                && this.type == other.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }
    
}
