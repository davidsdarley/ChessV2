package server;

import server.carriers.*;
import spark.*;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Server {
    private static ChessService service;
    public Server(ChessService service){
        this.service = service;
    }
    public Server(){
        this.service = new ChessService();
    }
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
        Spark.post("/session", Server::handleLogin);
        Spark.delete("/session", Server::handleLogout);
        Spark.get("/game", Server::handleListGames);
        Spark.post("/game",Server::handleCreateGame);

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private static Object handleHello(Request req, Response res) {
        return "Hello chess players!";}
    private static Object handleRegister(Request req, Response res) {
        //to register, I receive a JSON dictionary with username, password, and email. I return JSON username and authtoken.
        var registration = new Gson().fromJson(req.body(), RegisterRequest.class);
        Object result = service.register(registration);
        System.out.println(result);
        return new Gson().toJson(result);
    }
    private static Object handleLogin(Request req, Response res) {
        var log = new Gson().fromJson(req.body(), LoginRequest.class);
        Object result = service.login(log);
        if (result.getClass() != LoginResult.class){
            res.status(409);
        }
        System.out.println(result);
        return new Gson().toJson(result);
    }
    private static Object handleLogout(Request req, Response res) {
        String auth = req.headers("authToken");
        Object result = service.logout(new LogoutRequest(auth));
        if (result.getClass() != LogoutResult.class){
            res.status(409);
            return "{}";
        }
        LogoutResult logout = (LogoutResult)result;
        if (logout.getResult()){
            res.status(200);

        }
        else{
            res.status(404);
        }
        return "{}";
    }
    private static Object handleListGames(Request req, Response res){
        String auth = req.headers("authToken");
        Object result = service.listGames(new AuthorizationRequest(auth));

        if (!(result instanceof ArrayList)){
            res.status(409);
            return "{}";
        }

        res.status(200);
        Map<String, ArrayList> message = new HashMap<>();
        message.put("games", (ArrayList)result);
        return new Gson().toJson(message);
    }
    private static Object handleCreateGame(Request req, Response res){
        String auth = req.headers("authToken");
        Object result = service.createGame(new AuthorizationRequest(auth));
        System.out.println(req.body());

        return true;
    }
}
