package interfaces;

import java.util.Scanner;

import io.IOHandler;
import interfaces.Deposit;
import interfaces.Withdraw;

public class ATMTransaction {
    private final static int NUM_MENUS = 2;

    public ATMTransaction() {
        super();
    }

    public static void menu() {
        Scanner in = new Scanner(System.in);
        System.out.println("\nWould you like to make a deposit or withdraw?");
        System.out.println("\t1: Deposit");
        System.out.println("\t2: Withdraw");
        System.out.print("> ");

        int menuSelection = IOHandler.getMenuSelection(in, NUM_MENUS);

        if (menuSelection == 1) System.out.println("Deposit");
        else if (menuSelection == 2) System.out.println("Withdraw");
    }
}