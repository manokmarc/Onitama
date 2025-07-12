import java.io.*;
import java.util.*;

public class Main {
    private static void menuScreen() {
        Scanner scanner = new Scanner(System.in);

        int option = 0;
        System.out.println("/////////////////////////");
        System.out.println("///WELCOME TO ONITAMA!///");
        System.out.println("/////////////////////////");
        System.out.println("Type 1: Start Game");
        System.out.println("Type 2: Quit Game");
        System.out.println("Type 3: Hall Of Fame");

        while (option != 1) { // keeps asking until a valid input is given
            try {
                System.out.print("Select Option: ");
                option = scanner.nextInt();
            } catch (InputMismatchException e) {
                scanner.nextLine(); // clear the invalid input
                continue; // skip to the next loop iteration
            }
            switch (option) { // close program
                case 2:
                    System.out.println("***GOODBYE***");
                    System.exit(0);
                case 3: // go to hall of fame
                    hallOfFameScreen();
                    return; // makes sure the menu screen instance is exited
            }
        }
    }

    private static void hallOfFameScreen() {
        Scanner scanner = new Scanner(System.in);

        HallOfFame hallOfFame = new HallOfFame();
        hallOfFame.printScores(); // display scores
        System.out.println();
        System.out.println("⚪ Press Enter to go back to menu screen ⚪");
        scanner.nextLine(); // waits for Enter
        menuScreen(); // return to menu screen
    }

    private static List<String> getStartingCards() {
        String filePath = "movecards.txt";
        Random rand = new Random();
        List<String> chosenCards = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            int deckSize = Integer.parseInt(reader.readLine()); // gets the size of the whole card deck from the first line
            Set<Integer> uniqueRandomNums = new HashSet<>();
            while (uniqueRandomNums.size() < 5) { // gets 5 random numbers
                int randNum = rand.nextInt(deckSize) + 1; // generate random number based on deck size
                uniqueRandomNums.add(randNum);
            }
            // get cards based on the random numbers generated
            String line;
            int curIndex = 1; // start at 1 because we already read the first line
            while ((line = reader.readLine()) != null) {
                if (uniqueRandomNums.contains(curIndex)) { // gets the card based on the random number as the index
                    chosenCards.add(line);
                }
                curIndex++;
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        } catch (IOException e) {
            System.out.println("Something went wrong");
        }
        return chosenCards; // returns the list of card names
    }

    private static List<Card> getRealCards(List<String> playCardNames, List<Integer> range) {
        List<Card> playerCards = new ArrayList<>(List.of());

        for (Integer index : range) { // gets cards based of the range
            playerCards.add(new Card(playCardNames.get(index))); // turn the card names into actual cards
        }
        return playerCards; // return actual card classes
    }

    private static void cardDistributionScreen(List<String> playCardNames, Card offDeck) throws InterruptedException {
        Scanner scanner = new Scanner(System.in);

        System.out.println();
        System.out.println("Shuffling cards...");
        Thread.sleep(1000);
        for (String card : playCardNames.subList(0, 2)) { // show first 2 cards (Player Red)
            System.out.println("Player Red gets: " + card);
            Thread.sleep(500);
        }
        for (String card : playCardNames.subList(2, 4)) { // show next 2 cards (Player Blue)
            System.out.println("Player Blue gets: " + card);
            Thread.sleep(500);
        }
        System.out.println("The off card is: " + playCardNames.get(4)); // show last card (Off Deck)
        Thread.sleep(500);

        System.out.println();
        System.out.println("★★★ Player " + offDeck.color + " goes first ★★★"); // determines who goes first from the off deck card color

        System.out.println("Press ENTER to continue...");
        scanner.nextLine(); // waits for Enter
        System.out.println("Continuing...");
        System.out.println("--------------------------------------------------");
        Thread.sleep(1000);
    }

    private static void cardMovesScreen(Player playerRed, Player playerBlue, Card offDeck) throws InterruptedException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("⚪ These are the card moves ⚪");
        System.out.println();

        // display the move patterns of each drawn card
        System.out.println("The off card is: ");
        System.out.println(offDeck.name + ":");
        offDeck.printCardMovePattern();
        System.out.println();
        Thread.sleep(500);
        System.out.println("Player Red has: ");
        for (Card card : playerRed.hand) {
            System.out.println(card.name + ":");
            card.printCardMovePattern();
        }
        System.out.println();
        Thread.sleep(500);
        System.out.println("Player Blue has: ");
        for (Card card : playerBlue.hand) {
            System.out.println(card.name + ":");
            card.printCardMovePattern();
        }
        Thread.sleep(500);

        System.out.println("Press ENTER to continue...");
        scanner.nextLine(); // waits for Enter
        System.out.println("Continuing...");
        System.out.println("--------------------------------------------------");
    }

    private static List<String> getNames() {
        Scanner scanner = new Scanner(System.in);

        List<String> playerNames = new ArrayList<>();
        System.out.println("Enter Name Player 1: ");
        playerNames.add(scanner.nextLine());
        System.out.println("Enter Name Player 2: ");
        playerNames.add(scanner.nextLine());
        return playerNames;
    }

    private static void playerLoop(Player player, Player opponent, Board board) {
        Scanner scanner = new Scanner(System.in);

        int selectedMove = 0;
        while (true) {
            System.out.println("⭐⭐⭐" + player.colour.toUpperCase() + " TO MOVE⭐⭐⭐"); // display who is moving
            board.displayBoard(board.board, player, opponent); // display the board

            if (!player.hasAnyMove()) { // end player turn if there are no card piece combination moves possible
                player.noMovesLeft(); // ask player to discard a card
                return;
            }

            player.chosePieceAndCard();// ask player to select a piece and card
            board.showPossibleMoves(player.currentSelectedPiece, player.currentSelectedCard, player, opponent); // display the board with the possible moves
            selectedMove = board.chooseMove(); // ask which move position to move piece in

            // loops back if 1 is pressed
            System.out.print("Press ENTER to confirm your move... [TYPE 1 TO UNDO ACTION]: ");
            String input = scanner.nextLine(); // waits for enter
            if (!input.equals("1")) { // end loop if 1 is not selected
                break;
            }
        }
        board.movePiece(selectedMove, player.currentSelectedPiece, player, opponent); // moves and updates the data of the pieces in the board
    }

    private static boolean endScreen(String winner) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("/////////////////////////");
        System.out.println("/////// " + winner + " WON ////////");
        System.out.println("/////////////////////////");
        System.out.println("Type 1: Play Again");
        System.out.println("Type 2: Quit Game");

        int option = 0;
        while (option != 1) { // keeps asking until a valid input is given
            try {
                System.out.print("DO YOU WANNA PLAY AGAIN: ");
                option = scanner.nextInt();
            } catch (InputMismatchException e) {
                scanner.nextLine(); // clear the invalid input
                continue; // skip to the next loop iteration
            }
            if (option == 2) {
                System.out.println("***GOODBYE***");
                System.exit(0); // close program
            }
        }
        return true;
    }

    public static void main(String[] args) throws InterruptedException {
        Scanner scanner = new Scanner(System.in);

        boolean isRunning = true;
        while (isRunning) { // the game has started
            int moveCount = 0;
            HallOfFame hallOfFame = new HallOfFame();

            menuScreen(); // show menu
            List<String> playerNames = getNames(); // gets player name

            // get playing cards names
            List<String> playCardNames = getStartingCards();

            // set up the players and turn the card names into actual cards
            Player playerRed = new Player("Red", "[R]", getRealCards(playCardNames, Arrays.asList(0, 1)));
            Player playerBlue = new Player("Blue", "[B]", getRealCards(playCardNames, Arrays.asList(2, 3)));

            // make the board
            Board board = new Board(playerRed, playerBlue, new Card(playCardNames.getLast()));

            // starts the card UI collecting sequence and show who goes first
            cardDistributionScreen(playCardNames, board.offCard);

            // display card move patterns
            cardMovesScreen(playerRed, playerBlue, board.offCard);

            // set player move rotation
            Player currentPlayer;
            Player nextplayer;
            if (board.offCard.color.equals("Red")) { // red goes first
                currentPlayer = playerRed;
                nextplayer = playerBlue;
            } else { // blue goes first
                currentPlayer = playerBlue;
                nextplayer = playerRed;
            }

            // game loop
            while (currentPlayer.isAlive) { // repeat actions while both players are alive
                playerLoop(currentPlayer, nextplayer, board); // player move selection sequence
                board.offCard = currentPlayer.drawAndDiscardCard(board.offCard); // discards a card and get the off card

                // switch current player
                Player temp = currentPlayer;
                currentPlayer = nextplayer;
                nextplayer = temp;

                moveCount++;
            }
            hallOfFame.saveScore(playerNames, moveCount); // adds the score to the hall of fame if valid
            isRunning = endScreen(nextplayer.colour); // loads the end screen
        }
    }
}