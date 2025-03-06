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
    public String getUsername(){
        return username;
    }

    @Override
    public boolean equals(Object obj){
        if (obj.getClass() != AuthData.class){
            return false;
        }
        AuthData other = (AuthData)obj;
        return (authToken == other.authToken && username == other.username);
    }

}
