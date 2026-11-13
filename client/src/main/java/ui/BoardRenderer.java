package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.Objects;

public class BoardRenderer {
    private final ChessBoard board;
    private final ChessGame.TeamColor playerColor;

    /**
     * Creates a renderer for a board from the given player's perspective.
     *
     * @param board       board to render
     * @param playerColor perspective from which to view the board
     */
    public BoardRenderer(
            ChessBoard board,
            ChessGame.TeamColor playerColor
    ) {
        this.board = Objects.requireNonNull(board);
        this.playerColor = Objects.requireNonNull(playerColor);
    }

    /**
     * Converts the chessboard into a colored terminal string.
     *
     * @return formatted representation of the chessboard
     */
    public String render() {
        StringBuilder result = new StringBuilder();

        boolean whitePerspective =
                playerColor == ChessGame.TeamColor.WHITE;

        int rowStart = whitePerspective ? 8 : 1;
        int rowEnd = whitePerspective ? 0 : 9;
        int rowStep = whitePerspective ? -1 : 1;

        int columnStart = whitePerspective ? 1 : 8;
        int columnEnd = whitePerspective ? 9 : 0;
        int columnStep = whitePerspective ? 1 : -1;

        appendColumnLabels(
                result,
                columnStart,
                columnEnd,
                columnStep
        );

        for (
                int row = rowStart;
                row != rowEnd;
                row += rowStep
        ) {
            appendRowLabel(result, row);

            for (
                    int column = columnStart;
                    column != columnEnd;
                    column += columnStep
            ) {
                appendSquare(result, row, column);
            }

            appendRowLabel(result, row);
            result.append('\n');
        }

        appendColumnLabels(
                result,
                columnStart,
                columnEnd,
                columnStep
        );

        result.append(EscapeSequences.RESET_TEXT_COLOR);
        result.append(EscapeSequences.RESET_BG_COLOR);

        return result.toString();
    }

    /**
     * Adds one square, including its background color and piece.
     */
    private void appendSquare(
            StringBuilder result,
            int row,
            int column
    ) {
        result.append(squareBackground(row, column));

        ChessPosition position =
                new ChessPosition(row, column);

        ChessPiece piece = board.getPiece(position);

        if (piece == null) {
            result.append(EscapeSequences.EMPTY);
        } else {
            result.append(pieceTextColor(piece));
            result.append(pieceSymbol(piece));
        }

        result.append(EscapeSequences.RESET_TEXT_COLOR);
        result.append(EscapeSequences.RESET_BG_COLOR);
    }

    /**
     * Returns the correct background color for a board coordinate.
     *
     * The square a1 is a dark square, so positions whose row and
     * column sum is even use the dark background.
     */
    private String squareBackground(int row, int column) {
        if ((row + column) % 2 == 0) {
            return EscapeSequences.SET_BG_COLOR_DARK_GREEN;
        }

        return EscapeSequences.SET_BG_COLOR_LIGHT_GREY;
    }

    /**
     * Selects a readable text color for a piece.
     */
    private String pieceTextColor(ChessPiece piece) {
        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            return EscapeSequences.SET_TEXT_COLOR_WHITE;
        }

        return EscapeSequences.SET_TEXT_COLOR_BLACK;
    }

    /**
     * Selects the Unicode symbol matching a piece's color and type.
     */
    private String pieceSymbol(ChessPiece piece) {
        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            return whitePieceSymbol(piece.getPieceType());
        }

        return blackPieceSymbol(piece.getPieceType());
    }

    /**
     * Returns the symbol for a White piece.
     */
    private String whitePieceSymbol(
            ChessPiece.PieceType pieceType
    ) {
        return switch (pieceType) {
            case KING -> EscapeSequences.WHITE_KING;
            case QUEEN -> EscapeSequences.WHITE_QUEEN;
            case BISHOP -> EscapeSequences.WHITE_BISHOP;
            case KNIGHT -> EscapeSequences.WHITE_KNIGHT;
            case ROOK -> EscapeSequences.WHITE_ROOK;
            case PAWN -> EscapeSequences.WHITE_PAWN;
        };
    }

    /**
     * Returns the symbol for a Black piece.
     */
    private String blackPieceSymbol(
            ChessPiece.PieceType pieceType
    ) {
        return switch (pieceType) {
            case KING -> EscapeSequences.BLACK_KING;
            case QUEEN -> EscapeSequences.BLACK_QUEEN;
            case BISHOP -> EscapeSequences.BLACK_BISHOP;
            case KNIGHT -> EscapeSequences.BLACK_KNIGHT;
            case ROOK -> EscapeSequences.BLACK_ROOK;
            case PAWN -> EscapeSequences.BLACK_PAWN;
        };
    }

    /**
     * Adds the file labels above or below the board.
     */
    private void appendColumnLabels(
            StringBuilder result,
            int columnStart,
            int columnEnd,
            int columnStep
    ) {
        result.append(labelStyle());
        result.append("   ");

        for (
                int column = columnStart;
                column != columnEnd;
                column += columnStep
        ) {
            char file = (char) ('a' + column - 1);
            result.append(' ')
                    .append(file)
                    .append(' ');
        }

        result.append("   ");
        result.append(EscapeSequences.RESET_TEXT_COLOR);
        result.append(EscapeSequences.RESET_BG_COLOR);
        result.append('\n');
    }

    /**
     * Adds a rank label to either side of a board row.
     */
    private void appendRowLabel(
            StringBuilder result,
            int row
    ) {
        result.append(labelStyle());
        result.append(' ')
                .append(row)
                .append(' ');
        result.append(EscapeSequences.RESET_TEXT_COLOR);
        result.append(EscapeSequences.RESET_BG_COLOR);
    }

    /**
     * Returns the ANSI formatting used by coordinate labels.
     */
    private String labelStyle() {
        return EscapeSequences.SET_BG_COLOR_BLACK
                + EscapeSequences.SET_TEXT_COLOR_WHITE;
    }
}