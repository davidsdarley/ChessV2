package server.carriers;
import java.util.UUID;

public class AuthData {
    String username;
    String authToken;
    public AuthData(String username){
        this.username = username;
        this.authToken = UUID.randomUUID().toString();
    }
    public String getToken(){
        return authToken;
    }
}
