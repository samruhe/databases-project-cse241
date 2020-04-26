package interfaces;

import java.util.Scanner;
import java.sql.Connection;

import interfaces.items.CreateAccount;
import io.IOHandler;

public class OpenAccount {
    private final static int NUM_ACCT_TYPE = 2;
    public OpenAccount() {
        super();
    }

    public static void menu(Scanner in, Connection db) {
        IOHandler.print("\nPlease enter your ID to begin or enter 0 for new customer");
        IOHandler.printPrompt();
        String customer_id = IOHandler.getCustomerID(in, true);

        IOHandler.print("\nWhich type of account would you like to open?");
        IOHandler.print("\t1: Checking");
        IOHandler.print("\t2: Savings");
        IOHandler.print("\tb: Back");
        IOHandler.print("\tq: Quit");
        IOHandler.printPrompt();
        
        int menuSelection = IOHandler.getMenuSelection(in, true, NUM_ACCT_TYPE);
        if (menuSelection == 1) {
            CreateAccount.openAccount(in, "checking", customer_id, db);
            return;
        }
        else if (menuSelection == 2) {
            CreateAccount.openAccount(in, "savings", customer_id, db);
            return;
        }
        else if (menuSelection == -1) return;
        else if (menuSelection == 0) {
            IOHandler.print("\nThank you.");
            System.exit(0);
        }
    }
}