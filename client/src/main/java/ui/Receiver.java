package ui;

import javax.websocket.*;
import java.net.URI;
import java.util.Scanner;

import carriers.*;
import chess.ChessGame;
import com.google.gson.Gson;


public class Receiver extends Endpoint{
    private UserInterface user;
    private Scanner scanner;
    public Session session;
    private String color;

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public static void main(String[] args) throws Exception {
        var ws = new Receiver("WHITE");
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter a message you want to echo");
        while (true) {
            ws.send(scanner.nextLine());
        }
    }


    public Receiver(UserInterface ui, String color) throws Exception {
        URI uri = new URI("ws://localhost:8081/ws");
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this, uri);
        user = ui;
        scanner = new Scanner(System.in);




        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            public void onMessage(String message) {     //expects a GameData message, in the form of a JSON
                GameData gameData =  new Gson().fromJson(message, GameData.class);
                ChessGame game = gameData.getGame();
                ui.print(game, color);
            }
        });
    }

    public void send(String msg) throws Exception {
        this.session.getBasicRemote().sendText(msg);
    }
}
