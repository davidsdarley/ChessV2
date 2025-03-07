//This needs to be written later. For now I'm just making all of them return affirmative results so that I can
//test stuff, and I'll need to write them later.
package dataaccess;

import server.carriers.*;
import java.util.ArrayList;


public class DataAccess {

    public InMemoryDatabase db;

    public DataAccess(){
        this.db = new InMemoryDatabase();
    }
    public UserData getUser(String username) throws DataAccessException{
        return db.getUser(username);
    }
    public boolean add(GameData game){
        return db.add(game);
    }
    public boolean add(UserData user){
        return db.add(user);
    }
    public boolean add(AuthData auth){
        return db.add(auth);
    }
    public boolean delete(AuthData auth){
        return db.delete(auth);
    }
    public AuthData getAuth(String authToken) throws DataAccessException{
        if (authToken == null){
            throw new DataAccessException("no authtoken provided");
        }
        return db.getAuth(authToken);
    }
    public AuthData alreadyLoggedIn(String username){
        for(AuthData auth: db.getAuthdb()){
            if (auth.getUsername().equals(username)){
                return auth;
            }
        }
        return null;
    }
    public ArrayList<GameData> getGames(){
        return db.getGamesdb();
    }
    public int makeGameID(){//eventually figure out what the next available game ID is and return that so no repeats
        int gamebdSize = db.getGamesdb().size();
        if (gamebdSize < 1){
            return 1000;
        }
        return db.getGamesdb().get(gamebdSize-1).getGameID() + 1;
    }
    public GameData getGame(int gameID){
        for(GameData game: db.getGamesdb()){
            if (game.getGameID() == gameID){
                System.out.println("Flag 1"+game.getName());
                return game;
            }
        }
        return null;
    }
    public JoinResult update(GameData target, JoinRequest join, String username){
        for(GameData game: db.getGamesdb()){
            if (game.getGameID() == target.getGameID()){
                if (!(join.getColor() == null) && join.getColor().equals("WHITE")){
                    if (game.setWhiteUsername(username)){
                        return new JoinResult(true, 200);
                    }
                    return new JoinResult(false, 403);
                }
                if (!(join.getColor() == null) && join.getColor().equals("BLACK")){
                    if (game.setBlackUsername(username)){
                        return new JoinResult(true, 200);
                    }
                    return new JoinResult(false, 403);
                }
            }
        }
        return new JoinResult(false, 400);
    }
    public boolean clearDatabase(){
        db = new InMemoryDatabase();
        return true;
    }
}
