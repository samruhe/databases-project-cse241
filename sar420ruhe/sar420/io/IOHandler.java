package io;

import java.util.Scanner;
import java.io.Console;

public class IOHandler {
    public IOHandler() {
        super();
    }

    public static String[] getCreds(Scanner in) {
        Console cnsl = System.console();
        String[] creds = new String[2];

        System.out.print("Enter Oracle user id: ");
        String username = in.nextLine();
        System.out.print("Enter Oracle password for " + username + ": ");
        
        // Uncomment next line and comment next two lines if you want password visible when entered
        // String password = in.nextLine();
        char[] pass = cnsl.readPassword();
        String password = String.copyValueOf(pass);
        creds[0] = username;
        creds[1] = password;

        return creds;
    }

    public static int getMenuSelection(Scanner in, int maxItems) {
        // Scanner in = new Scanner(System.in);

        int input = 0;
        while (true) {
            if (!in.hasNextInt()) {
                System.out.println("Please select an option by inputing the correct number.");
                System.out.print("> ");
                in.next();
                continue;
            }

            input = in.nextInt();
            if (input > 0 && input <= maxItems) return input;
            else {
                System.out.println("Please select an option by inputing the correct number.");
                System.out.print("> ");
            }
        }
    }
}