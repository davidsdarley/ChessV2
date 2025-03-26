package ui;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import static ui.EscapeSequences.*;

import java.util.Locale;


public class Printer {
    public Printer(){

    }

    public void printWhiteBoard(ChessGame game){
        if (game == null){
            game = new ChessGame();
        }

        String squareColor = "WHITE";
        for (int row = 1; row < 9; row +=1){
            for (int col = 1; col < 9; col +=1){
                ChessPiece piece = game.getBoard().getPiece(new ChessPosition(row, col));
                printSquare(piece, squareColor);
                if (squareColor.equals("WHITE")){
                    squareColor = "BLACK";
                }
                else{
                    squareColor = "WHITE";
                }
            }
            printSquare(null, "NONE");
            System.out.println();
            if (squareColor.equals("WHITE")){
                squareColor = "BLACK";
            }
            else{
                squareColor = "WHITE";
            }
        }
    }
    public void printSquare(ChessPiece piece, String color){
        String item;
        if (piece == null){
            item = EMPTY;
        }
        else if (piece.getTeamColor() == ChessGame.TeamColor.WHITE){
            if (piece.getPieceType() == ChessPiece.PieceType.PAWN){
                item = WHITE_PAWN;
            }
            else if (piece.getPieceType() == ChessPiece.PieceType.KNIGHT){
                item = WHITE_KNIGHT;
            }
            else if (piece.getPieceType() == ChessPiece.PieceType.BISHOP){
                item = WHITE_BISHOP;
            }
            else if (piece.getPieceType() == ChessPiece.PieceType.ROOK){
                item = WHITE_ROOK;
            }
            else if (piece.getPieceType() == ChessPiece.PieceType.QUEEN){
                item = WHITE_QUEEN;
            }
            else{
                item = WHITE_KING;
            }
        }
        else{
            if (piece.getPieceType() == ChessPiece.PieceType.PAWN){
                item = BLACK_PAWN;
            }
            else if (piece.getPieceType() == ChessPiece.PieceType.KNIGHT){
                item = BLACK_KNIGHT;
            }
            else if (piece.getPieceType() == ChessPiece.PieceType.BISHOP){
                item = BLACK_BISHOP;
            }
            else if (piece.getPieceType() == ChessPiece.PieceType.ROOK){
                item = BLACK_ROOK;
            }
            else if (piece.getPieceType() == ChessPiece.PieceType.QUEEN){
                item = BLACK_QUEEN;
            }
            else{
                item = BLACK_KING;
            }
        }

        if (color.toUpperCase() == "BLACK"){
            print(SET_BG_COLOR_BLACK);
        }
        else if(color.toUpperCase() == "WHITE"){
            print(SET_BG_COLOR_WHITE);
        }
        else{
            print(RESET_BG_COLOR);
        }
        print(item);
    }

    public void print(String item){
        System.out.print(item);
    }

    public static void main(String[] args) {
        Printer printer = new Printer();
        printer.printWhiteBoard(null);
    }
}
