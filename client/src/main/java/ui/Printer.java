package ui;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import websocket.messages.ServerMessage;

import java.time.Year;
import java.util.ArrayList;
import java.util.Collection;

import static ui.EscapeSequences.*;

public class Printer {
    public Printer(){

    }
    public void printBoard(ChessGame game, String color){
        System.out.println();
        if (game == null){
            game = new ChessGame();
        }
        if (color.toUpperCase().equals("BLACK")){
            printBlackBoard(game, null);
        }
        else{
            printWhiteBoard(game, null);
        }
        System.out.println();
    }

    public void printBoard(ChessGame game, String color, Collection<ChessMove> highlighted){
        System.out.println();
        if (game == null){
            game = new ChessGame();
        }
        if (color.toUpperCase().equals("BLACK")){
            printBlackBoard(game, highlighted);
        }
        else{
            printWhiteBoard(game, highlighted);
        }
        System.out.println();
    }


    public String getPrintablePiece(ChessPiece piece){
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
        return item;
    }
    public void printBoard(ChessGame game){
        if (game == null){
            game = new ChessGame();
        }
        System.out.println();
        printWhiteBoard(game, null);
        System.out.println();
    }
    public void printWhiteBoard(ChessGame game, Collection<ChessMove> highlighted){
        String squareColor = "WHITE";
        String[] letters = {"   "," a ", " b ", " c ", " d ", " e ", " f " , " g ", " h ", "   "};
        String[] numbers = {"   ", " 8 ", " 7 ", " 6 ", " 5 ", " 4 " ," 3 ", " 2 ", " 1 ", "   "};
        for (int i = 0; i <=9; i += 1){
            printSquare(letters[i]);
        }
        printSquare(null, "NONE", false);

        System.out.println();
        for (int row = 1; row < 9; row +=1){
            printSquare(numbers[row]);
            for (int col = 1; col < 9; col +=1){
                squareColor = processSquare(new ChessPosition(row, col), game, squareColor,
                        (ArrayList<ChessMove>) highlighted);
            }
            squareColor = finishRow(squareColor, numbers[row]);
        }
        for (int i = 0; i <=9; i += 1){
            printSquare(letters[i]);
        }
        printSquare(null, "NONE", false);
    }
    private String finishRow(String squareColor, String number){
        printSquare(number);
        printSquare(null, "NONE", false);
        System.out.println();
        if (squareColor.equals("WHITE")){
            return "BLACK";
        }
        else{
            return "WHITE";
        }
    }
    private String processSquare(ChessPosition position, ChessGame game, String squareColor,
                                 ArrayList<ChessMove> highlighted){

        boolean highlight = false;
        if (highlighted != null){
            for(int i = 0; i < highlighted.size(); i++){
                ChessMove move = highlighted.get(i);
                if (move.getEndPosition().equals(position)){
                    highlight = true;
                    break;
                }
            }
        }

        ChessPiece piece = game.getBoard().getPiece(position);
        printSquare(piece, squareColor, highlight);
        if (squareColor.equals("WHITE")){
            return "BLACK";
        }
        else{
            return "WHITE";
        }
    }
    public void printBlackBoard(ChessGame game, Collection<ChessMove> highlighted){
        String squareColor = "WHITE";
        String[] letters = {"   "," h ", " g ", " f ", " e ", " d ", " c " , " b ", " a ", "   "};
        String[] numbers = {"   ", " 8 ", " 7 ", " 6 ", " 5 ", " 4 " ," 3 ", " 2 ", " 1 ", "   "};
        for (int i = 0; i <=9; i += 1){
            printSquare(letters[i]);
        }
        printSquare(null, "NONE", false);

        System.out.println();
        for (int row = 8; row > 0; row -=1){
            printSquare(numbers[row]);
            for (int col = 8; col > 0; col -=1){
                ChessPosition position = new ChessPosition(row, col);
                squareColor =  processSquare(position, game, squareColor, (ArrayList<ChessMove>) highlighted);
            }
            squareColor = finishRow(squareColor, numbers[row]);
        }
        for (int i = 0; i <=9; i += 1){
            printSquare(letters[i]);
        }
        printSquare(null, "NONE", false);
    }
    public void printSquare(ChessPiece piece, String color, boolean highlighted){

        String item;
        item = getPrintablePiece(piece);
        if (highlighted){
            if (color.toUpperCase() == "BLACK"){
                print(SET_BG_COLOR_DARK_GREEN);
            }
            else if(color.toUpperCase() == "WHITE"){
                print(SET_BG_COLOR_GREEN);
            }
            else{
                print(RESET_BG_COLOR);
            }
        }
        else{
            if (color.toUpperCase() == "BLACK"){
                print(SET_BG_COLOR_BLACK);
            }
            else if(color.toUpperCase() == "WHITE"){
                print(SET_BG_COLOR_WHITE);
            }
            else{
                print(RESET_BG_COLOR);
            }
        }

        print(item);
    }
    public void printSquare(String item){
        print(SET_BG_COLOR_DARK_GREY);
        print(item);
    }
    public void print(String item){
        System.out.print(item);
    }

    public static void main(String[] args) {
        Printer printer = new Printer();
        ChessGame game = new ChessGame();
        printer.printBoard(game);
        printer.printBoard(game, "BLACK");
    }

    public boolean printHighlights(ServerMessage serverMessage, String color) {
        //do the same as usual, but this time highlight the pertinent squares
        ChessGame game =  serverMessage.getGameData().getGame();
        ChessPosition position = serverMessage.getPosition();
        ChessPiece piece = game.getBoard().getPiece(position);
        if (!( (color.toUpperCase().equals("WHITE")) && piece.getTeamColor().equals(ChessGame.TeamColor.WHITE)
        || (color.toUpperCase().equals("BLACK")) && piece.getTeamColor().equals(ChessGame.TeamColor.BLACK) ) ){
            System.out.print("Not your piece!");
            printBoard(game,color);
            return false;
        }
        Collection<ChessMove> moves = game.validMoves(position);

        printBoard(serverMessage.getGameData().getGame(), color, moves);

        //check if the spot has a valid piece
        return true;
    }
}
