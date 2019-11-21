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
        dim = PopUp.setSize();

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
            grid.getChildren().removeAll(cell, empty);
            cell.setRow(empty.getRow());
            cell.setCol(empty.getCol());

            grid.add(cell, cell.getCol(), cell.getRow());
            empty.setRow(row);
            empty.setCol(col);
            grid.add(empty, empty.getCol(), empty.getRow());
            if (hasWon(col, row))
                replay(true);
        }
    }

    /**
     * Whenever the empty tile reaches the bottom right corner:
     * Obtain the transpositions left in the grid.
     * The game is won when there are no transpositions left.
     *
     * @param row The new row value of the empty tile upon swap
     * @param col The new col value of the empty tile upon swap
     */

    private boolean hasWon(int row, int col) {
        if (row == dim - 1 && col == dim - 1)
            return getTranspositions(flatMatrix(gridAsMatrix())) == 0;
        return false;
    }

    /**
     * This method flattens a multi dimensional array and returns it
     * @param matrix is a 2d array
     * @return a 1d array
     */

    private int[] flatMatrix(int[][] matrix) {
        return Arrays.stream(matrix).flatMapToInt(Arrays::stream).toArray();
    }

    /**
     * Convert the grid into a matrix with the correct layout
     * @return a 2d array
     */

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
     * You can say if a puzzle is solvable at the beginning.
     * - If the grid's width is odd, the transpositions (number of (i, j) couples where i > j) are even
     * - The the grid's width is even:
     *      - The transpositions are odd and the empty tile is on an even row, counting from the bottom
     *      - The transpositions are even and the empty tile on an odd row, counting from the bottom
     *
     *  A transposition occurs when on a flatten matrix, a number comes before a lesser number:
     *  For instance: {1, 2, 4, 3, 7, 5, 6, 8, '_'} where '_' is the empty tile
     *
     *  4 comes before 3. (1 transposition)
     *  7 comes before 5 and 6. (2 transposition)
     *
     *  A setup with no transposition would be a finite puzzle ({1, 2, 3, 4, 5, 6, 7, 8, '_'})
     *
     *  In the case above, the grid's width is odd (3 x 3) and the transpositions (parity) are odd, so this puzzle is not solvable
     *
     * @param matrix is the grid in a flat array
     * @return true if the puzzle is solvable
     */
    private boolean isSolvable(int[] matrix) {
        int parity = getTranspositions(matrix);

        return (dim % 2 == 0)
                ? ((dim - empty.getRow()) % 2 != parity % 2)
                : parity % 2 == 0;
    }

    /**
     * Iterate over each element and check if they come before any lesser number (case for a transposition)
     * Counts the transposed couples
     *
     * @param matrix the flatten grid
     * @return the number of transposed couples
     */

    private int getTranspositions(int[] matrix) {
        int parity = 0;

        for (int i = 0; i < matrix.length; i++)
            if (matrix[i] != empty.getValue())
                for (int j = i + 1; j < matrix.length; j++)
                    if (matrix[i] > matrix[j] && matrix[j] != empty.getValue())
                        parity++;

        return parity;
    }
}
