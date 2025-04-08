package websocket.messages;

import carriers.*;
import chess.*;

import java.util.Objects;

/**
 * Represents a Message the server can send through a WebSocket
 * 
 * Note: You can add to this class, but you should not alter the existing
 * methods.
 */
public class ServerMessage {
    ServerMessageType serverMessageType;
    public enum ServerMessageType {
        LOAD_GAME,
        ERROR,
        NOTIFICATION,
        //my additions
    }
    GameData game;
    String message;
    ChessPosition position;

    public void setPosition(ChessPosition position){
        this.position = position;
    }
    public ChessPosition getPosition(){
        return position;
    }
    public void setGame(GameData game){
        this.game = game;
    }
    public GameData getGameData(){
        return this.game;
    }
    public void setMessage(String message){
        this.message = message;
    }
    public String getMessage(){
        return message;
    }

    public ServerMessage(ServerMessageType type) {
        this.serverMessageType = type;
    }
    public ServerMessage(ServerMessageType type, String message) {
        this.message = message;
        this.serverMessageType = type;
    }

    public ServerMessageType getServerMessageType() {
        return this.serverMessageType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ServerMessage)) {
            return false;
        }
        ServerMessage that = (ServerMessage) o;
        return getServerMessageType() == that.getServerMessageType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getServerMessageType());
    }

    @Override
    public String toString(){
        return "Type: " + serverMessageType + "\nMessage: "+ message;
    }
}
