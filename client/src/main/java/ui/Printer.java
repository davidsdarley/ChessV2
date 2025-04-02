package ui;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
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
            printBlackBoard(game);
        }
        else{
            printWhiteBoard(game);
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
        printWhiteBoard(game);
        System.out.println();
    }
    public void printWhiteBoard(ChessGame game){
        String squareColor = "WHITE";
        String[] letters = {"   "," a ", " b ", " c ", " d ", " e ", " f " , " g ", " h ", "   "};
        String[] numbers = {"   ", " 8 ", " 7 ", " 6 ", " 5 ", " 4 " ," 3 ", " 2 ", " 1 ", "   "};
        for (int i = 0; i <=9; i += 1){
            printSquare(letters[i]);
        }
        printSquare(null, "NONE");

        System.out.println();
        for (int row = 1; row < 9; row +=1){
            printSquare(numbers[row]);
            for (int col = 1; col < 9; col +=1){
                squareColor = processSquare(row, col, game, squareColor);
            }
            squareColor = finishRow(squareColor, numbers[row]);
        }
        for (int i = 0; i <=9; i += 1){
            printSquare(letters[i]);
        }
        printSquare(null, "NONE");
    }
    private String finishRow(String squareColor, String number){
        printSquare(number);
        printSquare(null, "NONE");
        System.out.println();
        if (squareColor.equals("WHITE")){
            return "BLACK";
        }
        else{
            return "WHITE";
        }
    }
    private String processSquare(int row, int col, ChessGame game, String squareColor){
        ChessPiece piece = game.getBoard().getPiece(new ChessPosition(row, col));
        printSquare(piece, squareColor);
        if (squareColor.equals("WHITE")){
            return "BLACK";
        }
        else{
            return "WHITE";
        }
    }
    public void printBlackBoard(ChessGame game){
        String squareColor = "WHITE";
        String[] letters = {"   "," h ", " g ", " f ", " e ", " d ", " c " , " b ", " a ", "   "};
        String[] numbers = {"   ", " 8 ", " 7 ", " 6 ", " 5 ", " 4 " ," 3 ", " 2 ", " 1 ", "   "};
        for (int i = 0; i <=9; i += 1){
            printSquare(letters[i]);
        }
        printSquare(null, "NONE");

        System.out.println();
        for (int row = 8; row > 0; row -=1){
            printSquare(numbers[row]);
            for (int col = 8; col > 0; col -=1){
                squareColor =  processSquare(row, col, game, squareColor);
            }
            squareColor = finishRow(squareColor, numbers[row]);
        }
        for (int i = 0; i <=9; i += 1){
            printSquare(letters[i]);
        }
        printSquare(null, "NONE");
    }
    public void printSquare(ChessPiece piece, String color){
        String item;
        item = getPrintablePiece(piece);
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
}
