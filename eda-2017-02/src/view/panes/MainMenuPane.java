package view.panes;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import view.MenuButton;

public class MainMenuPane extends GameMenuPane {
	private ImageView logo;
	private MenuButton btnPlay;
	private MenuButton btnExit;

	public MainMenuPane() {
		logo = new ImageView(new Image(LOGO_PATH));
		logo.setFitWidth(LOGO_WIDTH);
		btnPlay = new MenuButton("Play");
		btnExit = new MenuButton("Exit");

		list.getChildren().addAll(logo, btnPlay, btnExit);
	}

	public boolean isPlayPressed() {
		return btnPlay.isPressed();
	}

	public boolean isExitPressed() {
		return btnExit.isPressed();
	}
}
