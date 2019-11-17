package View;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        //AnchorPane root = FXMLLoader.load(getClass().getResource("Anchor.fxml"));
        GridPane grid = FXMLLoader.load(getClass().getResource("Grid.fxml"));
        //root.getChildren().add(grid);
        Scene scene = new Scene(grid, 240, 240);
        scene.getStylesheets().add("grid.css");

        primaryStage.setTitle("Taquin");
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setResizable(false);
    }


    public static void main(String[] args) {
        launch(args);
    }
}
