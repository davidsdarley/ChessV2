package ui;

import server.Server;
import java.util.Scanner;

public class UserInterface {
    Server server;
    Scanner scanner;
    String auth;
    public UserInterface(Server server) {
        //either connect to the server or make a new one
        this.server = server;
        scanner = new Scanner(System.in);
    }

    public static void main(String[] args) {
    UserInterface ui = new UserInterface(new Server());

    }
}
