package client;

import com.google.gson.Gson;
import org.junit.jupiter.api.*;
import server.Server;
import carriers.*;
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
        LoginResult login = new Gson().fromJson(service.login(
                "sylphrena", "lookatthiscoolcrab"), LoginResult.class);
        String auth = login.getAuthToken();
        service.create("the true desolation", auth);
        service.create("Battle of Thaylen Field", auth);
        HttpResponse<String> result = service.list(auth);
        Assertions.assertTrue(result.statusCode() == 200);
    }
    @Test
    @Order(8)
    @DisplayName("ListNegative")
    public void failList(){
        setup();
        LoginResult login = new Gson().fromJson(service.login(
                "sylphrena", "lookatthiscoolcrab"), LoginResult.class);
        String auth = login.getAuthToken();
        service.create("the true desolation", auth);
        service.create("Battle of Thaylen Field", auth);
        HttpResponse<String> result = service.list("unauthorized fools");
        Assertions.assertFalse(result.statusCode() == 200);
    }
    @Test
    @Order(9)
    @DisplayName("JoinPositive")
    public void successJoin(){
        setup();
        LoginResult login = new Gson().fromJson(service.login(
                "sylphrena", "lookatthiscoolcrab"), LoginResult.class);
        String auth = login.getAuthToken();
        HttpResponse<String> game = service.create("Bridge 4", auth);
        int gameID =  new Gson().fromJson(game.body(), GameData.class).getGameID();
        service.list(auth);
        HttpResponse<String> actual = service.join(gameID, "WHITE", auth);
        System.out.println(actual);
        Assertions.assertTrue(actual.statusCode() == 200);
    }
    @Test
    @Order(10)
    @DisplayName("JoinNegative")
    public void failJoin(){
        setup();
        LoginResult syl = new Gson().fromJson(service.login(
                "sylphrena", "lookatthiscoolcrab"), LoginResult.class);
        LoginResult lift = new Gson().fromJson(service.register("lifttheawesome",
                "JourneyBeforePancakes", "lift&wyndle@radiant.org"), LoginResult.class);

        service.create("Bridge 4", syl.getAuthToken());
        service.list(syl.getAuthToken());
        service.join(1, "WHITE", syl.getAuthToken());
        HttpResponse<String> actual = service.join(1, "WHITE", lift.getAuthToken());

        Assertions.assertFalse(actual.statusCode() == 200);
    }
    @Test
    @Order(11)
    @DisplayName("clearDB")
    public void clearTest(){
        setup();
        Assertions.assertTrue(service.reset());
    }
    @Test
    @Order(12)
    @DisplayName("LogoutPositive")
    public void successLogout() {
        setup();
        LoginResult login = new Gson().fromJson(service.login(
                "sylphrena", "lookatthiscoolcrab"), LoginResult.class);
        String auth = login.getAuthToken();
        HttpResponse<String> result = service.logout(auth);
        Assertions.assertTrue(result.statusCode() == 200);
    }
    @Test
    @Order(13)
    @DisplayName("LogoutNegative")
    public void failLogout(){
        setup();

        Assertions.assertFalse(service.logout("Moash").statusCode() == 200);
    }

}
