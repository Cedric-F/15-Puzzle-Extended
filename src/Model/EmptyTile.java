package Model;

public class EmptyTile extends Tile {

    public EmptyTile(int c, int r, int v) {
        super();
        col = c;
        row = r;
        value = v;
    }


    @Override
    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    @Override
    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    @Override
    public int getValue() {
        return this.value;
    }
}
