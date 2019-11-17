package Model;

import javafx.scene.layout.TilePane;

abstract public class Tile extends TilePane {
    int col;
    int row;
    int value;

    public abstract int getCol();
    public abstract int getRow();
    public abstract int getValue();
}
