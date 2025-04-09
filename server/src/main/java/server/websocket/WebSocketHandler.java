package server.websocket;

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
import java.util.HashSet;
import java.util.Set;


@WebSocket
public class WebSocketHandler {
    WebSocketSessions sessions;
    DatabaseManager db;
    private boolean deBug = false;

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
    private boolean verifyPlayer(String authToken, UserGameCommand command, Session session){
        AuthData authData;
        GameData gameData = db.getGame(command.getGameID());
        try{
            authData = db.getAuth(authToken);}
        catch (DataAccessException e) {
            ServerMessage reply = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
            reply.setErrorMessage("You are just an observer, you can't take this action");
            send(reply, session);
            return false;
        }

        if ((authData.getUsername().equals(gameData.getBlackUsername())
                || authData.getUsername().equals(gameData.getWhiteUsername()))       ){
            return true;
        }
        ServerMessage reply = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
        reply.setErrorMessage("You are just an observer, you can't take this action");
        send(reply, session);
        return false;
    }
    private void handleLeave(UserGameCommand command, Session session) {
        ServerMessage reply;
        AuthData authData;
        try {
             authData = db.getAuth(command.getAuthToken());
        }
        catch (DataAccessException e){
            return;
        }
        // remove the session from the game
        sessions.removeSessionFromGame(command.getGameID(), session);
        //tell everyone about it.
        reply = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        reply.setMessage(authData.getUsername()+" has left.");
        broadcastMessage(reply, command.getGameID());
        reply = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        reply.setMessage("LEAVE from Handler");
        //remove the user from the game if they aren't an observer
        db.removeUser(command);

        send(reply, session);
    }
    private void handleConnect(UserGameCommand command, Session session){
        GameData game;
        ServerMessage reply;

        if (command.getMessage()==null){
            command.setMessage("new user has connected!");
        }

        try{
            //verify the authtoken

            if (db.getAuth(command.getAuthToken()) == null){
                reply = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
                reply.setErrorMessage("Failed to connect - Unauthorized");
                send(reply, session);
                return;
            }
            //Find the game
            game = db.getGame(command.getGameID());

            if (game == null){

                reply = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
                reply.setErrorMessage("Failed to connect - Game does not exist");
                send(reply, session);
                debug("DEBUG: reply == "+reply);

                return;
            }
        }
        catch (DataAccessException e) {
            reply = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
            reply.setErrorMessage("Failed to connect - "+e.getMessage());
            send(reply, session);
            return;
        }

        if (command.getCommandType().equals(UserGameCommand.CommandType.CONNECT)) {
            //notify others
            ServerMessage message = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
            message.setMessage(command.getMessage());
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
        GameData gameData = db.getGame(command.getGameID());
        ServerMessage reply;
        ChessGame game = gameData.getGame();

        if (!(verifyAuth(command.getAuthToken()))){
            reply = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
            reply.setErrorMessage("Failed to connect - Unauthorized");
            send(reply, session);
            return;
        }
        if(!(verifyPlayer(command.getAuthToken(), command, session))){
            return;
        }

        //check if the move is your piece  :|
        String username;
        try {
            username = db.getAuth(command.getAuthToken()).getUsername();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        ChessPiece piece = game.getBoard().getPiece(command.getMove().getStartPosition());
        if ( (username.equals(gameData.getWhiteUsername()) && piece.getTeamColor().equals(ChessGame.TeamColor.BLACK))
                || (username.equals(gameData.getBlackUsername()) && piece.getTeamColor().equals(ChessGame.TeamColor.WHITE))
        ){
            reply = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
            reply.setErrorMessage("Not your piece");
            send(reply, session);
            return;
        }
        //ChessMove move = command.getChessMove();
        ChessMove move = command.getMove();
        // Get the game.
        try{
            game.makeMove(move);
            gameData.setGame(game);
            db.updateGame(gameData);

            reply = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
            reply.setGame(gameData);
            reply.setMessage(null);
            broadcastMessage(reply, command.getGameID());

            reply = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);

            String message = username + " moved " + move;
            String white = gameData.getWhiteUsername();
            String black = gameData.getBlackUsername();
            if (game.isInCheck(game.getTeamTurn())){
                if(game.getTeamTurn().equals(ChessGame.TeamColor.WHITE)){
                    message+="\n" + white + " is in Checkmate!";
                }
                else{
                    message+="\n" + black + " is in Checkmate!";
                }
            }
            else if (game.isInCheck(game.getTeamTurn())){
                if(game.getTeamTurn().equals(ChessGame.TeamColor.WHITE)){
                    message+="\n" + white + " is in Check!";
                }
                else{
                    message+="\n" + black + " is in Check!";
                }
            }
            else if (game.isInStalemate(game.getTeamTurn())){
                if(game.getTeamTurn().equals(ChessGame.TeamColor.WHITE)){
                    message+="\n" + white + " is in Stalemake!";
                }
                else{
                    message+="\n" + black + " is in Stalemate!";
                }
            }


            reply.setMessage(message);

            broadcastMessage(reply, gameData.getGameID(), session);


        } catch (InvalidMoveException e) {
            reply = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
            reply.setErrorMessage("Illegal move.");
            //reply.setMessage(e.getMessage()+": sent from handleMakeMoves error catcher");     //DEBUG

            send (reply, session);
        }
    }
    private void handleResign(UserGameCommand command, Session session){
        GameData gameData = db.getGame(command.getGameID());
        ChessGame game = gameData.getGame();
        AuthData authData;
        try{
            authData = db.getAuth(command.getAuthToken());} catch (DataAccessException e) {
            debug("uh oh...");
            return;
        }
        //check if you are allowed to resign
            //is the game already over?
            if (game.getGameOver()){
                ServerMessage reply =  new ServerMessage(ServerMessage.ServerMessageType.ERROR);
                reply.setErrorMessage("Game's over, you can't end it again.");
                send(reply, session);
                return;
            }
            //is your authtoken one of the people that can resign?
            if (!verifyPlayer(command.getAuthToken(), command, session)){
                return;
            }
        // Tell the other people you resign.
        if (command.getMessage() == null){
            command.setMessage(authData.getUsername() + " has resigned.");
        }
        broadcastMessage(new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, command.getMessage())
                ,command.getGameID());
        // end the game
        game.endGame();
        gameData.setGame(game);
        db.updateGame(gameData);
    }
    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws Exception {
        debug("DEBUG: message received in handler!");

        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        debug("\n"+command+"\n");
        ServerMessage reply = null;

        if ( (command.getCommandType().equals(UserGameCommand.CommandType.CONNECT))
        || (command.getCommandType().equals(UserGameCommand.CommandType.GET)) ){

            handleConnect(command, session);
        }
        else if (command.getCommandType().equals(UserGameCommand.CommandType.MAKE_MOVE)){
            handleMakeMove(command, session);
        }
        else if (command.getCommandType().equals(UserGameCommand.CommandType.RESIGN)){
            handleResign(command, session);
        }
        else if(command.getCommandType().equals(UserGameCommand.CommandType.LEAVE)){
            handleLeave(command, session);
        }
        else{
            reply = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
            reply.setMessage("ILLEGAL COMMAND");        }
            //send(reply, session);
    }
    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason){
        sessions.removeSession(session);
    }
    @OnWebSocketConnect
    public void onConnect(Session session){
    }
    @OnWebSocketError
    public void onError(Throwable throwable){
        System.out.println("Error! "+throwable.getMessage());
    }
    public void broadcastMessage(ServerMessage message, int gameID, Session excludedSession){
        Set<Session> receivers = new HashSet<>();
        Set<Session> possibles = sessions.getSessionsForGame(gameID);
        for(Session session: possibles){
            if (!session.equals(excludedSession)){
                receivers.add(session);
            }
        }
        broadcastMessage(message, receivers);
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

    public void send(ServerMessage message, Session session) {
        debug("sending "+message);
        if (session.isOpen()) {
            try {
                String reply = new Gson().toJson(message);
                debug(reply);
                session.getRemote().sendString(reply);
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

    public void debug(String message){
        if (deBug){
            System.out.println("DEBUG: "+message);
        }
    }
}
