package server.carriers;
import chess.ChessGame.TeamColor;


public class GameData {
    int gameID;
    String name;
    String whiteUsername;
    String blackUsername;

    public GameData(String name, int gameID){
        this.gameID =gameID;
        this.whiteUsername = null;
        this.blackUsername = null;
        this.name = name;
    }
    public int getGameID(){
        return this.gameID;
    }
    @Override
    public String toString(){
        String str = "gameID: "+ gameID+" whiteUsername: ";
        if (whiteUsername != null){
            str += whiteUsername ;
        }
        else{
            str += "None";
        }
        str += " blackUsername: ";
        if (blackUsername != null){
            str += blackUsername;
        }
        else{
            str += "None";
        }
        return str;
    }
    @Override
    public boolean equals(Object obj){
        if (obj.getClass() != GameData.class){
            return false;
        }
        GameData other = (GameData)obj;
        if (gameID == other.gameID){
            //if any of them are null, check this.

            if ((whiteUsername == null || other.whiteUsername == null) && (whiteUsername == null ^ other.whiteUsername == null)){
                return false;
            }
            if ((blackUsername == null || other.blackUsername == null) && (blackUsername == null ^ other.blackUsername == null)){
                return false;
            }
            return true;
        }
    return false;
    }

    public String getWhiteUsername(){
        return whiteUsername;
    }
    public String getBlackUsername() {
        return blackUsername;
    }
    public boolean setWhiteUsername(String username){
        if (whiteUsername == null){
            whiteUsername = username;
            return true;
        }
        return false;
    }
    public boolean setBlackUsername(String username){
        if (blackUsername == null){
            blackUsername = username;
            return true;
        }
        return false;
    }
}
