//This needs to be written later. For now I'm just making all of them return affirmative results so that I can
//test stuff, and I'll need to write them later.
package dataaccess;

import server.carriers.AuthData;
import server.carriers.RegisterRequest;
import server.carriers.UserData;

public class DataAccess {
    private String users = "ChessUsers123.json";
    //private  String games;

    public DataAccess(String userfile){
        this.users = userfile;
    }
    public DataAccess(){
        this.users = "ChessUsers123.json"; //
    }
    public UserData getUser(String username) throws DataAccessException{
//        try {
//            Gson gson = new Gson();
//            BufferedReader reader = new BufferedReader(new FileReader(users));
//            List<UserData> userList = gson.fromJson(reader, new TypeToken<List<UserData>>(){}.getType());
//
//            for (UserData user: userList) {
//                System.out.println(user);
//                if (user.username == username) {
//                    return user;
//                }
//            }
//        }
//        catch(IOException e){
//            throw new DataAccessException("JSON reading error");
//        }
        if (username.equals( "davidsdarley")){
            return new UserData(new RegisterRequest(username, "goliathsux123", "email@place.com"));}
        return null;
    }
    public boolean add(UserData user){
        System.out.println(user);
        return true;
    }
    public boolean add(AuthData auth){
        System.out.println(auth);
        return true;
    }
    public boolean delete(AuthData auth){
        System.out.println(auth);
        return true;
    }
    public AuthData getAuth(String authToken) throws DataAccessException{
        if (authToken == null){
            throw new DataAccessException("no authtoken provided");
        }
        return null;
    }

}
