package server;

import carriers.*;
import spark.*;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Server {
    private static ChessService service;
    private static WebSocketHandler webSocketHandler;

    public Server(ChessService service){
        this.service = service;
        webSocketHandler = new WebSocketHandler();
    }
    public Server(){
        this.service = new ChessService();
        webSocketHandler = new WebSocketHandler();
    }
    public void setCommit(boolean set){
        service.setCommit(set);
    }
    public void rollback(){
        service.rollback();
    }
    public static void main(String[] args) {
        Server chessServer = new Server();
        int port = chessServer.run(8080);
        System.out.println(port);
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
        Spark.put("/game", Server::handleJoinGame);
        Spark.delete("/db", Server::handleDatabaseDoomsday);

        Spark.webSocket("ws/", webSocketHandler);

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
        if (!registration.filledOut()){
            res.status(400);
            return new Gson().toJson(service.makeMessage("bad request"));        }
        Object result = service.register(registration);
        if (result instanceof LoginResult){
            return new Gson().toJson(result);
        }
        res.status(403);
        return new Gson().toJson(service.makeMessage("already taken"));
    }
    private static Object handleLogin(Request req, Response res){
        try {
            var log = new Gson().fromJson(req.body(), LoginRequest.class);
            LoginResult result = service.login(log);
            if (result.isEmpty()) {
                res.status(401);
                return new Gson().toJson(service.makeMessage("unauthorized"));
            }
            res.status(200);
            return new Gson().toJson(result);
        }
        catch(RuntimeException e){
            res.status(500);
            return new Gson().toJson(service.makeMessage("unauthorized"));        }
    }
    private static Object handleLogout(Request req, Response res) {
        try{
            String auth = req.headers("authorization");
            LogoutResult result = service.logout(new LogoutRequest(auth));
            System.out.println(result);

            if (result.getResult()){
                res.status(200);
                return "{}";
            }
            else{
                res.status(401);
                return new Gson().toJson(service.makeMessage("unauthorized"));
            }
        }
        catch(RuntimeException e){
            res.status(500);
            return new Gson().toJson(service.makeMessage(e.toString()));        }
    }
    private static Object handleListGames(Request req, Response res){
        String auth = req.headers("authorization");
        Object result = service.listGames(new AuthorizationRequest(auth));

        if (!(result instanceof ArrayList)){
            res.status(401);
            return new Gson().toJson(service.makeMessage("unauthorized"));
        }

        res.status(200);
        Map<String, ArrayList> message = new HashMap<>();
        message.put("games", (ArrayList)result);
        return new Gson().toJson(message);
    }
    private static Object handleCreateGame(Request req, Response res){
        System.out.println("Running");
        String auth = req.headers("authorization");
        var gameRequest = new Gson().fromJson(req.body(), GameRequest.class);
        gameRequest.setAuthToken(auth);
        if (gameRequest.getGameName() == null){
            res.status(400);
            return new Gson().toJson(service.makeMessage("bad request"));
        }
        Object result = service.createGame(gameRequest);
        if (!(result instanceof GameData)){
            res.status(401);
            return new Gson().toJson(service.makeMessage("unauthorized"));
        }
        GameData game = ((GameData) result);
        res.status(200);

        Map<String, String> message = new HashMap<>();
        message.put("gameID", String.valueOf(game.getGameID()));
        return new Gson().toJson(message);
    }
    private static Object handleJoinGame(Request req, Response res){
        String auth = req.headers("authorization");
        var joinRequest = new Gson().fromJson(req.body(), JoinRequest.class);
        if (joinRequest.getGameID() == 0){
            res.status(400);
            return new Gson().toJson( service.makeMessage("bad request"));
        }
        joinRequest.setAuthToken(auth);
        JoinResult result = service.joinGame(joinRequest);

        if (result.getResult()){
            res.status(200);
            return "{}";
        }
        else{
            res.status(result.getCode());
            if (result.getCode() == 400){
                return new Gson().toJson( service.makeMessage("bad request"));
            }
            if (result.getCode() == 401){
                return new Gson().toJson( service.makeMessage("unauthorized"));
            }
            if (result.getCode() == 403){
                return new Gson().toJson( service.makeMessage("unauthorized"));
            }
            res.status(500);

                return new Gson().toJson(service.makeMessage("Dunno. It broke real good"));

        }
    }
    private static Object handleDatabaseDoomsday(Request req, Response res){
        boolean result = service.clearDatabase();
        if (result){
            res.status(200);
        }
        else{
            res.status(500);
        }
        return "{}";
    }
}
