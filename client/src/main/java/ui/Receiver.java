package ui;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;

import chess.*;
import com.google.gson.Gson;

import carriers.*;

import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;


public class Receiver extends Endpoint{
    private UserInterface user;
    public Session session;
    private String color;
    public boolean turn;
    public boolean gameOver;

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {

    }

    public Receiver(UserInterface ui, String color) throws Exception {
        URI uri = new URI("ws://localhost:8081/ws");
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this, uri);
        user = ui;
        this.color = color;
        gameOver = false;


        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            public void onMessage(String message) {     //expects a ServerMessage, in the form of a JSON
                ServerMessage serverMessage =  new Gson().fromJson(message, ServerMessage.class);
                user.debug("MESSAGE RECEIEVED"+serverMessage.getServerMessageType());
                user.debug(message);


                handleMessage(serverMessage);
            }
        });
    }

    public void highlight(ChessPosition position, int gameID){
        //send a message to the server for a highlight request.
        UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.GET, user.auth, gameID);
        command.setPosition(position);
        command.setMessage("highlight legal moves");
        sendCommand(command);
    }
    public void makeMove(ChessMove move){
        UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.MAKE_MOVE,
                user.auth, user.activeGame);
        command.setMove(move);
        command.setMessage(user.userName+" moved "+move);

        sendCommand(command);
    }
    private void handleMessage(ServerMessage serverMessage){
        user.debug("Message received!");

        if (serverMessage.getServerMessageType().equals(ServerMessage.ServerMessageType.LOAD_GAME)){
            user.debug("LOAD_GAME message received.");
            handleLoadGame(serverMessage);
        }
        else if(serverMessage.getServerMessageType().equals(ServerMessage.ServerMessageType.NOTIFICATION)){
            user.debug("DEBUG: NOTIFICATION message received.");
            handleNotification(serverMessage);
        }
        else if(serverMessage.getServerMessageType().equals(ServerMessage.ServerMessageType.ERROR)){
            user.debug("ERROR message received.");
            handleError(serverMessage);
        }
        else if(serverMessage.getServerMessageType().equals(ServerMessage.ServerMessageType.ERROR)){
            handleError(serverMessage);
        }
        System.out.print(user.state+" >>> ");
    }

    private void handleLoadGame(ServerMessage serverMessage){
        GameData game = serverMessage.getGameData();
        gameOver = game.getGame().getGameOver();
        user.debug("activeColor: "+user.activeColor);
        user.debug("gameTurn: "+game.getGame().getTeamTurn());

        if (user.state.equals("OBSERVING")){
            user.activeColor = "WHITE";
        }
        else if ( game.getGame().getTeamTurn().equals(ChessGame.TeamColor.WHITE) && user.activeColor.equals("WHITE")
        || (game.getGame().getTeamTurn().equals(ChessGame.TeamColor.BLACK) && user.activeColor.equals("BLACK")) ){
            user.debug("turn change: my turn");
            turn = true;
            System.out.println("Your turn");
        }
        else{
            user.debug("turn change: their turn");
            turn = false;
            System.out.println("Opponent's turn");
        }
        if (serverMessage.getPosition() != null){
            user.printer.printHighlights(serverMessage, user.activeColor);
        }
        else {
            if (user.activeColor == null){
                user.printer.printBoard(game.getGame());
            }
            else{
                user.printer.printBoard(game.getGame(), user.activeColor);
            }
        }
        user.printer.printLine(serverMessage);

        if(!turn) {
            checkCheck(game);
        }
    }
    private void checkCheck(GameData game){
        String white = game.getWhiteUsername();
        String black = game.getBlackUsername();
        if(game.getGame().isInCheckmate(ChessGame.TeamColor.WHITE)){
            System.out.println(white + " is in Checkmate!");
        }
        else if(game.getGame().isInCheckmate(ChessGame.TeamColor.BLACK)){
            System.out.println(black + " is in Checkmate!");
        }
        else if(game.getGame().isInCheck(ChessGame.TeamColor.WHITE)){
            System.out.println(white + " is in Check!");
        }
        else if(game.getGame().isInCheck(ChessGame.TeamColor.BLACK)){
            System.out.println(black + " is in Check!");
        }
        else if(game.getGame().isInStalemate(ChessGame.TeamColor.WHITE)){
            System.out.println(white + " is in Stalemate.");
        }
        else if(game.getGame().isInStalemate(ChessGame.TeamColor.BLACK)){
            System.out.println(black + " is in Stalemate.");
        }
    }

    private void handleNotification(ServerMessage serverMessage){
        //print the notification
        System.out.println();
        System.out.println(serverMessage.getMessage());
        if (serverMessage.getMessage().equals("Your turn!")){
            turn = true;
        }
    }
    private void handleError(ServerMessage serverMessage){
        user.debug("ERROR!");
        //catch and handle the error
        System.out.println("Error: "+serverMessage.getErrorMessage());
    }

    public void observe(UserGameCommand command){
        try{
            command.setMessage(user.userName+ " has joined as an observer!");
            send(new Gson().toJson(command));
        }
        catch (Exception e) {
            System.out.println("OBSERVATION FAILED");
        }
    }
    public void sendCommand(UserGameCommand command){
        try{
            send(new Gson().toJson(command));
            user.debug("SENT FROM sendCommand()");
        }
        catch (Exception e) {
            System.out.println("MESSAGE FAILED TO SEND");
        }
    }

    public void send(String msg) throws Exception {
        user.debug("SENT FROM send()");
        this.session.getBasicRemote().sendText(msg);
    }

    public void stop() throws IOException {
        session.close();
    }
}
