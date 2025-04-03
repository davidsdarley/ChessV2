package server.webSocket;

import chess.*;

public class Command {
    public String commandType;
    public String authToken;
    public int gameID;
    ChessMove move;

    public Command(String commandType){
        this.commandType = commandType;

    }
    public String getCommand(){
        return commandType;
    }
}
