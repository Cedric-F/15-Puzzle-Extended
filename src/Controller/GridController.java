package Controller;

import Model.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static javafx.scene.input.KeyCode.*;

public class GridController {
    @FXML   private GridPane grid;
    private int dim;
    private List<Integer> checker;
    private EmptyTile empty;

    /**
     * Called immediately upon render
     */

    @FXML   void initialize() {
        grid.getStyleClass().add("grid");
        preStart();
    }

    /**
     * On launch, ask the user for a grid size between 3x3 and 10x10
     * The condition to win the game is set here
     * We clear the grid's children to start anew
     */

    public void preStart() {
        System.out.println(grid.getParent());
        dim = PopUp.setSize();

        checker = IntStream.range(1, (int) Math.pow(dim, 2) + 1).boxed().collect(Collectors.toList());

        grid.getChildren().clear();
        grid.setMaxWidth(dim * 50);
        grid.setMaxHeight(dim * 50);
        play();
    }

    /**
     * Generate a list of values from 1 to dim, which will be randomly assigned to the tiles
     * The tiles are randomly placed, and receive a click event listener to handle the user inputs
     */

    private void play() {
        boolean solvable;
        do {
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
            solvable = isSolvable(flatMatrix(gridAsMatrix()));
            System.out.println(solvable ? "Solvable" : "Pas solvable");
        } while (!solvable);
    }

    /**
     * Is triggered when pressing an arrow key.
     * When an arrow is pressed, check if the empty tile has a neighbor at the related direction.
     * If so, swap them
     * @param e is the pressed key code
     */

    public void arrowHandler(KeyCode e) {
        HashMap<KeyCode, Node> neighbors = new HashMap<>();

        Node[][] matrix = new Node[dim][dim];

        /* ---- Get a copy of the grid as matrix with no layout constraints ---- */
        for (Node o : grid.getChildren())
            matrix[((Tile) o).getCol()][((Tile) o).getRow()] = o;

        /* ---- Get the direct neighbor of the empty cell if there is any */
        if (empty.getRow() + 1 < dim) neighbors.put(UP, matrix[empty.getCol()][empty.getRow() + 1]);
        if (empty.getRow() - 1 >= 0)  neighbors.put(DOWN, matrix[empty.getCol()][empty.getRow() - 1]);
        if (empty.getCol() + 1 < dim) neighbors.put(LEFT, matrix[empty.getCol() + 1][empty.getRow()]);
        if (empty.getCol() - 1 >= 0) neighbors.put(RIGHT, matrix[empty.getCol() - 1][empty.getRow()]);

        /* ---- Swap the empty cell and the neighbor linked to the pressed arrow key ---- */
        if (neighbors.containsKey(e))
            swap(((FullTile) neighbors.get(e)), empty);
    }

    /**
     * Check if the empty cell is right next to the selected one.
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
    }

    /**
     * Whenever the empty tile reaches the bottom right corner (only way one can win)
     * Create a matrix representation of the grid's children (which is a List, so we need to bypass the layout constraint)
     * Convert this 2d array into a string (e.g: "[[1, 2 3], [4, 5, 6], [7, 8, 9]]") and compare it to a finite ordered set of numbers
     * @param row The new row value of the empty tile upon swap
     * @param col The new col value of the empty tile upon swap
     */

    private void checkConfig(int row, int col) {
        if (row == dim - 1 && col == dim - 1) { // bottom right corner
            int[][] matrix = gridAsMatrix();


            /* ---- If there is a match with the winning condition, finish the game and ask for a rematch ---- */
            if (Arrays.toString(flatMatrix(matrix)).equals(Arrays.toString(checker.toArray()))) {
                    replay(true);
            }
        }
    }

    private int[] flatMatrix(int[][] matrix) {
        return Arrays.stream(matrix).flatMapToInt(Arrays::stream).toArray();
    }

    private int[][] gridAsMatrix() {
        int[][] matrix = new int[dim][dim];

        /* ---- Add each element of the grid to the matrix, at their corresponding location ---- */
        for (Node o : grid.getChildren())
            matrix[((Tile) o).getRow()][((Tile) o).getCol()] = ((Tile) o).getValue();

        return matrix;
    }

    /**
     * Offers a rematch. If the answer is false, close the game
     * @param won defines whether the restart follows a win or a menu input so it doesn't ask confirmation when false
     */

    public void replay(boolean won) {
        if (PopUp.replay(won)) play();
        else Platform.exit();
    }


    /**
     * The puzzle is solvable only if the parity of the permutation is identical to the parity of the empty tile.
     * https://en.wikipedia.org/wiki/15_puzzle
     *
     * @param matrix the grid in a flat array
     * @return true if the puzzle is solvable
     */
    private boolean isSolvable(int[] matrix) {
        int parity = 0;

        for (int i = 0; i < matrix.length; i++)
            if (matrix[i] != empty.getValue()) // unless we reach the empty tile
                for (int j = i + 1; j < matrix.length; j++) // check the pair (i, j) for transpositions
                    if (matrix[i] > matrix[j] && matrix[j] != empty.getValue()) //
                        parity++;

        return (dim % 2 == 0)
                ? (empty.getRow() + 1 % 2 == 0) == (parity % 2 == 0)
                : parity % 2 == 0;
    }
}
