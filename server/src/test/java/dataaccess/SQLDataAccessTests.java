package dataaccess;

import carriers.*;
import org.junit.jupiter.api.*;


import java.sql.SQLException;
import java.util.ArrayList;

public class SQLDataAccessTests {
    static DatabaseManager data;
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
            data = new DatabaseManager();
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
    public void addUser() {
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
    public void addAuth() {
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
    @Order(9)
    @DisplayName("getUser")
    public void getUser(){
        setup();
        try{
            UserData actual = data.getUser(testUser.getUsername());
            Assertions.assertTrue(actual.getUsername().equals(testUser.getUsername()));
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
    @Test
    @Order(10)
    @DisplayName("fail getUser")
    public void failGetUser(){
        setup();
        try {
            Assertions.assertTrue(data.getUser("JarJarBinks") == null);
        }
        catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

    }
    @Test
    @Order(11)
    @DisplayName("getAuth")
    public void getAuth(){
        setup();
        try {
            Assertions.assertTrue(data.getAuth(testAuth.getToken()).equals(testAuth));
        }
        catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
    @Test
    @Order(12)
    @DisplayName("fail getAuth")
    public void failGetAuth(){
        setup();
        try {
            Assertions.assertTrue(data.getAuth("Fakeauthtoken") == null);
        }
        catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
    @Test
    @Order(13)
    @DisplayName("getGame")
    public void getGame(){
        setup();
        Assertions.assertTrue(data.getGame(testGame.getGameID()).equals(testGame));
    }
    @Test
    @Order(14)
    @DisplayName("fail getGame")
    public void failGetGame(){
        setup();
        Assertions.assertTrue(data.getGame(1234) == null);
    }
    @Test
    @Order(15)
    @DisplayName("getGames")
    public void getGames(){
        setup();
        ArrayList<GameData> games = data.getGames();
        Assertions.assertTrue(games.get(0).equals(testGame));
    }
    @Test
    @Order(16)
    @DisplayName("fail getGames")
    public void failGetGames(){
        data.clearDatabase();
        ArrayList<GameData> games = data.getGames();
        Assertions.assertTrue(games.size() == 0);
    }
    @Test
    @Order(17)
    @DisplayName("Test making gameIDs")
    public void makeGameID(){
        setup();
        try{Assertions.assertTrue(data.makeGameID() == 1001);} catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
    //the above can't really fail unless something goes wrong in the SQLConnection, so no failed test here
    @Test
    @Order(18)
    @DisplayName("Test clear")
    public void clearDB(){
        data.clearDatabase();
        ArrayList<GameData> games = data.getGames();
        Assertions.assertTrue(games.size() == 0);
        Assertions.assertFalse(data.delete(testAuth));
        try {
            Assertions.assertTrue(data.getUser(testUser.getUsername()) == null);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @Order(19)
    @DisplayName("join game")
    public void joinGame(){
        setup();
        JoinResult actual = data.update(testGame, new JoinRequest(1000, "WHITE", testAuth.getToken()), testUser.getUsername());
        Assertions.assertTrue(actual.getResult());
    }
    @Test
    @Order(20)
    @DisplayName("fail join game")
    public void failJoinGame(){
        setup();
        data.update(testGame, new JoinRequest(1000, "WHITE", testAuth.getToken()), testUser.getUsername());
        JoinResult actual = data.update(testGame, new JoinRequest(1000, "WHITE", testAuth.getToken()), "Anakin Skywalker");
        Assertions.assertFalse(actual.getResult());
    }
}
