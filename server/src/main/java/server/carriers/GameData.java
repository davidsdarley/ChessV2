package server.carriers;
import java.util.Random;

public class GameData {
    int gameID;
    String name;
    String whiteUsername;
    String blackUsername;
    public GameData(int gameID, String whiteUsername , String blackUsername){
        this.gameID =gameID;
        this.whiteUsername = whiteUsername;
        this.blackUsername = blackUsername;
        this.name = "game"+String.valueOf(gameID);
    }
    public GameData(String name, int gameID){
        this.gameID =gameID;
        this.whiteUsername = null;
        this.blackUsername = null;
        this.name = name;
    }
    public GameData(int gameID){
        this.gameID = gameID;
        this.whiteUsername = null;
        this.blackUsername = null;
        this.name = "game"+String.valueOf(gameID);
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
}
