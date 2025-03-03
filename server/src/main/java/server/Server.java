package server;

import spark.*;
import com.google.gson.Gson;

public class Server {


    public static void main(String[] args) {
        Server chessServer = new Server();
        int port = chessServer.run(8080);
        System.out.println(port);
        //chessServer.stop();
        //System.out.println("Stopped");

    }
        public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.get("/hello", Server::handleHello);
        Spark.post("/user", Server::handleRegister);

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private static Object handleHello(RegisterRequest req, Response res) {
        return "Hello chess players!";}
    private static Object handleRegister(RegisterRequest req, Response res) {
        //to register, I receive a JSON dictionary with username, password, and email. I return JSON username and authtoken.
        var details = new Gson().fromJson(req.body(), )
        return "Hello chess players!";}
}
