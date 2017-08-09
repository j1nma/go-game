package model;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

import static model.BoardManager.floodFill;
import static model.BoardManager.nodeMatrix;

public class HeuristicGo implements Heuristic, ModelConstants {

    @Override
    public int value(Board b) {
        int value = 0;
        value += (howManyEyes(b, WHITE) - howManyEyes(b, BLACK)) * 5;
        value += liberties(b, WHITE) - liberties(b, BLACK) * 1.5;
        value += (countPrisoners(b, WHITE) - countPrisoners(b, BLACK)) * 50;
        value += -(badTriangles(b, WHITE) - badTriangles(b, BLACK)) * 5;
        value += (knightMoves(b, WHITE) - knightMoves(b, BLACK)) / 3;
        value += (tigers(b, WHITE) - tigers(b, BLACK));
        value += (bamboo(b, WHITE) - bamboo(b, BLACK));
        value += -(dumplings(b, WHITE) - dumplings(b, BLACK)) * 2;
        return value;
    }

    private int dumplings(Board b, int player) {

        int total = 0;

        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (b.getMatrix()[i][j] == player) {
                    total += dumpling(b.getMatrix(), i, j, player);
                }
            }
        }
        return total;
    }

    private int dumpling(int[][] board, int i, int j, int player) {
        int dumplings = 0;

        if (isPlayersStone(board, i + 1, j, player) && isPlayersStone(board, i, j + 1, player)
                && isPlayersStone(board, i + 1, j + 1, player))
            dumplings++;

        return dumplings;
    }

    private int bamboo(Board b, int player) {

        int total = 0;

        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (b.getMatrix()[i][j] == player) {
                    total += bamboo(b.getMatrix(), i, j, player);
                }
            }
        }
        return total;
    }

    private int bamboo(int[][] board, int i, int j, int player) {
        int bamboos = 0;

        if (isPlayersStone(board, i + 2, j, player) && isPlayersStone(board, i + 2, j + 1, player)
                && isPlayersStone(board, i, j + 1, player))
            bamboos++;

        if (isPlayersStone(board, i + 1, j, player) && isPlayersStone(board, i, j + 2, player)
                && isPlayersStone(board, i + 1, j + 2, player))
            bamboos++;


        return bamboos;
    }


    private int tigers(Board b, int player) {

        int total = 0;

        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (b.getMatrix()[i][j] == player) {
                    total += tiger(b.getMatrix(), i, j, player);
                }
            }
        }
        return total;
    }

    private int tiger(int[][] board, int i, int j, int player) {
        int tiger = 0;

        if (isPlayersStone(board, i - 1, j - 1, player) && isPlayersStone(board, i + 1, j - 1, player))
            tiger++;

        if (isPlayersStone(board, i - 1, j + 1, player) && isPlayersStone(board, i - 1, j + 1, player))
            tiger++;

        if (isPlayersStone(board, i - 1, j + 1, player) && isPlayersStone(board, i + 1, j + 1, player))
            tiger++;

        if (isPlayersStone(board, i + 1, j - 1, player) && isPlayersStone(board, i + 1, j + 1, player))
            tiger++;

        return tiger;

    }


    private int knightMoves(Board b, int player) {

        int total = 0;

        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (b.getMatrix()[i][j] == player) {
                    total += knightMove(b.getMatrix(), i, j, player);
                }
            }
        }
        return total;
    }

    private int knightMove(int[][] board, int i, int j, int player) {
        int knights = 0;

        if (isPlayersStone(board, i + 1, j + 2, player))
            knights++;

        if (isPlayersStone(board, i + 1, j - 2, player))
            knights++;

        if (isPlayersStone(board, i - 1, j + 2, player))
            knights++;

        if (isPlayersStone(board, i - 1, j - 2, player))
            knights++;

        return knights;

    }

    private boolean isPlayersStone(int[][] board, int i, int j, int player) {
        return (i < BOARD_SIZE && i >= 0 && j < BOARD_SIZE && j >= 0 && board[i][j] == player);
    }

    private int badTriangles(Board b, int player) {

        int total = 0;

        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (b.getMatrix()[i][j] == player) {
                    total += badTriangle(b.getMatrix(), i, j, player);
                }
            }
        }
        return total;
    }

    private int badTriangle(int[][] board, int i, int j, int player) {
        int tri = 0;

        if (isUpStone(board, i, j, player) && isRightStone(board, i, j, player))
            tri++;
        if (isUpStone(board, i, j, player) && isLeftStone(board, i, j, player))
            tri++;

        if (isDownStone(board, i, j, player) && isRightStone(board, i, j, player))
            tri++;
        if (isDownStone(board, i, j, player) && isLeftStone(board, i, j, player))
            tri++;

        return tri;

    }

    private boolean isUpStone(int[][] board, int i, int j, int player) {
        return (i + 1 < BOARD_SIZE && j < BOARD_SIZE && board[i + 1][j] == player);
    }

    private boolean isRightStone(int[][] board, int i, int j, int player) {
        return (i < BOARD_SIZE && j + 1 < BOARD_SIZE && board[i][j + 1] == player);
    }

    private boolean isDownStone(int[][] board, int i, int j, int player) {
        return (i - 1 >= 0 && j < BOARD_SIZE && board[i - 1][j] == player);
    }

    private boolean isLeftStone(int[][] board, int i, int j, int player) {
        return (i < BOARD_SIZE && j - 1 >= 0 && board[i][j - 1] == player);
    }

    private int liberties(Board b, int player) {
        int total = 0;

        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (b.getMatrix()[i][j] == player) {
                    total += libertiesAtPoint(b.getMatrix(), i, j, player);
                }
            }
        }
        return total;
    }

    private int libertiesAtPoint(int[][] matrix, int i, int j, int player) {
        int total = 0;

        if ((i + 1 < BOARD_SIZE && matrix[i + 1][j] != otherPlayer(player))) {
            total++;
        }
        if ((i - 1 >= 0 && matrix[i - 1][j] != otherPlayer(player))) {
            total++;
        }
        if ((j + 1 < BOARD_SIZE && matrix[i][j + 1] != otherPlayer(player))) {
            total++;
        }
        if ((j - 1 >= 0 && matrix[i][j - 1] != otherPlayer(player))) {
            total++;
        }

        return total;
    }

    private int howManyEyes(Board b, int player) {
        int matrix[][] = b.getMatrix();
        int eyes = 0;

        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (matrix[i][j] == 0) {

                    // Corner eye

                    if (i == 0 && j == 0 && matrix[i + 1][j] == player && matrix[i][j + 1] == player)
                        eyes++;

                    if (i == BOARD_SIZE - 1 && j == BOARD_SIZE - 1 && matrix[i - 1][j] == player && matrix[i][j - 1] == player)
                        eyes++;

                    if (i == 0 && j == BOARD_SIZE - 1 && matrix[i + 1][j] == player && matrix[i][j - 1] == player)
                        eyes++;

                    if (i == BOARD_SIZE - 1 && j == 0 && matrix[i - 1][j] == player && matrix[i][j + 1] == player)
                        eyes++;


                    // Border eye

                    if (i == 0 && matrix[i + 1][j] == player
                            && (j - 1 >= 0 && matrix[i][j - 1] == player)
                            && (j + 1 < BOARD_SIZE && matrix[i][j + 1] == player))
                        eyes++;

                    if (i == BOARD_SIZE - 1 && matrix[i - 1][j] == player
                            && (j - 1 >= 0 && matrix[i][j - 1] == player)
                            && (j + 1 < BOARD_SIZE && matrix[i][j + 1] == player))
                        eyes++;

                    if (j == 0 && matrix[i][j + 1] == player
                            && (i - 1 >= 0 && matrix[i - 1][j] == player)
                            && (i + 1 < BOARD_SIZE && matrix[i + 1][j] == player))
                        eyes++;

                    if (j == BOARD_SIZE - 1 && matrix[i][j - 1] == player
                            && (i - 1 >= 0 && matrix[i - 1][j] == player)
                            && (i + 1 < BOARD_SIZE && matrix[i + 1][j] == player))
                        eyes++;


                    // Non-border eye

                    if ((i + 1 < BOARD_SIZE && matrix[i + 1][j] == player)
                            && (i - 1 >= 0 && matrix[i - 1][j] == player)
                            && (j + 1 < BOARD_SIZE && matrix[i][j + 1] == player)
                            && (j - 1 >= 0 && matrix[i][j - 1] == player))
                        eyes++;
                }
            }
        }
        return eyes;
    }


    private int countPrisoners(Board b, int player) {
        int[][] matrix = b.getMatrix();
        Point point = b.getPreviousPlay();
        int total = 0;
        BoardManager.Node[][] nm = nodeMatrix(b);

        if (point != null) {
            // West
            if (point.x - 1 >= 0 && matrix[point.x - 1][point.y] == otherPlayer(player)) {
                Set<Point> visited = new HashSet<>();
                if (floodFill(nm, nm[point.x - 1][point.y], otherPlayer(player), visited)) {
                    total += visited.size();
                }
            }

            nm = nodeMatrix(b);

            // East
            if (point.x + 1 < BOARD_SIZE) {
                if (matrix[point.x + 1][point.y] == otherPlayer(player)) {
                    Set<Point> visited = new HashSet<>();
                    if (floodFill(nm, nm[point.x + 1][point.y], otherPlayer(player), visited)) {
                        total += visited.size();
                    }
                }
            }

            nm = nodeMatrix(b);

            // South
            if (point.y + 1 < BOARD_SIZE && matrix[point.x][point.y + 1] == otherPlayer(player)) {
                Set<Point> visited = new HashSet<>();
                if (floodFill(nm, nm[point.x][point.y + 1], otherPlayer(player), visited)) {
                    total += visited.size();
                }
            }

            nm = nodeMatrix(b);

            // North
            if (point.y - 1 >= 0 && matrix[point.x][point.y - 1] == otherPlayer(player)) {
                Set<Point> visited = new HashSet<>();
                if (floodFill(nm, nm[point.x][point.y - 1], otherPlayer(player), visited)) {
                    total += visited.size();
                }
            }
        }
        return total;
    }

    private static int otherPlayer(int currentPlayer) {
        return currentPlayer == BLACK ? WHITE : BLACK;
    }
}
