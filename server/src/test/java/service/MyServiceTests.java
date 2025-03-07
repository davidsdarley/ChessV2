package service;
import org.junit.jupiter.api.*;
import server.ChessService;
import server.carriers.*;

public class MyServiceTests {
    static ChessService service;

    static void setup(){
        service.clearDatabase();
        service.register(new RegisterRequest("sylphrena",
                "lookatthiscoolcrab", "ancientdaughter@heralds.org"));
    }
    @AfterAll
    static void stopServer() {
        service = null;
    }
    @BeforeAll
    public static void init(){
        service = new ChessService();
    }
    @Test
    @Order(1)
    @DisplayName("RegisterPositive")
    public void successRegister(){
        setup();
        RegisterRequest input = new RegisterRequest("lifttheawesome",
                "JourneyBeforePancakes", "lift&wyndle@radiant.org");
        Assertions.assertTrue(service.register(input) instanceof LoginResult);
        }


    @Test
    @Order(2)
    @DisplayName("RegisterNegative")
    public void failRegister() {
        setup();
        RegisterRequest input = new RegisterRequest("lifttheawesome",
                "JourneyBeforePancakes", null);
        service.register(input);
        Object result = service.register(input);
        if (result instanceof LoginResult) {
            LoginResult actual = (LoginResult) result;
            Assertions.assertFalse(actual.loginSuccessful());
        }
        else{
            Assertions.assertFalse((boolean)result);
        }
    }
    @Test
    @Order(3)
    @DisplayName("LoginPositive")
    public void successLogin(){
        setup();
        LoginRequest input = new LoginRequest("sylphrena", "lookatthiscoolcrab");
        Assertions.assertTrue(service.login(input).loginSuccessful());
    }
    @Test
    @Order(4)
    @DisplayName("LoginNegative")
    public void failLogin(){
        setup();
        LoginRequest input = new LoginRequest("lifttheawesome", "JourneyBeforePancakes");
        Assertions.assertFalse(service.login(input).loginSuccessful());
    }

    @Test
    @Order(3)
    @DisplayName("LogoutPositive")
    public void successLogout() {
        setup();
        LoginResult login = service.login(new LoginRequest("sylphrena", "lookatthiscoolcrab"));
        LogoutResult actual = service.logout(new LogoutRequest(login.getAuthToken()));
        Assertions.assertTrue(actual.getResult());
    }

    @Test
    @Order(4)
    @DisplayName("LogoutNegative")
    public void failLogout(){
        setup();
        LogoutResult actual = service.logout(new LogoutRequest("fakeAuthToken"));
        Assertions.assertFalse(actual.getResult());
    }

    @Test
    @Order(5)
    @DisplayName("CreatePositive")
    public void successCreate(){
        setup();
        LoginResult user = service.login(new LoginRequest("sylphrena", "lookatthiscoolcrab"));
        Object actual = service.createGame(new GameRequest("the true desolation", user.getAuthToken()));
        Assertions.assertTrue(actual instanceof GameData);
    }
    @Test
    @Order(6)
    @DisplayName("CreateNegative")
    public void failCreate(){
        setup();
        Object actual = service.createGame(new GameRequest("the true desolation", "NOTaNaUTHTOKEN"));
        Assertions.assertFalse(actual instanceof GameData);
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
}

