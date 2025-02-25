package interfaces.items;

import java.util.Scanner;
import java.sql.*;
import java.util.ArrayList;

import io.IOHandler;

public class TellerTransaction {
    private final static int NUM_MENUS = 2;
    public TellerTransaction() {
        super();
    }

    public static void menu(Scanner in, Connection db) {
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

        IOHandler.print("\nPlease enter your ID number:");
        IOHandler.printPrompt();
        String custID = IOHandler.getCustomerID(in, false);

        ArrayList<String> acct_nums = findAccounts(custID, db);
        if (acct_nums.size() > 0) {
            IOHandler.print("b: Back");
            IOHandler.print("q: Quit");
            IOHandler.print("\nPlease select an account by entering the number:");
            IOHandler.printPrompt();
            int menuSelection = IOHandler.getMenuSelection(in, true, acct_nums.size());
            if (menuSelection == -1) return;
            else if (menuSelection == 0) {
                IOHandler.print("\nThank you for using the teller service.");
                System.exit(0);
            } else {
                nextMenu(in, branch, acct_nums.get(menuSelection - 1), db);
            }
        } else return;
    }

    private static ArrayList<String> findAccounts(String id, Connection db) {
        try (PreparedStatement s = db.prepareStatement("(SELECT account_number, balance, minimum_balance, 'Savings' as type FROM savings_acct WHERE customer_id=?) UNION (SELECT account_number, balance, minimum_balance, 'Checking' as type FROM checking_acct WHERE customer_id=?) ORDER BY TYPE")
        ){
            s.setString(1, id);
            s.setString(2, id);
            ResultSet rs = s.executeQuery();
            
            ArrayList<String> ret = new ArrayList<>();
            int numAcct = 1;
            if (rs.next()) {
                System.out.println("");
                System.out.printf("   %-20s%-20s%-20s%-20s\n", "Account Number", "Balance", "Minimum Balance", "Account Type");
                System.out.printf("   %-20s%-20s%-20s%-20s\n", "--------------", "----------", "---------------", "------------");
                System.out.print(numAcct++ + ": ");
                for (int i = 1; i <= 4; i++) {
                    String formatVal = "";
                    if (i == 1) {
                        formatVal = String.format("%-20s", "******" + rs.getString(i).substring(6));
                        ret.add(rs.getString(i));
                    }
                    else if (i == 4) formatVal = String.format("%-20s", rs.getString(i));
                    else formatVal = String.format("$%-20.2f", rs.getDouble(i));
                    System.out.print(formatVal);
                }
                System.out.println();
                while (rs.next()) {
                    System.out.print(numAcct++ + ": ");
                    for (int i = 1; i <= 4; i++) {
                        String formatVal = "";
                        if (i == 1) {
                            formatVal = String.format("%-20s", "******" + rs.getString(i).substring(6));
                            ret.add(rs.getString(i));
                        }
                        else if (i == 4) formatVal = String.format("%-20s", rs.getString(i));
                        else formatVal = String.format("$%-20.2f", rs.getDouble(i));
                        System.out.print(formatVal);

                    }
                    System.out.println();
                }
                return ret;
            } else {
                IOHandler.print("\nYou have no accounts.");
                return ret;
            }
        } catch (SQLException ex) {
            IOHandler.print("There was an issue. Please try again later.");
            return new ArrayList<String>();
        }
    }

    private static void nextMenu(Scanner in, String branch_id, String account_number, Connection db) {
        do {
            IOHandler.print("\nWould you like to make a deposit or withdraw?");
            IOHandler.print("\t1: Deposit");
            IOHandler.print("\t2: Withdraw");
            IOHandler.print("\tb: Back");
            IOHandler.print("\tq: Quit");
            IOHandler.printPrompt();

            int menuSelection = IOHandler.getMenuSelection(in, true, NUM_MENUS);

            if (menuSelection == 1) {
                Deposit.makeDeposit(in, branch_id, account_number, db);
                return;
            }
            else if (menuSelection == 2) {
                Withdraw.tellerWithdraw(in, branch_id, account_number, db);
                return;
            }
            else if (menuSelection == -1) return;
            else if (menuSelection == 0) {
                IOHandler.print("\nThank you for using the ATM.");
                System.exit(0);
            }
        } while(true);
    }

    private static ArrayList<String> displayBranches(Scanner in, Connection db) {
        ArrayList<String> branches = new ArrayList<>();

        try (PreparedStatement s = db.prepareStatement("(SELECT branch_id, line1, city, state, zip FROM teller JOIN address USING (address_id))");
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