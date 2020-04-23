package interfaces.items;

import java.util.Scanner;

import io.IOHandler;
import interfaces.items.CheckBalance;
import interfaces.items.Withdraw;

public class ATMTransaction {
    private final static int NUM_MENUS = 2;

    public ATMTransaction() {
        super();
    }

    public static void menu() {
        Scanner in = new Scanner(System.in);
        IOHandler.print("\nWould you like to make a deposit or withdraw?");
        IOHandler.print("\t1: Check Balance");
        IOHandler.print("\t2: Withdraw");
        IOHandler.printPrompt();

        int menuSelection = IOHandler.getMenuSelection(in, NUM_MENUS);

        if (menuSelection == 1) CheckBalance.checkBalance(in);
        else if (menuSelection == 2) System.out.println("Withdraw");
    }
}