package Model;

import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class PopUp {

    private static boolean answer;
    private static int dimensions;

    /**
     * Pop up modal to offer a rematch when the user won the last game
     * If triggered from the menu, the modal doesn't show and restart the game directly
     * @param won defines where the trigger comes from
     * @return true if starting a new game, or false to close
     */

    public static boolean replay(boolean won) {
        if (!won) return true;
        Stage window = new Stage();

        window.initModality(Modality.APPLICATION_MODAL);
        window.initStyle(StageStyle.UNDECORATED);
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
        input.textProperty().addListener((ObservableValue<? extends String> observable, String prev, String next) -> {
            if (!next.matches("\\d*"))
                input.setText(next.replaceAll("[^\\d]", ""));
            else if (next.length() > 0)
                dimensions = Integer.parseInt(next);
        });

        Button play = new Button("OK");

        play.setOnAction(e -> {
            if (dimensions >= 3 && dimensions <= 10)
                window.close();
        });

        VBox layout = new VBox(10);
        layout.getChildren().addAll(message, input, play);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();
        if (dimensions == 0) System.exit(0);
        return dimensions;
    }
}
