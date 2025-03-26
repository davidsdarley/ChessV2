package ui.carriers;

public class GameRequest {
    String gameName;
    String authToken;
    public GameRequest(String gameName){
        this.gameName = gameName;
    }
    public GameRequest(String gameName, String authToken){
        this.gameName = gameName;
        this.authToken = authToken;
    }
    public void setAuthToken(String authToken){
        this.authToken = authToken;
    }
    public String getGameName(){
        return gameName;
    }
    public String getAuthToken(){
        return authToken;
    }

    @Override
    public String toString(){
        String str = "gameName: ";
        str += gameName + "     authToken: " + authToken;
        return str;
    }
}

