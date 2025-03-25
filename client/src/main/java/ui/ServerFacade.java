package ui;

import server.carriers.*;
import java.io.IOException;
import java.net.http.*;
import java.net.URI;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest;
import com.google.gson.Gson;


public class ServerFacade {
    HttpClient client;
    String site;
    public ServerFacade(String site){
        client = HttpClient.newHttpClient();
        this.site = site;
    }
    public ServerFacade(){
        client = HttpClient.newHttpClient();
        this.site = "http://localhost:8080";
    }
    public Object hello(){ //testing purposes
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(site+"/hello"))
                .GET()
                .build();
        try{
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        }

        catch (IOException | InterruptedException e) {
            return false;
        }
    }
    public String login(String username, String password){
        Gson gson = new Gson();
        LoginRequest login = new LoginRequest(username, password);

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(site+"/session"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(login)))
                .build();
        try{
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200){
                return response.body();
            }
            else{
                return "Unauthorized";
            }
        }
        catch (IOException | InterruptedException e) {
            return "Error! We're so sorry your login has failed";
        }
    }
    public String register(String username, String password, String email){
        Gson gson = new Gson();
        RegisterRequest register = new RegisterRequest(username, password, email);

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(site+"/user"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(register)))
                .build();

        try{
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            int code = response.statusCode();

            if(code == 400){
                return "bad request";
            }
            else if(code == 403){
                return "already taken";
            }
            else{
                return response.body();
            }
        }

        catch (IOException | InterruptedException e) {
            return "Error! We're so sorry your registration has failed";
        }
    }
    public HttpResponse<String> create(String name, String authToken){
        Gson gson = new Gson();
        GameRequest game = new GameRequest(name, authToken);

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(site+"/game"))
                .header("Content-Type", "application/json")
                .header("authorization", authToken)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(game)))
                .build();
        try{
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                return response;
        }
        catch (IOException | InterruptedException e) {
            throw new RuntimeException("Error! We're so sorry your creation has failed") ;
        }
    }
    public HttpResponse<String> join(int id, String color, String authToken){
        Gson gson = new Gson();
        JoinRequest join = new JoinRequest(id, color, authToken);

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(site+"/game"))
                .header("Content-Type", "application/json")
                .header("authorization", authToken)
                .PUT(HttpRequest.BodyPublishers.ofString(gson.toJson(join)))
                .build();
        try{
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response;
        }
        catch (IOException | InterruptedException e) {
            throw new RuntimeException("Error! We're so sorry your join game has failed") ;
        }
    }
}
