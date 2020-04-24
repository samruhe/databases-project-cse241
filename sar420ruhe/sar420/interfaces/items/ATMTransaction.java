package interfaces.items;

import java.util.Scanner;
import java.sql.Connection;

import io.IOHandler;
import interfaces.items.CheckBalance;
import interfaces.items.Withdraw;

public class ATMTransaction {
    private final static int NUM_MENUS = 2;

    public ATMTransaction() {
        super();
    }

    public static void menu(Scanner in, Connection db) {
        // Scanner in = new Scanner(System.in);
        do {
            IOHandler.print("\nWould you like to make a deposit or withdraw?");
            IOHandler.print("\t1: Check Balance");
            IOHandler.print("\t2: Withdraw");
            IOHandler.print("\tb: Back");
            IOHandler.print("\tq: Quit");
            IOHandler.printPrompt();

            int menuSelection = IOHandler.getMenuSelection(in, true, NUM_MENUS);

            if (menuSelection == 1) CheckBalance.checkBalance(in, db);
            else if (menuSelection == 2) Withdraw.withdraw(in, db);
            else if (menuSelection == 9) return;
            else if (menuSelection == 0) {
                IOHandler.print("\nThank you for using the ATM.");
                System.exit(0);
            }
        } while(true);
    }
}