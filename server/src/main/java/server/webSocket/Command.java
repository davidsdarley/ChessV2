package server.webSocket;

import chess.*;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class Command {
    public String commandType;
    public String authToken;
    public int gameID;
    ChessMoveConstructor move;
    ChessMove chessMove;

    public Command(String commandType){
        this.commandType = commandType;
    }
    public Command(String commandType, String authToken, int gameID){
        this.commandType = commandType;
        this.authToken = authToken;
        this.gameID = gameID;
    }
    public String getCommand(){
        return commandType;
    }

    public ChessMove getChessMove() {
        if (chessMove == null && move != null){
            chessMove = move.getMove();
        }
        //return chessMove;
        return chessMove;
    }

    @Override
    public String toString(){
        return "commandType: "+commandType+ "\nauthToken: "+authToken + "\ngameID: "+gameID+ "\nmove: "+move;
    }

}
