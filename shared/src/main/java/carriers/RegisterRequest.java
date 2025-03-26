package carriers;

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
    public String getUsername(){
        return username;
    }
    public boolean filledOut(){
        if (username == null || password == null || email == null){
            return false;
        }
        return true;
    }
    public String getPassword(){return password;}
    public String getEmail(){return email;}
    @Override
    public String toString(){
        String str = "username: ";
        str += username + "     password: " + password + "     email: " +email;
        return str;
    }
}
