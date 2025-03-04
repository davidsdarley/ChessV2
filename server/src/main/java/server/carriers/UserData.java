package server.carriers;

public class UserData {
    String username;
    String password;
    String email;

    public UserData(RegisterRequest request){
        username = request.username;
        password = request.password;
        email = request.email;
    }
    public String getUsername(){
        return username;
    }

    @Override
    public boolean equals(Object other){
        if (other.getClass() != UserData.class){
            return false;
        }
        UserData otherUser = (UserData)other;
        if (otherUser.username == username && otherUser.password == password && otherUser.email == email){
            return true;
        }
        return false;
    }
}
