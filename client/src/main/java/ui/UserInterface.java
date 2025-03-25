package ui;

import com.google.gson.Gson;
import server.Server;
import server.carriers.*;

import java.net.http.HttpResponse;
import java.util.Scanner;

public class UserInterface {
    Server server;
    Scanner scanner;
    ServerFacade client;
    String auth;
    String state;
    public UserInterface(Server server) {
        //either connect to the server or make a new one
        this.server = server;
        scanner = new Scanner(System.in);
        client = new ServerFacade();
    }
    public void run(){
        System.out.println("Welcome to Chess! Type Help to get started");
        state = "LOGGED_OUT";
        String input;
        while ( !(state.equals("QUIT")) ){
            System.out.print(state +" >>> ");
            input = scanner.nextLine().toUpperCase();
            performOperation(input);
        }
    }
    private void register(){
        System.out.print("Please enter username: ");
        String username = scanner.nextLine();
        System.out.print("Please enter password: ");
        String password = scanner.nextLine();
        System.out.print("Please enter email: ");
        String email = scanner.nextLine();

        String response = client.register(username, password, email);

        if (response.equals("")){
            System.out.println("Error! We're so sorry your registration has failed");
        }
        else if(response.equals("bad request")|| response.equals("already taken")){
            System.out.println(response);
        }
        else{
            System.out.println("Registration Successful!");
            LoginResult login = new Gson().fromJson(response, LoginResult.class);
            System.out.println(response);
            auth = login.getAuthToken();
            state = "LOGGED_IN";
        }
    }
    private void login(){
        System.out.print("Please enter username: ");
        String username = scanner.nextLine();
        System.out.print("Please enter password: ");
        String password = scanner.nextLine();

        String response = client.login(username, password);

        if (response.equals("Unauthorized") || response.equals("Error! We're so sorry your login has failed")){
            System.out.println("Error! We're so sorry your registration has failed");
        }
        else{
            LoginResult login = new Gson().fromJson(response, LoginResult.class);
            auth = login.getAuthToken();
            System.out.println("Logged in as "+username);
            state = "LOGGED_IN";
        }
    }

    private void create(){
        System.out.print("Game name: ");
        String name = scanner.nextLine();
        HttpResponse<String> response = client.create(name, auth);

        if (response.statusCode() == 200){
            GameData game = new Gson().fromJson(response.body(), GameData.class);
            System.out.println("Game " + name + " has been created. GameID is " + game.getGameID());
        }
        else{
            System.out.println(response.body());
        }
    }
    private void join(){
        System.out.print("GameID: ");
        int id = Integer.parseInt(scanner.nextLine());
        System.out.println(id);
        System.out.print("Choose WHITE or BLACK: ");
        String color = scanner.nextLine().toUpperCase();
        HttpResponse<String> response = client.join(id, color, auth);

        if (response.statusCode() == 200){
            System.out.println("Joined game as "+ color);
        }
        else if(response.statusCode() == 400){
            System.out.println("Join failed. Bad request");
        }
        else if(response.statusCode() == 500) {
            System.out.println("Join failed. Internal error, we apologize for the inconvenience");
        }
        else{
            System.out.println(("Join failed. Unauthorized"));
        }
    }
    private void list(){
        HttpResponse<String> response = client.list(auth);
        if (response.statusCode() == 200){
            System.out.println(response.body());
        }
    }
    private void performOperation(String input){
        if (input.equals("QUIT")){
            state = input;
        }
        else if(state == "LOGGED_OUT"){
            if (input.equals("HELP")){
                System.out.println("   register <USERNAME> <PASSWORD> <EMAIL>");
                System.out.println("   login <USERNAME> <PASSWORD>");
                System.out.println("   quit");
                System.out.println("   help");
            }
            else if(input.equals("REGISTER")){
                register();
            }
            else if (input.equals("LOGIN")){
                login();
            }
            else{
                System.out.println("Invalid input. Type Help to see available commands");
            }
        }

        else if(state == "LOGGED_IN"){
            if(input.equals("HELP")){
                System.out.println("create <NAME>");
                System.out.println("list");
                System.out.println("join <ID> [WHITE/BLACK}");
                System.out.println("observe <ID>");
                System.out.println("logout");
                System.out.println("quit");
                System.out.println("help");
            }
            else if (input.equals("CREATE")){
                create();
            }
            else if (input.equals("JOIN")){
                join();
            }
            else if (input.equals("LIST")){
                list();
            }
            else if (input.equals("OBSERVE")){

            }
            else if (input.equals("LOGOUT")){

            }
            else{
                System.out.println("Invalid input. Type Help to see available commands");
            }
        }


        else{
            System.out.println("Invalid input. Type Help to see available commands");
        }
    }

    public static void main(String[] args) {
    UserInterface ui = new UserInterface(new Server());
    ui.run();
    }
}
