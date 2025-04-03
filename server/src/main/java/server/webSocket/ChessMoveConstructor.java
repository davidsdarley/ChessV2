package server.webSocket;

import chess.*;

import java.util.Map;

public class ChessMoveConstructor {
    Map<String, Integer> startPosition;
    Map<String, Integer> endPosition;
    String promotionPiece;

    public ChessPosition getStart(){
        return new ChessPosition(startPosition.get("row"), endPosition.get("column"));
    }
    public ChessPosition getEnd(){
        return new ChessPosition(endPosition.get("row"), endPosition.get("column"));
    }

    public ChessMove getMove(){
        return new ChessMove(getStart(), getEnd(), getPromotionPiece(promotionPiece));
    }
    private ChessPiece.PieceType getPromotionPiece(String type){
        if (type == null){
            return null;
        }
        if (type.equals("QUEEN")){
            return ChessPiece.PieceType.QUEEN;
        }
        if (type.equals("ROOK")){
            return ChessPiece.PieceType.ROOK;
        }
        if (type.equals("BISHOP")){
            return ChessPiece.PieceType.BISHOP;
        }
        if (type.equals("KNIGHT")){
            return ChessPiece.PieceType.KNIGHT;
        }
        if (type.equals("PAWN")){
            return ChessPiece.PieceType.PAWN;
        }
        return null;
    }
}
