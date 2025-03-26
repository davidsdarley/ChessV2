package carriers;

public class LoginRequest {
    //RegisterRequests have username, password, and email.
    String username;
    String password;

    public LoginRequest(String username, String password){
        this.username = username;
        this.password = password;
    }
    public String getUsername(){
        return username;
    }
    public String getPassword(){
        return password;
    }
    @Override
    public String toString(){
        String str = "username: ";
        str += username + "     password: " + password;
        return str;
    }
}
