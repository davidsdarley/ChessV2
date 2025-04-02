package server;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.eclipse.jetty.websocket.api.*;
import spark.Spark;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@WebSocket
public class WebSocketHandler {
    Map<Integer, Set<Session>> sessions;

    public WebSocketHandler(){
        sessions = new HashMap<>();
    }

    public static void main(String[] args) {
        Spark.port(8080);
        Spark.webSocket("/ws", WebSocketHandler.class);
        Spark.get("/echo/:msg", (req, res) -> "HTTP response: " + req.params(":msg"));
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws Exception {
        session.getRemote().sendString("WebSocket response: " + message);
    }
    @OnWebSocketClose
    public void onClose(Session session){
        removeSession(session);
    }
    @OnWebSocketConnect
    public void onConnect(Session session){
    }
    @OnWebSocketError
    public void onError(Throwable throwable){
    }

    //connection manager
    private void addSessionToGame(int gameID, Session session){
        Set<Session> currentSet = getSessionsForGame(gameID);
        currentSet.add(session);
        sessions.put(gameID, currentSet);
    }
    private void removeSessionFromGame(int gameID, Session session){
        Set<Session> currentSet = getSessionsForGame(gameID);
        currentSet.remove(session);
        session.close();
        sessions.put(gameID, currentSet);
    }

    private Set<Session> getSessionsForGame(int gameID){
        Set<Session> currentSet;
        if (sessions.containsKey(gameID)){
            currentSet = sessions.get(gameID);
        }
        else{
            currentSet = new HashSet<>();
        }
        return currentSet;
    }
    private boolean removeSession(Session session){
        for(int game: sessions.keySet()){
            Set<Session> currentSet = getSessionsForGame(game);
            if (currentSet.contains(session)){
                currentSet.remove(session);
                session.close();
                return true;
            }
        }
        return false;
    }
}