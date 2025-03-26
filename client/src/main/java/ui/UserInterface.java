package ui;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import server.Server;
import server.carriers.*;

import java.lang.reflect.Type;
import java.net.http.HttpResponse;
import java.util.*;

public class UserInterface {
    Scanner scanner;
    ServerFacade client;
    String auth;
    String state;
    Printer printer;
    Map<Integer, GameData> games;
    public UserInterface() {
        //either connect to the server or make a new one
        scanner = new Scanner(System.in);
        client = new ServerFacade();
        printer = new Printer();
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
            System.out.println(response);
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
    private void join(int id, String color){
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
        Gson gson = new Gson();
        HttpResponse<String> response = client.list(auth);

        Type type = new TypeToken<HashMap<String, ArrayList<GameData>>>() {}.getType();

        if (response.statusCode() == 200){
            HashMap<String, ArrayList<GameData>> data = gson.fromJson(response.body(), type);
            ArrayList<GameData> gameList = data.get("games");
            HashMap<Integer, GameData> newGames = new HashMap<>();
            int counter = 1;
            for(GameData game: gameList){
                newGames.put(counter, game);
                System.out.println(counter + ": "+game);
                counter +=1;
            }
            games = newGames;
        }
        else{
            System.out.println(response.body());
        }
    }
    private void observe(){
        int id;
        System.out.print("Enter a game number: ");
        id = Integer.parseInt(scanner.nextLine());
        if (games.size() >= id){
            GameData game = games.get(id);
            System.out.println(game);
            //Change when gameplay implemented to get the ChessGame from GameData
            printer.printBoard(null);
            state = "OBSERVING";
        }
        else{
            System.out.println("Invalid ID. Type List to get game IDs");
        }
    }
    private void play(){
        int id;
        System.out.print("Enter a game number: ");
        id = Integer.parseInt(scanner.nextLine());
        if (games.size() >= id){
            GameData game = games.get(id);
            System.out.print("Choose WHITE or BLACK: ");
            String color = scanner.nextLine().toUpperCase();
            join(game.getGameID(), color);
            //Change when gameplay implemented to get the ChessGame from GameData
            printer.printBoard(null, color);
            state = "PLAYING";
        }
        else{
            System.out.println("Invalid ID. Type List to get game IDs");
        }
    }
    private void logout(){
        HttpResponse<String> response = client.logout(auth);

        if (response.statusCode() == 200){
            System.out.println("Logged out");
            state = "LOGGED_OUT";
            auth = null;
        }
        else{
            System.out.println(response.body());
        }    }
    private void performOperation(String input){
        if (input.equals("QUIT")){
            logout();
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
                System.out.println("join <gameID> [WHITE/BLACK}");
                System.out.println("play <ID> [WHITE/BLACK]");
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
                observe();
            }
            else if (input.equals("LOGOUT")){
                logout();
            }
            else if (input.equals("PLAY")){
                play();
            }
            else{
                System.out.println("Invalid input. Type Help to see available commands");
            }
        }
        else if(state == "PLAYING"){
            if(input.equals("BACK")){
                state = "LOGGED_IN";
            }
            else if(input.equals("HELP")) {
                if (input.equals("HELP")) {
                    System.out.println("   back");
                    System.out.println("   quit");
                }
            }
        }
        else if(state == "OBSERVING"){
            if(input.equals("BACK")){
                state = "LOGGED_IN";
            }
            else if(input.equals("HELP")) {
                if (input.equals("HELP")) {
                    System.out.println("   back");
                    System.out.println("   quit");
                }
            }
        }

        else{
            System.out.println("Invalid input. Type Help to see available commands");
        }
    }

    public static void main( String[] args) {
    UserInterface ui = new UserInterface();
    ui.run();
    }
}
