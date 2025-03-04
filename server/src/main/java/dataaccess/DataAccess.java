package dataaccess;

import server.carriers.AuthData;
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
        System.out.println("DataAccess.getUser");
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

}
