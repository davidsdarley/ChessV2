package ui;
import java.io.IOException;
import java.net.http.*;
import java.net.URI;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest;

public class ClientRequester {
    HttpClient client;
    String site;
    public ClientRequester(String site){
        client = HttpClient.newHttpClient();
        this.site = site;
    }
    public ClientRequester(){
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

        catch (IOException e) {
            return false;
        }
        catch (InterruptedException e) {
            return false;
        }
    }
    public Object login(String username, String password){
        //HttpRequest request = "hello there";
        return false;
    }
}
