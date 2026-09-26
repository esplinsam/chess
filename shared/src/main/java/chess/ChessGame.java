package chess;

import java.util.Collection;
import java.util.Objects;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * A class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    ChessBoard board = new ChessBoard();
    ChessGame.TeamColor whoseTurn = TeamColor.WHITE;
    ArrayList<ChessPosition> whitePieces = new ArrayList<>();
    ArrayList<ChessPosition> blackPieces = new ArrayList<>();


    public ChessGame() {
        board.resetBoard();
        int[] firstRank = {1, 0};
        int[] secondRank = {2, 0};
        int[] seventhRank = {7, 0};
        int[] eighthRank = {8, 0};
        for (int i = 1; i < 9; i++) {
            firstRank[1] = i;
            secondRank[1] = i;
            seventhRank[1] = i;
            eighthRank[1] = i;
            whitePieces.add(new ChessPosition(firstRank[0], firstRank[1]));
            whitePieces.add(new ChessPosition(secondRank[0], secondRank[1]));
            blackPieces.add(new ChessPosition(seventhRank[0], seventhRank[1]));
            blackPieces.add(new ChessPosition(eighthRank[0], eighthRank[1]));
        }
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        // return the color of the team whose turn it is to move
        return whoseTurn;
    }

    /**
     * Sets which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        // set the next team to move.
        // Use getTeamTurn() to determine whose turn it was
        // then switch to the other team

        this.whoseTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets all valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        // Check ChessPiece's move set

        ChessPiece piece = board.getPiece(startPosition);
        Collection<ChessMove> moves = piece.pieceMoves(board, startPosition);

        return moves;
    }

    /**
     * Makes a move in the chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition startPosition = move.getStartPosition();
        ChessPosition endPosition = move.getEndPosition();
        ChessPiece piece = board.getPiece(startPosition);
        ChessPiece.PieceType promotionPiece = move.getPromotionPiece();

        if (piece.getTeamColor() != whoseTurn) {
            throw new InvalidMoveException("Color of piece being moved does not match the team whose turn it is.");
        }

        int startRow = startPosition.getRow() - 1;
        int startColumn = startPosition.getColumn() - 1;

        int endRow = endPosition.getRow() - 1;
        int endColumn = endPosition.getColumn() - 1;

        board.board[startRow][startColumn] = null;

        if (promotionPiece != null) {
            ChessPiece newPiece = new ChessPiece(whoseTurn, promotionPiece);
            board.board[endRow][endColumn] = newPiece;
        }

        board.board[endRow][endColumn] = piece;

        if (piece.getTeamColor() == TeamColor.WHITE) {
            whitePieces.remove(startPosition);
            whitePieces.add(endPosition);
        } else {
            blackPieces.remove(startPosition);
            blackPieces.add(endPosition);
        }

    }

    /**
     * Helper function to look at all of black's possible moves
     */
    public HashSet<ChessPosition> blackMoves() {
        HashSet<ChessPosition> blackMoves = new HashSet<>();
        for (ChessPosition position : blackPieces) {
            ChessPiece piece = board.getPiece(position);
            Collection<ChessMove> pieceMoves = piece.pieceMoves(this.board, position);
            for (ChessMove move : pieceMoves) {
                ChessPosition endPosition = move.getEndPosition();
                blackMoves.add(endPosition);
            }
        }
        return blackMoves;
    }

    /**
     * Helper function to look at all of white's possible moves
     */
    public HashSet<ChessPosition> whiteMoves() {
        HashSet<ChessPosition> whiteMoves = new HashSet<>();
        for (ChessPosition position : whitePieces) {
            ChessPiece piece = board.getPiece(position);
            Collection<ChessMove> pieceMoves = piece.pieceMoves(this.board, position);
            for (ChessMove move : pieceMoves) {
                ChessPosition endPosition = move.getEndPosition();
                whiteMoves.add(endPosition);
            }
        }
        return whiteMoves;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        // Check if the king is in check by looking at the available moves of other pieces
        ChessPosition kingPosition = null;
        if (teamColor == TeamColor.WHITE) {
            for (ChessPosition position : whitePieces) {
                ChessPiece piece = this.board.getPiece(position);
                if (piece.getPieceType() == ChessPiece.PieceType.KING) {
                    kingPosition = position;
                }
            }

            HashSet<ChessPosition> blackMoves = blackMoves();
            for (ChessPosition endPosition : blackMoves) {
                if (Objects.equals(endPosition, kingPosition)) {
                    return true;
                }
            }
        }

        if (teamColor == TeamColor.BLACK) {
            for (ChessPosition position : blackPieces) {
                ChessPiece piece = this.board.getPiece(position);
                if (piece.getPieceType() == ChessPiece.PieceType.KING) {
                    kingPosition = position;
                }
            }

            HashSet<ChessPosition> whiteMoves = whiteMoves();
            for (ChessPosition endPosition : whiteMoves) {
                if (Objects.equals(endPosition, kingPosition)) {
                    return true;
                }
            }
        }



        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        // Triggers if king is in check
        // Checks if any moves can remove king from check
        return false;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        // Triggers if not in check and none of the pieces could move
        if (isInCheck(teamColor)) {
            return false;
        }

        return false;
    }

    /**
     * Sets this game's chessboard to a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        // implement setBoard here
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                this.board.board[i][j] = board.board[i][j];
            }
        }
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        // return the current state of the board
        return this.board;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (!(object instanceof ChessGame)) {
            return false;
        }

        ChessGame other = (ChessGame) object;

        if (Objects.equals(this.board, other.board) && this.whoseTurn == other.whoseTurn) {
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.board.hashCode(), whoseTurn);
    }
}
