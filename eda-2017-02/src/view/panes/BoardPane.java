package view.panes;

import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import view.ViewConstants;

import java.awt.*;

public class BoardPane extends GridPane implements ViewConstants {
    private boolean mouseIsPressed;
    private Point currentMousePosition;

    public BoardPane() {
        this.setHeight(BOARD_SIZE * BLOCKVIEW_HEIGHT);
        this.setWidth(BOARD_SIZE * BLOCKVIEW_WIDTH);
        this.setAlignment(Pos.CENTER);

        for (int i = 0; i < BOARD_SIZE; i++) {
            ColumnConstraints colConst = new ColumnConstraints(BLOCKVIEW_WIDTH);
            this.getColumnConstraints().add(colConst);

            RowConstraints rowConst = new RowConstraints(BLOCKVIEW_HEIGHT);
            this.getRowConstraints().add(rowConst);
        }

        this.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                mouseIsPressed = true;
                currentMousePosition = convertMouseToGridSquare(e);
            }
        });

        this.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent e) {
                mouseIsPressed = false;
            }
        });
    }

    private Point convertMouseToGridSquare(MouseEvent e) {
        Double gridSquareSizeX = BLOCKVIEW_WIDTH;
        Double gridSquareSizeY = BLOCKVIEW_HEIGHT;

        Double unprocessedX = (e.getX()) / gridSquareSizeX;
        Double unprocessedY = (e.getY()) / gridSquareSizeY;

        Integer gridPosX = unprocessedX.intValue();
        Integer gridPosY = unprocessedY.intValue();

        return new Point(gridPosX, gridPosY);
    }

    public Point getCurrentMousePosition() {
        return currentMousePosition;
    }

    public boolean isMousePressed() {
        return mouseIsPressed;
    }

}
