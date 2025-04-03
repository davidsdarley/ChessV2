package server.webSocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;

import java.io.IOException;
import java.util.Set;


@WebSocket
public class WebSocketHandler {
    WebSocketSessions sessions;

    public WebSocketHandler(){
        sessions = new WebSocketSessions();
    }


    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws Exception {
        System.out.println(message);

        Command command = new Gson().fromJson(message, Command.class);

        String reply = "";
        if (command.getCommand().equals("CONNECT")){
            System.out.println("CONNECT");
            reply = "CONNECT";
        }
        else if (command.getCommand().equals("MAKE_MOVE")){
            System.out.println("MOVE");
            reply = "MOVE";
        }
        else if (command.getCommand().equals("RESIGN")){
            System.out.println("RESIGN");
            reply = "RESIGN";
        }
        else if(command.getCommand().equals("LEAVE")){
            System.out.println("LEAVE");
            reply = "LEAVE";
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
