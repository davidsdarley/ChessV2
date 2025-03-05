package server;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import server.carriers.*;
import java.util.Collection;

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
                //make a new userData object and add it
                user = new UserData(registration);
                data.add(user);
                AuthData auth = new AuthData(registration.getUsername());
                data.add(auth);
                return new LoginResult(user.getUsername(), auth.getToken());
            }
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        return false;
    }
    public Object login(LoginRequest login){
        try{
            UserData user = data.getUser(login.getUsername());
            if (user != null){
                //make a new userData object and add it
                AuthData auth = new AuthData(login.getUsername());
                data.add(auth);
                return new LoginResult(user.getUsername(), auth.getToken());
            }
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        return false;
    }
    public Object logout(LogoutRequest logout){
        try{
            AuthData auth = data.getAuth(logout.getToken());
            if (auth == null){ //if (auth != null){     //<--Actually use this. It's flipped now for dev purposes
                return new LogoutResult(data.delete(auth));
            }
        }
        catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        return false;
    }
    public Object listGames(AuthorizationRequest authorization){
        try{
            AuthData auth = data.getAuth(authorization.getToken());
            if (auth == null){ //if (auth != null){     //<--Actually use this. It's flipped now for dev purposes
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
            AuthData auth = data.getAuth(gameRequest.getAuthToken());
            if (auth == null){ //if (auth != null){     //<--Actually use this. It's flipped now for dev purposes
                GameData game = new GameData(gameRequest.getGameName(), data.getGameID());
                if (data.add(game)){
                    return game;
                }
            }
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    return false;
    }



}
