package Controller;

import Model.PopUp;
import Model.Tile;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import Model.EmptyTile;
import Model.FullTile;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static javafx.scene.input.KeyCode.*;

public class GridController {
    @FXML   private GridPane grid;
    private int dim;
    private List<Integer> checker;
    EmptyTile empty;

    /**
     * Initialize the grid
     */

    @FXML   void initialize() {
        grid.getStyleClass().add("grid");
        preStart();
    }

    public void preStart() {
        dim = PopUp.setSize();
        grid.getChildren().clear();
        grid.setMaxWidth(dim * 50);
        grid.setMaxHeight(dim * 50);
        play();
    }

    private void play() {
        checker = IntStream.range(1, (int) Math.pow(dim, 2) + 1).boxed().collect(Collectors.toList());

        /* ---- Randomly set the empty cell position ---- */
        Random dice = new Random();

        int randomCol = dice.nextInt(dim);
        int randomRow = dice.nextInt(dim);

        empty = new EmptyTile(randomCol, randomRow, (int) Math.pow(dim, 2));
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

    public void arrowHandler(KeyCode e) {
        HashMap<KeyCode, Node> neighbors = new HashMap<>();
        int index = dim * empty.getCol() + empty.getRow();

        Object[][] matrix = new Object[dim][dim];

        List<Object> nodes = Arrays.asList(grid.getChildren().toArray());

        /* ---- Get a copy of the grid as matrix with no layout constraints ---- */
        for (Object o : nodes)
            matrix[((Tile) o).getCol()][((Tile) o).getRow()] = o;

        /* ---- Get the direct neighbors of the empty cell if there is any */
        if (empty.getRow() + 1 < dim) neighbors.put(UP, ((Tile) matrix[empty.getCol()][empty.getRow() + 1]));
        if (empty.getRow() - 1 >= 0)  neighbors.put(DOWN, ((Tile) matrix[empty.getCol()][empty.getRow() - 1]));
        if (empty.getCol() + 1 < dim) neighbors.put(LEFT, ((Tile) matrix[empty.getCol() + 1][empty.getRow()]));
        if (empty.getCol() - 1 >= 0) neighbors.put(RIGHT, ((Tile) matrix[empty.getCol() - 1][empty.getRow()]));

        /* ---- Swap the empty cell and the neighbor linked to the pressed arrow key ---- */
        if (neighbors.containsKey(e))
            swap(((FullTile) neighbors.get(e)), empty);
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
         */

    }

    private void checkConfig(int row, int col) {
        if (row == dim - 1 && col == dim - 1) {
            int[][] matrix = new int[dim][dim];

            List<Object> nodes = Arrays.asList(grid.getChildren().toArray());

            for (Object o : nodes)
                matrix[((Tile) o).getRow()][((Tile) o).getCol()] = ((Tile) o).getValue();

            if (Arrays.toString(Arrays.stream(matrix).flatMapToInt(Arrays::stream).toArray()).equals(Arrays.toString(checker.toArray()))) {
                replay(true);
            }
        }
    }

    public void replay(boolean won) {
        if (PopUp.replay(won)) play();
        else Platform.exit();
    }
}
