package server.webSocket;

import org.eclipse.jetty.websocket.api.Session;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class WebSocketSessions {

    final ConcurrentHashMap<Integer, Set<Session>> sessions;

    public WebSocketSessions(){
        sessions = new ConcurrentHashMap<>();
    }


    public void addSessionToGame(int gameID, Session session){
        Set<Session> currentSet = getSessionsForGame(gameID);
        currentSet.add(session);
        sessions.put(gameID, currentSet);
    }
    public void removeSessionFromGame(int gameID, Session session){
        Set<Session> currentSet = getSessionsForGame(gameID);
        currentSet.remove(session);
        session.close();
        sessions.put(gameID, currentSet);
    }

    public Set<Session> getSessionsForGame(int gameID){
        Set<Session> currentSet;
        if (sessions.containsKey(gameID)){
            currentSet = sessions.get(gameID);
        }
        else{
            currentSet = new HashSet<>();
        }
        return currentSet;
    }
    public boolean removeSession(Session session){
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
    @Override
    public String toString(){
        return "WS is running";
    }
}
