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

    private ServerMessage handleConnect(UserGameCommand command, Session session){
        System.out.println("CONNECT");
        GameData game;
        ServerMessage reply;
        try{
            //verify the authtoken
            if (db.getAuth(command.getAuthToken()) == null){
                reply = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
                reply.setMessage("Failed to connect - Unauthorized");
                return reply;
            }
            //Find the game
            game = db.getGame(command.getGameID());
            if (game == null){
                reply = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
                reply.setMessage("Failed to connect - Game does not exist");
                return reply;
            }
        }
        catch (DataAccessException e) {
            reply = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
            reply.setMessage("Failed to connect - "+e.getMessage());
            return reply;
        }
        //notify others that someone is observing
        ServerMessage message = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                command.getMessage());
        broadcastMessage(message, command.getGameID());
        //send back the board
        sessions.addSessionToGame(command.getGameID(), session);
        reply = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
        reply.setGame(game);
        return reply;
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws Exception {

        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);

        ServerMessage reply;
        if (command.getCommandType().equals(UserGameCommand.CommandType.CONNECT)){
            reply = handleConnect(command, session);
            send(reply, session);
        }
        else if (command.getCommandType().equals(UserGameCommand.CommandType.MAKE_MOVE)){
            ChessMove move = command.getChessMove();
            // Get the game.
            // Check the validity of the move.
            // Change the game.
            // Send the updated game to everyone and store it in databases
            reply = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
            reply.setMessage("MOVE");
        }
        else if (command.getCommandType().equals(UserGameCommand.CommandType.RESIGN)){
            System.out.println("RESIGN");
            // Tell the other people you resign.
            // Delete the game
            reply = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
            reply.setMessage("RESIGN");
        }
        else if(command.getCommandType().equals(UserGameCommand.CommandType.LEAVE)){
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
        }
        else{
            reply = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
            reply.setMessage("ILLEGAL COMMAND");        }


        //determine message type and call appropriate response
            //makeMove      service.makeMove
            //leaveGame     sendMessage
            //resignGame    broadcastMessage
        System.out.println(reply);
        session.getRemote().sendString(new Gson().toJson(reply));
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
            }
        }
    }
    @Override
    public String toString(){
        return "WS is running";
    }
}
