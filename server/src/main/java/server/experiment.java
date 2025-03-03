package server;
import spark.Spark;
import spark.Request;
import spark.Response;



public class experiment {

//    Spark.get(“/hello”, (req, res) -> “Hello BYU!”);
    public static void main(String[] args) {
        Spark.port(8080); // Set the server port

        Spark.get("/hello", experiment::handleHello); // Example GET endpoint

        Spark.post("/game/start", (req, res) -> {
            // Handle starting a new chess game
            return "Game started!";
        });

    System.out.println("Server is running on http://localhost:8080");
}

    private static Object handleHello(Request req, Response res) {
        return "Hello BYU!";}


}
