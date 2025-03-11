package dataaccess;

import com.google.gson.Gson;
import server.carriers.*;

import javax.xml.crypto.Data;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class SQLDataAccess {
    //initial setup
    private static final String DATABASE_NAME;
    private static final String USER;
    private static final String PASSWORD;
    private static final String CONNECTION_URL;
    //assign the values from db.properties
    static {
        try {
            try (var propStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("db.properties")) {
                if (propStream == null) {
                    throw new Exception("Unable to load db.properties");
                }
                Properties props = new Properties();
                props.load(propStream);
                DATABASE_NAME = props.getProperty("db.name");
                USER = props.getProperty("db.user");
                PASSWORD = props.getProperty("db.password");

                var host = props.getProperty("db.host");
                var port = Integer.parseInt(props.getProperty("db.port"));
                CONNECTION_URL = String.format("jdbc:mysql://%s:%d", host, port);
            }
        } catch (Exception ex) {
            throw new RuntimeException("unable to process db.properties. " + ex.getMessage());
        }
    }

    public SQLDataAccess() throws DataAccessException{
        configureDatabase();
    }
    //done
    public boolean add(GameData game){
        try(var conn = getConnection()){
            if (getGame(game.getGameID()) != null){
                return false;
            }
            var query = "INSERT INTO gameData (gameID, gameName, json) VALUES (?, ?, ?)";
            var json = new Gson().toJson(game);

            try (PreparedStatement command = conn.prepareStatement(query)){
                command.setInt(1, game.getGameID());
                command.setString(2, game.getName());
                command.setString(3, json);

                int result = command.executeUpdate();
                return result > 0;
            }

        }
        catch(DataAccessException | SQLException e){
            System.out.println(e.getMessage());
            return false;
        }
    }
    public boolean add(UserData user){

        try(var conn = getConnection()){
            if(getUser(user.getUsername()) != null){
                return false;
            }
            var query = "INSERT INTO userData (username, password, email, json) VALUES (?, ?, ?, ?)";
            var json = new Gson().toJson(user);

            try (PreparedStatement command = conn.prepareStatement(query)){
                command.setString(1, user.getUsername());
                command.setString(2, user.getPassword());
                command.setString(3, user.getEmail());
                command.setString(4, json);

                int result = command.executeUpdate();
                return result > 0;
            }

        }
        catch(DataAccessException | SQLException e){
            System.out.println(e.getMessage());
            return false;
        }
    }
    public boolean add(AuthData auth){
        try(var conn = getConnection()){
            var query = "INSERT INTO authData (username, authToken, json) VALUES (?, ?, ?)";
            var json = new Gson().toJson(auth);

            try (PreparedStatement command = conn.prepareStatement(query)){
                command.setString(1, auth.getUsername());
                command.setString(2, auth.getToken());
                command.setString(3, json);

                int result = command.executeUpdate();
                return result > 0;
            }

        }
        catch(DataAccessException | SQLException e){
            System.out.println(e.getMessage());
            return false;
        }
    }
    public boolean delete(AuthData auth){
        try(var conn = getConnection()){
            var query = "DELETE FROM authData WHERE authToken = ?";

            try (PreparedStatement command = conn.prepareStatement(query)){
                command.setString(1, auth.getToken());

                int result = command.executeUpdate();
                return result > 0;
            }

        }
        catch(DataAccessException | SQLException e){
            System.out.println(e.getMessage());
            return false;
        }
    }
    public UserData getUser(String username) throws DataAccessException{
        try(var conn = getConnection()){
            var query = "SELECT json FROM userData WHERE username = ?";

            try (PreparedStatement command = conn.prepareStatement(query)){
                command.setString(1, username);

                try(var result = command.executeQuery()){
                    if (result.next()){
                        String json = result.getString("json");
                        return new Gson().fromJson(json, UserData.class);
                    }
                    return null;
                }

            }

        }
        catch(DataAccessException | SQLException e){
            System.out.println(e.getMessage());
            return null;
        }
    }
    public AuthData getAuth(String authToken) throws DataAccessException{
        try(var conn = getConnection()){
            var query = "SELECT json FROM authData WHERE authToken = ?";

            try (PreparedStatement command = conn.prepareStatement(query)){
                command.setString(1, authToken);

                try(var result = command.executeQuery()){
                    if (result.next()){
                        String json = result.getString("json");
                        return new Gson().fromJson(json, AuthData.class);
                    }
                    return null;
                }

            }

        }
        catch(DataAccessException | SQLException e){
            System.out.println(e.getMessage());
            return null;
        }
    }
    public GameData getGame(int gameID){

        try(var conn = getConnection()){
            var query = "SELECT json FROM gameData WHERE gameID = ?";

            try (PreparedStatement command = conn.prepareStatement(query)){
                command.setInt(1, gameID);

                try(var result = command.executeQuery()){
                    if (result.next()){
                        String json = result.getString("json");
                        return new Gson().fromJson(json, GameData.class);
                    }
                    return null;
                }

            }

        }
        catch(DataAccessException | SQLException e){
            System.out.println(e.getMessage());
            return null;
        }
    }
    public ArrayList<GameData> getGames(){
        ArrayList<GameData> games = new ArrayList<>();
        try(var conn = getConnection()){
            var query = "SELECT json FROM gameData";

            try (PreparedStatement command = conn.prepareStatement(query)){
                try(var result = command.executeQuery()){
                    while (result.next()){
                        games.add(new Gson().fromJson(result.getString("json"), GameData.class));
                    }
                }

            }

        }
        catch(DataAccessException | SQLException e){
            System.out.println(e.getMessage());
        }
        finally{
            return games;
        }
    }
    public int makeGameID() throws DataAccessException{

        try(var conn = getConnection()){
            var query = "SELECT gameID FROM gameData ORDER BY gameID DESC LIMIT 1";

            try (PreparedStatement command = conn.prepareStatement(query)){
                try(var result = command.executeQuery()){
                    if (result.next()){
                        return result.getInt("gameID")+1;
                    }
                    return 1000;
                }
            }

        }
        catch(DataAccessException | SQLException e){
            System.out.println(e.getMessage());
            throw new DataAccessException(e.getMessage());
        }
    }
    private boolean deleteTable(String table){
        try(var conn = getConnection()){
            var query = "DELETE FROM "+table;
            try (PreparedStatement command = conn.prepareStatement(query)){
                int result = command.executeUpdate();
                return result > 0;
            }

        }
        catch(DataAccessException | SQLException e){
            System.out.println(e.getMessage());
            return false;
        }
    }
    public boolean clearDatabase(){
        String[] tables = {"authData", "userData", "gameData"};
        for(String table: tables){
            deleteTable(table);
        }
        return true;
    }
    //in progress
    private boolean updateGame(GameData game){
        try(var conn = getConnection()){
                var query = "UPDATE gameData SET json = ? WHERE gameID = ?";

            try (PreparedStatement command = conn.prepareStatement(query)){
                command.setString(1, new Gson().toJson(game));
                command.setInt(2, game.getGameID());

                int result = command.executeUpdate();
                return result > 0;
                }
            }
        catch(DataAccessException | SQLException e){
            System.out.println(e.getMessage());
            return false;
        }
    }
    public JoinResult update(GameData target, JoinRequest join, String username){
        GameData game = getGame(target.getGameID());
        if (game != null){
            if(join.getColor() == "WHITE" && game.setWhiteUsername(username)){
                if(updateGame(game)){
                    return new JoinResult(true, 200);
                }
                return new JoinResult(false, 403);
            }

            else if(join.getColor() == "BLACK" && game.setBlackUsername(username)){
                if(updateGame(game)){
                    return new JoinResult(true, 200);
                }
                return new JoinResult(false, 403);
            }
        }
        return new JoinResult(false, 400);
    }



    //Initial creation and configuration details
    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  authData (
              `id` int NOT NULL AUTO_INCREMENT,
              `username` varchar(256) NOT NULL,
              `authToken` varchar(50) NOT NULL,
              `json` TEXT DEFAULT NULL,
              PRIMARY KEY (`id`),
              INDEX(username),
              INDEX(authToken)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """,
            """
            CREATE TABLE IF NOT EXISTS gameData (
                `id` int NOT NULL AUTO_INCREMENT,
                `gameID` INT NOT NULL,
                `gameName` varchar(256) NOT NULL,
                `json` TEXT DEFAULT NULL,
                PRIMARY KEY (`id`),
                INDEX(gameID),
                INDEX(gameName)
            )
            ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """,
            """
            CREATE TABLE IF NOT EXISTS userData (
                `id` int NOT NULL AUTO_INCREMENT,
                `username` varchar(256) NOT NULL,
                `password` varchar(256) NOT NULL,
                `email` varchar(256) NOT NULL,
                `json` TEXT DEFAULT NULL,
                PRIMARY KEY (`id`),
                INDEX(username)
            )
            ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };
    static void createDatabase() throws DataAccessException {
        try {
            var statement = "CREATE DATABASE IF NOT EXISTS " + DATABASE_NAME;
            var conn = DriverManager.getConnection(CONNECTION_URL, USER, PASSWORD);
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }
    static Connection getConnection() throws DataAccessException {
        try {
            var conn = DriverManager.getConnection(CONNECTION_URL, USER, PASSWORD);
            conn.setCatalog(DATABASE_NAME);
            return conn;
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }
    private void configureDatabase() throws DataAccessException {
        createDatabase();
        try (var conn = getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }

    public static void main(String[] args) {
        try{
            SQLDataAccess tester = new SQLDataAccess();

            System.out.println(tester.add(new GameData("Star Wars", 1111)));
            System.out.println(tester.add(new UserData("Obi-Wan", "HelloThere", "general_kenobi@jedi.org")));
            //System.out.println(tester.add(new AuthData("Obi-Wan")));

            GameData game = tester.getGame(1111);
            System.out.println(game);
            AuthData auth = new AuthData("Obi-Wan");
            System.out.println(tester.add(auth));

            JoinRequest join = new JoinRequest(1111, "BLACK", auth.getToken());
            System.out.println(join);
            System.out.println(tester.update(game, join, "Anakin"));



        }
        catch(DataAccessException e){
            System.out.println(e.getMessage());
            throw new RuntimeException();
        }

    }

}
