package model;

import java.awt.*;
import java.io.Serializable;
import java.util.Arrays;


public class Board implements ModelConstants, Serializable {
    private int[][] matrix; // read-only
    private int hashCode;
    static long[][][] ZobristTable;
    private Point previousPlay;

    /**
     * New board from scratch.
     */
    public Board() {
        this.matrix = new int[BOARD_SIZE][BOARD_SIZE];
    }

    public Board(int[][] fileMatrix) {
        this.matrix = fileMatrix;
        this.hashCode = this.hashCode();
    }

    public Board(int[][] matrix, int hashCode) {
        this.matrix = matrix;
        this.hashCode = hashCode;
    }

    /**
     * Verifies that the stone to add falls inside the board and updates the hashcode value.
     * Rather than computing the hash for the entire board every time, the hash value of a
     * board can be updated simply by XORing out the bitstring(s) for positions that have changed.
     */
    public boolean addStone(Point point, int player) {
        if ((point.x < 0 || point.x >= BOARD_SIZE) || (point.y < 0 || point.y >= BOARD_SIZE)) {
            return false;
        } else {
            matrix[point.x][point.y] = player;
            hashCode ^= ZobristTable[point.x][point.y][player];
            return true;
        }
    }

    public void removeStone(Point point) {
        if ((point.x >= 0 && point.x < BOARD_SIZE) && (point.y >= 0 && point.y < BOARD_SIZE)) {
            hashCode ^= ZobristTable[point.x][point.y][matrix[point.x][point.y]];
            matrix[point.x][point.y] = 0;
        }
    }

    public int[][] getMatrix() {
        return matrix;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Board board = (Board) o;

        // return Arrays.deepEquals(matrix, board.matrix);

        // If it gets to this point, the 2 boards are the same or they are transpositions of each other.
        return hashCode == board.hashCode;
    }

    /**
     * Zobrist Hashing for transposition boards.
     */
    @Override
    public int hashCode() {
        if (hashCode == 0) {
            for (int i = 0; i < BOARD_SIZE; i++) {
                for (int j = 0; j < BOARD_SIZE; j++) {
                    if (matrix[i][j] != 0) {
                        hashCode ^= ZobristTable[i][j][matrix[i][j]];
                    }
                }
            }
        }
        return hashCode;
    }

    public Point getPreviousPlay() {
        return previousPlay;
    }

    void setPreviousPlay(Point previousPlay) {
        this.previousPlay = previousPlay;
    }

    static int[][] deepCopy(int[][] original) {
        if (original == null) {
            return null;
        }
        final int[][] result = new int[original.length][];
        for (int i = 0; i < original.length; i++) {
            result[i] = Arrays.copyOf(original[i], original[i].length);
        }
        return result;
    }
}
