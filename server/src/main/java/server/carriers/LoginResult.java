package server.carriers;

public class LoginResult {
    public String username;
    public String authToken;
    public LoginResult(String username, String authToken){
        this.username = username;
        this.authToken = authToken;
    }
    public String getUsername(){
        return username;
    }
    public String getAuthToken(){
        return authToken;
    }
    public boolean isEmpty(){
        return (username == null || authToken == null);
    }

    @Override
    public String toString(){
        return "username: "+ username +" authToken: " + authToken;
    }
    @Override
    public boolean equals(Object obj){
        if (! (obj instanceof LoginResult)){
            return false;
        }
        LoginResult other = (LoginResult) obj;
        return(other.getAuthToken().equals(authToken) && other.getUsername().equals(username));
    }
    public boolean loginSuccessful(){
        return authToken != null;
    }
}
