package server;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import server.carriers.*;

public class ChessService {
    private final DataAccess data;

    public ChessService(){
        data = new DataAccess();
    }
    public ChessService(DataAccess dataAccess){
        data = dataAccess;
    }
    public Object register(RegisterRequest registration){
        System.out.println("ChessService.register");
        try{
            UserData user = data.getUser(registration.getUsername());
            if (user == null){
                //make a new userData object and add it
                user = new UserData(registration);
                data.add(user);
                AuthData auth = new AuthData(registration.getUsername());
                data.add(auth);
                return new RegisterResult(user.getUsername(), auth.getToken());
            }
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        return false;
    }
    public Object login(LoginRequest login){

        return false;
    }




}
