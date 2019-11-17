package Model;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class Replay {
    public static void popUp() {
        Stage window = new Stage();

        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("You win!");
        window.setWidth(300);
        window.setHeight(150);
        Label message = new Label();
        message.setText("Play again?");
        Button play = new Button("Yes");
        play.setOnAction(e -> window.close());
        VBox layout = new VBox(10);
        layout.getChildren().addAll(message, play);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();
    }
}
