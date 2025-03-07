package dataaccess;
import server.carriers.*;
import java.util.ArrayList;

public class InMemoryDatabase {
    //Lists: GameData, AuthData, UserData,
    ArrayList<GameData> gamesdb;
    ArrayList<AuthData> authdb;
    ArrayList<UserData> userdb;

    public InMemoryDatabase(){
        gamesdb = new ArrayList<>();
        authdb = new ArrayList<>();
        userdb = new ArrayList<>();
    }
    public GameData getGame(GameData target){
        for(GameData game: gamesdb){
            if (target.getGameID() == game.getGameID()){
                return game;
            }
        }
        return null;
    }
    public UserData getUser(String username){
        for(UserData user: userdb){
            if (username.equals(user.getUsername())){
                return user;
            }
        }
        return null;
    }
    public AuthData getAuth(String authToken){
        for(AuthData auth: authdb){
            if (authToken.equals(auth.getToken())){
                return auth;
            }
        }
        return null;
    }


    public boolean add(GameData newGame){
        for(GameData game: gamesdb){
            if(newGame.getGameID() == game.getGameID()){
                return false;
            }
        }
        gamesdb.add(newGame);
        return true;
    }
    public boolean add(AuthData newAuth){
        for(AuthData auth: authdb){
            if(newAuth.usernameConflict(auth)){
                return false;
            }
        }
        authdb.add(newAuth);
        return true;
    }
    public boolean add(UserData newUser){
        for(UserData user: userdb){
            if(newUser.equals(user)){
                return false;
            }
        }
        userdb.add(newUser);
        return true;
    }

    public boolean delete(AuthData target){
        for(AuthData auth: authdb){
            if(target.getToken().equals(auth.getToken())){
                authdb.remove(target);
                return true;
            }
        }
        return false;
    }

    public ArrayList<AuthData> getAuthdb(){
        return authdb;
    }
    public ArrayList<UserData> getUserdb(){
        return userdb;
    }
    public ArrayList<GameData> getGamesdb(){
        return gamesdb;
    }
}
