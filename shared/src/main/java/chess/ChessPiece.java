package chess;

import java.util.Collection;
import java.util.Objects;
import java.util.ArrayList;

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
     * Helper method for adding bishop moves
     */
    public void addBishopMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> moves) {
        int [][] directions = {
                {1, 1},
                {1, -1},
                {-1, 1},
                {-1, -1}
        };

        for (int[] direction: directions) {
            int row = myPosition.getRow() + direction[0];
            int col = myPosition.getColumn() + direction[1];

            while (row >= 1 && col >= 1
                    && row <= 8 && col <= 8) {
                // Define the target position and the piece occupying that square
                ChessPosition endPosition = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(endPosition);

                // Define the move to make
                ChessMove move = new ChessMove(myPosition, endPosition, null);

                if (piece == null) { // simply add the move if the square is empty
                    moves.add(move);
                }
                else if (this.pieceColor != piece.getTeamColor()) { // if square is occupied by opposing piece add move and stop looking in that direction
                    moves.add(move);
                    break;
                }
                else { // if square is occupied by piece of same color stop looking in that direction
                    break;
                }

                // move to the next square in that direction
                row += direction[0];
                col += direction[1];

            }
        }
    }

    /**
     * Helper method for adding rook moves
     */
    public void addRookMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> moves) {
        int [][] directions = {
                {0, 1},
                {0, -1},
                {1, 0},
                {-1, 0}
        };

        for (int[] direction: directions) {
            int row = myPosition.getRow() + direction[0];
            int col = myPosition.getColumn() + direction[1];

            while (row >= 1 && col >= 1
                    && row <= 8 && col <= 8) {
                // Define the target position and the piece occupying that square
                ChessPosition endPosition = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(endPosition);

                // Define the move to make
                ChessMove move = new ChessMove(myPosition, endPosition, null);

                if (piece == null) { // simply add the move if the square is empty
                    moves.add(move);
                }
                else if (this.pieceColor != piece.getTeamColor()) { // if square is occupied by opposing piece add move and stop looking in that direction
                    moves.add(move);
                    break;
                }
                else { // if square is occupied by piece of same color stop looking in that direction
                    break;
                }

                // move to the next square in that direction
                row += direction[0];
                col += direction[1];

            }
        }
    }

    /**
     * Helper method for adding queen moves
     */
    public void addQueenMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> moves) {
        int [][] directions = {
                {1, 1},
                {1, -1},
                {-1, 1},
                {-1, -1},
                {0, 1},
                {0, -1},
                {1, 0},
                {-1, 0}
        };

        for (int[] direction: directions) {
            int row = myPosition.getRow() + direction[0];
            int col = myPosition.getColumn() + direction[1];

            while (row >= 1 && col >= 1
                    && row <= 8 && col <= 8) {
                // Define the target position and the piece occupying that square
                ChessPosition endPosition = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(endPosition);

                // Define the move to make
                ChessMove move = new ChessMove(myPosition, endPosition, null);

                if (piece == null) { // simply add the move if the square is empty
                    moves.add(move);
                }
                else if (this.pieceColor != piece.getTeamColor()) { // if square is occupied by opposing piece add move and stop looking in that direction
                    moves.add(move);
                    break;
                }
                else { // if square is occupied by piece of same color stop looking in that direction
                    break;
                }

                // move to the next square in that direction
                row += direction[0];
                col += direction[1];

            }
        }
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
        Collection<ChessMove> moves = new ArrayList<>();

        // implement here
        switch (this.getPieceType()) {
            case BISHOP ->
                addBishopMoves(board, myPosition, moves);
            case ROOK -> {
                addRookMoves(board, myPosition, moves);
            }
            case KNIGHT -> {
                // Implement horsie's moveset here
            }
            case QUEEN -> {
                addQueenMoves(board, myPosition, moves);
            }
            case KING -> {
                // Implement king's moveset here
            }
            case PAWN -> {
                // Implement pawn's moveset here
            }
        }

        return moves;
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
