package controller.handlers;

import controller.GameStateManager;
import controller.states.GameState;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import model.*;
import view.panes.BoardPane;
import view.panes.GamePane;

import java.awt.*;

/**
 * Handles GamePane from GameState.
 */
public class GameHandler extends Handler<GamePane> implements ModelConstants {
    private Stage primaryStage;
    protected Board board;
    private BoardManager boardManager;
    private BoardPane grid;
    private boolean mouseWasPressed;
    private int playerToPlay;
    private boolean blackPassed;
    private boolean whitePassed;
    private boolean isWaitingForOther;
    private int maxTime;
    private int depth;
    private boolean pruneActivated;
    private boolean treeActivated;

    public GameHandler(GameStateManager gsm, GameState state, Stage primaryStage, int maxTime, int depth, boolean pruneActivated, boolean treeActivated) {
        super(gsm, state);
        this.primaryStage = primaryStage;
        this.board = state.getBoard();
        this.boardManager = new BoardManager();
        this.grid = pane.getGrid();
        this.playerToPlay = BLACK; // Black always starts.
        this.maxTime = maxTime;
        this.depth = depth;
        this.pruneActivated = pruneActivated;
        this.treeActivated = treeActivated;
    }

    @Override
    public void handle(long now) {

        if (pane.isPassPressed()) {
            Point currentMousePosition;

            if (!mouseWasPressed) {

//                 /*
                mouseWasPressed = true;

                pane.setPlayerLabel("White's turn");

                blackPassed = true;
                isWaitingForOther = true;

                MMTree mini = new MMTree();
                Board b = null;

                if (maxTime != 0) {
                    b = mini.minimaxWithMaxTime(board, WHITE, maxTime, pruneActivated, treeActivated);
                } else if (depth != 0) {
                    b = mini.minimaxWithDepth(board, WHITE, depth, pruneActivated, treeActivated);
                }

                if (b == null) {
                    whitePassed = true;
                    isWaitingForOther = true;
                    checkWinCondition(board);
                } else {
                    blackPassed = false;
                    isWaitingForOther = false;
                    boardManager.addStone(board, b.getPreviousPlay(), WHITE);
                    currentMousePosition = b.getPreviousPlay();

                    pane.refresh(currentMousePosition);
                    pane.setBlackPrisoners(boardManager.getBlackPrisoners());
                    pane.setWhitePrisoners(boardManager.getWhitePrisoners());
                    pane.setPlayerLabel("Black's turn");

                }

//                */

                /*

                if (playerToPlay == BLACK) {
                    blackPassed = true;
                    playerToPlay = WHITE;
                    pane.setPlayerLabel("White's turn");
                } else {
                    whitePassed = true;
                    playerToPlay = BLACK;
                    pane.setPlayerLabel("Black's turn");
                }

                mouseWasPressed = true;
                isWaitingForOther = true;


                checkWinCondition(board);

//                */
            }
        }

        if (pane.isMousePressedOnGrid()) {
            processMouse();
            checkWinCondition(board);
        }

        if (!pane.isMousePressedOnGrid() && !pane.isPassPressed() && mouseWasPressed) {
            mouseWasPressed = false;
        }
    }

    private void processMouse() {
        Point currentMousePosition = grid.getCurrentMousePosition();

        if (!mouseWasPressed) {

            /*
            if (boardManager.addStone(board, new Point(currentMousePosition.x, currentMousePosition.y), playerToPlay)) {

                if (playerToPlay == BLACK) {

                    pane.setPlayerLabel("White's turn");
                    playerToPlay = WHITE;

                } else {

                    pane.setPlayerLabel("Black's turn");
                    playerToPlay = BLACK;
                }

                isWaitingForOther = false;

                pane.refresh(currentMousePosition);
                pane.setBlackPrisoners(boardManager.getBlackPrisoners());
                pane.setWhitePrisoners(boardManager.getWhitePrisoners());
            }

//            */

//            /*

            if (boardManager.addStone(board, new Point(currentMousePosition.x, currentMousePosition.y), BLACK)) {

                isWaitingForOther = false;

                pane.refresh(currentMousePosition);
                pane.setBlackPrisoners(boardManager.getBlackPrisoners());
                pane.setWhitePrisoners(boardManager.getWhitePrisoners());
                pane.setPlayerLabel("White's turn");

                MMTree mini = new MMTree();
                Board b = null;

                if (maxTime != 0) {
                    b = mini.minimaxWithMaxTime(board, WHITE, maxTime, pruneActivated, treeActivated);
                } else if (depth != 0) {
                    b = mini.minimaxWithDepth(board, WHITE, depth, pruneActivated, treeActivated);
                }

                if (b == null) {
                    whitePassed = true;
                    isWaitingForOther = true;
                    pane.setPlayerLabel("White passed");

                    checkWinCondition(board);
                } else {
                    blackPassed = false;
                    isWaitingForOther = false;
                    boardManager.addStone(board, b.getPreviousPlay(), WHITE);
                    currentMousePosition = b.getPreviousPlay();

                    pane.refresh(currentMousePosition);
                    pane.setPlayerLabel("Black's turn");
                }

                pane.setBlackPrisoners(boardManager.getBlackPrisoners());
                pane.setWhitePrisoners(boardManager.getWhitePrisoners());
            }

//            */

            mouseWasPressed = true;

        }
    }

    /**
     * Checks if a player won the game comparing the red car's position to the
     * exit one.
     *
     * @param board
     */
    private void checkWinCondition(Board board) {

        if (blackPassed && whitePassed) {

            boardManager.calculateTerritory(board);

            int blacks = boardManager.getBlackPoints();
            int whites = boardManager.getWhitePoints();

            if (blacks > whites) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        createAlert(AlertType.INFORMATION, "Black " + blacks + " | " + whites
                                + " White", "Black won!");
                        gsm.pop();
                        System.exit(0);

                    }
                });
            } else if (blacks < whites) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        createAlert(AlertType.INFORMATION, "Black " + blacks + " | " + whites
                                + " White", "White won!");
                        gsm.pop();
                        System.exit(0);

                    }
                });
            } else {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        createAlert(AlertType.INFORMATION, "", "Tied game.");
                        gsm.pop();
                        System.exit(0);

                    }
                });
            }

        } else {

            if (!isWaitingForOther) {
                blackPassed = false;
                whitePassed = false;
            }

        }


    }

    private void createAlert(AlertType type, String header, String context) {
        Alert insertAlert = new Alert(type);
        insertAlert.setHeaderText("Game finished: " + header);
        insertAlert.setContentText(context);
        insertAlert.showAndWait();
    }

}
