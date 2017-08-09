package controller.states;

import controller.GameStateManager;
import controller.handlers.GameHandler;
import javafx.stage.Stage;
import model.Board;
import model.ModelConstants;
import view.panes.GamePane;

public class GameState extends State implements ModelConstants {
    protected Board board;

    public GameState(GameStateManager gsm, Stage primaryStage, int maxTime, int depth, boolean pruneActivated, boolean treeActivated) {
        super(gsm);

        board = new Board();
        pane = new GamePane(board);
        handler = new GameHandler(gsm, this, primaryStage, maxTime, depth, pruneActivated, treeActivated);
    }

    public Board getBoard() {
        return board;
    }
}
