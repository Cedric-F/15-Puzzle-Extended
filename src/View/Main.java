package View;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        BorderPane root = FXMLLoader.load(getClass().getResource("BorderPane.fxml"));
        GridPane grid = FXMLLoader.load(getClass().getResource("Grid.fxml"));

        Menu game = new Menu("Game");
        game.getItems().add(new MenuItem("Restart"));
        game.getItems().add(new MenuItem("Change dimensions"));
        MenuBar menu = new MenuBar();
        menu.getMenus().add(game);

        root.setTop(menu);
        root.setCenter(grid);
        Scene scene = new Scene(root, 500, 500);
        scene.getStylesheets().add("grid.css");

        primaryStage.setTitle("Slide blocks puzzle");
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setResizable(false);
    }


    public static void main(String[] args) {
        launch(args);
    }
}
