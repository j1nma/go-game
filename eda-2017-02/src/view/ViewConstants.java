package view;

import javafx.geometry.Insets;

public interface ViewConstants {

    // 13 lines and 12 cells in a column
    int BOARD_SIZE = 13;

    int PLAYER_1_STONE = 1;
    int PLAYER_2_STONE = 2;

    int BACKGROUND_SIZE = 500;
    int BACKGROUND_GAUSSIAN_BLUR = 5;
    int LOGO_WIDTH = 200;
    String BACKGROUND_PATH = "res/images/background.jpg";
    String LOGO_PATH = "res/images/GoMenuLogo.png";

    int GAME_BUTTON_HEIGHT = 35;
    int GAME_BUTTON_WIDTH = 120;

    double BOTTOM_BOX_HEIGHT = GAME_BUTTON_HEIGHT * 1.5;

    int BORDER_MARGIN = 20;
    int DOUBLE_BORDER_MARGIN = BORDER_MARGIN * 2;

    int FONT_SIZE_MENU = 30;
    int FONT_SIZE_EDITOR = 13;
    int FONT_SIZE_GAME = 20;

    int INPUT_WIDTH = 75;
    int INPUT_HEIGHT = 10;

    int NODE_SEPARATION = 10;

    int BOARDSIZE_LIMIT = 8;

    double BLOCKVIEW_HEIGHT = 40.0;
    double BLOCKVIEW_WIDTH = 40.0;

    Insets TOPBOX_PADDING = new Insets(15, 15, 0, 15);
    Insets BOTTOMBOX_PADDING = new Insets(0, 0, 5, 0);

    double BUTTON_OPACITY = 0.6;
    Insets BUTTON_PADDING = new Insets(10, 10, 10, 10);
    String BUTTON_STYLE_DEFAULT = "-fx-base: black;-fx-focus-color: transparent; -fx-faint-focus-color: transparent;";
    String BUTTON_STYLE_ENTERED = "-fx-base: white;-fx-focus-color: transparent; -fx-faint-focus-color: transparent;";
    String PLAYER_BUTTON_STYLE = "-fx-color: -fx-base;-fx-focus-color: transparent; -fx-faint-focus-color: transparent;";

    String GAMEPANE_STYLE = "-fx-background-color: lightblue";

    String BLACK_STONE_PATH = "res/images/black_stone.png";
    String WHITE_STONE_PATH = "res/images/white_stone.png";

}
