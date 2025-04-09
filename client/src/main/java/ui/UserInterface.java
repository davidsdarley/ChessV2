package ui;

import chess.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import carriers.*;
import websocket.commands.UserGameCommand;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.http.HttpResponse;
import java.util.*;

public class UserInterface {
    boolean deBug = false;
    int activeGame;
    String activeColor;
    Scanner scanner;
    ServerFacade client;
    String auth;
    String userName;
    String state;
    Printer printer;
    Map<Integer, GameData> games;
    Receiver receiver;
    public UserInterface() {
        //either connect to the server or make a new one
        scanner = new Scanner(System.in);
        client = new ServerFacade();
        printer = new Printer();
        games = new HashMap<>();
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
    private ChessPiece.PieceType getPieceType(){
        System.out.print("Enter promotion piece (if applicable):");
        String input = scanner.nextLine().toUpperCase();
        if (input.equals("QUEEN")){
            return ChessPiece.PieceType.QUEEN;
        }
        if (input.equals("ROOK")){
            return ChessPiece.PieceType.ROOK;
        }
        if (input.equals("KNIGHT")){
            return ChessPiece.PieceType.KNIGHT;
        }
        if (input.equals("BISHOP")){
            return ChessPiece.PieceType.BISHOP;
        }
        return null;
    }
    private void leave(){
        try{
            UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.LEAVE, auth, activeGame);
            command.setMessage(userName + " has left the game.");
            if(state.equals("PLAYING")){
                command.setLeaveRequest(new JoinRequest(activeGame, activeColor, auth));
            }
            receiver.sendCommand(command);
            receiver.stop();
            activeGame = 0;
            activeColor = null;
            state = "LOGGED_IN";
            System.out.println("Left game");
        } catch (IOException e) {
            System.out.println("Failed to close connection");
        }
    }
    private void resign(){
        try{
            UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.RESIGN, auth, activeGame);
            command.setMessage(userName + " has resigned. Game is finished.");
            receiver.sendCommand(command);
            receiver.stop();
            activeGame = 0;
            activeColor = null;
            state = "LOGGED_IN";
        } catch (IOException e) {
            System.out.println("Failed to close connection");
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

        if (response.equals("") || response.equals("Error! We're so sorry your registration has failed")){
            System.out.println("Error! We're so sorry your registration has failed");
        }
        else if(response.equals("bad request")|| response.equals("already taken")){
            System.out.println(response);
        }
        else{
//            System.out.println(response);
            LoginResult login = new Gson().fromJson(response, LoginResult.class);
            auth = login.getAuthToken();
            userName = login.getUsername();
            state = "LOGGED_IN";
            System.out.println("Registration Successful!");

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
            userName = login.getUsername();
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
            System.out.println("Game " + name + " has been created.");
        }
        else{
            System.out.println(response.body());
        }
    }
    private boolean join(){
        System.out.print("GameID: ");
        int id = Integer.parseInt(scanner.nextLine());
        GameData game = games.get(id);
        System.out.println(game);
        System.out.print("Choose WHITE or BLACK: ");
        String color = scanner.nextLine().toUpperCase();

        HttpResponse<String> response = client.join(game.getGameID(), color, auth);

        if (response.statusCode() == 200){
            System.out.println("Joined game as "+ color);
            return true;
        }
        else if(response.statusCode() == 403){
            System.out.println("Join failed. Already taken");
            return false;
        }
        else if(response.statusCode() == 400){
            System.out.println("Join failed. Bad request");
            return false;
        }
        else if(response.statusCode() == 500) {
            System.out.println("Join failed. Internal error, we apologize for the inconvenience");
            return false;
        }
        else{
            System.out.println(("Join failed. Unauthorized"));
            return false;
        }
    }
    private boolean join(int id, String color){
        HttpResponse<String> response = client.join(id, color, auth);

        if (response.statusCode() == 200){
            System.out.println("Joined game as "+ color);
            return true;
        }
        else if(response.statusCode() == 400){
            System.out.println("Join failed. Bad request");
        }
        else if(response.statusCode() == 500) {
            System.out.println("Join failed. Internal error, we apologize for the inconvenience");
        }
        else if(response.statusCode() == 403){
            System.out.println("Join failed. Color already taken");
        }
        else{
            System.out.println(("Join failed. Unauthorized"));
        }
        return false;
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
    private void observe() throws Exception {
        int id;
        System.out.print("Enter a game number: ");
        String input = scanner.nextLine();
        try{
            id = Integer.parseInt(input);
            if (id < 1){
                System.out.println("Invalid ID. Please enter a positive number");
            }
            else if (games.size() >= id){
                GameData game = games.get(id);
                activeGame= game.getGameID();
                System.out.println(game);

                receiver = new Receiver(this, "WHITE");
                UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.CONNECT,
                        auth, activeGame);
                command.setMessage(userName + " has joined as an observer!");
                receiver.observe(command);


                state = "OBSERVING";

            }
            else{
                System.out.println("Invalid ID. Type List to get game IDs");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID. Please enter a number");
        }

    }
    private void play(){
        int id;
        System.out.print("Enter a game number: ");
        String input = scanner.nextLine();
        try {
            id = Integer.parseInt(input);
            if (id < 1){
                System.out.println("Invalid ID. Please enter a positive number");
            }
            else if (games.size() >= id) {
                GameData game = games.get(id);
                System.out.print("Choose WHITE or BLACK: ");
                String color = scanner.nextLine().toUpperCase();
                if (join(game.getGameID(), color)) {
                    //Change when gameplay implemented to get the ChessGame from GameData
                    this.activeGame = game.getGameID();
                    this.activeColor = color;
                    //printer.printBoard(game.getGame(), color);
                    try{
                    receiver = new Receiver(this, "WHITE");
                    UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.CONNECT,
                            auth, activeGame);
                    command.setMessage(userName + " has joined as "+color+"!");
                    debug("SENDING FROM play()");
                    receiver.sendCommand(command);

                    state = "PLAYING";
                    }
                    catch (Exception e){
                        System.out.println("Join game failed: " + e.getMessage());
                    }
                }
            } else {
                System.out.println("Invalid ID. Type List to get game IDs");
            }
        }
        catch(NumberFormatException e){
            System.out.println("Invalid ID. Please enter a number");
        }
    }
    private void logout() {
        if (auth == null) {
            System.out.print("");
        }
        else {
            HttpResponse<String> response = client.logout(auth);

            if (response.statusCode() == 200) {
                System.out.println("Logged out");
                state = "LOGGED_OUT";
                auth = null;
            } else {
                System.out.println(response.body());
            }
        }
    }
    private ChessPosition getPositionInput(){
        System.out.print("\nPlease enter piece column letter (a-h): ");
        String[] options = {"a", "b", "c", "d", "e", "f", "g", "h"};
        String columnLetter = scanner.nextLine().toLowerCase();
        int col = -1;
        for (int i = 0; i<8; i++){
            if (columnLetter.equals(options[i])){
                col = i+1;
                break;
            }
        }
        if(col != -1) {
            try {
                System.out.print("\nPlease enter piece row number (1-8): ");
                int row = scanner.nextInt();
                String scrubber = scanner.nextLine();
                if (row > 0 && row < 9) {
                    return new ChessPosition(row, col);

                } else {
                    System.out.println("Invalid row. Please enter a number 1-8");
                    return null;
                }

            } catch (NumberFormatException e) {
                System.out.println("Invalid row. Please enter a number");
                return null;
            }
        }
        else {
            System.out.println("Invalid row. Please enter a-h");
            return null;
        }
    }
    private void getInfo(){
        System.out.println("gameOver: "+receiver.gameOver);
        System.out.println("turn: "+receiver.turn);
    }
    private void handleHighlight(){
        ChessPosition position = getPositionInput();
        if(position != null) {
            receiver.highlight(position, activeGame);
        }

    }
    private boolean handleMakeMove(){
        //See if the game is over
        if (receiver.gameOver){
            System.out.println("Game is over! No more moves can be made.");
            return false;
        }
        //see if it's your turn
        if (receiver.turn){
            //get starting and ending squares
            ChessPosition start = getPositionInput();
            if (start == null){
                return false;
            }
            ChessPosition end = getPositionInput();
            if (end == null){
                return false;
            }
            ChessPiece.PieceType promotion = getPieceType();
            //send it away and try to make the move.
            ChessMove move = new ChessMove(start, end, promotion);
            receiver.makeMove(move);
            return true;
        }
        else{
            System.out.println("Not your turn!");
        }
        return false;
    }
    private void performOperation(String input){
        if (input.equals("QUIT")){
            //close any sessions you may be in
            if (state.equals("OBSERVING") || state.equals("PLAYING")) {
                leave();
            }
            //logout
            logout();
            state = input;
        }

        else if(state.equals("LOGGED_OUT")){
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

        else if(state.equals("LOGGED_IN")){
            if(input.equals("HELP")){
                System.out.println("create <NAME>");
                System.out.println("list");
                System.out.println("play <ID> [WHITE/BLACK]");
                System.out.println("observe <ID>");
                System.out.println("logout");
                System.out.println("quit");
                System.out.println("help");
            }
            else if (input.equals("CREATE")){
                create();
            }
            //else if (input.equals("JOIN")){join();}
            else if (input.equals("LIST")){
                list();
            }
            else if (input.equals("OBSERVE")){
                try{
                observe();
                }
                catch (Exception e) {
                    System.out.println("Call to OBSERVE failed");
                }
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

        else if(state.equals("PLAYING")){
            handlePlaying(input);
        }
        else if(state.equals("OBSERVING") ){ //passive message searching
            if(input.equals("BACK")){
                leave();
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
    private void handlePlaying(String input){

        if(input.equals("BACK")){
            leave();
        }
        else if(input.equals("HELP")) {
            System.out.println("   back");
            System.out.println("   quit");
            System.out.println("   resign");
            System.out.println("   redraw");
            System.out.println("   highlight legal moves");
            System.out.println("   make move");
        }
        else if(input.equals("INFO")){
            getInfo();
        }
        else if(input.equals("REDRAW")){
            UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.GET, auth, activeGame);
            debug("sending from redraw");
            receiver.sendCommand(command);
        }
        else if(input.equals("HIGHLIGHT LEGAL MOVES")){
            handleHighlight();
        }
        else if(input.equals("RESIGN")){
            System.out.println("Are you sure you want to resign?  YES | NO");
            input = scanner.nextLine().toUpperCase();
            if (input.equals("YES")){
                resign();
            }
            else{
                System.out.println("Confirmation failed. Game continuing.");
            }
        }
        else if(input.equals("MAKE MOVE")){
            handleMakeMove();
        }
        else{
            System.out.println("Invalid input. Type Help to see available commands");
        }
    }
    public static void main( String[] args) {
    UserInterface ui = new UserInterface();
    ui.run();
    }
    public void debug(String message){
        if (deBug){
            System.out.println("DEBUG: " + message);
        }
    }
}