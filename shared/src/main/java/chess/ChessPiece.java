package chess;

import java.util.Collection;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    ChessGame.TeamColor color;
    PieceType type;


    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.color = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return this.color;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return this.type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public int hashCode(){
        //There are 6 types of chess pieces. White adds 6 to the basic value
        int hash = 0;
        if(color == ChessGame.TeamColor.WHITE){
            hash += 6;
        }
        if (type == PieceType.PAWN){
            hash += 0;
        }
        else if (type == PieceType.KNIGHT){
            hash += 1;
        }
        else if (type == PieceType.BISHOP){
            hash += 2;
        }
        else if (type == PieceType.ROOK){
            hash += 3;
        }
        else if (type == PieceType.QUEEN){
            hash += 4;
        }
        else if (type == PieceType.KING){
            hash += 5;
        }
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj.getClass() != ChessPiece.class){
            return false;
        }
        ChessPiece other = (ChessPiece) obj;
        if(other.hashCode() == this.hashCode()){
            return true;
        }
        return false;
    }

    @Override
    public String toString(){
        if(color == ChessGame.TeamColor.WHITE){
            if (type == PieceType.PAWN){
                return "P|";
            }
            else if (type == PieceType.KNIGHT){
                return "N|";
            }
            else if (type == PieceType.BISHOP){
                return "B|";
            }
            else if (type == PieceType.ROOK){
                return "R|";
            }
            else if (type == PieceType.QUEEN){
                return "Q|";
            }
            else{
                return "K|";
            }
        }
        else{
            if (type == PieceType.PAWN){
                return "p|";
            }
            else if (type == PieceType.KNIGHT){
                return "n|";
            }
            else if (type == PieceType.BISHOP){
                return "b|";
            }
            else if (type == PieceType.ROOK){
                return "r|";
            }
            else if (type == PieceType.QUEEN){
                return "q|";
            }
            else{
                return "k|";
            }
        }
    }
}
