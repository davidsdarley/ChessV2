package dataaccess;

import org.junit.jupiter.api.*;
import server.ChessService;
import server.carriers.*;


import com.google.gson.Gson;

import javax.xml.crypto.Data;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class SQLDataAccessTests {
    static SQLDataAccess data;
    static UserData testUser;
    static AuthData testAuth;
    static GameData testGame;

    private static void setup(){
        //start with empty database
        data.clearDatabase();
        //starting data
        testUser = new UserData("Obi-Wan", "HelloThere", "general_kenobi@jedi.org") ;
        testAuth = new AuthData("Obi-Wan");
        testGame = new GameData("High Ground", 1000);
        //add the starting data
        data.add(testAuth);
        data.add(testGame);
        data.add(testUser);
    }

    @BeforeAll
    public static void init(){
        try{//setup
            data = new SQLDataAccess();
            data.setCommit(false);
            setup();
        }
        catch(DataAccessException e){
            System.out.println(e.getMessage());
            assert false;
        }
    }


    @AfterAll
    public static void cleanup(){
        try{
            data.rollback();
            data.setCommit(true);
            data.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @Order(1)
    @DisplayName("success Add game")
    public void successAddGame() {
        setup();
        GameData game = new GameData("Star Trek", 9999);
        Assertions.assertTrue(data.add(game));
    }
    @Test
    @Order(2)
    @DisplayName("fail Add game")
    public void failAddGame() {
        setup();
        GameData game = new GameData("High Ground", 1000);
        Assertions.assertFalse(data.add(game));
    }

    @Test
    @Order(3)
    @DisplayName("pass add user")
    public void AddUser() {
        setup();
        UserData user = new UserData("Anakin Skywalker", "nowThisIsPodracing", "thechosenone@jedi.org");
        Assertions.assertTrue(data.add(user));
    }
    @Test
    @Order(4)
    @DisplayName("fail add user")
    public void failAddUser() {
        setup();
        Assertions.assertFalse(data.add(testUser));
    }
    @Test
    @Order(5)
    @DisplayName("add auth")
    public void AddAuth() {
        setup();
        AuthData auth = new AuthData(testUser.getUsername());
        Assertions.assertTrue(data.add(auth));
    }
    @Test
    @Order(6)
    @DisplayName("fail add auth")
    public void failAddAuth() {
        setup();
        AuthData auth = null;
        Assertions.assertFalse(data.add(auth));
    }
    @Test
    @Order(7)
    @DisplayName("delete auth")
    public void deleteAuth(){
        setup();
        Assertions.assertTrue(data.delete(testAuth));
    }
    @Test
    @Order(8)
    @DisplayName("fail delete auth")
    public void failDeleteAuth(){
        setup();
        AuthData badAuth = new AuthData("JarJarBinks");
        Assertions.assertFalse(data.delete(badAuth));
    }
    @Test
    @Order()

}
