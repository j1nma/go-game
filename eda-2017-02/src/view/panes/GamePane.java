package view.panes;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import model.Board;
import view.BoardView;
import view.ViewConstants;

import java.awt.*;

/**
 * GamePane is a BorderPane that has a surrender button and the game's grid. It
 * constructs a board view from the board received and assigns its grid to the
 * local BoardPane grid. It sets the grid in the center and the surrender button
 * at the top.
 */
public class GamePane extends BorderPane implements ViewConstants {
    private BoardView boardView;
    private Button passButton;
    private BoardPane grid;
    private HBox hTopBox;
    private HBox hBottomBox;
    private StackPane stackpane;
    private Button playerLabel;
    private Button blacks;
    private Button whites;

    public GamePane(Board board) {
        passButton = new Button("Pass");
        passButton.setFont(Font.font(FONT_SIZE_GAME));

        boardView = new BoardView(board);
        grid = boardView.getGrid();

        stackpane = new StackPane();
        ImageView woodBackground = new ImageView(new Image("res/images/woodBoardBackground.png"));
        woodBackground.setFitWidth(BLOCKVIEW_WIDTH * (BOARD_SIZE - 1));
        woodBackground.setFitHeight(BLOCKVIEW_HEIGHT * (BOARD_SIZE - 1));
        stackpane.getChildren().add(woodBackground);
        stackpane.getChildren().add(grid);

        hTopBox = new HBox(passButton);
        hTopBox.setAlignment(Pos.TOP_CENTER);
        hTopBox.setMinHeight(GAME_BUTTON_HEIGHT);
        hTopBox.setSpacing(NODE_SEPARATION);

        setPlayerLabel("Black's turn");

        blacks = new Button(0 + " prisoners");
        whites = new Button(0 + " prisoners");
        blacks.setFont(Font.font(FONT_SIZE_GAME));
        blacks.setStyle("-fx-background-color:black;" +
                "-fx-text-fill:white;");
        whites.setFont(Font.font(FONT_SIZE_GAME));
        whites.setStyle("-fx-background-color:white;" +
                "-fx-text-fill:black;");

        hBottomBox = new HBox(blacks, whites);
        hBottomBox.setAlignment(Pos.BOTTOM_CENTER);
        hBottomBox.setMinHeight(BOTTOM_BOX_HEIGHT);
        hBottomBox.setSpacing(NODE_SEPARATION);

        this.setCenter(stackpane);
        this.setTop(hTopBox);
        this.setBottom(hBottomBox);
        BorderPane.setMargin(hTopBox, new Insets(DOUBLE_BORDER_MARGIN, 0, 0, 0));
        BorderPane.setMargin(stackpane,
                new Insets(0, DOUBLE_BORDER_MARGIN, 0, DOUBLE_BORDER_MARGIN));
        BorderPane.setMargin(hBottomBox, new Insets(0, 0, DOUBLE_BORDER_MARGIN, 0));
        this.setStyle(GAMEPANE_STYLE);

        this.setHeight(grid.getHeight() + hTopBox.getMinHeight() + hBottomBox.getMinHeight() + DOUBLE_BORDER_MARGIN * 3);
        this.setWidth(grid.getWidth() + DOUBLE_BORDER_MARGIN * 2);
    }

    public boolean isPassPressed() {
        return passButton.isPressed();
    }

    public boolean isMousePressedOnGrid() {
        return grid.isMousePressed();
    }

    public BoardPane getGrid() {
        return grid;
    }

    public void refresh(Point p) {
        boardView.refresh(p);
        boardView.refresh();
    }

    public void setPlayerLabel(String label) {

        hTopBox.getChildren().remove(playerLabel);

        playerLabel = new Button(label);
        playerLabel.setFont(Font.font(FONT_SIZE_GAME));
        playerLabel.setFocusTraversable(false);
        playerLabel.setDisable(false);
        playerLabel.setStyle(PLAYER_BUTTON_STYLE);

        hTopBox.getChildren().add(playerLabel);

    }

    public void setBlackPrisoners(int blackPrisoners) {
        hBottomBox.getChildren().remove(blacks);

        blacks = new Button(blackPrisoners + " prisoners");
        blacks.setFont(Font.font(FONT_SIZE_GAME));
        blacks.setStyle("-fx-background-color:black;" +
                "-fx-text-fill:white;");

        hBottomBox.getChildren().add(blacks);

    }

    public void setWhitePrisoners(int whitePrisoners) {
        hBottomBox.getChildren().remove(whites);

        whites = new Button(whitePrisoners + " prisoners");
        whites.setFont(Font.font(FONT_SIZE_GAME));
        whites.setStyle("-fx-background-color:white;" +
                "-fx-text-fill:black;");

        hBottomBox.getChildren().add(whites);

    }

    public void setBoard(Board board) {
        this.boardView.setBoard(board);
    }
}
