package client;

import com.google.gson.Gson;
import org.junit.jupiter.api.*;
import server.Server;
import server.carriers.*;
import ui.ServerFacade;

import java.net.http.HttpResponse;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade service;

    static void setup(){
        service.reset();
        service.register("sylphrena", "lookatthiscoolcrab", "ancientdaughter@heralds.org");
    }

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);

        server.setCommit(false);
        service = new ServerFacade("http://localhost:", port);
    }

    @AfterAll
    static void stopServer() {
        server.rollback();
        server.setCommit(true);
        server.stop();

    }


//    @Test
//    public void sampleTest() {
//        Assertions.assertTrue(true);
//    }
    @Test
    @Order(1)
    @DisplayName("RegisterPositive")
    public void successRegister(){
        setup();
        String result = service.register("lifttheawesome", "JourneyBeforePancakes",
                "lift&wyndle@radiant.org");
        Assertions.assertFalse(result.equals("bad request")
                || result.equals("already taken")
                || result.equals("Error! We're so sorry your registration has failed"));
    }


    @Test
    @Order(2)
    @DisplayName("RegisterNegative")
    public void failRegister() {
        setup();
        String result = service.register("lifttheawesome",
                "JourneyBeforePancakes", null);
        Assertions.assertTrue(result.equals("bad request"));
    }

    @Test
    @Order(3)
    @DisplayName("LoginPositive")
    public void successLogin(){
        setup();
        String result = service.login("sylphrena", "lookatthiscoolcrab");
        Assertions.assertFalse(result.equals("Unauthorized")
                || result.equals("Error! We're so sorry your login has failed"));
    }
    @Test
    @Order(4)
    @DisplayName("LoginNegative")
    public void failLogin(){
        setup();
        String result = service.login("lifttheawesome", "JourneyBeforePancakes");
        Assertions.assertTrue(result.equals("Unauthorized"));
    }

    @Test
    @Order(5)
    @DisplayName("CreatePositive")
    public void successCreate(){
        setup();
        LoginResult login = new Gson().fromJson(service.login(
                "sylphrena", "lookatthiscoolcrab"), LoginResult.class);
        String auth = login.getAuthToken();
        HttpResponse<String> actual = service.create("the true desolation", auth);
        Assertions.assertTrue(actual.statusCode() == 200);
    }
    @Test
    @Order(6)
    @DisplayName("CreateNegative")
    public void failCreate(){
        setup();
        HttpResponse<String> actual = service.create("the true desolation", "NOTaNaUTHTOKEN");
        Assertions.assertFalse(actual.statusCode() == 200);
    }

    @Test
    @Order(7)
    @DisplayName("ListPositive")
    public void successList(){
        setup();
        LoginResult user = service.login(new LoginRequest("sylphrena", "lookatthiscoolcrab"));
        service.createGame(new GameRequest("the true desolation", user.authToken));
        service.createGame(new GameRequest("Battle of Thaylen Field", user.authToken));
        Object result = service.listGames(new AuthorizationRequest(user.authToken));
        Assertions.assertTrue(result.toString().equals("[gameID: 1000 whiteUsername: None blackUsername: " +
                "None, gameID: 1001 whiteUsername: None blackUsername: None]"));
    }
    @Test
    @Order(8)
    @DisplayName("ListNegative")
    public void failList(){
        setup();
        service.createGame(new GameRequest("the true desolation", "unauthorized fools"));
        service.createGame(new GameRequest("Battle of Thaylen Field", "unauthorized fools"));
        Object result = service.listGames(new AuthorizationRequest("unauthorized fools"));
        Assertions.assertFalse(result.toString().equals("[gameID: 1000 whiteUsername: None " +
                "blackUsername: None, gameID: 1001 whiteUsername: None blackUsername: None]"));
    }
    @Test
    @Order(9)
    @DisplayName("JoinPositive")
    public void successJoin(){
        setup();
        LoginResult syl = service.login(new LoginRequest("sylphrena", "lookatthiscoolcrab"));
        GameData game = (GameData) service.createGame(new GameRequest("Bridge 4", syl.authToken));
        JoinResult actual =  service.joinGame(new JoinRequest(game.getGameID(),"WHITE", syl.getAuthToken()));
        Assertions.assertTrue(actual.getResult());
    }

    @Test
    @Order(10)
    @DisplayName("JoinNegative")
    public void failJoin(){
        setup();
        LoginResult syl = service.login(new LoginRequest("sylphrena", "lookatthiscoolcrab"));
        LoginResult lift = (LoginResult) service.register(new RegisterRequest("lifttheawesome",
                "JourneyBeforePancakes", "lift&wyndle@radiant.org"));
        GameData game = (GameData) service.createGame(new GameRequest("Bridge 4", syl.authToken));
        service.joinGame(new JoinRequest(game.getGameID(),"WHITE", syl.getAuthToken()));
        JoinResult actual =  service.joinGame(new JoinRequest(game.getGameID(),"WHITE", lift.getAuthToken()));

        Assertions.assertFalse(actual.getResult());
    }
    @Test
    @Order(11)
    @DisplayName("clearDB")
    public void clearTest(){
        setup();
        Assertions.assertTrue(service.clearDatabase());
    }
    @Test
    @Order(12)
    @DisplayName("LogoutPositive")
    public void successLogout() {
        setup();
        LoginResult login = service.login(new LoginRequest("sylphrena", "lookatthiscoolcrab"));
        LogoutResult actual = service.logout(new LogoutRequest(login.getAuthToken()));
        Assertions.assertTrue(actual.getResult());
    }

    @Test
    @Order(13)
    @DisplayName("LogoutNegative")
    public void failLogout(){
        setup();
        LogoutResult actual = service.logout(new LogoutRequest("fakeAuthToken"));
        Assertions.assertFalse(actual.getResult());
    }

}
