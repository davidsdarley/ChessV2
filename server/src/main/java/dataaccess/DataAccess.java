//This needs to be written later. For now I'm just making all of them return affirmative results so that I can
//test stuff, and I'll need to write them later.
package dataaccess;

import chess.ChessGame;
import server.carriers.*;
import java.util.ArrayList;


public class DataAccess {

    InMemoryDatabase db;

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
    public ArrayList<GameData> getGames(){
        return db.getGamesdb();
    }
    public int makeGameID(){//eventually figure out what the next available game ID is and return that so no repeats
        return db.getGamesdb().size();
    }
    public GameData getGame(int gameID){
        for(GameData game: db.getGamesdb()){
            if (game.getGameID() == gameID){
                return game;
            }
        }
        return null;
    }
    public boolean update(GameData target, JoinRequest join, String username){
        for(GameData game: db.getGamesdb()){
            if (game.getGameID() == target.getGameID()){
                if (join.getColor() == ChessGame.TeamColor.WHITE){
                    return game.setWhiteUsername(username);
                }
                if (join.getColor() == ChessGame.TeamColor.BLACK){
                    return game.setBlackUsername(username);
                }
            }
        }
        return false;
    }
    public boolean clearDatabase(){
        db = new InMemoryDatabase();
        return true;
    }
}
