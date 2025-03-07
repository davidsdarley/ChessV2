package chess;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    ChessPiece [][] board;
    public ChessBoard() {
        board = new ChessPiece[9][9];
    }

    public ChessBoard(ChessBoard other){
        board = new ChessPiece[9][9];
        for (int row = 1; row < 9; row +=1){
            for (int col = 1; col < 9; col +=1 ){
                this.board[row][col] = other.board[row][col];
            }
        }
    }
    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        int row = position.getRow();
        int col = position.getColumn();
        board[row][col] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        int row = position.getRow();
        int col = position.getColumn();
        return board[row][col];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        board = new ChessPiece[9][9];
        this.setSide(ChessGame.TeamColor.WHITE);
        this.setSide(ChessGame.TeamColor.BLACK);
    }
    public void setSide(ChessGame.TeamColor teamColor){
        int row;
        if (teamColor == ChessGame.TeamColor.WHITE){row = 2;}
        else{row = 7;}
        for(int col = 8; col>0; col -=1){
            ChessPosition target = new ChessPosition(row, col);
            addPiece(target, new ChessPiece(teamColor, ChessPiece.PieceType.PAWN));
        }

        if (teamColor == ChessGame.TeamColor.WHITE){row = 1;}
        else{row = 8;}
        addPiece(new ChessPosition(row, 1), new ChessPiece(teamColor, ChessPiece.PieceType.ROOK));
        addPiece(new ChessPosition(row, 8), new ChessPiece(teamColor, ChessPiece.PieceType.ROOK));

        addPiece(new ChessPosition(row, 2), new ChessPiece(teamColor, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(row, 7), new ChessPiece(teamColor, ChessPiece.PieceType.KNIGHT));

        addPiece(new ChessPosition(row, 3), new ChessPiece(teamColor, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(row, 6), new ChessPiece(teamColor, ChessPiece.PieceType.BISHOP));

        addPiece(new ChessPosition(row, 4), new ChessPiece(teamColor, ChessPiece.PieceType.QUEEN));
        addPiece(new ChessPosition(row, 5), new ChessPiece(teamColor, ChessPiece.PieceType.KING));
    }


    public static void main(String[] args){
        ChessBoard test = new ChessBoard();
        test.resetBoard();
        ChessPosition pos = new ChessPosition(5,5);
        ChessPiece piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
        test.addPiece(pos, piece);

        System.out.println(piece.pieceMoves(test, pos));
        System.out.println(test);
    }

    @Override
    public String toString(){
        String str = "";
        for(int row=8; row > 0; row -=1){
            str += ("|");
            for(int col = 1; col < 9; col +=1) {
                if (board[row][col] == null){
                str +=(" |");
                }
                else{
                    str+= board[row][col].toString();
                }
            }
            str += "\n";
        }
        return str;
    }

    @Override
    public boolean equals(Object obj){
        if(obj.getClass() != ChessBoard.class){
            return false;
        }
        ChessBoard other = (ChessBoard) obj;
        for(int row=8; row >= 0; row -=1){
            for(int col = 0; col < 9; col +=1) {
                if (other.board[row][col] == null || this.board[row][col] == null) {
                    if(! (this.board[row][col] == null && other.board[row][col] == null)){
                        return false;
                    }
                }
                else if(! this.board[row][col].equals(other.board[row][col]) ){
                    return false;
                }
            }
        }

        return true;
    }
    @Override
    public int hashCode(){
        return 1;
    }
}
