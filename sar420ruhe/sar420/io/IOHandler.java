package io;

import java.util.InputMismatchException;
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

    public static int getMenuSelection(Scanner in, boolean backAllowed, int maxItems) {
        String input = "";

        do {
            if (in.hasNext()) {
                input = in.nextLine().trim();
                if (input.equals("q")) {
                    in.close();
                    return 0;
                } else if (input.equals("b") && backAllowed) {
                    return 9;
                } else {
                    try {
                        int inputNum = Integer.parseInt(input);
                        if (inputNum > 0 && inputNum <= maxItems) return inputNum;
                        else {
                            print("Please select a valid option by inputing the correct number or letter");
                            printPrompt();
                        }
                    } catch (NumberFormatException ex) {
                        print("Please select a valid option by inputing the correct number or letter");
                        printPrompt();
                    }
                }
            } else {
                print("Please select a valid option by inputing the correct number or letter");
                printPrompt();
            }
        } while (true);
    }

    public static String getCardNumber(Scanner in) {
        // Scanner in = new Scanner(System.in);
        // if (in.hasNextLine()) {
        //     in.nextLine();
        // }
        long input = 0;
        while (true) {
            if (!in.hasNextLong()) {
                print("Please enter a valid card number, with no spaces");
                printPrompt();
                in.nextLine();
                continue;
            }

            input = in.nextLong();
            if (String.valueOf(input).length() == 16) {
                // in.close();
                return String.valueOf(input);
            } else {
                print("Please enter a valid card number, with no spaces");
                printPrompt();
            }
        }
    }

    public static String getCardExpiration(Scanner in) {
        // Scanner in = new Scanner(System.in);
        // if (in.hasNextLine()) {
        //     in.nextLine();
        // }
        String input = "";
        while (true) {
            input = in.nextLine();
            if (input.trim().length() == 4) {
                try {
                    Integer.parseInt(input);
                    // in.close();
                    return input.trim();
                } catch (NumberFormatException ex) {
                    print("Please enter the expiration data, in the format 'MMYY'");
                    printPrompt();
                }
            } else if (input.trim().length() > 0) {
                print("Please enter the expiration data in the format 'MMYY'");
                printPrompt();
            }
        }
    }

    public static String getCardSecurity(Scanner in) {
        // Scanner in = new Scanner(System.in);
        // if (in.hasNextLine()) {
        //     in.nextLine();
        // }
        String input = "";
        while (true) {
            input = in.nextLine();
            if (input.trim().length() == 3) {
                try {
                    Integer.parseInt(input);
                    // in.close();
                    return input.trim();
                } catch (NumberFormatException ex) {
                    print("Please enter a valid security code");
                    printPrompt();
                }
            } else {
                print("Please enter a valid secuirty code");
                printPrompt();
            }
        }
    }

    public static String getCardPin(Scanner in) {
        // Scanner in = new Scanner(System.in);
        // if (in.hasNextLine()) {
        //     in.nextLine();
        // }
        String input = "";
        while (true) {
            input = in.nextLine();
            if (input.trim().length() > 0 && input.trim().length() <= 10) {
                try {
                    Integer.parseInt(input);
                    // in.close();
                    return input.trim();
                } catch (NumberFormatException ex) {
                    print("Please enter a valid pin");
                    printPrompt();
                }
            } else {
                print("Please enter a valid pin");
                printPrompt();
            }
        }
    }

    public static double getWithdrawAmount(Scanner in, boolean isAtm) {
        Double input = 0.0;
        while (true) {
            try {
                input = in.nextDouble();
            } catch (InputMismatchException ex) {
                print("Please enter a valid number");
                printPrompt();
                continue;
            }

            if (isAtm) {
                if (input % 20 == 0) {
                    return input;
                } else {
                    print("Please enter a whole number that is a multiple of 20");
                    System.out.print("> $");
                }
            } else return input;
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