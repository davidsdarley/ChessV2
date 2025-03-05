package server.carriers;

public class GameRequest {
    String gameName;
    String authToken;
    public GameRequest(String gameName){
        this.gameName = gameName;
    }
    public void setAuthToken(String authToken){
        this.authToken = authToken;
    }

    @Override
    public String toString(){
        String str = "gameName: ";
        str += gameName + "     authToken: " + authToken;
        return str;
    }
}

