package chess;

/**
 * Represents moving a chess piece on a chessboard
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessMove {
    ChessPosition start;
    ChessPosition end;
    ChessPiece.PieceType piece;
    public ChessMove(ChessPosition startPosition, ChessPosition endPosition,
                     ChessPiece.PieceType promotionPiece) {
        start = startPosition;
        end = endPosition;
        piece = promotionPiece;

    }

    /**
     * @return ChessPosition of starting location
     */
    public ChessPosition getStartPosition() {
        return start;
    }

    /**
     * @return ChessPosition of ending location
     */
    public ChessPosition getEndPosition() {
        return end;
    }

    /**
     * Gets the type of piece to promote a pawn to if pawn promotion is part of this
     * chess move
     *
     * @return Type of piece to promote a pawn to, or null if no promotion
     */
    public ChessPiece.PieceType getPromotionPiece() {
        return piece;
    }

    @Override
    public int hashCode(){
        int hash = start.hashCode()+end.hashCode();
        if (piece != null){
            hash += piece.hashCode();
        }
        return hash;
    }
    @Override
    public boolean equals(Object obj){
        if (obj.getClass() != ChessMove.class){
            return false;
        }
        ChessMove other = (ChessMove)obj;
        if(other.start.equals(this.start) && other.end.equals(this.end) && other.piece == piece){
            return true;}
        return false;
    }
    @Override
    public String toString(){

        return start + " to " + end;
    }

}
