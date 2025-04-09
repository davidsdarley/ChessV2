package chess;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {
    int row;
    int col;

    public ChessPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }
    public ChessPosition(ChessPosition other){
        this.row = other.getRow();
        this.col = other.getColumn();
    }
    public int getRow() {return this.row;}

    /**
     * @return which column this position is in
     * 1 codes for the left row
     */
    public int getColumn() {return this.col;}

    @Override
    public int hashCode(){
        return (this.row*10 + this.col);
    }
    @Override
    public boolean equals(Object other){
        if (other.getClass() != ChessPosition.class){
            return false;
        }
        ChessPosition obj = (ChessPosition)other;
        if(obj.getRow() == row && obj.getColumn() == col){
        return true;}
        return false;
    }
    @Override
    public String toString(){
        String[] options = {"a", "b", "c", "d", "e", "f", "g", "h"};

        return options[col-1]+""+row;
    }
}
