import java.util.Random;
import java.util.Scanner;

public class Game {
    private Board board;
    private Player player1;
    private Player player2;
    private Player currentPlayer;
    private Scanner scanner;

    public Game() {
        board = new Board();
        scanner = new Scanner(System.in);

        player1 = new Player("Player 1 (Blue)");
        player2 = new Player("Player 2 (Green)");
    }

    public void initializeGame() {
        int choice;
    
        do {
            System.out.println("\n=== Jungle King ===");
            System.out.println("1. Start Game");
            System.out.println("2. Exit Program");
            System.out.print("Choose an option: ");
    
            choice = scanner.nextInt();
    
            if (choice != 1 && choice != 2) {
                System.out.println("Invalid choice! Please enter 1 or 2.");
            }
    
        } while (choice != 1 && choice != 2);
    
        if (choice == 2) {
            System.out.println("Exiting game...");
            return;
        }
    
        shuffleAndAssignPieces();
        playGame();
    }

    private void shuffleAndAssignPieces() {
        int player1Choice = 0, player2Choice = 0;
        Random rand = new Random();

        // Create pieces
        Piece dog = new Dog("Dog", 0, 0, "", "");
        Piece cat = new Cat("Cat", 0, 0, "", "");
        
        Piece[] pieces = {dog, cat};
        
        // Shuffle the pieces
        for (int i = 0; i < pieces.length; i++) {
            int randomIndex = rand.nextInt(pieces.length);
            Piece temp = pieces[i];
            pieces[i] = pieces[randomIndex];
            pieces[randomIndex] = temp;
        }
        
        
        System.out.println("\nShuffling pieces...");
        System.out.println(pieces.length + " hidden numbers contain different animals.");
        
        // Pick number
        while (player1Choice < 1 || player1Choice > pieces.length) {
            System.out.print("Player 1, pick a number between 1-" + pieces.length + ": ");
            player1Choice = scanner.nextInt();
            if (player1Choice < 1 || player1Choice > pieces.length) {
                System.out.println("Invalid choice. Please pick a valid number.");
            }
        }
        
        // TEMP: FOR MCO1 ONLY
        if (pieces.length == 2) {
            player2Choice = (player1Choice == 1) ? 2 : 1;
            System.out.println("Player 2, your number is: " + player2Choice);
        } else {
            while (player2Choice < 1 || player2Choice > pieces.length || player2Choice == player1Choice) {
                System.out.print("Player 2, pick a number between 1-" + pieces.length + ": ");
                player2Choice = scanner.nextInt();
                if (player2Choice < 1 || player2Choice > pieces.length || player2Choice == player1Choice) {
                    System.out.println("Invalid choice. Please pick a valid number.");
                }
            }
        }
        
        Piece player1Piece = pieces[player1Choice - 1];
        Piece player2Piece = pieces[player2Choice - 1];
        
        System.out.println("\nRevealing hidden animals...");
        System.out.println("Player 1 selected: " + player1Piece.getName() + " (Strength " + player1Piece.getStrength() + ")");
        System.out.println("Player 2 selected: " + player2Piece.getName() + " (Strength " + player2Piece.getStrength() + ")");
        
        // Initialize pieces
        Piece p1Dog = new Dog("Dog", 5, 1, "BD", "Blue");
        Piece p1Cat = new Cat("Cat", 1, 1, "BC", "Blue");
        
        Piece p2Dog = new Dog("Dog", 1, 7, "GD", "Green");
        Piece p2Cat = new Cat("Cat", 5, 7, "GC", "Green");
        
        // Add pieces to players
        player1.addPiece(p1Dog);
        player1.addPiece(p1Cat);
        
        player2.addPiece(p2Dog);
        player2.addPiece(p2Cat);
        
        // Determine first player based on strength
        if (player1Piece.getStrength() > player2Piece.getStrength()) {
            currentPlayer = player1;
            System.out.println("\n" + currentPlayer.getName() + " goes first because " + player1Piece.getName() + " is stronger than " + player2Piece.getName() + "!\n");
        } else if (player2Piece.getStrength() > player1Piece.getStrength()) {
            currentPlayer = player2;
            System.out.println("\n" + currentPlayer.getName() + " goes first because " + player2Piece.getName() + " is stronger than " + player1Piece.getName() + "!\n");
        } else {
            if (rand.nextBoolean()) {
                currentPlayer = player1;
            } else {
                currentPlayer = player2;
            }
            System.out.println("\n" + currentPlayer.getName() + " goes first (randomly selected for equal strength)!\n");
        }
        
        // Place the initialized pieces on the board
        for (Piece piece : player1.getAllPieces()) {
            board.placePiece(piece);
        }
        
        for (Piece piece : player2.getAllPieces()) {
            board.placePiece(piece);
        }
    }
    
    private void playGame() {
        boolean gameOver = false;
        
        while (!gameOver) {
            board.displayBoard();
            System.out.println(currentPlayer.getName() + "'s Turn");

            // check pieces
            if (currentPlayer.getActivePieces().isEmpty()) {
                System.out.println(currentPlayer.getName() + " has no pieces left. Turn forfeited.");
                switchTurn();
            } else {
                Piece selectedPiece = selectPiece();
                char direction = getDirectionInput();

                currentPlayer.movePiece(selectedPiece, direction, board);

                if (checkWinCondition(selectedPiece)) {
                    System.out.println("\n" + currentPlayer.getName() + " wins the game!");
                    gameOver = true;
                } else {
                    switchTurn();
                }
            }
            
            // Check draw
            if (player1.getActivePieces().isEmpty() && player2.getActivePieces().isEmpty()) {
                System.out.println("\nBoth players have no pieces left. Game ends in a draw!");
                gameOver = true;
            }
        }

        System.out.println("Game Over!");
    }

    private Piece selectPiece() {
        Piece selectedPiece = null;
        while (selectedPiece == null) {
            System.out.print("Select a piece to move (Dog/Cat): ");
            String pieceType = scanner.next();
            selectedPiece = currentPlayer.getPiece(pieceType);
            
            if (selectedPiece == null) {
                System.out.println("Invalid selection. Try again.\n");
            }
        }
        return selectedPiece;
    }

    private char getDirectionInput() {
        char direction;
        while (true) {
            System.out.print("Enter direction (W = Up, S = Down, A = Left, D = Right): ");
            direction = scanner.next().toUpperCase().charAt(0);
            if (direction == 'W' || direction == 'S' || direction == 'A' || direction == 'D') {
                break;
            }
            System.out.println("Invalid direction. Use W, A, S, or D.\n");
        }
        return direction;
    }

    private boolean checkWinCondition(Piece piece) {
        if (piece.getOwner().equals("Blue") && board.isHomeBase(piece.getRow(), piece.getCol(), "Green")) {
            return true; // Blue wins by capturing Green's home base
        }
        if (piece.getOwner().equals("Green") && board.isHomeBase(piece.getRow(), piece.getCol(), "Blue")) {
            return true; // Green wins by capturing Blue's home base
        }
        return false;
    }
    

    private void switchTurn() {
        if (currentPlayer == player1) {
            currentPlayer = player2;
        } else {
            currentPlayer = player1;
        }
    }
}
