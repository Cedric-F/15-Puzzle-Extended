package Model;

import Controller.GridController;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class PopUp {

    private static boolean answer;
    private static int dimensions;

    public static boolean replay(boolean won) {
        if (!won) return true;
        Stage window = new Stage();

        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("You win!");
        window.setWidth(300);
        window.setHeight(150);
        Label message = new Label();
        message.setText("Play again?");

        /* ---- Buttons ---- */
        Button play = new Button("Yes");
        Button close = new Button("No");

        play.setOnAction(e -> {
            answer = true;
            window.close();
        });
        close.setOnAction(e -> {
            answer = false;
            window.close();
        });

        VBox layout = new VBox(30);
        HBox buttons = new HBox(20);
        buttons.getChildren().addAll(play, close);
        layout.getChildren().addAll(message, buttons);
        layout.setAlignment(Pos.CENTER);
        buttons.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();

        return answer;
    }

    public static int setSize() {
        Stage window = new Stage();

        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Settings");
        window.setWidth(300);
        window.setHeight(150);
        Label message = new Label();
        message.setText("Select the puzzle dimensions");

        TextField input = new TextField();
        input.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*")) {
                    input.setText(newValue.replaceAll("[^\\d]", ""));
                } else {
                    dimensions = Integer.parseInt(newValue);
                }
            }
        });

        Button play = new Button("OK");

        play.setOnAction(e -> {
            if (dimensions >= 3 && dimensions <= 10) {
                window.close();
            }
        });

        VBox layout = new VBox(10);
        layout.getChildren().addAll(message, input, play);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();

        return dimensions;
    }
}
