package server.carriers;

public class LoginResult {
    public String username;
    public String authToken;
    public LoginResult(String username, String authToken){
        this.username = username;
        this.authToken = authToken;
    }
}
