package dataaccess;

import com.google.gson.Gson;
import server.carriers.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;

public class SQLDataAccess implements AutoCloseable{
    //initial setup
    private static final String DATABASE_NAME;
    private static final String USER;
    private static final String PASSWORD;
    private static final String CONNECTION_URL;
    private Connection conn;
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
        conn = getConnection();
    }
    private Connection getConn(){
        return this.conn;
    }
    public boolean setCommit(boolean state){
        try{
            conn.setAutoCommit(state);
            return true;
        }
        catch(SQLException e){
            return false;
        }
    }
    public void rollback() throws SQLException {
        try{
            conn.rollback();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            assert false;
        }
    }
    @Override
    public void close() throws SQLException {
        if (conn != null && !conn.isClosed()) {
            conn.close();
            System.out.println("Database connection closed.");
        }
    }

    //public methods
    public boolean add(GameData game){
        var conn = getConn();
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
        catch(SQLException e){
            System.out.println(e.getMessage());
            return false;
        }
    }
    public boolean add(UserData user){

        try{
            var conn = getConn();
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
        if (auth == null){
            return false;
        }
        var conn = getConn();
        var query = "INSERT INTO authData (username, authToken, json) VALUES (?, ?, ?)";
        var json = new Gson().toJson(auth);

        try (PreparedStatement command = conn.prepareStatement(query)){
            command.setString(1, auth.getUsername());
            command.setString(2, auth.getToken());
            command.setString(3, json);

            int result = command.executeUpdate();
            return result > 0;
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
            return false;
        }
    }
    public boolean delete(AuthData auth){
        var conn = getConn();
        var query = "DELETE FROM authData WHERE authToken = ?";

        try (PreparedStatement command = conn.prepareStatement(query)){
            command.setString(1, auth.getToken());

            int result = command.executeUpdate();
            return result > 0;
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
            return false;
        }
    }
    public UserData getUser(String username) throws DataAccessException{
        var conn = getConn();
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
        catch(SQLException e){
            System.out.println(e.getMessage());
            return null;
        }
    }
    public AuthData getAuth(String authToken) throws DataAccessException{
        var conn = getConn();
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

        catch(SQLException e){
            System.out.println(e.getMessage());
            return null;
        }
    }
    public GameData getGame(int gameID){

        var conn = getConn();
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
        catch(SQLException e){
            System.out.println(e.getMessage());
            return null;
        }
    }
    public ArrayList<GameData> getGames(){
        ArrayList<GameData> games = new ArrayList<>();
        var conn = getConn();
        var query = "SELECT json FROM gameData";

        try (PreparedStatement command = conn.prepareStatement(query)){
            try(var result = command.executeQuery()){
                while (result.next()){
                    games.add(new Gson().fromJson(result.getString("json"), GameData.class));
                }
            }
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
        }
        finally{
            return games;
        }
    }
    public int makeGameID() throws DataAccessException{

        var conn = getConn();
        var query = "SELECT gameID FROM gameData ORDER BY gameID DESC LIMIT 1";

        try (PreparedStatement command = conn.prepareStatement(query)){
            try(var result = command.executeQuery()){
                if (result.next()){
                    return result.getInt("gameID")+1;
                }
                return 1000;
            }
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
            throw new DataAccessException(e.getMessage());
        }
    }
    private boolean deleteTable(String table){
        var conn = getConn();
        var query = "DELETE FROM "+table;
        int result = 0;
        try (PreparedStatement command = conn.prepareStatement(query)){
            result = command.executeUpdate();
        }
        catch(SQLException e){
            System.out.println(e.getMessage());
            return false;
        }
        if (result > 0){
            query = "ALTER TABLE "+ table+ " AUTO_INCREMENT = 1";
            try (PreparedStatement command = conn.prepareStatement(query)){
                result = command.executeUpdate();
            }
            catch(SQLException e){
                System.out.println(e.getMessage());
                return false;
            }
        }


        return result > 0;
    }
    public boolean clearDatabase(){
        String[] tables = {"authData", "userData", "gameData"};
        for(String table: tables){
            deleteTable(table);
        }
        return true;
    }
    private boolean updateGame(GameData game){
        var conn = getConn();
        var query = "UPDATE gameData SET json = ? WHERE gameID = ?";

        try (PreparedStatement command = conn.prepareStatement(query)){
            command.setString(1, new Gson().toJson(game));
            command.setInt(2, game.getGameID());

            int result = command.executeUpdate();
            return result > 0;
            }
        catch(SQLException e){
            System.out.println(e.getMessage());
            return false;
        }
    }
    public JoinResult update(GameData target, JoinRequest join, String username){
        GameData game = getGame(target.getGameID());
        if (join.getColor() == null || !(join.getColor().equals("WHITE") || join.getColor().equals("BLACK")) ){
            return new JoinResult(false, 400);
        }
        if (game != null){

            if(join.getColor().equals("WHITE") && game.setWhiteUsername(username)){
                if(updateGame(game)){
                    return new JoinResult(true, 200);
                }
            }

            else if(join.getColor().equals("BLACK") && game.setBlackUsername(username)){
                System.out.println("FLAG1");

                if(updateGame(game)){
                    return new JoinResult(true, 200);
                }
            }
            else{
                return new JoinResult(false, 403);

            }
        }
        System.out.println(getGames());
        System.out.println(game + " not found");
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
            conn.close();
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
            tester.clearDatabase();
            tester.add(new GameData("Star Wars", tester.makeGameID()));
        }
        catch(DataAccessException e){
            System.out.println(e.getMessage());
            throw new RuntimeException();
        }
    }
}
