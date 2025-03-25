package ui;

import server.Server;
import java.util.Scanner;

public class UserInterface {
    Server server;
    Scanner scanner;
    ClientRequester client;
    String auth;
    public UserInterface(Server server) {
        //either connect to the server or make a new one
        this.server = server;
        scanner = new Scanner(System.in);
        client = new ClientRequester();
    }

    public static void main(String[] args) {
    UserInterface ui = new UserInterface(new Server());
    System.out.println(ui.client.hello());
    }
}
