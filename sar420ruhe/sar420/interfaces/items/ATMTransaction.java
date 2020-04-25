package interfaces.items;

import java.util.Scanner;
import java.sql.*;
import java.util.ArrayList;

import io.IOHandler;

public class ATMTransaction {
    private final static int NUM_MENUS = 2;

    public ATMTransaction() {
        super();
    }

    public static void menu(Scanner in, Connection db) {
        boolean success = false;
        String cardNumber = "";
        String cardExp = "";
        String cardSecurity = "";
        String cardPin = "";
        String branch = "";

        ArrayList<String> branches = displayBranches(in, db);
        IOHandler.print("b: Back");
        IOHandler.print("q: Quit");
        IOHandler.print("\nPlease select a branch location by entering the number:");
        IOHandler.printPrompt();
        int menuSelect = IOHandler.getMenuSelection(in, true, branches.size());

        if (menuSelect == -1) return;
        else if (menuSelect == 0) {
            IOHandler.print("\nThank you for using the teller service.");
            System.exit(0);
        } else branch = branches.get(menuSelect - 1);

        do {
            IOHandler.print("\nEnter card Information:");
            IOHandler.print("---------------------------------------------");
            IOHandler.print("Please enter your card number, without spaces");
            IOHandler.printPrompt();
            cardNumber = IOHandler.getCardNumber(in);

            IOHandler.print("Please enter your expiration date in the form, 'MMYY'");
            IOHandler.printPrompt();
            cardExp = IOHandler.getCardExpiration(in);

            IOHandler.print("Please enter your security code");
            IOHandler.printPrompt();
            cardSecurity = IOHandler.getCardSecurity(in);

            IOHandler.print("Please enter your pin");
            IOHandler.printPrompt();
            cardPin = IOHandler.getCardPin(in);

            success = findAccount(cardNumber, cardExp, cardSecurity, cardPin, db);
        } while (!success);

        IOHandler.print("\nAccount Verified");
        IOHandler.printBreak();

        do {
            IOHandler.print("\nWould you like to check your balance or make a withdraw?");
            IOHandler.print("\t1: Check Balance");
            IOHandler.print("\t2: Withdraw");
            IOHandler.print("\tb: Back");
            IOHandler.print("\tq: Quit");
            IOHandler.printPrompt();

            int menuSelection = IOHandler.getMenuSelection(in, true, NUM_MENUS);

            if (menuSelection == 1) CheckBalance.checkBalance(in, cardNumber, cardExp, cardSecurity, cardPin, db);
            else if (menuSelection == 2) {
                Withdraw.withdraw(in, branch, cardNumber, cardExp, cardSecurity, cardPin, db, true);
                return;
            }
            else if (menuSelection == -1) return;
            else if (menuSelection == 0) {
                IOHandler.print("\nThank you for using the ATM.");
                System.exit(0);
            }
        } while(true);
    }

    private static boolean findAccount(String num, String exp, String sec, String pin, Connection db) {
        try (PreparedStatement s = db.prepareStatement("SELECT account_number, balance FROM debit_card JOIN checking_acct USING (account_number) WHERE card_number=? AND expiration=? AND security_code=? AND pin=?");
        ) {
            s.setString(1, num);
            s.setString(2, exp);
            s.setString(3, sec);
            s.setString(4, pin);
            ResultSet rs = s.executeQuery();
            
            if (rs.next()) {
                return true;
            } else {
                IOHandler.print("\nThere is no account matching the entered card information.");
                return false;
            }
        } catch (SQLException ex) {
            IOHandler.print("There was an issue, please try again.");
            return false;
        }
    }

    private static ArrayList<String> displayBranches(Scanner in, Connection db) {
        ArrayList<String> branches = new ArrayList<>();

        try (PreparedStatement s = db.prepareStatement("(SELECT branch_id, line1, city, state, zip FROM atm JOIN address USING (address_id))");
        ) {
            ResultSet rs = s.executeQuery();

            IOHandler.print("\nPlease select a branch:");
            System.out.printf("   %-20s%-20s%-20s%-20s\n", "Street", "City", "State", "Zip Code");
            System.out.printf("   %-20s%-20s%-20s%-20s\n", "-------------------", "-------------------", "-------------------", "---------");
            
            int numBranch = 1;
            while (rs.next()) {    
                System.out.print(numBranch++ + ": ");
                for (int i = 1; i <= 5; i++) {
                    String formatVal = "";
                    if (i == 1) {
                        branches.add(rs.getString(i));
                    }
                    else {
                        String value = rs.getString(i).length() > 18 ? rs.getString(i).substring(0, 18) : rs.getString(i);
                        formatVal = String.format("%-20s", value);
                    }
                    System.out.print(formatVal);
                }
                System.out.println();
            }
        } catch (SQLException ex) {
            IOHandler.print("\nThere was an issue, exiting.");
            System.exit(0);
        }

        return branches;
    }
}