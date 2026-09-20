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
     * @return whether or not given position is on the board
     */
    public boolean onBoard(int row, int col) {
        return (row >= 1 && col >= 1
                && row <= 8 && col <= 8);
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

            while (onBoard(row, col)) {
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

            while (onBoard(row, col)) {
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

            while (onBoard(row, col)) {
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
     * Helper function for adding horsie moves
     */
    public void addHorsieMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> moves) {
        int[][] directions = {
                {1, 2},
                {2, 1},
                {-1, 2},
                {-2, 1},
                {1, -2},
                {2, -1},
                {-1, -2},
                {-2, -1}
        };

        for (int[] direction: directions) {
            int row = myPosition.getRow() + direction[0];
            int col = myPosition.getColumn() + direction[1];
            if (onBoard(row, col)) {
                // Define an end position and find which piece is there
                ChessPosition endPosition = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(endPosition);

                // Define the move to make
                ChessMove move = new ChessMove(myPosition, endPosition, null);

                if (piece == null) {
                    moves.add(move);
                } else if (this.pieceColor != piece.getTeamColor()) {
                    moves.add(move);
                }
            }
        }
    }

    /**
     * Helper function for adding king moves
     */
    public void addKingMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> moves) {
        int[][] directions = {
                {1, 0},
                {-1, 0},
                {0, 1},
                {0, -1},
                {1, 1},
                {1, -1},
                {-1, 1},
                {-1, -1}
        };

        for (int[] direction: directions) {
            int row = myPosition.getRow() + direction[0];
            int col = myPosition.getColumn() + direction[1];
            if (onBoard(row, col)) {
                // Define an end position and find which piece is there
                ChessPosition endPosition = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(endPosition);

                // Define the move to make
                ChessMove move = new ChessMove(myPosition, endPosition, null);

                if (piece == null) {
                    moves.add(move);
                } else if (this.pieceColor != piece.getTeamColor()) {
                    moves.add(move);
                }
            }
        }
    }

    /**
     * Helper function for adding pawn moves
     */
    public void addPawnMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> moves) {
        int direction;
        int startingRow;
        int promotionRow;

        // Set a few variables based on color
        if (this.pieceColor == ChessGame.TeamColor.WHITE) {
            direction = 1;
            startingRow = 2;
            promotionRow = 8;
        } else{
            direction = -1;
            startingRow = 7;
            promotionRow = 1;
        }

        // One square forward move
        int row = myPosition.getRow() + direction;
        int col = myPosition.getColumn();
        ChessPosition endPosition = new ChessPosition(row, col);

        if (onBoard(row, col)) {
            ChessPiece piece = board.getPiece(endPosition);
            if (piece == null) {
                if (row == promotionRow) {
                    ChessMove makeQueen = new ChessMove(myPosition, endPosition, PieceType.QUEEN);
                    ChessMove makeRook = new ChessMove(myPosition, endPosition, PieceType.ROOK);
                    ChessMove makeHorsie = new ChessMove(myPosition, endPosition, PieceType.KNIGHT);
                    ChessMove makeBishop = new ChessMove(myPosition, endPosition, PieceType.BISHOP);
                    moves.add(makeQueen);
                    moves.add(makeRook);
                    moves.add(makeHorsie);
                    moves.add(makeBishop);
                }
                else {
                    ChessMove move = new ChessMove(myPosition, endPosition, null);
                    moves.add(move);
                }
            }
        }

        // two square forward initial move
        if (myPosition.getRow() == startingRow) {
            ChessPosition betweenPosition = new ChessPosition(row, col);
            row += direction;
            endPosition = new ChessPosition(row, col);

            ChessPiece betweenPiece = board.getPiece(betweenPosition);
            ChessPiece piece = board.getPiece(endPosition);
            if (betweenPiece == null && piece == null) {
                ChessMove move = new ChessMove(myPosition, endPosition, null);
                moves.add(move);
            }
            row -= direction;
        }

        // capture on diagonals
        int left = col - 1;
        int right = col + 1;
        ChessPosition leftDiag = new ChessPosition(row, left);
        ChessPosition rightDiag = new ChessPosition(row, right);

        if (onBoard(row, left)) {
            ChessPiece leftPiece = board.getPiece(leftDiag);
            if (leftPiece != null) {
                if (leftPiece.pieceColor != this.pieceColor) {
                    if (row == promotionRow) {
                        ChessMove makeQueen = new ChessMove(myPosition, leftDiag, PieceType.QUEEN);
                        ChessMove makeRook = new ChessMove(myPosition, leftDiag, PieceType.ROOK);
                        ChessMove makeHorsie = new ChessMove(myPosition, leftDiag, PieceType.KNIGHT);
                        ChessMove makeBishop = new ChessMove(myPosition, leftDiag, PieceType.BISHOP);
                        moves.add(makeQueen);
                        moves.add(makeRook);
                        moves.add(makeHorsie);
                        moves.add(makeBishop);
                    }
                    else {
                        ChessMove move = new ChessMove(myPosition, leftDiag, null);
                        moves.add(move);
                    }
                }
            }
        }

        if (onBoard(row, right)) {
            ChessPiece rightPiece = board.getPiece(rightDiag);
            if (rightPiece != null) {
                if (rightPiece.pieceColor != this.pieceColor) {
                    if (row == promotionRow) {
                        ChessMove makeQueen = new ChessMove(myPosition, rightDiag, PieceType.QUEEN);
                        ChessMove makeRook = new ChessMove(myPosition, rightDiag, PieceType.ROOK);
                        ChessMove makeHorsie = new ChessMove(myPosition, rightDiag, PieceType.KNIGHT);
                        ChessMove makeBishop = new ChessMove(myPosition, rightDiag, PieceType.BISHOP);
                        moves.add(makeQueen);
                        moves.add(makeRook);
                        moves.add(makeHorsie);
                        moves.add(makeBishop);
                    }
                    else {
                        ChessMove move = new ChessMove(myPosition, rightDiag, null);
                        moves.add(move);
                    }
                }
            }
        }

        // promotion



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
                addHorsieMoves(board, myPosition, moves);
            }
            case QUEEN -> {
                addQueenMoves(board, myPosition, moves);
            }
            case KING -> {
                addKingMoves(board, myPosition, moves);
            }
            case PAWN -> {
                addPawnMoves(board, myPosition, moves);
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
