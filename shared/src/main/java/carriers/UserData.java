package carriers;

public class UserData {
    String username;
    String password;
    String email;

    public UserData(RegisterRequest request){
        username = request.username;
        password = request.password;
        email = request.email;
    }
    public UserData(String username, String password, String email){
        this.username = username;
        this.password = password;
        this.email = email;
    }
    public String getUsername(){
        return username;
    }
    public String getPassword(){
        return password;
    }
    public String getEmail(){
        return email;
    }
    @Override
    public boolean equals(Object other){
        if (other.getClass() != UserData.class){
            return false;
        }
        UserData otherUser = (UserData)other;
        return (otherUser.username.equals(username) && otherUser.password.equals(password) && otherUser.email.equals(email));
    }
    @Override
    public String toString(){
        return ("username: "+username+"    password: "+ password + "     email: "+email);
    }
}