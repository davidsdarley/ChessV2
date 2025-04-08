package server.webSocket;

import carriers.*;
import chess.*;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import websocket.commands.UserGameCommand;
import websocket.messages.*;

import java.io.IOException;
import java.util.Set;


@WebSocket
public class WebSocketHandler {
    WebSocketSessions sessions;
    DatabaseManager db;

    public WebSocketHandler(DatabaseManager db){
        sessions = new WebSocketSessions();
        this.db = db;
    }
    private boolean verifyAuth(String authToken){
        try {
            if (db.getAuth(authToken) == null) {
                return false;
            }
            return true;
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
    private void handleLeave(UserGameCommand command, Session session){
        ServerMessage reply;
        // remove the session from the game
        sessions.removeSessionFromGame(command.getGameID(), session);
        //tell everyone about it.
        broadcastMessage(new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, command.getMessage())
                ,command.getGameID());
        reply = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        reply.setMessage("LEAVE");
        //remove the user from the game if they aren't an observer
        if (command.getLeaveRequest() == null){

        }
        else{
            db.removeUser(command.getLeaveRequest());
        }
        send(reply, session);
    }

    private void handleConnect(UserGameCommand command, Session session){
        GameData game;
        ServerMessage reply;

        try{
            //verify the authtoken
            if (db.getAuth(command.getAuthToken()) == null){
                reply = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
                reply.setMessage("Failed to connect - Unauthorized");
                return;
            }
            //Find the game
            game = db.getGame(command.getGameID());
            if (game == null){
                reply = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
                reply.setMessage("Failed to connect - Game does not exist");
                return;
            }
        }
        catch (DataAccessException e) {
            reply = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
            reply.setMessage("Failed to connect - "+e.getMessage());
            return;
        }

        if (command.getCommandType().equals(UserGameCommand.CommandType.CONNECT)) {
            //notify others
            ServerMessage message = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                    command.getMessage());
            broadcastMessage(message, command.getGameID());
            //send back the board
            sessions.addSessionToGame(command.getGameID(), session);
        }
        reply = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
        reply.setGame(game);

        if (command.getCommandType().equals(UserGameCommand.CommandType.GET)){
            reply.setPosition(command.getPosition());
        }

        send(reply, session);
    }

    private void handleMakeMove(UserGameCommand command, Session session){
        ServerMessage reply;

        ChessMove move = command.getChessMove();
        // Get the game.
        GameData gameData = db.getGame(command.getGameID());
        ChessGame game = gameData.getGame();
        try{
            game.makeMove(move);
            gameData.setGame(game);
            db.updateGame(gameData);

            reply = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
            reply.setGame(gameData);
            reply.setMessage(move.toString());

        } catch (InvalidMoveException e) {
            reply = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
            reply.setMessage(e.getMessage());
            send (reply, session);
        }
        broadcastMessage(reply, gameData.getGameID());
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws Exception {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        ServerMessage reply = null;

        if ( (command.getCommandType().equals(UserGameCommand.CommandType.CONNECT))
        || (command.getCommandType().equals(UserGameCommand.CommandType.GET)) ){

            handleConnect(command, session);
        }
        else if (command.getCommandType().equals(UserGameCommand.CommandType.MAKE_MOVE)){
            handleMakeMove(command, session);
        }
        else if (command.getCommandType().equals(UserGameCommand.CommandType.RESIGN)){
            sessions.removeSessionFromGame(command.getGameID(), session);
            // Tell the other people you resign.
            broadcastMessage(new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, command.getMessage())
                    ,command.getGameID());
            // Delete the game
            db.delete(command.getGameID());
            reply = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
            reply.setMessage("RESIGN");
            send(reply, session);
        }
        else if(command.getCommandType().equals(UserGameCommand.CommandType.LEAVE)){
            handleLeave(command, session);
        }
        else{
            reply = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
            reply.setMessage("ILLEGAL COMMAND");        }
            send(reply, session);
    }
    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason){
        System.out.println("Closing: "+reason);
        sessions.removeSession(session);
    }
    @OnWebSocketConnect
    public void onConnect(Session session){
        System.out.println("Connected");
    }
    @OnWebSocketError
    public void onError(Throwable throwable){
        System.out.println("Error! "+throwable.getMessage());
    }
    public void broadcastMessage(ServerMessage message, int gameID){
        Set<Session> receivers = sessions.getSessionsForGame(gameID);
        broadcastMessage(message, receivers);
    }
    public void broadcastMessage(ServerMessage message, Set<Session> receivers){
        for (Session session: receivers){
            send(message, session);
        }
    }

    public void sendMessage(ServerMessage message, Session session) {
        send(message, session);
    }

    public void send(ServerMessage message, Session session) {
        if (session.isOpen()) {
            try {

                session.getRemote().sendString(new Gson().toJson(message));
            }
            catch (IOException e) {
                //maybe do something here
                System.out.println("IOException: "+ e.getMessage());
            }
        }
    }
    @Override
    public String toString(){
        return "WS is running";
    }
}
