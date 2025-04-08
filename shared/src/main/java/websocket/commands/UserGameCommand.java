package websocket.commands;

import carriers.JoinRequest;
import chess.*;

import java.util.Objects;

/**
 * Represents a command a user can send the server over a websocket
 *
 * Note: You can add to this class, but you should not alter the existing
 * methods.
 */
public class UserGameCommand {

    private final CommandType commandType;

    private final String authToken;

    private final Integer gameID;
    //my additions
    ChessMoveConstructor move;
    ChessMove chessMove;
    String message;
    JoinRequest leaveRequest;
    ChessPosition position;

    public UserGameCommand(CommandType commandType, String authToken, Integer gameID) {
        this.commandType = commandType;
        this.authToken = authToken;
        this.gameID = gameID;
        //my additions
    }

    public enum CommandType {
        CONNECT,
        MAKE_MOVE,
        LEAVE,
        RESIGN,
        GET
    }

    public CommandType getCommandType() {
        return commandType;
    }

    public String getAuthToken() {
        return authToken;
    }

    public Integer getGameID() {
        return gameID;
    }

    //my additions
    public ChessMove getChessMove() {
        if (chessMove == null && move != null){
            chessMove = move.getMove();
        }
        //return chessMove;
        return chessMove;
    }
    public void setPosition(ChessPosition position){
        this.position = position;
    }
    public ChessPosition getPosition(){
        return position;
    }
    public void setMessage(String message){
        this.message = message;
    }
    public String getMessage(){
        return message;
    }
    public void setLeaveRequest(JoinRequest leaveRequest) {
        this.leaveRequest = leaveRequest;
    }
    public JoinRequest getLeaveRequest(){
        return leaveRequest;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserGameCommand)) {
            return false;
        }
        UserGameCommand that = (UserGameCommand) o;
        return getCommandType() == that.getCommandType() &&
                Objects.equals(getAuthToken(), that.getAuthToken()) &&
                Objects.equals(getGameID(), that.getGameID());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCommandType(), getAuthToken(), getGameID());
    }

    @Override
    public String toString(){
        return "commandType: "+commandType+ "\nauthToken: "+authToken + "\ngameID: "+gameID+ "\nmove: "+move;
    }
}
