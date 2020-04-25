package interfaces;

import java.util.Scanner;
import java.sql.Connection;

import io.IOHandler;
import interfaces.items.CardPurchase;

public class Purchases {
    private final static int NUM_CARDS = 2;
    public Purchases() {
        super();
    }

    public static void menu(Scanner in, Connection db) {
        IOHandler.print("\nWhere are you trying to make a purchase?");
        IOHandler.printPrompt();
        String vendor = IOHandler.getVendorName(in);

        IOHandler.print("\nWould you like to use a credit or debit card?");
        IOHandler.print("\t1: Credit Card");
        IOHandler.print("\t2: Debit Card");
        IOHandler.print("\tb: Back");
        IOHandler.print("\tq: Quit");
        IOHandler.printPrompt();
        int menuSelection = IOHandler.getMenuSelection(in, true, NUM_CARDS);

        if (menuSelection == 1) CardPurchase.makePurchase(in, vendor, false, db);
        else if (menuSelection == 2) CardPurchase.makePurchase(in, vendor, true, db);
        else if (menuSelection == -1) return;
        else if (menuSelection == 0) {
            IOHandler.print("\nThank you.");
            System.exit(0);
        }
    }
}