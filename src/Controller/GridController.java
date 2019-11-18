package Controller;

import Model.PopUp;
import Model.Tile;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
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
    private int dim;
    private List<Integer> checker;

    /**
     * Initialize the grid
     */

    @FXML   void initialize() {
        grid.getChildren().removeAll();
        dim = PopUp.setSize();
        checker = IntStream.range(1, (int) Math.pow(dim, 2) + 1).boxed().collect(Collectors.toList());
        grid.setMaxWidth(dim * 50);
        grid.setMaxHeight(dim * 50);
        grid.getStyleClass().add("grid");

        /* ---- Randomly set the empty cell position ---- */
        Random dice = new Random();

        int randomCol = dice.nextInt(dim);
        int randomRow = dice.nextInt(dim);

        EmptyTile empty = new EmptyTile(randomCol, randomRow, (int) Math.pow(dim, 2));
        empty.getStyleClass().add("empty");

        /* ---- Generate a list of numbers from 1 to 24 ---- */
        List<Integer> numbers = IntStream.range(1, (int) Math.pow(dim, 2)).boxed().collect(Collectors.toList());

        /* ---- Fill the grid with cells, and give them a random item from the list of numbers above ---- */
        for (int col = 0; col < dim; col++) {
            for (int row = 0; row < dim; row++) {
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
            checkConfig(col, row);
        }
        /*
         * TODO: Swap a whole line
         * TODO: Display replay popup
         */

    }

    private void checkConfig(int row, int col) {
        if (row == dim - 1 && col == dim - 1) {
            int[][] matrix = new int[dim][dim];

            List<Object> nodes = Arrays.asList(grid.getChildren().toArray());

            for (Object o : nodes)
                matrix[((Tile) o).getRow()][((Tile) o).getCol()] = ((Tile) o).getValue();

            if (Arrays.toString(Arrays.stream(matrix).flatMapToInt(Arrays::stream).toArray()).equals(Arrays.toString(checker.toArray()))) {
                if (replay())
                    initialize();
                else
                    Platform.exit();
            }
        }
    }

    private boolean replay() {
        return PopUp.replay();
    }
}
