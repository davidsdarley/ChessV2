package service;
import com.google.gson.Gson;
import org.junit.jupiter.api.*;
import server.Server;
import server.carriers.LoginResult;
import spark.Spark;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


public class myAPITests {
    static Server server;

    @AfterAll
    static void stopServer() {
        server.stop();
        Spark.awaitStop();
    }

    @BeforeAll
    public static void init(){
        server = new Server();
        server.run(8080);
        Spark.awaitInitialization();
    }

    @Test
    @Order(1)
    @DisplayName("RegisterPositive")
    public void successRegister(){
        server.reset();
        String json = "{\"username\":\"user1name1\", \"password\":\"supercalifragilisticexpialadocious\", \"email\":\"madeupemail@internet.com\"}";

        HttpRequest.Builder bob = HttpRequest.newBuilder();
        bob.uri(URI.create("http://localhost:8080/user"));
        bob.POST(HttpRequest.BodyPublishers.ofString(json));

        HttpRequest request = bob.build();

        HttpClient testRequester = HttpClient.newHttpClient();
        try{
            HttpResponse<String> actual = testRequester.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println(actual);
            Assertions.assertTrue(200 == actual.statusCode());
        }
        catch(java.io.IOException e){
            assert false: "You had an IOException";
        }
        catch(java.lang.InterruptedException e){
            assert false: "You had an InterruptedException";
        }
    }
    @Test
    @Order(2)
    @DisplayName("RegisterPositive")
    public void failRegister(){
        String json = "{\"password\":\"supercalifragilisticexpialadocious\", \"email\":\"madeupemail@internet.com\"}";

        HttpRequest.Builder bob = HttpRequest.newBuilder();
        bob.uri(URI.create("http://localhost:8080/user"));
        bob.POST(HttpRequest.BodyPublishers.ofString(json));

        HttpRequest request = bob.build();

        HttpClient testRequester = HttpClient.newHttpClient();
        try{
            HttpResponse<String> actual = testRequester.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println(actual);
            Assertions.assertFalse(200 == actual.statusCode());
        }
        catch(java.io.IOException e){
            assert false: "You had an IOException";
        }
        catch(java.lang.InterruptedException e){
            assert false: "You had an InterruptedException";
        }
    }

    @Test
    @Order(3)
    @DisplayName("LoginPositive")
    public void successLogin(){
        String json = "{\"username\":\"user1name1\", \"password\":\"supercalifragilisticexpialadocious\", \"email\":\"madeupemail@internet.com\"}";

        HttpRequest.Builder bob = HttpRequest.newBuilder();
        bob.uri(URI.create("http://localhost:8080/user"));
        bob.POST(HttpRequest.BodyPublishers.ofString(json));

        HttpRequest request = bob.build();

        HttpClient testRequester = HttpClient.newHttpClient();

        try{
            HttpResponse<String> actual = testRequester.send(request, HttpResponse.BodyHandlers.ofString());
        }
        catch(java.io.IOException e){
            assert false: "You had an IOException";
        }
        catch(java.lang.InterruptedException e){
            assert false: "You had an InterruptedException";
        }

        json = "{\"username\":\"user1name1\", \"password\":\"supercalifragilisticexpialadocious\"}";

        bob = HttpRequest.newBuilder();
        bob.uri(URI.create("http://localhost:8080/session"));
        bob.POST(HttpRequest.BodyPublishers.ofString(json));

        request = bob.build();
        testRequester = HttpClient.newHttpClient();

        try{
            HttpResponse<String> actual = testRequester.send(request, HttpResponse.BodyHandlers.ofString());
            Assertions.assertTrue(200 == actual.statusCode());
        }
        catch(java.io.IOException e){
            assert false: "You had an IOException";
        }
        catch(java.lang.InterruptedException e){
            assert false: "You had an InterruptedException";
        }
    }

    @Test
    @Order(4)
    @DisplayName("LoginNegative")
    public void failLogin(){
        String json = "{\"username\":\"user1name1\", \"password\":\"supercalifragilisticexpialadocious\", \"email\":\"madeupemail@internet.com\"}";

        HttpRequest.Builder bob = HttpRequest.newBuilder();
        bob.uri(URI.create("http://localhost:8080/user"));
        bob.POST(HttpRequest.BodyPublishers.ofString(json));

        HttpRequest request = bob.build();

        HttpClient testRequester = HttpClient.newHttpClient();

        try{
            HttpResponse<String> actual = testRequester.send(request, HttpResponse.BodyHandlers.ofString());
        }
        catch(java.io.IOException e){
            assert false: "You had an IOException";
        }
        catch(java.lang.InterruptedException e){
            assert false: "You had an InterruptedException";
        }

        json = "{\"username\":\"user1name1\", \"password\":\"wrongPassword\"}";

        bob = HttpRequest.newBuilder();
        bob.uri(URI.create("http://localhost:8080/session"));
        bob.POST(HttpRequest.BodyPublishers.ofString(json));

        request = bob.build();
        testRequester = HttpClient.newHttpClient();

        try{
            HttpResponse<String> actual = testRequester.send(request, HttpResponse.BodyHandlers.ofString());
            Assertions.assertTrue(401 == actual.statusCode());
        }
        catch(java.io.IOException e){
            assert false: "You had an IOException";
        }
        catch(java.lang.InterruptedException e){
            assert false: "You had an InterruptedException";
        }
    }

    @Test
    @Order(3)
    @DisplayName("LogoutPositive")
    public void successLogout(){
        server.reset();

        String json = "{\"username\":\"user1name1\", \"password\":\"supercalifragilisticexpialadocious\", \"email\":\"madeupemail@internet.com\"}";

        HttpRequest.Builder bob = HttpRequest.newBuilder();
        bob.uri(URI.create("http://localhost:8080/user"));
        bob.POST(HttpRequest.BodyPublishers.ofString(json));

        HttpRequest request = bob.build();

        HttpClient testRequester = HttpClient.newHttpClient();

        try{
            HttpResponse<String> actual = testRequester.send(request, HttpResponse.BodyHandlers.ofString());
            LoginResult result = new Gson().fromJson(actual.body(), LoginResult.class);
            String auth =  result.getAuthToken();

            bob = HttpRequest.newBuilder();
            bob.uri(URI.create("http://localhost:8080/session"));
            bob.header("authorization", auth);
            bob.DELETE();

            request = bob.build();
            testRequester = HttpClient.newHttpClient();

            actual = testRequester.send(request, HttpResponse.BodyHandlers.ofString());
            Assertions.assertTrue(200 == actual.statusCode());
        }
        catch(java.io.IOException e){
            assert false: "You had an IOException";
        }
        catch(java.lang.InterruptedException e){
            assert false: "You had an InterruptedException";
        }
    }

    @Test
    @Order(4)
    @DisplayName("LogoutNegative")
    public void failLogout(){
        String json = "{\"username\":\"user1name1\", \"password\":\"supercalifragilisticexpialadocious\", \"email\":\"madeupemail@internet.com\"}";

        HttpRequest.Builder bob = HttpRequest.newBuilder();
        bob.uri(URI.create("http://localhost:8080/user"));
        bob.POST(HttpRequest.BodyPublishers.ofString(json));

        HttpRequest request = bob.build();

        HttpClient testRequester = HttpClient.newHttpClient();

        try{
            HttpResponse<String> actual = testRequester.send(request, HttpResponse.BodyHandlers.ofString());
            LoginResult result = new Gson().fromJson(actual.body(), LoginResult.class);
            String auth =  "definitely not the auth token";
            System.out.println(result);

            bob = HttpRequest.newBuilder();
            bob.uri(URI.create("http://localhost:8080/session"));
            bob.header("authorization", auth);
            bob.DELETE();

            request = bob.build();
            testRequester = HttpClient.newHttpClient();

            actual = testRequester.send(request, HttpResponse.BodyHandlers.ofString());
            Assertions.assertTrue(401 == actual.statusCode());
        }
        catch(java.io.IOException e){
            assert false: "You had an IOException";
        }
        catch(java.lang.InterruptedException e){
            assert false: "You had an InterruptedException";
        }
    }

    @Test
    @Order(5)
    @DisplayName("CreatePositive")
    public void successCreate(){
        server.reset();

        String json = "{\"username\":\"user1name1\", \"password\":\"supercalifragilisticexpialadocious\", \"email\":\"madeupemail@internet.com\"}";
        HttpRequest.Builder bob = HttpRequest.newBuilder();
        bob.uri(URI.create("http://localhost:8080/user"));
        bob.POST(HttpRequest.BodyPublishers.ofString(json));

        HttpRequest request = bob.build();

        HttpClient testRequester = HttpClient.newHttpClient();

        try{
            HttpResponse<String> actual = testRequester.send(request, HttpResponse.BodyHandlers.ofString());
            LoginResult result = new Gson().fromJson(actual.body(), LoginResult.class);
            System.out.println(result);
            String auth =  result.getAuthToken();


            json = "{\"gameName\":\"coolGame1\"}";
            bob = HttpRequest.newBuilder();
            bob.uri(URI.create("http://localhost:8080/game"));
            bob.header("authorization", auth);
            bob.POST(HttpRequest.BodyPublishers.ofString(json));

            request = bob.build();
            testRequester = HttpClient.newHttpClient();

            actual = testRequester.send(request, HttpResponse.BodyHandlers.ofString());
            Assertions.assertTrue(200 == actual.statusCode());
        }
        catch(java.io.IOException e){
            assert false: "You had an IOException";
        }
        catch(java.lang.InterruptedException e){
            assert false: "You had an InterruptedException";
        }
    }

    @Test
    @Order(6)
    @DisplayName("CreatePositive")
    public void failCreate(){
        String json = "{\"username\":\"user1name1\", \"password\":\"supercalifragilisticexpialadocious\", \"email\":\"madeupemail@internet.com\"}";
        HttpRequest.Builder bob = HttpRequest.newBuilder();
        bob.uri(URI.create("http://localhost:8080/user"));
        bob.POST(HttpRequest.BodyPublishers.ofString(json));

        HttpRequest request = bob.build();

        HttpClient testRequester = HttpClient.newHttpClient();

        try{
            HttpResponse<String> actual = testRequester.send(request, HttpResponse.BodyHandlers.ofString());
            LoginResult result = new Gson().fromJson(actual.body(), LoginResult.class);
            System.out.println(result);
            String auth =  "definitely wrong";


            json = "{\"gameName\":\"coolGame1\"}";
            bob = HttpRequest.newBuilder();
            bob.uri(URI.create("http://localhost:8080/game"));
            bob.header("authorization", auth);
            bob.POST(HttpRequest.BodyPublishers.ofString(json));

            request = bob.build();
            testRequester = HttpClient.newHttpClient();

            actual = testRequester.send(request, HttpResponse.BodyHandlers.ofString());
            Assertions.assertTrue(401 == actual.statusCode());
        }
        catch(java.io.IOException e){
            assert false: "You had an IOException";
        }
        catch(java.lang.InterruptedException e){
            assert false: "You had an InterruptedException";
        }
    }


    @Test
    @Order(7)
    @DisplayName("ListPositive")
    public void successList(){
        server.reset();

        String json = "{\"username\":\"user1name1\", \"password\":\"supercalifragilisticexpialadocious\", \"email\":\"madeupemail@internet.com\"}";
        HttpRequest.Builder bob = HttpRequest.newBuilder();
        bob.uri(URI.create("http://localhost:8080/user"));
        bob.POST(HttpRequest.BodyPublishers.ofString(json));

        HttpRequest request = bob.build();

        HttpClient testRequester = HttpClient.newHttpClient();

        try{
            HttpResponse<String> actual = testRequester.send(request, HttpResponse.BodyHandlers.ofString());
            LoginResult result = new Gson().fromJson(actual.body(), LoginResult.class);
            System.out.println(result);
            String auth =  result.getAuthToken();

            //make a game
            json = "{\"gameName\":\"coolGame1\"}";
            bob = HttpRequest.newBuilder();
            bob.uri(URI.create("http://localhost:8080/game"));
            bob.header("authorization", auth);
            bob.POST(HttpRequest.BodyPublishers.ofString(json));

            request = bob.build();
            testRequester = HttpClient.newHttpClient();

            //list the game
            bob = HttpRequest.newBuilder();
            bob.uri(URI.create("http://localhost:8080/game"));
            bob.header("authorization", auth);
            bob.GET();

            request = bob.build();
            testRequester = HttpClient.newHttpClient();

            actual = testRequester.send(request, HttpResponse.BodyHandlers.ofString());
            Assertions.assertTrue(200 == actual.statusCode());
        }
        catch(java.io.IOException e){
            assert false: "You had an IOException";
        }
        catch(java.lang.InterruptedException e){
            assert false: "You had an InterruptedException";
        }
    }

    @Test
    @Order(8)
    @DisplayName("ListNegative")
    public void failList(){
        server.reset();

        String json = "{\"username\":\"user1name1\", \"password\":\"supercalifragilisticexpialadocious\", \"email\":\"madeupemail@internet.com\"}";
        HttpRequest.Builder bob = HttpRequest.newBuilder();
        bob.uri(URI.create("http://localhost:8080/user"));
        bob.POST(HttpRequest.BodyPublishers.ofString(json));

        HttpRequest request = bob.build();

        HttpClient testRequester = HttpClient.newHttpClient();

        try{
            HttpResponse<String> actual = testRequester.send(request, HttpResponse.BodyHandlers.ofString());
            LoginResult result = new Gson().fromJson(actual.body(), LoginResult.class);
            System.out.println(result);
            String auth =  result.getAuthToken();

            //make a game
            json = "{\"gameName\":\"coolGame1\"}";
            bob = HttpRequest.newBuilder();
            bob.uri(URI.create("http://localhost:8080/game"));
            bob.header("authorization", auth);
            bob.POST(HttpRequest.BodyPublishers.ofString(json));

            request = bob.build();
            testRequester = HttpClient.newHttpClient();

            //list the game
            bob = HttpRequest.newBuilder();
            bob.uri(URI.create("http://localhost:8080/game"));
            bob.header("authorization", "auth but not actually");
            bob.GET();

            request = bob.build();
            testRequester = HttpClient.newHttpClient();

            actual = testRequester.send(request, HttpResponse.BodyHandlers.ofString());
            Assertions.assertTrue(401 == actual.statusCode());
        }
        catch(java.io.IOException e){
            assert false: "You had an IOException";
        }
        catch(java.lang.InterruptedException e){
            assert false: "You had an InterruptedException";
        }
    }

    @Test
    @Order(9)
    @DisplayName("JoinPositive")
    public void successJoin(){
        String json = "{\"username\":\"user1name1\", \"password\":\"supercalifragilisticexpialadocious\", \"email\":\"madeupemail@internet.com\"}";
        HttpRequest.Builder bob = HttpRequest.newBuilder();
        bob.uri(URI.create("http://localhost:8080/user"));
        bob.POST(HttpRequest.BodyPublishers.ofString(json));

        HttpRequest request = bob.build();

        HttpClient testRequester = HttpClient.newHttpClient();

        try{
            HttpResponse<String> actual = testRequester.send(request, HttpResponse.BodyHandlers.ofString());
            LoginResult result = new Gson().fromJson(actual.body(), LoginResult.class);
            String auth =  result.getAuthToken();

            //make a game
            json = "{\"gameName\":\"coolGame1\"}";
            bob = HttpRequest.newBuilder();
            bob.uri(URI.create("http://localhost:8080/game"));
            bob.header("authorization", auth);
            bob.POST(HttpRequest.BodyPublishers.ofString(json));

            request = bob.build();
            testRequester = HttpClient.newHttpClient();
            actual = testRequester.send(request, HttpResponse.BodyHandlers.ofString());

            //join the game
            json = "{\"playerColor\":\"WHITE\", \"gameID\":\"1000\"}";
            bob = HttpRequest.newBuilder();
            bob.uri(URI.create("http://localhost:8080/game"));
            bob.header("authorization", auth);
            bob.PUT(HttpRequest.BodyPublishers.ofString(json));

            request = bob.build();
            testRequester = HttpClient.newHttpClient();

            actual = testRequester.send(request, HttpResponse.BodyHandlers.ofString());
            Assertions.assertTrue(200 == actual.statusCode());
        }
        catch(java.io.IOException e){
            assert false: "You had an IOException";
        }
        catch(java.lang.InterruptedException e){
            assert false: "You had an InterruptedException";
        }
    }

    @Test
    @Order(10)
    @DisplayName("JoinNegative")
    public void failJoin(){
        server.reset();

        String json = "{\"username\":\"user1name1\", \"password\":\"supercalifragilisticexpialadocious\", \"email\":\"madeupemail@internet.com\"}";
        HttpRequest.Builder bob = HttpRequest.newBuilder();
        bob.uri(URI.create("http://localhost:8080/user"));
        bob.POST(HttpRequest.BodyPublishers.ofString(json));

        HttpRequest request = bob.build();

        HttpClient testRequester = HttpClient.newHttpClient();

        try{
            HttpResponse<String> actual = testRequester.send(request, HttpResponse.BodyHandlers.ofString());
            LoginResult result = new Gson().fromJson(actual.body(), LoginResult.class);
            String auth =  result.getAuthToken();

            //make a game
            json = "{\"gameName\":\"coolGame1\"}";
            bob = HttpRequest.newBuilder();
            bob.uri(URI.create("http://localhost:8080/game"));
            bob.header("authorization", auth);
            bob.POST(HttpRequest.BodyPublishers.ofString(json));

            request = bob.build();
            testRequester = HttpClient.newHttpClient();
            actual = testRequester.send(request, HttpResponse.BodyHandlers.ofString());

            //join the game
            json = "{\"playerColor\":\"WHITE\", \"gameID\":\"1000\"}";
            bob = HttpRequest.newBuilder();
            bob.uri(URI.create("http://localhost:8080/game"));
            bob.header("authorization", "auth");
            bob.PUT(HttpRequest.BodyPublishers.ofString(json));

            request = bob.build();
            testRequester = HttpClient.newHttpClient();

            actual = testRequester.send(request, HttpResponse.BodyHandlers.ofString());
            Assertions.assertTrue(401 == actual.statusCode());
        }
        catch(java.io.IOException e){
            assert false: "You had an IOException";
        }
        catch(java.lang.InterruptedException e){
            assert false: "You had an InterruptedException";
        }
    }

    @Test
    @Order(11)
    @DisplayName("clearDB")
    public void clearTest(){
        String json = "{\"username\":\"user1name1\", \"password\":\"supercalifragilisticexpialadocious\", \"email\":\"madeupemail@internet.com\"}";
        HttpRequest.Builder bob = HttpRequest.newBuilder();
        bob.uri(URI.create("http://localhost:8080/user"));
        bob.POST(HttpRequest.BodyPublishers.ofString(json));

        HttpRequest request = bob.build();

        HttpClient testRequester = HttpClient.newHttpClient();

        try{
            HttpResponse<String> actual = testRequester.send(request, HttpResponse.BodyHandlers.ofString());
            LoginResult result = new Gson().fromJson(actual.body(), LoginResult.class);
            String auth =  result.getAuthToken();

            //make a game
            json = "{\"gameName\":\"coolGame1\"}";
            bob = HttpRequest.newBuilder();
            bob.uri(URI.create("http://localhost:8080/game"));
            bob.header("authorization", auth);
            bob.POST(HttpRequest.BodyPublishers.ofString(json));

            request = bob.build();
            testRequester = HttpClient.newHttpClient();
            actual = testRequester.send(request, HttpResponse.BodyHandlers.ofString());

            //join the game
            json = "{\"playerColor\":\"WHITE\", \"gameID\":\"1000\"}";
            bob = HttpRequest.newBuilder();
            bob.uri(URI.create("http://localhost:8080/game"));
            bob.header("authorization", auth);
            bob.PUT(HttpRequest.BodyPublishers.ofString(json));

            request = bob.build();
            testRequester = HttpClient.newHttpClient();

            actual = testRequester.send(request, HttpResponse.BodyHandlers.ofString());


            bob = HttpRequest.newBuilder();
            bob.uri(URI.create("http://localhost:8080/db"));
            bob.header("authorization", auth);
            bob.DELETE();

            request = bob.build();
            testRequester = HttpClient.newHttpClient();

            actual = testRequester.send(request, HttpResponse.BodyHandlers.ofString());

            Assertions.assertTrue(200 == actual.statusCode());
        }
        catch(java.io.IOException e){
            assert false: "You had an IOException";
        }
        catch(java.lang.InterruptedException e){
            assert false: "You had an InterruptedException";
        }
    }
}
