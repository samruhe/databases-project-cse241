package interfaces;

import java.util.Scanner;
import java.sql.Connection;

import io.IOHandler;
import interfaces.items.ATMTransaction;
import interfaces.items.TellerTransaction;

public class DepositWithdraw {
    private final static int NUM_MENUS = 2;
    protected Connection db;

    public DepositWithdraw(Connection db) {
        this.db = db;
    }

    public static void menu (Scanner in, Connection db) {
        do {
            IOHandler.print("\nWould you like to use an ATM or a teller, please enter a number:");
            IOHandler.print("\t1: ATM");
            IOHandler.print("\t2: Teller");
            IOHandler.print("\tb: Back");
            IOHandler.print("\tq: Quit");
            IOHandler.printPrompt();

            int menuSelection = IOHandler.getMenuSelection(in, true, NUM_MENUS);
            
            if (menuSelection == 1) ATMTransaction.menu(in, db);
            else if (menuSelection == 2) TellerTransaction.menu(in, db);
            else if (menuSelection == -1) return;
            else if (menuSelection == 0) {
                IOHandler.print("\nThank you for using the deposit/withdraw interface.");
                System.exit(0);
            }
        } while (true);
    }
}