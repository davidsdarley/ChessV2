package server;

public class RegisterRequest {
    //RegisterRequests have username, password, and email.
    String username;
    String password;
    String email;

    public RegisterRequest(String username, String password, String email){
        this.username = username;
        this.password = password;
        this.email = email;
    }

    @Override
    public String toString(){
        String str = "username: ";
        str += username + "     password: " + password + "     email: " +email;
        return str;
    }
}
