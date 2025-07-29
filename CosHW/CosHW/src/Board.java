import java.util.*;

public class Board {
    private final List<Player> players = new ArrayList<>();
    List<List<String>> board = new ArrayList<>();
    Map<Integer, List<Integer>> possibleMoves = new HashMap<>();
    Card offCard;

    Board(Player playerRed, Player playerBlue, Card offCard) {
        this.players.add(playerRed);
        this.players.add(playerBlue);
        this.offCard = offCard;

        // set up board
        this.board.add(Arrays.asList("s4B", "s3B", "m1B", "s2B", "s1B"));
        this.board.add(Arrays.asList("...", "...", "...", "...", "..."));
        this.board.add(Arrays.asList("...", "...", "...", "...", "..."));
        this.board.add(Arrays.asList("...", "...", "...", "...", "..."));
        this.board.add(Arrays.asList("s1R", "s2R", "m1R", "s3R", "s4R"));

        // flip board if blue starts first
        if (offCard.color.equals("Blue")) {
            flipBoard();
        }
    }

    private void flipBoard() {
        // flip board
        for (List<String> row : board) {
            Collections.reverse(row); // flip rows
        }
        Collections.reverse(board); // flip columns

        // updates each piece position in memory of the pieces (so it aligned with the new flipped board position)
        for (Player player : players) {
            for (Piece piece : player.activePieces) {
                piece.flipPosition();
            }
        }
    }

    void showPossibleMoves(Piece selectedPiece, Card selectedCard, Player player, Player opponent) {
        // makes a list for oponent piece names
        List<String> opponentPieceNames = new ArrayList<>();
        for (Piece piece : opponent.activePieces) {
            opponentPieceNames.add(piece.name);
        }

        // makes a deep copy of the board
        List<List<String>> tempBoard = new ArrayList<>();
        for (List<String> row : board) {
            tempBoard.add(new ArrayList<>(row));
        }

        // find possible moves and update the temporary board
        int moveNumberMarker = 1;
        for (List<Integer> resultingPosition : selectedPiece.getPossibleMovePositions(player.activePieces, selectedCard)) {
            try { // updates the temporary board to add markers of where the piece could go
                String boardValue = board.get(resultingPosition.get(0)).get(resultingPosition.get(1));
                if (boardValue.equals("...") || boardValue.equals(player.base)) { // checks if space is empty or has player base
                    tempBoard.get(resultingPosition.get(0)).set(resultingPosition.get(1), "(" + moveNumberMarker + ")");
                    possibleMoves.put(moveNumberMarker, resultingPosition);
                    moveNumberMarker++;
                } else if (opponentPieceNames.contains(boardValue)) { // checks if space is occupied by opponents piece
                    tempBoard.get(resultingPosition.get(0)).set(resultingPosition.get(1), "X" + moveNumberMarker + "X");
                    possibleMoves.put(moveNumberMarker, resultingPosition);
                    moveNumberMarker++;
                } else if (boardValue.equals(opponent.base)) { // check if space has opponent base
                    tempBoard.get(resultingPosition.get(0)).set(resultingPosition.get(1), "#" + moveNumberMarker + "#");
                    possibleMoves.put(moveNumberMarker, resultingPosition);
                    moveNumberMarker++;
                }
            } catch (IndexOutOfBoundsException e) { // removes all out of bound moves
                System.out.print("");
            }
        }
        System.out.println("➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖➖");
        displayBoard(tempBoard, player, opponent); // display the board with the possible moves
    }

    void movePiece(int chosenMove, Piece selectedPiece, Player player, Player opponent) {
        List<Integer> targetPosition = possibleMoves.get(chosenMove); // where the piece will go to

        // updates the piece position in the memory board and the visual board (does weird shit with function so do it separately)
        String targetBoardValue = board.get(targetPosition.get(0)).get(targetPosition.get(1));


        if (!targetBoardValue.equals("...")) { // check for board
            if (targetBoardValue.equals(opponent.base) || targetBoardValue.equals(board.getFirst().get(2))) { // if opponent base is the target the opponent dies
                opponent.isAlive = false;
            } else if (!targetBoardValue.equals(player.base)) { // if an oponent piece is the target
                opponent.removePiece(targetBoardValue); // remove piece from opponent memory
            }
            targetBoardValue = "...";
        }

        // swap or update the piece position in the board and update the piece position in memory
        board.get(targetPosition.get(0)).set(targetPosition.get(1), board.get(selectedPiece.position.get(0)).get(selectedPiece.position.get(1))); // sets the target value to the current selected piece
        board.get(selectedPiece.position.get(0)).set(selectedPiece.position.get(1), targetBoardValue); // sets the starting board value to the target value
        selectedPiece.position = targetPosition;

        // cheks if space is the player base position then turn it into a base
        if (board.getLast().get(2).equals("...")) {
            board.getLast().set(2, player.base);
        }

        flipBoard(); // flip board position
    }

    int chooseMove() {
        Scanner scanner = new Scanner(System.in);

        int typedMovePosition = 0;
        while (!possibleMoves.containsKey(typedMovePosition)) { // cheks if selected move position is valid
            try {
                System.out.print("Select move position: ");
                typedMovePosition = scanner.nextInt();
            } catch (InputMismatchException e) { // catch non int values
                scanner.nextLine(); // clear the invalid input
                typedMovePosition = 0; // resets value
            }
        }
        return typedMovePosition; // return move position
    }

    void displayBoard(List<List<String>> board, Player player, Player opponent) {
        // oponent side
        System.out.println("+--------------------+");
        System.out.print("|" + opponent.colour + ": ");
        for (Card card : opponent.hand) {
            System.out.print("(" + card.name + ")");
        }
        System.out.println();
        System.out.println("+--------------------+");

        // board
        int index = 0;
        for (List<String> row : board) {
            System.out.print("|");
            for (String value : row) {
                System.out.print(value + " "); // adds the board values
            }
            System.out.print("|");
            if (index == 2) {
                System.out.print("offCard: (" + offCard.name + ")");
            }
            System.out.println(); // move to next line
            index++;
        }

        // player side
        System.out.println("+--------------------+");
        System.out.print("|" + player.colour + ": ");
        for (Card card : player.hand) {
            System.out.print("(" + card.name + ")");
        }
        System.out.println();
        System.out.println("+--------------------+");
    }
}
