package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * useful searching function for going through the board
 * for(int row = 1; row <9; row+=1){
 *             for(int col = 1; col < 9; col +=1){
 *
 *             }
 *         }
 */

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */

public class ChessGame {
    ChessBoard board;
    TeamColor turn;

    public ChessGame() {
        board = new ChessBoard();
        board.resetBoard();
        turn = TeamColor.WHITE;
    }
    public ChessGame(ChessGame other){
        board = new ChessBoard(other.board);
        turn = other.getTeamTurn();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return turn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        turn = team;
    }

    public void changeTurn(){
        if (turn == TeamColor.WHITE){
            turn = TeamColor.BLACK;
        }
        else{
            turn = TeamColor.WHITE;
        }
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        Collection<ChessMove> potentialMoves = piece.pieceMoves(board, startPosition);
        //of the moves in potentialMoves, see which of them don't result in the king getting in check. return those.
        ArrayList<ChessMove> valid =  new ArrayList<>();
        for(ChessMove move: potentialMoves){
            if (testMove(move)){
                valid.add(move);
            }
        }
        return valid;
    }

    public boolean testMove(ChessMove move){
        ChessGame test =  new ChessGame(this);


        try {
            test.makeMove(move);
        }
        catch(Exception InvalidMoveException){
            return false;
        }
        return true;
    }
    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {

        //Check if it's a real move
        ChessPosition start = move.getStartPosition();
        if( this.board.board[start.getRow()][start.getColumn()] == null){
            throw new InvalidMoveException("no piece in starting space");
        }
        ChessPosition end = move.getEndPosition();
        ChessPiece piece = board.getPiece(start);
        Collection<ChessMove> potentialMoves = piece.pieceMoves(board, start);

        if(piece.getTeamColor() != getTeamTurn()){
            throw new InvalidMoveException("Not your turn");
        }

        boolean exists = false;
        for(ChessMove option: potentialMoves){
            if(move.equals(option)){
                exists = true;
                break;
            }
        }
        if (!exists){
            throw new InvalidMoveException("impossible move");
        }

        if( move.getPromotionPiece() != null){
            board.addPiece(end, new ChessPiece(piece.getTeamColor(), move.getPromotionPiece()));
        }
        else{
            board.addPiece(end, piece);
        }
        board.addPiece(start, null);

        if(isInCheck(turn)){
            board.addPiece(start, piece);
            board.addPiece(end, null);
            throw new InvalidMoveException("Move results in check");
        }
        else{
            changeTurn();
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */

    public boolean isInCheck(TeamColor teamColor) {
        //you are in check if your king is under attack. So, find where the teamColor king is
        ChessPosition kingPosition = null;
        for(int row = 1; row <9 && (kingPosition == null); row+=1){
            for(int col = 1; col < 9; col +=1){
                ChessPiece piece = board.getPiece(new ChessPosition(row, col));
                if (piece != null && piece.equals(new ChessPiece(teamColor, ChessPiece.PieceType.KING))){
                    kingPosition = new ChessPosition(row, col);
                    break;
                }
            }
        }
        //see if any of your opponents pieces have any moves that end in the position of your king
        //go through board and find the pieces that aren't yours
        for(int row = 1; row <9; row+=1){
            for(int col = 1; col < 9; col +=1){
                ChessPiece piece = board.getPiece(new ChessPosition(row, col));
                if(piece != null && piece.getTeamColor() != teamColor){
                    // see what moves it can make
                    Collection<ChessMove> potentialMoves = piece.pieceMoves(board, new ChessPosition(row, col));
                    //see if any of them end in kingPosition
                    for(ChessMove move: potentialMoves){
                        if (move.getEndPosition().equals(kingPosition)){
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = new ChessBoard(board);
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }
}
