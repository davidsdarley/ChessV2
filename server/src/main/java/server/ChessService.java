package server;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import server.carriers.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ChessService {
    private final DataAccess data;

    public ChessService(){
        data = new DataAccess();
    }
    public ChessService(DataAccess dataAccess){
        data = dataAccess;
    }
    public Object register(RegisterRequest registration){
        System.out.println("ChessService.register");
        try{
            UserData user = data.getUser(registration.getUsername());
            if (user == null){
                user = new UserData(registration);
                data.add(user);
                AuthData auth = new AuthData(registration.getUsername());
                data.add(auth);
                return new LoginResult(user.getUsername(), auth.getToken());
            }
            else{
                return false;
            }
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
    public LoginResult login(LoginRequest login){
        try{
            UserData user = data.getUser(login.getUsername());
            if (user != null){
                if (user.getPassword().equals(login.getPassword())){
                    AuthData auth = data.alreadyLoggedIn(login.getUsername());
                    if(auth != null){
                        return new LoginResult(user.getUsername(), auth.getToken());
                    }
                    auth = new AuthData(login.getUsername());
                    data.add(auth);
                    return new LoginResult(user.getUsername(), auth.getToken());
                }
                else{
                    return new LoginResult(null, null);
                }
            }
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        return new LoginResult(null, null);
    }
    public LogoutResult logout(LogoutRequest logout){
        try{
            AuthData auth = data.getAuth(logout.getToken());
            if (auth != null){
                return new LogoutResult(data.delete(auth));
            }
        }
        catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        return new LogoutResult(false);
    }
    public Object listGames(AuthorizationRequest authorization){
        try{
            AuthData auth = data.getAuth(authorization.getToken());
            if (auth != null){     //<--Actually use this. It's flipped now for dev purposes
                return data.getGames();
            }
        }
        catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        return false;
    }
    public Object createGame(GameRequest gameRequest){
        try {
            if (gameRequest.getGameName() == null){

            }
            AuthData auth = data.getAuth(gameRequest.getAuthToken());
            if (auth != null){
                GameData game = new GameData(gameRequest.getGameName(), data.makeGameID());
                if (data.add(game)){
                    return game;
                }
            }
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    return false;
    }
    public JoinResult joinGame(JoinRequest joinRequest){
        try {
            AuthData auth = data.getAuth(joinRequest.getAuthToken());
            if (auth != null){
                GameData game = data.getGame(joinRequest.getGameID());
                if (game != null){
                    String username = auth.getUsername();
                    return data.update(game, joinRequest, username);
                }
            }
            else{
                return new JoinResult(false, 401);
            }
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        return new JoinResult(false,500);
    }
    public boolean clearDatabase(){
        return data.clearDatabase();
    }

    public Map<String, String> makeMessage(String message){
        Map<String, String> messageMap = new HashMap<>();
        String fullMessage = "Error: " + message;
        messageMap.put("message", fullMessage);
        return messageMap;
    }
}