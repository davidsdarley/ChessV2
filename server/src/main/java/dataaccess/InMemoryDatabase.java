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
    public boolean add(GameData newGame){
        for(GameData game: gamesdb){
            if(newGame.equals(game)){
                return false;
            }
        }
        gamesdb.add(newGame);
        return true;
    }
    public boolean add(AuthData newAuth){
        for(GameData auth: gamesdb){
            if(newAuth.equals(auth)){
                return false;
            }
        }
        authdb.add(newAuth);
        return true;
    }
    public boolean add(UserData newUser){
        for(GameData user: gamesdb){
            if(newUser.equals(user)){
                return false;
            }
        }
        userdb.add(newUser);
        return true;
    }

}
