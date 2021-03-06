package view.panes;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import view.ViewConstants;

/**
 * GameMenuPane is a StackPane with a background image on the back and a VBox
 * list of Nodes (MenuButtons and/or Canvas) on the front.
 *
 */
public abstract class GameMenuPane extends StackPane implements ViewConstants {
	protected VBox list;

	public GameMenuPane(Node... children) {
		list = new VBox(NODE_SEPARATION, children);

		list.setAlignment(Pos.CENTER);
		list.setPrefWidth(BACKGROUND_SIZE);
		list.setPrefHeight(BACKGROUND_SIZE);
		this.setHeight(BACKGROUND_SIZE);
		this.setWidth(BACKGROUND_SIZE);

		ImageView menuImage = new ImageView(new Image(BACKGROUND_PATH));
		menuImage.setEffect(new GaussianBlur(BACKGROUND_GAUSSIAN_BLUR));

		this.getChildren().addAll(menuImage, list);
	}
}
