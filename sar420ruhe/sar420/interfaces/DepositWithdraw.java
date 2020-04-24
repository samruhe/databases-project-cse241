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

    public static void menu (Connection db) {
        System.out.println("\nWould you like to use an ATM or a teller, please enter a number:");
        System.out.println("\t1: ATM");
        System.out.println("\t2: Teller");
        System.out.print("> ");

        Scanner in = new Scanner(System.in);
        int menuSelection = IOHandler.getMenuSelection(in, NUM_MENUS);
        
        if (menuSelection == 1) ATMTransaction.menu(db);
        else if (menuSelection == 2) TellerTransaction.menu();
    }
}