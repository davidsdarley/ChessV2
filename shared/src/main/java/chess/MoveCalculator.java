package chess;
import java.util.Collection;
import java.util.ArrayList;


public class MoveCalculator {
    Collection<ChessMove> moves;
    ChessPiece piece;
    ChessPosition origin;
    ChessPiece.PieceType type;
    ChessGame.TeamColor color;
    ChessPiece[][] board;

    public MoveCalculator(ChessPiece active, ChessPosition start, ChessPiece[][] board){
        piece = active;
        origin = start;
        type = active.getPieceType();
        color = active.getTeamColor();
        moves = new ArrayList<>();
        this.board = board;
    }

    public enum Result {
        EMPTY,
        BLOCKED,
        CAPTURE,
        OoB
    }

    public Collection<ChessMove> findMoves(){
        switch(type){
            case KING -> kingMoves();
            case QUEEN -> queenMoves();
            case PAWN -> pawnMoves();
            case ROOK -> rookMoves();
            case BISHOP -> bishopMoves();
            case KNIGHT -> knightMoves();
        }
        return moves;
    }

    public void kingMoves(){
        this.checkSpace(1, -1, new ChessPosition(origin));
        this.checkSpace(1, 0, new ChessPosition(origin));
        this.checkSpace(1, 1, new ChessPosition(origin));

        this.checkSpace(0, -1, new ChessPosition(origin));
        this.checkSpace(0, 1, new ChessPosition(origin));

        this.checkSpace(-1, -1, new ChessPosition(origin));
        this.checkSpace(-1, 0, new ChessPosition(origin));
        this.checkSpace(-1, 1, new ChessPosition(origin));
    }
    public void queenMoves(){
        rookMoves();
        bishopMoves();
    }
    public void rookMoves(){
        this.checkDir(0, -1, new ChessPosition(origin));
        this.checkDir(0, 1, new ChessPosition(origin));
        this.checkDir(1, 0, new ChessPosition(origin));
        this.checkDir(-1, 0, new ChessPosition(origin));
    }
    public void bishopMoves(){
        this.checkDir(1, -1, new ChessPosition(origin));
        this.checkDir(1, 1, new ChessPosition(origin));
        this.checkDir(-1, -1, new ChessPosition(origin));
        this.checkDir(-1, 1, new ChessPosition(origin));
    }
    public void knightMoves(){
        this.checkSpace(1, -2, new ChessPosition(origin));
        this.checkSpace(2, -1, new ChessPosition(origin));
        this.checkSpace(2, 1, new ChessPosition(origin));
        this.checkSpace(1, 2, new ChessPosition(origin));
        this.checkSpace(-1, 2, new ChessPosition(origin));
        this.checkSpace(-2, 1, new ChessPosition(origin));
        this.checkSpace(-2, -1, new ChessPosition(origin));
        this.checkSpace(-1, -2, new ChessPosition(origin));
    }
    public void pawnMoves(){
        int direction;
        if(color == ChessGame.TeamColor.WHITE){
            direction = 1;
        }
        else{
            direction = -1;
        }
        int start;
        if(color == ChessGame.TeamColor.WHITE){
            start = 2;
        }
        else{
            start = 7;
        }

        int row = origin.getRow();
        int col = origin.getColumn();
        ChessPosition target = new ChessPosition(row+direction, col);
        Result result = valid(target);
        if (result == Result.EMPTY){
            addPawnMove(target);
            target = new ChessPosition(target.getRow()+direction, target.getColumn());
            if(origin.getRow() == start && valid(target) == Result.EMPTY){
                addPawnMove(target);
            }
        }
        target = new ChessPosition(row+direction, col-1);
        if (valid(target) == Result.CAPTURE){
            addPawnMove(target);
        }
        target = new ChessPosition(row+direction, col+1);
        if (valid(target) == Result.CAPTURE){
            addPawnMove(target);
        }
    }
    public void checkSpace(int row, int col, ChessPosition target){
        target.row += row;
        target.col += col;
        Result result = valid(target);

        if(result == Result.EMPTY || result == Result.CAPTURE){
            if(type != ChessPiece.PieceType.PAWN) {
                moves.add(new ChessMove(new ChessPosition(origin), new ChessPosition(target), null));
            }
            else if(result == Result.EMPTY){
                addPawnMove(new ChessPosition(target));
            }
        }
    }
    public void checkDir(int row, int col, ChessPosition target){

        target.row += row;
        target.col += col;
        Result result = valid(target);

        if(result == Result.EMPTY){
            moves.add(new ChessMove(new ChessPosition(origin), new ChessPosition(target), null));
            this.checkDir(row, col, target);
        }
        if(result == Result.CAPTURE){
            moves.add(new ChessMove(new ChessPosition(origin), new ChessPosition(target), null));
        }
    }

    public Result valid(ChessPosition pos){

        if (pos.getRow() < 1 || pos.getRow() > 8 || pos.getColumn() < 1 || pos.getColumn() > 8 ){
            return Result.OoB;
        }
        if(board[pos.getRow()][pos.getColumn()] == null){
            return Result.EMPTY;
        }
        ChessPiece target = board[pos.getRow()][pos.getColumn()];
        if(target.getTeamColor() == color){
            return Result.BLOCKED;
        }
        else{
            return Result.CAPTURE;
        }
    }
    public void addPawnMove(ChessPosition pos){
        int target;
        if(color == ChessGame.TeamColor.WHITE){
             target = 8;
        }
        else{
             target = 1;
        }
        if(pos.getRow() == target){
            moves.add(new ChessMove(origin, pos, ChessPiece.PieceType.QUEEN));
            moves.add(new ChessMove(origin, pos, ChessPiece.PieceType.ROOK));
            moves.add(new ChessMove(origin, pos, ChessPiece.PieceType.BISHOP));
            moves.add(new ChessMove(origin, pos, ChessPiece.PieceType.KNIGHT));
        }
        else{
            moves.add(new ChessMove(origin, pos, null));
        }
    }
}
