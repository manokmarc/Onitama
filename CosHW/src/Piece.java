import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Piece {
    String name;
    List<Integer> position;

    Piece(String name, List<Integer> position) {
        this.name = name;
        this.position = position;
    }

    private boolean isValidPosition(List<Piece> pieces, List<Integer> position) {
        // checks if the move position is valid
        for (Integer value : position) {
            if (value < 0 || value > 4) { // checks if position is outside of board
                return false;
            }
            for (Piece piece : pieces) {
                if (piece.position.equals(position)) { // checks if own piece is occupying the position
                    return false;
                }
            }
        }
        return true;
    }

    List<List<Integer>> getPossibleMovePositions(List<Piece> pieces, Card card) {
        List<List<Integer>> movePositions = new ArrayList<>(); // list of all resulting positions
        for (List<Integer> move : card.moves) {
            List<Integer> resultingPosition = Arrays.asList(
                    position.get(0) - move.get(1), // get x vector
                    position.get(1) + move.get(0)); // get y vector
            if (isValidPosition(pieces, resultingPosition)) { // adds piece to the options if position is valid
                movePositions.add(resultingPosition);
            }
        }
        return movePositions; // return all positions
    }

    void flipPosition() {
        position.replaceAll(integer -> 4 - integer);
    }
}
