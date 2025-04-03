package server.webSocket;

import carriers.*;
import chess.*;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;

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

    private String handleConnect(Command command, Session session){
        System.out.println("CONNECT");
        GameData game;
        try{
            //verify the authtoken
            if (db.getAuth(command.authToken) == null){
                return "Failed to connect - Unauthorized";
            }
            //Find the game
            game = db.getGame(command.gameID);
            if (game == null){
                return "Failed to connect - Game does not exist";
            }
            } catch (DataAccessException e) {
            return "Error - " + e.getMessage();
        }
        sessions.addSessionToGame(command.gameID, session);
        return new Gson().toJson(game);
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws Exception {

        Command command = new Gson().fromJson(message, Command.class);

        String reply = "";
        if (command.getCommand().equals("CONNECT")){
            reply = handleConnect(command, session);

        }
        else if (command.getCommand().equals("MAKE_MOVE")){
            ChessMove move = command.getChessMove();
            // Get the game.
            // Check the validity of the move.
            // Change the game.
            // Send the updated game to everyone and store it in databases
            reply = "MOVE";
        }
        else if (command.getCommand().equals("RESIGN")){
            System.out.println("RESIGN");
            // Tell the other people you resign.
            // Delete the game
            reply = "RESIGN";
        }
        else if(command.getCommand().equals("LEAVE")){
            System.out.println("LEAVE");
            // remove the session from the game.
            //
            reply = "LEAVE";
        }
        else{
            reply = "Illegal command.";
        }


        //determine message type and call appropriate response
            //makeMove      service.makeMove
            //leaveGame     sendMessage
            //resignGame    broadcastMessage
        reply = new Gson().toJson("WebSocket response: " + reply);
        session.getRemote().sendString(reply);
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
    public void broadcastMessage(String message, int gameID){
        Set<Session> receivers = sessions.getSessionsForGame(gameID);
        broadcastMessage(message, receivers);
    }
    public void broadcastMessage(String message, Set<Session> receivers){
        for (Session session: receivers){
            send(message, session);
        }
    }

    public void sendMessage(String message, Session session) {
        send(message, session);
    }

    public void send(String message, Session session) {
        if (session.isOpen()) {
            try {
                session.getRemote().sendString(message);
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
