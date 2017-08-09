package model;

import java.awt.*;
import java.util.*;

import static model.ModelConstants.*;

public class BoardManager {
    private static int blackLastTurnHashcode;
    private static int whiteLastTurnHashcode;
    private static int previousBoardHashcode;
    private static int currentBoardHashcode;
    private static int blackPrisoners;
    private static int whitePrisoners;
    private static int blackTerritory;
    private static int whiteTerritory;

    public BoardManager() {
        initZobristTable();
    }

    public boolean addStone(Board b, Point point, int player) {
        previousBoardHashcode = currentBoardHashcode;

        if (player == BLACK) {
            if (!isValid(b, point, player, currentBoardHashcode, blackLastTurnHashcode)) {
                return false;
            }
        } else {
            if (!isValid(b, point, player, currentBoardHashcode, whiteLastTurnHashcode)) {
                return false;
            }
        }

        b.addStone(point, player);
        checkPrisoners(b, point, player);

        currentBoardHashcode = b.hashCode();

        if (player == BLACK) {
            blackLastTurnHashcode = currentBoardHashcode;
        } else {
            whiteLastTurnHashcode = currentBoardHashcode;
        }
        return true;
    }

    private static void removeStone(Board b, Point position) {
        b.removeStone(position);
    }

    private static void checkPrisoners(Board b, Point point, int player) {
        int[][] matrix = b.getMatrix();
        Node[][] nm = nodeMatrix(b);

        // West
        if (point.x - 1 >= 0 && matrix[point.x - 1][point.y] == otherPlayer(player)) {
            Set<Point> visited = new HashSet<>();
            if (floodFill(nm, nm[point.x - 1][point.y], otherPlayer(player), visited)) {
                stoneRemover(visited, b, player);
            }
        }

        nm = nodeMatrix(b);

        // East
        if (point.x + 1 < BOARD_SIZE) {
            if (matrix[point.x + 1][point.y] == otherPlayer(player)) {
                Set<Point> visited = new HashSet<>();
                if (floodFill(nm, nm[point.x + 1][point.y], otherPlayer(player), visited)) {
                    stoneRemover(visited, b, player);
                }
            }
        }

        nm = nodeMatrix(b);

        // South
        if (point.y + 1 < BOARD_SIZE && matrix[point.x][point.y + 1] == otherPlayer(player)) {
            Set<Point> visited = new HashSet<>();
            if (floodFill(nm, nm[point.x][point.y + 1], otherPlayer(player), visited)) {
                stoneRemover(visited, b, player);
            }
        }

        nm = nodeMatrix(b);

        // North
        if (point.y - 1 >= 0 && matrix[point.x][point.y - 1] == otherPlayer(player)) {
            Set<Point> visited = new HashSet<>();
            if (floodFill(nm, nm[point.x][point.y - 1], otherPlayer(player), visited)) {
                stoneRemover(visited, b, player);
            }
        }
    }

    private static void stoneRemover(Set<Point> visited, Board b, int player) {
        for (Point p : visited) {
            removeStone(b, p);
            if (player == BLACK) {
                blackPrisoners++;
            } else {
                whitePrisoners++;
            }
        }
    }

    static boolean isValid(Board b, Point point, int player, int currentHash, int previousHash) {
        return !spotOccupied(b.getMatrix(), point) && !ruleSuicide(b, point, player) && !ruleKo(b, point, player, currentHash, previousHash);
    }

    private static boolean spotOccupied(int[][] m, Point point) {
        return m[point.x][point.y] != 0;
    }

    /**
     * Devuelve true si el movimiento es suicida.
     */
    private static boolean ruleSuicide(Board b, Point point, int player) {
        int[][] matrix = b.getMatrix();

        if (point.x + 1 < BOARD_SIZE) {
            if (matrix[point.x + 1][point.y] == otherPlayer(player)) {
                Node[][] nm = nodeMatrix(b);
                nm[point.x][point.y] = new Node(point.x, point.y, player);
                if (floodFill(nm, nm[point.x + 1][point.y], otherPlayer(player), new HashSet<>())) {
                    return false;
                }
            } else if (matrix[point.x + 1][point.y] == player) {
                Node[][] nm = nodeMatrix(b);
                nm[point.x][point.y] = new Node(point.x, point.y, player);
                if (!floodFill(nm, nm[point.x + 1][point.y], player, new HashSet<>())) {
                    return false;
                }
            } else {
                return false;
            }
        }

        if (point.x - 1 >= 0) {
            if (matrix[point.x - 1][point.y] == otherPlayer(player)) {
                Node[][] nm = nodeMatrix(b);
                nm[point.x][point.y] = new Node(point.x, point.y, player);
                if (floodFill(nm, nm[point.x - 1][point.y], otherPlayer(player), new HashSet<>())) {
                    return false;
                }
            } else if (matrix[point.x - 1][point.y] == player) {
                Node[][] nm = nodeMatrix(b);
                nm[point.x][point.y] = new Node(point.x, point.y, player);
                if (!floodFill(nm, nm[point.x - 1][point.y], player, new HashSet<>())) {
                    return false;
                }
            } else {
                return false;
            }
        }

        if (point.y + 1 < BOARD_SIZE) {
            if (matrix[point.x][point.y + 1] == otherPlayer(player)) {
                Node[][] nm = nodeMatrix(b);
                nm[point.x][point.y] = new Node(point.x, point.y, player);
                if (floodFill(nm, nm[point.x][point.y + 1], otherPlayer(player), new HashSet<>())) {
                    return false;
                }
            } else if (matrix[point.x][point.y + 1] == player) {
                Node[][] nm = nodeMatrix(b);
                nm[point.x][point.y] = new Node(point.x, point.y, player);
                if (!floodFill(nm, nm[point.x][point.y + 1], player, new HashSet<>())) {
                    return false;
                }
            } else {
                return false;
            }
        }

        if (point.y - 1 >= 0) {
            if (matrix[point.x][point.y - 1] == otherPlayer(player)) {
                Node[][] nm = nodeMatrix(b);
                nm[point.x][point.y] = new Node(point.x, point.y, player);
                if (floodFill(nm, nm[point.x][point.y - 1], otherPlayer(player), new HashSet<>())) {
                    return false;
                }
            } else if (matrix[point.x][point.y - 1] == player) {
                Node[][] nm = nodeMatrix(b);
                nm[point.x][point.y] = new Node(point.x, point.y, player);
                if (!floodFill(nm, nm[point.x][point.y - 1], player, new HashSet<>())) {
                    return false;
                }
            } else {
                return false;
            }
        }
        return true;
    }

    /**
     * Creates a new Board with its matrix and introduces the desired change/placement on the board.
     * Then it checks the changes affected by the placement and takes those into effect.
     * Lastly, finds out the new hashcode to check if it matches the previous' board hashcode.
     */
    private static boolean ruleKo(Board b, Point point, int player, int currentHash, int previousHash) {
        Board aux = new Board(Board.deepCopy(b.getMatrix()), currentHash);

        aux.addStone(point, player);

        int auxBlackPrisoners = blackPrisoners;
        int auxWhitePrisoners = whitePrisoners;

        checkPrisoners(aux, point, player);

        blackPrisoners = auxBlackPrisoners;
        whitePrisoners = auxWhitePrisoners;

        return aux.hashCode() == previousHash;
    }

    static boolean floodFill(Node[][] matrix, Node node, int target, Set<Point> congruentTerritory) {
        int replacement = BLACK ^ WHITE;
        Deque<Node> q = new LinkedList<>();

        node.color = replacement;
        congruentTerritory.add(new Point(node.x, node.y));

        q.offer(node);

        while (!q.isEmpty()) {
            Node n = q.poll();

            // West
            if (n.x - 1 >= 0) {
                Node west = matrix[n.x - 1][n.y];
                if (west.color == target) {
                    west.color = replacement;
                    congruentTerritory.add(new Point(west.x, west.y));
                    q.offer(west);
                } else if (west.color == 0) {
                    return false;
                }
            }

            // East
            if (n.x + 1 < BOARD_SIZE) {
                Node east = matrix[n.x + 1][n.y];
                if (east.color == target) {
                    east.color = replacement;
                    congruentTerritory.add(new Point(east.x, east.y));
                    q.offer(east);
                } else if (east.color == 0) {
                    return false;
                }
            }

            // North
            if (n.y - 1 >= 0) {
                Node north = matrix[n.x][n.y - 1];
                if (north.color == target) {
                    north.color = replacement;
                    congruentTerritory.add(new Point(north.x, north.y));
                    q.offer(north);
                } else if (north.color == 0) {
                    return false;
                }
            }

            // South
            if (n.y + 1 < BOARD_SIZE) {
                Node south = matrix[n.x][n.y + 1];
                if (south.color == target) {
                    south.color = replacement;
                    congruentTerritory.add(new Point(south.x, south.y));
                    q.offer(south);
                } else if (south.color == 0) {
                    return false;
                }
            }
        }

        return true;
    }

    public static void initZobristTable() {
        // fill the table with random numbers/bitstrings
        Board.ZobristTable = new long[BOARD_SIZE][BOARD_SIZE][3];
        Random rand = new Random();
        rand.setSeed(System.currentTimeMillis());
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                for (int k = 0; k < 3; k++) {
                    Board.ZobristTable[i][j][k] = rand.nextLong();
                }
            }
        }
    }

    public int getBlackPrisoners() {
        return blackPrisoners;
    }

    public int getWhitePrisoners() {
        return whitePrisoners;
    }

    public int getWhitePoints() {
        return whitePrisoners + whiteTerritory;
    }

    public int getBlackPoints() {
        return blackPrisoners + blackTerritory;
    }

    public void calculateTerritory(Board b) {
        blackTerritory = 0;
        whiteTerritory = 0;

        int[][] matrix = b.getMatrix();
        Node[][] nm = nodeMatrix(b);

        for (int index = 0; index < BOARD_SIZE * BOARD_SIZE; index++) {

            int territoryFor;

            if (nm[index % BOARD_SIZE][index / BOARD_SIZE].color == 0) {

                Set<Point> congruentTerritory = new HashSet<>();

                floodFill(nm, nm[index % BOARD_SIZE][index / BOARD_SIZE], 0, congruentTerritory);

                boolean isNeutral = false;
                territoryFor = -1;

                for (Point p : congruentTerritory) {

                    // West
                    if (p.x - 1 >= 0) {
                        if (matrix[p.x - 1][p.y] == BLACK) {
                            if (territoryFor == WHITE) {
                                isNeutral = true;
                            } else {
                                territoryFor = BLACK;
                            }
                        } else if (matrix[p.x - 1][p.y] == WHITE) {
                            if (territoryFor == BLACK) {
                                isNeutral = true;
                            } else {
                                territoryFor = WHITE;
                            }
                        }
                    }

                    if (isNeutral) {
                        break;
                    }

                    // East
                    if (p.x + 1 < BOARD_SIZE) {
                        if (matrix[p.x + 1][p.y] == BLACK) {
                            if (territoryFor == WHITE) {
                                isNeutral = true;
                            } else {
                                territoryFor = BLACK;
                            }
                        } else if (matrix[p.x + 1][p.y] == WHITE) {
                            if (territoryFor == BLACK) {
                                isNeutral = true;
                            } else {
                                territoryFor = WHITE;
                            }
                        }
                    }

                    if (isNeutral) {
                        break;
                    }

                    // North
                    if (p.y - 1 >= 0) {
                        if (matrix[p.x][p.y - 1] == BLACK) {
                            if (territoryFor == WHITE) {
                                isNeutral = true;
                            } else {
                                territoryFor = BLACK;
                            }
                        } else if (matrix[p.x][p.y - 1] == WHITE) {
                            if (territoryFor == BLACK) {
                                isNeutral = true;
                            } else {
                                territoryFor = WHITE;
                            }
                        }
                    }

                    if (isNeutral) {
                        break;
                    }

                    // South
                    if (p.y + 1 < BOARD_SIZE) {
                        if (matrix[p.x][p.y + 1] == BLACK) {
                            if (territoryFor == WHITE) {
                                isNeutral = true;
                            } else {
                                territoryFor = BLACK;
                            }
                        } else if (matrix[p.x][p.y + 1] == WHITE) {
                            if (territoryFor == BLACK) {
                                isNeutral = true;
                            } else {
                                territoryFor = WHITE;
                            }
                        }
                    }

                    if (isNeutral) {
                        break;
                    }
                }

                if (!isNeutral) {
                    if (territoryFor == BLACK) {
                        blackTerritory += congruentTerritory.size();
                    } else if (territoryFor == WHITE) {
                        whiteTerritory += congruentTerritory.size();
                    }
                }
            }
        }
    }

    static Iterator<Board> getIterator(Board board, int nextPlayer, int currentHash, int previousHash) {
        return new boardNeighbors(board, nextPlayer, currentHash, previousHash);
    }

    private static class boardNeighbors implements Iterator<Board> {
        private Board board;
        private Board nextBoard;
        private int move;
        private int nextPlayer;
        private int previousHash;
        private int currentHash;

        public boardNeighbors(Board board, int nextPlayer, int currentHash, int previousHash) {
            this.board = board;
            this.move = 0;
            this.nextPlayer = nextPlayer;
            this.currentHash = currentHash;
            this.previousHash = previousHash;
            this.nextBoard = calculateNext();
        }

        @Override
        public boolean hasNext() {
            return this.nextBoard != null;
        }

        @Override
        public Board next() {
            Board aux = this.nextBoard;
            this.nextBoard = calculateNext();
            return aux;
        }

        private Board calculateNext() {
            boolean boardFinished = false;
            Board nextB = null;
            while (nextB == null && !boardFinished) {   // Iterate until a new board is found or no more boards exist;
                if (move >= BOARD_SIZE * BOARD_SIZE) {          // Were all possible moves looked at? Are we finished?
                    boardFinished = true;                       // Yes.
                } else {
                    Point p = new Point(this.move % BOARD_SIZE, this.move / BOARD_SIZE);
                    if (isValid(board, p, nextPlayer, this.currentHash, this.previousHash)) {
                        int[][] clone = deepCopy(this.board.getMatrix());
                        clone[this.move % BOARD_SIZE][this.move / BOARD_SIZE] = nextPlayer;
                        Board b = new Board(clone, currentHash ^= Board.ZobristTable[this.move % BOARD_SIZE][this.move / BOARD_SIZE][nextPlayer]);
                        b.setPreviousPlay(p);
                        nextB = b;
//                        this.nextPlayer = otherPlayer(this.nextPlayer);
                    }
                    this.move++;
                }
            }
            return nextB;
        }
    }

    private static int otherPlayer(int currentPlayer) {
        return currentPlayer == BLACK ? WHITE : BLACK;
    }

    static int[][] deepCopy(int[][] original) {
        if (original == null) {
            return null;
        }
        int[][] result = new int[original.length][];
        for (int i = 0; i < original.length; i++) {
            result[i] = Arrays.copyOf(original[i], original[i].length);
        }
        return result;
    }

    static int getCurrentBoardHashcode() {
        return currentBoardHashcode;
    }

    static int getPreviousBoardHashcode() {
        return previousBoardHashcode;
    }

    static class Node {
        int x;
        int y;
        int color;

        Node(int x, int y, int c) {
            this.x = x;
            this.y = y;
            this.color = c;
        }
    }

    static Node[][] nodeMatrix(Board b) {
        int[][] matrix = b.getMatrix();
        Node[][] ans = new Node[BOARD_SIZE][BOARD_SIZE];

        for (int index = 0; index < BOARD_SIZE * BOARD_SIZE; index++) {
            Node n = new Node(index % BOARD_SIZE, index / BOARD_SIZE, matrix[index % BOARD_SIZE][index / BOARD_SIZE]);
            ans[index % BOARD_SIZE][index / BOARD_SIZE] = n;
        }
        return ans;
    }
}
