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

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {

    }

    public Receiver(UserInterface ui, String color) throws Exception {
        URI uri = new URI("ws://localhost:8081/ws");
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this, uri);
        user = ui;
        this.color = color;


        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            public void onMessage(String message) {     //expects a ServerMessage, in the form of a JSON
                ServerMessage serverMessage =  new Gson().fromJson(message, ServerMessage.class);

                handleMessage(serverMessage);
            }
        });
    }

    public void highlight(ChessPosition position, int gameID){
        //send a message to the server for a highlight request.
        UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.GET, user.auth, gameID);
        command.setPosition(position);
        sendCommand(command);
    }
    public void makeMove(ChessMove move){
        UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.MAKE_MOVE,
                user.auth, user.activeGame);
        command.setChessMove(move);

        sendCommand(command);
    }
    private void handleMessage(ServerMessage serverMessage){
        if (serverMessage.getServerMessageType().equals(ServerMessage.ServerMessageType.LOAD_GAME)){
            user.debug("LOAD_GAME message received.");
            handleLoadGame(serverMessage);
        }
        else if(serverMessage.getServerMessageType().equals(ServerMessage.ServerMessageType.NOTIFICATION)){
            user.debug("DEBUG: NOTIFICATION message received.");
            handleNotification(serverMessage);
        }
        else if(serverMessage.getServerMessageType().equals(ServerMessage.ServerMessageType.ERROR)){
            user.debug("DEBUG: ERROR message received.");
            handleError(serverMessage);
        }
        else if(serverMessage.getServerMessageType().equals(ServerMessage.ServerMessageType.ERROR)){
            user.debug("DEBUG: HIGHLIGHT message received.");
            handleError(serverMessage);
        }
        System.out.print(user.state+" >>> ");
    }

    private void handleLoadGame(ServerMessage serverMessage){
        GameData game = serverMessage.getGameData();
        user.debug("FLAG");
        if ( game.getGame().getTeamTurn().equals(ChessGame.TeamColor.WHITE) && color.equals("WHITE")
        || (game.getGame().getTeamTurn().equals(ChessGame.TeamColor.BLACK) && color.equals("BLACK")) ){
            turn = true;
        }
        else{
            turn = false;
        }

        if (user.state.equals("OBSERVING")){
            user.debug("observation starting");
            user.printer.printBoard(game.getGame());
        }
        else if (serverMessage.getPosition() != null){
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

    }
    private void handleNotification(ServerMessage serverMessage){
        //print the notification
        System.out.println(serverMessage.getMessage());
        if (serverMessage.getMessage().equals("Your turn!")){
            turn = true;
        }
    }
    private void handleError(ServerMessage serverMessage){
        //catch and handle the error
        System.out.println("Error: "+serverMessage.getMessage());
    }

    public void observe(UserGameCommand command){
        try{
            send(new Gson().toJson(command));
        }
        catch (Exception e) {
            System.out.println("OBSERVATION FAILED");
        }
    }
    public void sendCommand(UserGameCommand command){
        try{
            send(new Gson().toJson(command));
        }
        catch (Exception e) {
            System.out.println("MESSAGE FAILED TO SEND");
        }
    }

    public void send(String msg) throws Exception {
        this.session.getBasicRemote().sendText(msg);
    }

    public void stop() throws IOException {
        session.close();
    }
}
