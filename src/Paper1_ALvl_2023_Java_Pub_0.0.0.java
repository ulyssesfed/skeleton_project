/*
    Skeleton Program for the AQA A Level Paper 1 Summer 2023 examination
    this code should be used in conjunction with the Preliminary Material
    written by the AQA Programmer Team
    developed in NetBeans IDE 12.6 environment

*/

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class Dastan {
    protected List<Square> board;
    protected int noOfRows, noOfColumns, moveOptionOfferPosition;
    protected List<Player> players = new ArrayList<>();
    protected List<String> moveOptionOffer = new ArrayList<>();
    protected Player currentPlayer; 
    protected Random rGen = new Random();

    public Dastan(int r, int c, int noOfPieces ){
        players.add(new Player("Player One", 1));
        players.add(new Player("Player Two", -1));
        createMoveOptions();
        noOfRows = r;
        noOfColumns = c;
        moveOptionOfferPosition = 0;
        createMoveOptionOffer();
        createBoard();
        createPieces(noOfPieces);
        currentPlayer = players.get(0);
    }

    private void displayBoard(){
        Console.write(System.lineSeparator() + "   ");
        for (int column = 1; column <= noOfColumns; column++) {
            Console.write(column + "  ");
        }
        Console.write(System.lineSeparator() + "  ");
        for (int count = 1; count <= noOfColumns; count++) {
            Console.write("---");
        }
        Console.writeLine("-");
        for (int row = 1; row <= noOfRows; row++) {
            Console.write(row + " ");
            for (int column = 1; column <= noOfColumns; column++) {
                int index = getIndexOfSquare(row * 10 + column);
                Console.write("|" + board.get(index).getSymbol());
                Piece pieceInSquare = board.get(index).getPieceInSquare();
                if (pieceInSquare == null) {
                    Console.write(" ");
                } else {
                    Console.write(pieceInSquare.getSymbol());
                }
            }
            Console.writeLine("|");
        }
        Console.write("  -");
        for (int column = 1; column <= noOfColumns; column++) {
            Console.write("---");
        }
        Console.writeLine();
        Console.writeLine();
    }

    private void displayState(){
        displayBoard();
        Console.writeLine("Move option offer: " + moveOptionOffer.get(moveOptionOfferPosition));
        Console.writeLine();
        Console.writeLine(currentPlayer.getPlayerStateAsString());
        Console.writeLine("Turn: " + currentPlayer.getName());
        Console.writeLine();
    }

    private int getIndexOfSquare(int squareReference) {
        int row = squareReference / 10;
        int col = squareReference % 10;
        return (row - 1) * noOfColumns + (col - 1);
    }

    private boolean checkSquareInBounds(int squareReference){
        int row = squareReference / 10;
        int col = squareReference % 10;
        if (row < 1 || row > noOfRows) {
            return false;
        } else if (col < 1 || col > noOfColumns) {
            return false;
        } else {
            return true;
        }
    }

    private boolean checkSquareIsValid(int squareReference, boolean startSquare) {
        if (!checkSquareInBounds(squareReference)) {
            return false;
        }
        Piece pieceInSquare = board.get(getIndexOfSquare(squareReference)).getPieceInSquare();
        if (pieceInSquare == null) {
            if (startSquare) {
                return false;
            } else {
                return true;
            }
        } else if (currentPlayer.sameAs(pieceInSquare.getBelongsTo())) {
            if (startSquare) {
                return true;
            } else {
                return false;
            }
        } else {
            if (startSquare) {
                return false;
            } else {
                return true;
            }
        }
    }

    private boolean checkIfGameOver() {
        boolean player1HasMirza = false;
        boolean player2HasMirza = false;
        for (Square s : board) {
            Piece pieceInSquare = s.getPieceInSquare();
            if (pieceInSquare != null) {
                if (s.containsKotla() && pieceInSquare.getTypeOfPiece().equals("mirza") && !pieceInSquare.getBelongsTo().sameAs(s.getBelongsTo())) {
                    return true;
                } else if (pieceInSquare.getTypeOfPiece().equals("mirza") && pieceInSquare.getBelongsTo().sameAs(players.get(0))) {
                    player1HasMirza = true;
                } else if (pieceInSquare.getTypeOfPiece().equals("mirza") && pieceInSquare.getBelongsTo().sameAs(players.get(1))) {
                    player2HasMirza = true;
                }
            }
        }
        return !(player1HasMirza && player2HasMirza);
    }

    private int getSquareReference(String description) {
        int selectedSquare;
        Console.write("Enter the square " + description + " (row number followed by column number): ");
        selectedSquare = Integer.parseInt(Console.readLine());
        return selectedSquare;
    }

    private void useMoveOptionOffer() {
        int replaceChoice;
        Console.write("Choose the move option from your queue to replace (1 to 5): ");
        replaceChoice = Integer.parseInt(Console.readLine());
        currentPlayer.updateMoveOptionQueueWithOffer(replaceChoice - 1, createMoveOption(moveOptionOffer.get(moveOptionOfferPosition), currentPlayer.getDirection()));
        currentPlayer.changeScore(-(10 - (replaceChoice * 2)));
        moveOptionOfferPosition = rGen.nextInt(5);
    }

    private int getPointsForOccupancyByPlayer(Player currentPlayer) {
        int scoreAdjustment = 0;
        for (Square s : board) {
            scoreAdjustment += (s.getPointsForOccupancy(currentPlayer));
        }
        return scoreAdjustment;
    }

    private void updatePlayerScore(int pointsForPieceCapture) {
        currentPlayer.changeScore(getPointsForOccupancyByPlayer(currentPlayer) + pointsForPieceCapture);
    }

    private int calculatePieceCapturePoints(int finishSquareReference) {
        if (board.get(getIndexOfSquare(finishSquareReference)).getPieceInSquare() != null) {
            return board.get(getIndexOfSquare(finishSquareReference)).getPieceInSquare().getPointsIfCaptured();
        }
        return 0;
    }

    public void playGame() {
        boolean gameOver = false;
        while (!gameOver) {
            displayState();
            boolean squareIsValid = false;
            int choice;
            do {
                Console.write("Choose move option to use from queue (1 to 3) or 9 to take the offer: ");
                choice = Integer.parseInt(Console.readLine());
                if (choice == 9) {
                    useMoveOptionOffer();
                    displayState();
                }
            } while (choice < 1 || choice > 3);
            int startSquareReference = 0;
            while (!squareIsValid) {
                startSquareReference = getSquareReference("containing the piece to move");
                squareIsValid = checkSquareIsValid(startSquareReference, true);
            }
            int finishSquareReference = 0;
            squareIsValid = false;
            while (!squareIsValid) {
                finishSquareReference = getSquareReference("to move to");
                squareIsValid = checkSquareIsValid(finishSquareReference, false);
            }
            boolean moveLegal = currentPlayer.checkPlayerMove(choice, startSquareReference, finishSquareReference);
            if (moveLegal) {
                int pointsForPieceCapture = calculatePieceCapturePoints(finishSquareReference);
                currentPlayer.changeScore(-(choice + (2 * (choice - 1))));
                currentPlayer.updateQueueAfterMove(choice);
                updateboard(startSquareReference, finishSquareReference);
                updatePlayerScore(pointsForPieceCapture);
                Console.writeLine("New score: " + currentPlayer.getScore() + System.lineSeparator());
            }
            if (currentPlayer.sameAs(players.get(0))) {
                currentPlayer = players.get(1);
            } else {
                currentPlayer = players.get(0);
            }
            gameOver = checkIfGameOver();
        }
        displayState();
        displayFinalResult();
    }

    private void updateboard(int startSquareReference, int finishSquareReference) {
        board.get(getIndexOfSquare(finishSquareReference)).setPiece(board.get(getIndexOfSquare(startSquareReference)).removePiece());
    }

    private void displayFinalResult() {
        if (players.get(0).getScore() == players.get(1).getScore()) {
            Console.writeLine("Draw!");
        } else if (players.get(0).getScore() > players.get(1).getScore()) {
            Console.writeLine(players.get(0).getName() + " is the winner!");
        } else {
            Console.writeLine(players.get(1).getName() + " is the winner!");
        }
    }

    private void createBoard() {
        Square s;
        board = new ArrayList<>();
        for (int row = 1; row <= noOfRows; row++) {
            for (int column = 1; column <= noOfColumns; column++) {
                if (row == 1 && column == noOfColumns / 2) {
                    s = new Kotla(players.get(0), "K");
                } else if (row == noOfRows && column == noOfColumns / 2 + 1) {
                    s = new Kotla(players.get(1), "k");
                } else {
                    s = new Square();
                }
                board.add(s);
            }
        }
    }

    private void createPieces(int noOfPieces) {
        Piece currentPiece;
        for (int count = 1; count <= noOfPieces; count++) {
            currentPiece = new Piece("piece", players.get(0), 1, "!");
            board.get(getIndexOfSquare(2 * 10 + count + 1)).setPiece(currentPiece);
        }
        currentPiece = new Piece("mirza", players.get(0), 5, "1");
        board.get(getIndexOfSquare(10 + noOfColumns / 2)).setPiece(currentPiece);
        for (int count = 1; count <= noOfPieces; count++) {
            currentPiece = new Piece("piece", players.get(1), 1, "\"");
            board.get(getIndexOfSquare((noOfRows - 1) * 10 + count + 1)).setPiece(currentPiece);
        }
        currentPiece = new Piece("mirza", players.get(1), 5, "2");
        board.get(getIndexOfSquare(noOfRows * 10 + (noOfColumns / 2 + 1))).setPiece(currentPiece);
    }

    private void createMoveOptionOffer() {
        moveOptionOffer.add("jazair");
        moveOptionOffer.add("chowkidar");
        moveOptionOffer.add("cuirassier");
        moveOptionOffer.add("ryott");
        moveOptionOffer.add("faujdar");
    }

    private MoveOption createRyottMoveOption(int direction) {
        MoveOption newMoveOption  = new MoveOption("ryott");
        Move newMove = new Move(0, 1 * direction);
        newMoveOption.addToPossibleMoves(newMove);
        newMove = new Move(0, -1 * direction);
        newMoveOption.addToPossibleMoves(newMove);
        newMove = new Move(1 * direction, 0);
        newMoveOption.addToPossibleMoves(newMove);
        newMove = new Move(-1 * direction, 0);
        newMoveOption.addToPossibleMoves(newMove);
        return newMoveOption;
    }

    private MoveOption createFaujdarMoveOption(int direction) { 
        MoveOption newMoveOption = new MoveOption("faujdar");
        Move newMove = new Move(0, -1 * direction);
        newMoveOption.addToPossibleMoves(newMove);
        newMove = new Move(0, 1 * direction);
        newMoveOption.addToPossibleMoves(newMove);
        newMove = new Move(0, 2 * direction);
        newMoveOption.addToPossibleMoves(newMove);
        newMove = new Move(0, -2 * direction);
        newMoveOption.addToPossibleMoves(newMove);
        return newMoveOption;
    }

    private MoveOption createJazairMoveOption(int direction) { 
        MoveOption newMoveOption = new MoveOption("jazair");
        Move newMove = new Move(2 * direction, 0);
        newMoveOption.addToPossibleMoves(newMove);
        newMove = new Move(2 * direction, -2 * direction);
        newMoveOption.addToPossibleMoves(newMove);
        newMove = new Move(2 * direction, 2 * direction);
        newMoveOption.addToPossibleMoves(newMove);
        newMove = new Move(0, 2 * direction);
        newMoveOption.addToPossibleMoves(newMove);
        newMove = new Move(0, -2 * direction);
        newMoveOption.addToPossibleMoves(newMove);
        newMove = new Move(-1 * direction, -1 * direction);
        newMoveOption.addToPossibleMoves(newMove);
        newMove = new Move(-1 * direction, 1 * direction);
        newMoveOption.addToPossibleMoves(newMove);
        return newMoveOption;
    }

    private MoveOption createCuirassierMoveOption(int direction) { 
        MoveOption newMoveOption = new MoveOption("cuirassier");
        Move newMove = new Move(1 * direction, 0);
        newMoveOption.addToPossibleMoves(newMove);
        newMove = new Move(2 * direction, 0);
        newMoveOption.addToPossibleMoves(newMove);
        newMove = new Move(1 * direction, -2 * direction);
        newMoveOption.addToPossibleMoves(newMove);
        newMove = new Move(1 * direction, 2 * direction);
        newMoveOption.addToPossibleMoves(newMove);
        return newMoveOption;
    }

    private MoveOption createChowkidarMoveOption(int direction) {
        MoveOption newMoveOption = new MoveOption("chowkidar");
        Move newMove = new Move(1 * direction, 1 * direction);
        newMoveOption.addToPossibleMoves(newMove);
        newMove = new Move(1 * direction, -1 * direction);
        newMoveOption.addToPossibleMoves(newMove);
        newMove = new Move(-1 * direction, 1 * direction);
        newMoveOption.addToPossibleMoves(newMove);
        newMove = new Move(-1 * direction, -1 * direction);
        newMoveOption.addToPossibleMoves(newMove);
        newMove = new Move(0, 2 * direction);
        newMoveOption.addToPossibleMoves(newMove);
        newMove = new Move(0, -2 * direction);
        newMoveOption.addToPossibleMoves(newMove);
        return newMoveOption;
    }

    private MoveOption createMoveOption(String name, int direction) {
        switch (name) {
            case "chowkidar":
                return createChowkidarMoveOption(direction);
            case "ryott":
                return createRyottMoveOption(direction);
            case "faujdar":
                return createFaujdarMoveOption(direction);
            case "jazair":
                return createJazairMoveOption(direction);
            default:
                return createCuirassierMoveOption(direction);
        }
    }

    private void createMoveOptions(){
        players.get(0).addToMoveOptionQueue(createMoveOption("ryott", 1));
        players.get(0).addToMoveOptionQueue(createMoveOption("chowkidar", 1));
        players.get(0).addToMoveOptionQueue(createMoveOption("cuirassier", 1));
        players.get(0).addToMoveOptionQueue(createMoveOption("faujdar", 1));
        players.get(0).addToMoveOptionQueue(createMoveOption("jazair", 1));
        players.get(1).addToMoveOptionQueue(createMoveOption("ryott", -1));
        players.get(1).addToMoveOptionQueue(createMoveOption("chowkidar", -1));
        players.get(1).addToMoveOptionQueue(createMoveOption("jazair", -1));
        players.get(1).addToMoveOptionQueue(createMoveOption("faujdar", -1));
        players.get(1).addToMoveOptionQueue(createMoveOption("cuirassier", -1));
    }
}

class Piece {
    protected String typeOfPiece, symbol; 
    protected int pointsIfCaptured;
    protected Player belongsTo;

    public Piece(String t, Player b, int p, String s) {
        typeOfPiece = t;
        belongsTo = b;
        pointsIfCaptured = p;
        symbol = s;
    }

    public String getSymbol() { 
        return symbol;
    }

    public String getTypeOfPiece() { 
        return typeOfPiece;
    }

    public Player getBelongsTo() { 
        return belongsTo;
    }

    public int getPointsIfCaptured() {
        return pointsIfCaptured;
    }
}

class Square {
    protected String symbol; 
    protected Piece pieceInSquare; 
    protected Player belongsTo; 

    public Square() {
        pieceInSquare = null;
        belongsTo = null;
        symbol = " ";
    }

    public void setPiece(Piece p) {
        pieceInSquare = p;
    }

    public Piece removePiece() { 
        Piece pieceToReturn  = pieceInSquare;
        pieceInSquare = null;
        return pieceToReturn;
    }

    public Piece getPieceInSquare() { 
        return pieceInSquare;
    }

    public String getSymbol() { 
        return symbol;
    }

    public int getPointsForOccupancy(Player currentPlayer) {
        return 0;
    }

    public Player getBelongsTo() { 
        return belongsTo;
    }

    public boolean containsKotla() {
        if (symbol.equals("K") || symbol.equals("k")) {
            return true;
        } else {
            return false;
        }
    }
}

class Kotla extends Square {

    public Kotla(Player p, String s) {
        super();
        belongsTo = p;
        symbol = s;
    }           

    @Override
    public int getPointsForOccupancy(Player currentPlayer) {
        if (pieceInSquare == null) {
            return 0;
        } else if (belongsTo.sameAs(currentPlayer)) {
            if (currentPlayer.sameAs(pieceInSquare.getBelongsTo()) && (pieceInSquare.getTypeOfPiece().equals("piece") || pieceInSquare.getTypeOfPiece().equals("mirza"))) {
                return 5;
            } else {
                return 0;
            }
        } else {
            if (currentPlayer.sameAs(pieceInSquare.getBelongsTo()) && (pieceInSquare.getTypeOfPiece().equals("piece") || pieceInSquare.getTypeOfPiece().equals("mirza"))) {
                return 1;
            } else {
                return 0;
            }
        }
    }
}

class MoveOption {
    protected String name; 
    protected List<Move> possibleMoves;

    public MoveOption(String n) {
        name = n;
        possibleMoves = new ArrayList<>();
    }

    public void addToPossibleMoves(Move m) {
        possibleMoves.add(m);
    }

    public String getName() { 
        return name;
    }

    public boolean checkIfThereIsAMoveToSquare(int startSquareReference, int finishSquareReference) {
        int startRow  = startSquareReference / 10;
        int startColumn = startSquareReference % 10;
        int finishRow = finishSquareReference / 10;
        int finishColumn = finishSquareReference % 10;
        for (Move m : possibleMoves) {
            if (startRow + m.getRowChange() == finishRow && startColumn + m.getColumnChange() == finishColumn) {
                return true;
            }
        }
        return false;
    }
}

class Move {
    protected int rowChange, columnChange;

    Move(int r, int c) {
        rowChange = r;
        columnChange = c;
    }

    public int getRowChange() {
        return rowChange;
    }

    public int getColumnChange() {
        return columnChange;
    }
}

class MoveOptionQueue {
    private List<MoveOption> queue = new ArrayList<>();

    public String getQueueAsString() {
        String queueAsString = "";
        int count = 1;
        for (MoveOption m : queue) {
            queueAsString += count + ". " + m.getName() + "   ";
            count += 1;
        }
        return queueAsString;
    }

    public void add(MoveOption newMoveOption){
        queue.add(newMoveOption);
    }

    public void replace(int position, MoveOption newMoveOption) {
        queue.set(position, newMoveOption);
    }

    public void moveItemToBack(int position) {
        MoveOption temp = queue.get(position);
        queue.remove(position);
        queue.add(temp);
    }

    public MoveOption getMoveOptionInPosition(int pos) {
        return queue.get(pos);
    }
}

class Player {
    private String name; 
    private int direction, score; 
    private MoveOptionQueue queue = new MoveOptionQueue();

    public Player(String n, int d) {
        score = 100;
        name = n;
        direction = d;
    }

    public boolean sameAs(Player APlayer) {
        if (APlayer == null) {
            return false;
        } else if (APlayer.getName().equals(name)) {
            return true;
        } else {
            return false;
        }
    }

    public String getPlayerStateAsString() { 
        return name + System.lineSeparator() + "Score: " + score + System.lineSeparator() + "Move option queue: " + queue.getQueueAsString() + System.lineSeparator();
    }

    public void addToMoveOptionQueue(MoveOption newMoveOption) {
        queue.add(newMoveOption);
    }

    public void updateQueueAfterMove(int position) {
        queue.moveItemToBack(position - 1);
    }

    public void updateMoveOptionQueueWithOffer(int position, MoveOption newMoveOption) {
        queue.replace(position, newMoveOption);
    }

    public int getScore() { 
        return score;
    }

    public String getName() { 
        return name;
    }

    public int getDirection() {
        return direction;
    }

    public void changeScore(int amount) {
        score += amount;
    }

    public boolean checkPlayerMove(int pos, int startSquareReference, int finishSquareReference) {
        MoveOption temp = queue.getMoveOptionInPosition(pos - 1);
        return temp.checkIfThereIsAMoveToSquare(startSquareReference, finishSquareReference);
    }
}