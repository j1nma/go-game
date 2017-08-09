import controller.GameStateManager;
import controller.states.GameState;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import model.Board;
import model.BoardManager;
import model.MMTree;
import utils.CommandParser;
import view.ScreenManager;

/**
 * Game sets the primary stage: loads the background image, creates a game
 * state manager and a screen manager. MainMenuState is pushed as the first state.
 */
public class Game extends Application {
    private GameStateManager gsm;
    private ScreenManager screenManager;
    private static int maxTime;
    private static int depth;
    private static boolean pruneActivated;
    private static boolean treeActivated;


    @Override
    public void start(Stage primaryStage) throws Exception {
        screenManager = new ScreenManager(primaryStage, "Go");
        gsm = new GameStateManager(screenManager);
        gsm.push(new GameState(gsm, primaryStage, maxTime, depth,pruneActivated,treeActivated));
        primaryStage.getIcons().add(new
                Image(this.getClass().getResourceAsStream("res/images/icon.png")));
        primaryStage.show();
    }

    public static void main(String[] args) {

        CommandParser.parse(args);
        maxTime = CommandParser.getMaxTime();
        depth = CommandParser.getDepth();
        pruneActivated = CommandParser.pruneActivated();
        treeActivated = CommandParser.treeActivated();

        if (!CommandParser.fileWasProvided()) {

            launch(args);

        } else {

            int[][] fileMatrix = CommandParser.getMatrix();
            int playerToPlay = CommandParser.getPlayerToPlay();
            BoardManager.initZobristTable();

            MMTree mmtree = new MMTree();

            if (maxTime != 0) {
                Board b = mmtree.minimaxWithMaxTime(new Board(fileMatrix), playerToPlay, maxTime, pruneActivated,
                        treeActivated);

                if (b == null) {
                    System.out.println("PASS");
                } else {
                    System.out.println("(" + b.getPreviousPlay().x + ", " + b.getPreviousPlay().y + ")");
                }

                System.exit(1);
            }

            if (depth != 0) {
                Board b = mmtree.minimaxWithDepth(new Board(fileMatrix), playerToPlay, depth, pruneActivated,
                        treeActivated);

                if (b == null) {
                    System.out.println("PASS");
                } else {
                    System.out.println("(" + b.getPreviousPlay().x + ", " + b.getPreviousPlay().y + ")");
                }

                System.exit(1);
            }


        }
    }
}
