package chess;
import java.util.Objects;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {
    public int row;
    public int column;

    public ChessPosition(int row, int col) {
        this.row = row;
        this.column = col;
    }


    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow() {
        return this.row;
    }

    /**
     * @return which column this position is in
     * 1 codes for the left column
     */
    public int getColumn() {
        return this.column;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        // Quickly check to make sure the other object is also of the class ChessPosition
        if (!(object instanceof ChessPosition)) {
            return false;
        }

        ChessPosition other = (ChessPosition) object;

        // Compare their saved data directly
        return this.row == other.row
                && this.column == other.column;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, column);
    }

    public static void main(String[] args) {
        chess.ChessPosition first = new chess.ChessPosition(2, 6);
        chess.ChessPosition same = new chess.ChessPosition(2, 6);
        chess.ChessPosition reversed = new chess.ChessPosition(6, 2);

        assert first.getRow() == 2 : "Incorrect Row";
        assert first.getColumn() == 6 : "Incorrect column";
        assert first.equals(same) : "Equivalent positions should be equal";
        assert !first.equals(reversed) : "Different position should not be equal";
        assert first.hashCode() == same.hashCode() : "Equivalent positions need same hash code";

        System.out.println("All ChessPosition checks passed.");

    }
}
