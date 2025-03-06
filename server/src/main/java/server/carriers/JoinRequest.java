package server.carriers;
import chess.ChessGame.TeamColor;

public class JoinRequest {
    int gameID;
    TeamColor color;
    String authToken;

    public JoinRequest(int id, String color){
        if (color.toLowerCase() == "white"){
            this.color = TeamColor.WHITE;
        }
        else{
            this.color = TeamColor.BLACK;
        }
        this.gameID = id;
    }
    public void setAuthToken(String auth){
        authToken = auth;
    }
    public String getAuthToken(){
        return authToken;
    }
    public int getGameID(){
        return gameID;
    }
    public TeamColor getColor(){
        return this.color;
    }

    @Override
    public String toString(){
        return "gameID: "+gameID+" color: "+color + " authToken: "+authToken;
    }
}
