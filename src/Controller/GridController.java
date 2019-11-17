package Controller;

import Model.Replay;
import Model.Tile;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import Model.EmptyTile;
import Model.FullTile;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GridController {
    @FXML   private GridPane grid;

    /**
     * Initialize the grid
     */

    @FXML   void initialize() {
        grid.getChildren().removeAll();
        grid.getStyleClass().add("grid");

        /* ---- Randomly set the empty cell position ---- */
        Random dice = new Random();

        int randomCol = dice.nextInt(4);
        int randomRow = dice.nextInt(4);

        EmptyTile empty = new EmptyTile(randomCol, randomRow);
        empty.getStyleClass().add("empty");

        /* ---- Generate a list of numbers from 1 to 24 ---- */
        List<Integer> numbers = IntStream.range(1, 25).boxed().collect(Collectors.toList());

        /* ---- Fill the grid with cells, and give them a random item from the list of numbers above ---- */
        for (int col = 0; col < 5; col++) {
            for (int row = 0; row < 5; row++) {
                if (col == randomCol && row == randomRow) { // place the cell when reaching it's coordinates
                    grid.add(empty, randomCol, randomRow);
                } else { // place all the other cells
                    int number = dice.nextInt(numbers.size());
                    int value = numbers.get(number);
                    numbers.remove(number); // this avoids duplicate values

                    FullTile cell = new FullTile(col, row, value);
                    Label cellValue = new Label("" + value);

                    cellValue.getStyleClass().add("text");

                    cell.setAlignment(Pos.CENTER);
                    cell.getChildren().add(cellValue);
                    cell.setPrefWidth(50);
                    cell.setPrefHeight(50);
                    cell.getStyleClass().add("cell");

                    /* ---- Add a click listener on the cells ---- */
                    cell.setOnMouseClicked(e -> swap(cell, empty));

                    grid.add(cell, col, row);
                }
            }
        }
    }

    /**
     * Check if the empty cell is right next to the clicked one.
     * If it is, swap them both.
     * @param cell the clicked cell
     * @param empty the empty cell
     */

    private void swap(FullTile cell, EmptyTile empty) {

        /* ---- Clicked coordinates ---- */
        int row = cell.getRow();
        int col = cell.getCol();

        /* ---- Distance between the empty cell's and clicked cell's positions ---- */
        int rowOffset = Math.abs(row - empty.getRow());
        int colOffset = Math.abs(col - empty.getCol());

        /* ---- If they are next to each other, remove them from the grid and add them back to their new position ---- */
        if (rowOffset == 0 && colOffset == 1 || rowOffset == 1 && colOffset == 0) {
            grid.getChildren().remove(cell);
            grid.getChildren().remove(empty);
            cell.setRow(empty.getRow());
            cell.setCol(empty.getCol());
            cell.setOnMouseClicked(e -> swap(cell, empty));
            grid.add(cell, cell.getCol(), cell.getRow());
            empty.setRow(row);
            empty.setCol(col);
            grid.add(empty, empty.getCol(), empty.getRow());
        }
        /*
         * TODO: Swap a whole line
         * TODO: Display replay popup
         */

    }
}
