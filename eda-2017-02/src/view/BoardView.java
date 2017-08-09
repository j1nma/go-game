package view;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import model.Board;
import view.panes.BoardPane;

import java.awt.*;
import java.util.HashMap;

/**
 * Graphical representation of PreviousBoard class.
 */
public class BoardView extends ObjectView<Board> {
    private BoardPane grid;
    private HashMap<Point, ImageView> nodes;

    public BoardView(Board board) {
        super(board);
        grid = new BoardPane();
        nodes = new HashMap<>();
    }

    public void refresh(Point p) {
        int[][] matrix = object.getMatrix();
        int cell = matrix[p.x][p.y];

        if (cell != 0) {
            ImageView imageview;

            if (cell == PLAYER_1_STONE) {
                imageview = new ImageView(new Image(BLACK_STONE_PATH));
            } else {
                imageview = new ImageView(new Image(WHITE_STONE_PATH));
            }

            imageview.setFitWidth(BLOCKVIEW_WIDTH);
            imageview.setFitHeight(BLOCKVIEW_HEIGHT);

            grid.add(imageview, p.x, p.y);
            nodes.put(new Point(p.x, p.y), imageview);
        }

    }

    public void refresh() {
        int[][] matrix = object.getMatrix();

        for (int index = 0; index < matrix.length * matrix.length; index++) {
            int cell = matrix[index % matrix.length][index / matrix.length];

            if (cell == 0) {

                grid.getChildren().removeAll(nodes.get(new Point(index % matrix.length, index / matrix.length)));
            }
        }
    }

    public BoardPane getGrid() {
        return this.grid;
    }

    public Node getNodeByRowColumnIndex(final int row, final int column, GridPane gridPane) {
        Node result = null;
        ObservableList<Node> childrens = gridPane.getChildren();

        for (Node node : childrens) {
            if (GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == column) {
                result = node;
                break;
            }
        }

        return result;
    }

    public void setBoard(Board board) {
        this.object = board;
    }
}
