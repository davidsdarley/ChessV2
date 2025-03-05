package server.carriers;

public class AuthorizationRequest {
    String authToken;
    public AuthorizationRequest(String authToken){
        this.authToken = authToken;
    }
    public String getToken(){
        return authToken;
    }
}
