package ui;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.util.Scanner;

import carriers.*;
import chess.ChessGame;
import com.google.gson.Gson;
import server.Server;
import server.webSocket.Command;
import websocket.messages.ServerMessage;


public class Receiver extends Endpoint{
    private UserInterface user;
    public Session session;
    private String color;

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {

    }

//    public static void main(String[] args) throws Exception {
//        UserInterface ui = new UserInterface();
//        var ws = new Receiver(ui, "WHITE");
//        Scanner scanner = new Scanner(System.in);
//
//    }


    public Receiver(UserInterface ui, String color) throws Exception {
        URI uri = new URI("ws://localhost:8081/ws");
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this, uri);
        user = ui;
        this.color = color;


        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            public void onMessage(String message) {     //expects a ServerMessage, in the form of a JSON
                ServerMessage serverMessage =  new Gson().fromJson(message, ServerMessage.class);
                System.out.println("\n"+serverMessage);

                //handle message
                if (serverMessage.getServerMessageType().equals(ServerMessage.ServerMessageType.LOAD_GAME)){
                    System.out.println("DEBUG: LOAD_GAME message received.");
                    handleLoadGame(serverMessage);
                }
                else if(serverMessage.getServerMessageType().equals(ServerMessage.ServerMessageType.NOTIFICATION)){
                    System.out.println("DEBUG: NOTIFICATION message received.");
                    handleNotification(serverMessage);
                }
                else if(serverMessage.getServerMessageType().equals(ServerMessage.ServerMessageType.ERROR)){
                    System.out.println("DEBUG: ERROR message received.");
                    handleError(serverMessage);
                }
            }
        });
    }

    private void handleLoadGame(ServerMessage serverMessage){
        //print the new board


    }
    private void handleNotification(ServerMessage serverMessage){
        //print the notification
    }
    private void handleError(ServerMessage serverMessage){
        //catch and handle the error
    }

    public void observe(Command command){
        try{
            send(new Gson().toJson(command));

        }
        catch (Exception e) {
            System.out.println("OBSERVATION FAILED");
        }
    }


    public void send(String msg) throws Exception {
        this.session.getBasicRemote().sendText(msg);
    }

    public void stop() throws IOException {
        session.close();
        System.out.println("STOPPED");
    }
}
