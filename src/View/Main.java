package View;

import Controller.GridController;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        BorderPane root = FXMLLoader.load(getClass().getResource("BorderPane.fxml"));
        FXMLLoader gridLoader = new FXMLLoader(getClass().getResource("Grid.fxml"));
        GridPane grid = gridLoader.load();
        GridController gridController = gridLoader.getController();

        MenuBar menu = new MenuBar();

        Menu game = new Menu("Game");
        game.setStyle("-fx-font-size: 12");

        MenuItem restart = new MenuItem("Restart");
        restart.setOnAction(e -> gridController.replay(false));

        MenuItem changeSize = new MenuItem("Change dimensions");
        changeSize.setOnAction(e -> gridController.preStart());

        MenuItem quit = new MenuItem("Quit...");
        quit.setOnAction(e -> System.exit(0));

        SeparatorMenuItem separator = new SeparatorMenuItem();

        game.getItems().addAll(restart, changeSize, separator, quit);

        menu.getMenus().add(game);

        root.setTop(menu);
        root.setCenter(grid);
        Scene scene = new Scene(root, 500, 500);
        scene.getStylesheets().add("grid.css");

        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                gridController.arrowHandler(event.getCode());
            }
        });

        primaryStage.setTitle("Slide blocks puzzle");
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setResizable(false);
    }


    public static void main(String[] args) {
        launch(args);
    }
}
