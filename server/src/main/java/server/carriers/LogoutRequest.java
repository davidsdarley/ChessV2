package server.carriers;

public class LogoutRequest {
    String authToken;
    public LogoutRequest(String authToken){
        this.authToken = authToken;
    }
    public String getToken(){
        return authToken;
    }
    @Override
    public String toString(){
        return "authToken: " + authToken;
    }
}
