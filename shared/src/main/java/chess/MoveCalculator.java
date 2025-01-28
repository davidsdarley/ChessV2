package chess;
import java.util.Collection;
import java.util.ArrayList;


public class MoveCalculator {
    //the move calculator returns available moves.
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
        //call the appropriate move function for the relevant piece
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
        //check left
        this.checkDir(0, -1, new ChessPosition(origin));
        //check right
        this.checkDir(0, 1, new ChessPosition(origin));
        //check up
        this.checkDir(1, 0, new ChessPosition(origin));
        //check down
        this.checkDir(-1, 0, new ChessPosition(origin));
    }
    public void bishopMoves(){
        //check upleft
        this.checkDir(1, -1, new ChessPosition(origin));
        //check upright
        this.checkDir(1, 1, new ChessPosition(origin));
        //check downleft
        this.checkDir(-1, -1, new ChessPosition(origin));
        //check downright
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

        //Check one ahead
        int row = origin.getRow();
        int col = origin.getColumn();
        ChessPosition target = new ChessPosition(row+direction, col);
        Result result = valid(target);
        if (result == Result.EMPTY){
            addPawnMove(target);
            //check doublemove
            target = new ChessPosition(target.getRow()+direction, target.getColumn());
            if(origin.getRow() == start && valid(target) == Result.EMPTY){
                addPawnMove(target);
            }
        }
        //check possible double on first move
        target = new ChessPosition(row+direction, col-1);
        if (valid(target) == Result.CAPTURE){
            addPawnMove(target);
        }
        target = new ChessPosition(row+direction, col+1);
        if (valid(target) == Result.CAPTURE){
            addPawnMove(target);
        }
        //Check diagonal capture
    }
    public void checkSpace(int row, int col, ChessPosition target){
        target.row += row;
        target.col += col;
        Result result = valid(target);

        if(result == Result.EMPTY || result == Result.CAPTURE){
            if(type != ChessPiece.PieceType.PAWN) {//normal!
                moves.add(new ChessMove(new ChessPosition(origin), new ChessPosition(target), null));
            }
            else if(result == Result.EMPTY){//Pawns...  Only Checks the straight ahead option
                addPawnMove(new ChessPosition(target));
            }
        }
        //if its OoB or BL0CKED I do nothing
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
        //if its OoB or BL0CKED I do nothing
    }

    public Result valid(ChessPosition pos){
        //look at the spot on the board we want to go

        //make sure it's in bounds
        if (pos.getRow() < 1 || pos.getRow() > 8 || pos.getColumn() < 1 || pos.getColumn() > 8 ){
            return Result.OoB;
        }
        //if it's empty, return EMPTY
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
        //if it's the end, add one of each promotion type
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
