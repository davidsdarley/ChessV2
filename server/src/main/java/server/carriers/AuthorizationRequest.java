package server.carriers;

public class AuthorizationRequest {
    String authToken;
    String message;
    public AuthorizationRequest(String authToken){
        this.authToken = authToken;
        message = null;
    }
    public AuthorizationRequest(String authToken, String message){
        this.authToken = authToken;
        this.message = message;
    }
    public String getToken(){
        return authToken;
    }
}
