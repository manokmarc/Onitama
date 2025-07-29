import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Player {
    boolean isAlive = true;
    String colour;
    String base;
    List<Card> hand;
    List<Piece> activePieces = new ArrayList<>();
    Piece currentSelectedPiece;
    Card currentSelectedCard;

    Player(String colour, String base, List<Card> hand) {
        this.colour = colour;
        this.base = base;
        this.hand = hand;

        setupStartingPieces(colour); // sets up pieces
    }

    private void setupStartingPieces(String colour) {
        // set up standard starting piece locations
        List<List<Integer>> startingRedPieceLocations = Arrays.asList(Arrays.asList(4, 0), Arrays.asList(4, 1), Arrays.asList(4, 2), Arrays.asList(4, 3), Arrays.asList(4, 4));
        List<List<Integer>> startingBluePieceLocations = Arrays.asList(Arrays.asList(0, 4), Arrays.asList(0, 3), Arrays.asList(0, 2), Arrays.asList(0, 1), Arrays.asList(0, 0));

        // creates the actual pieces and adds to active pieces list
        int index = 0;
        switch (colour) { // name pieces based on color
            case ("Red"):
                for (int i = 1; i <= 2; i++) {
                    activePieces.add(new Piece("s" + i + "R", startingRedPieceLocations.get(index)));
                    index++;
                }
                activePieces.add(new Sensei("m1R", startingRedPieceLocations.get(index)));
                index++;
                for (int i = 3; i <= 4; i++) {
                    activePieces.add(new Piece("s" + i + "R", startingRedPieceLocations.get(index)));
                    index++;
                }
                break;
            case ("Blue"):
                for (int i = 1; i <= 2; i++) {
                    activePieces.add(new Piece("s" + i + "B", startingBluePieceLocations.get(index)));
                    index++;
                }
                activePieces.add(new Sensei("m1B", startingBluePieceLocations.get(index)));
                index++;
                for (int i = 3; i <= 4; i++) {
                    activePieces.add(new Piece("s" + i + "B", startingBluePieceLocations.get(index)));
                    index++;
                }
                break;
        }
    }

    private boolean hasValidMove(Piece piece, Card card) {
        if (card == null) { // checks if there is a given card
            return false;
        }
        return !piece.getPossibleMovePositions(activePieces, card).isEmpty(); // return true if there is any possible move positions
    }

    boolean hasAnyMove() {
        // checks if any card and piece combos have any valid move
        for (Card card : hand) {
            for (Piece piece : activePieces) {
                if (hasValidMove(piece, card)) { // return true if any single move is valid
                    return true;
                }
            }
        }
        return false; // there is no moves that a player could do
    }

    void noMovesLeft() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("❗❗❗ THERE ARE NO VALID MOVES LEFT ❗❗❗");

        // select a card to discard
        String typedCard = "";
        while (!hand.contains(currentSelectedCard)) { // check if selected card is valid
            System.out.print("Select a card to discard: ");
            typedCard = scanner.nextLine();
            for (Card card : hand) { // find the selected card
                if (card.name.equals(typedCard)) {
                    currentSelectedCard = card; // set the card
                }
            }
        }
    }

    void chosePieceAndCard() {
        Scanner scanner = new Scanner(System.in);

        boolean inLoop = true;
        while (inLoop) {
            // clears previous data
            inLoop = false;
            currentSelectedPiece = null;
            currentSelectedCard = null;

            // selects a piece
            String typedPiece = "";
            while (!activePieces.contains(currentSelectedPiece)) { // check if selected piece is valid
                System.out.print(colour + " select a piece to move: ");
                typedPiece = scanner.nextLine();

                for (Piece piece : activePieces) { // find the selected piece
                    if (piece.name.equals(typedPiece)) {
                        currentSelectedPiece = piece; // set the piece
                    }
                }
            }

            // selects a card
            String typedCard = "";
            while (!hand.contains(currentSelectedCard) || !hasValidMove(currentSelectedPiece, currentSelectedCard)) { // check if selected card is valid
                if (hand.contains(currentSelectedCard) && !hasValidMove(currentSelectedPiece, currentSelectedCard)) { // checks if selected card has no move with current piece
                    System.out.println("❗THIS PIECE HAS NO MOVE WITH THIS CARD.");
                }

                System.out.print(colour + " select a card to use [PRESS 1 TO UNDO ACTION]: ");
                typedCard = scanner.nextLine();
                if (typedCard.equals("1")) { //  move back to previous question
                    inLoop = true;
                    break;
                }

                for (Card card : hand) { // find the selected card
                    if (card.name.equals(typedCard)) {
                        currentSelectedCard = card; // set the card
                    }
                }
            }
        }
    }

    void removePiece(String name) {
        // checks names of active pieces and removes it form the list
        for (Piece piece : activePieces) {
            if (piece.name.equals(name)) {
                if (piece.getClass().getName().equals("Sensei")) { // player dies if sensei is captured
                    isAlive = false;
                }
                activePieces.remove(piece); // remove piece form active pieces
                break;
            }
        }
    }

    Card drawAndDiscardCard(Card offCard) {
        hand.add(offCard);
        hand.remove(currentSelectedCard);
        return currentSelectedCard;
    }
}
