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
                print("Please select an option by inputing the correct number.");
                printPrompt();
                in.next();
                continue;
            }

            input = in.nextInt();
            if (input > 0 && input <= maxItems) return input;
            else {
                print("Please select an option by inputing the correct number.");
                printPrompt();
            }
        }
    }

    public static String getCardNumber() {
        Scanner in = new Scanner(System.in);
        long input = 0;
        while (true) {
            input = in.nextLong();
            if (String.valueOf(input).length() == 16) {
                in.close();
                return String.valueOf(input);
            }
            else {
                print("Please enter A valid card number, with no spaces");
                printPrompt();
            }
        }
    }

    public static String getCardExpiration() {
        Scanner in = new Scanner(System.in);
        String input = "";
        while (true) {
            input = in.nextLine();
            if (input.trim().length() == 4) {
                try {
                    Integer.parseInt(input);
                    in.close();
                    return input.trim();
                } catch (NumberFormatException ex) {
                    print("Please enter the expiration data in the format 'MMYY'");
                    printPrompt();
                }
            }
            else {
                print("Please enter the expiration data in the format 'MMYY'");
                printPrompt();
            }
        }
    }

    public static String getCardSecurity() {
        Scanner in = new Scanner(System.in);
        String input = "";
        while (true) {
            input = in.nextLine();
            if (input.trim().length() == 3) {
                try {
                    Integer.parseInt(input);
                    in.close();
                    return input.trim();
                } catch (NumberFormatException ex) {
                    print("Please enter a valid security code");
                    printPrompt();
                }
            }
            else {
                print("Please enter a valid secuirty code");
                printPrompt();
            }
        }
    }

    public static String getCardPin() {
        Scanner in = new Scanner(System.in);
        String input = "";
        while (true) {
            input = in.nextLine();
            if (input.trim().length() > 0 && input.trim().length() <= 10) {
                try {
                    Integer.parseInt(input);
                    in.close();
                    return input.trim();
                } catch (NumberFormatException ex) {
                    print("Please enter a valid pin");
                    printPrompt();
                }
            }
            else {
                print("Please enter a valid pin");
                printPrompt();
            }
        }
    }

    public static void print(String line) {
        System.out.println(line);
    }

    public static void printPrompt() {
        System.out.print("> ");
    }

    public static void printBalance(String account_number, String balance) {
        System.out.println("Current posted balance for account ending in ******" + account_number.substring(6) + ":");
        System.out.printf("\t$%.2f\n", Double.parseDouble(balance));
    }
}