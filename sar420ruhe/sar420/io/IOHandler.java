package io;

import java.util.InputMismatchException;
import java.util.Scanner;
import java.io.Console;
import java.util.ArrayList;

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
                    return -1;
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

    public static double getDepositAmount(Scanner in) {
        Double input = 0.0;
        while (true) {
            try {
                input = in.nextDouble();
                return input;
            } catch (InputMismatchException ex) {
                print("Please enter a valid number");
                printPrompt();
                continue;
            }
        }
    }

    public static String getCustomerID(Scanner in, boolean allowNew) {
        String input = "";
        while (true) {
            input = in.nextLine();
            if (allowNew) {
                if (input.trim().length() == 1) {
                    if (input.trim().equals("0")) return input.trim();
                    else {
                        print("Please enter a valid ID");
                    }
                }
            }

            if (input.trim().length() == 10) {
                try {
                    Long.parseLong(input);
                    return input.trim();
                } catch (NumberFormatException ex) {
                    print("Please enter a valid ID");
                    printPrompt();
                }
            } else {
                print("Please enter a valid ID");
                printPrompt();
            }
        }
    }

    public static ArrayList<String> getNewCustomer(Scanner in) {
        ArrayList<String> address = new ArrayList<>();
        String line1 = "";
        String line2 = "";
        String city = "";
        String state = "";
        String zip = "";

        print("Please enter your street address (line 1):");
        printPrompt();
        while (true) {
            line1 = in.nextLine();
            if (line1.trim().length() > 0) {
                line1 = line1.trim().length() <= 30 ? line1 : line1.trim().substring(0, 29);
                address.add(line1);
                break;
            } else {
                print("Please enter a valid street address");
                printPrompt();
            }
        }

        print("Please enter your street address (line 2):");
        print("(Press enter to leave blank)");
        printPrompt();
        while (true) {
            line2 = in.nextLine();
            line2 = line2.trim().length() <= 30 ? line2 : line2.trim().substring(0, 29);
            address.add(line2);
            break;
        }

        print("Please enter your city:");
        printPrompt();
        while (true) {
            city = in.nextLine();
            if (city.trim().length() > 0) {
                city = city.trim().length() <= 20 ? city : city.trim().substring(0, 19);
                address.add(city);
                break;
            } else {
                print("Please enter a valid city");
                printPrompt();
            }
        }

        print("Please enter your state:");
        printPrompt();
        while (true) {
            state = in.nextLine();
            if (state.trim().length() > 0) {
                state = state.trim().length() <= 15 ? state : state.trim().substring(0, 14);
                address.add(state);
                break;
            } else {
                print("Please enter a valid state");
                printPrompt();
            }
        }

        print("Please enter your zip code:");
        printPrompt();
        while (true) {
            zip = in.nextLine();
            if (zip.trim().length() == 5) {
                address.add(zip);
                break;
            } else {
                print("Please enter a valid zip code");
                printPrompt();
            }
        }

        return address;
    }

    public static String getCustomerName(Scanner in) {
        String name = "";
        
        while (true) {
            name = in.nextLine();
            if (name.trim().length() > 0) {
                return name.trim().length() <= 20 ? name : name.trim().substring(0, 19);
            } else {
                print("Please enter your full name:");
                printPrompt();
            }
        }
    }

    public static String getVendorName(Scanner in) {
        String input = "";

        while (true) {
            input = in.nextLine();
            if (input.trim().length() > 0 && input.trim().length() <= 20) {
                return input.trim();
            } else if (input.trim().length() > 20) {
                return input.trim().substring(0, 19);
            } else {
                print("Please enter a valid store name");
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

    public static void printBreak() {
        System.out.println("------------------------------------------");
    }

    public static void printBalance(String account_number, String balance) {
        System.out.println("Current posted balance for account ending in ******" + account_number.substring(6) + ":");
        System.out.printf("\t$%.2f\n", Double.parseDouble(balance));
    }

    public static void printNewAccount(String account_number, double balance, double minimum_balance, double penalty, boolean isSavings) {
        System.out.println("New account ending in ******" + account_number.substring(6) + ":");
        System.out.printf("\tBalance:         $%.2f\n", balance);
        if (isSavings) {
            System.out.printf("\tMinimum Balance: $%.2f\n", minimum_balance);
            System.out.printf("\tPenalty:         $%.2f\n", penalty);
        }
    }
}