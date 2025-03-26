package client;

import org.junit.jupiter.api.*;
import server.Server;
import server.carriers.RegisterRequest;
import ui.ServerFacade;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade service;

    static void setup(){
        service.reset();
        service.register(new RegisterRequest("sylphrena",
                "lookatthiscoolcrab", "ancientdaughter@heralds.org"));
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
    public void sampleTest() {
        Assertions.assertTrue(true);
    }

}
