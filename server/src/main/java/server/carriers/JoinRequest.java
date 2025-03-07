package server.carriers;

public class JoinRequest {
    int gameID;
    String playerColor;
    String authToken;

    public JoinRequest(int id, String color){
        this.playerColor = color;
        this.gameID = id;
    }
    public JoinRequest(int id, String color, String authToken){
        this.playerColor = color;
        this.gameID = id;
        this.authToken = authToken;
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
    public String getColor(){
        return this.playerColor;
    }

    @Override
    public String toString(){
        return "gameID: "+gameID+" color: "+playerColor + " authToken: "+authToken;
    }
}
