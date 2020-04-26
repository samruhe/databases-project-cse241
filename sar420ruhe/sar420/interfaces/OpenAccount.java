package interfaces;

import java.util.Scanner;
import java.sql.Connection;

import io.IOHandler;

public class OpenAccount {
    public OpenAccount() {
        super();
    }

    public static void menu(Scanner in, Connection db) {
        IOHandler.print("\nPlease enter your ID to begin or enter 0 for new customer");
        IOHandler.printPrompt();
        String cust_id = IOHandler.getCustomerID(in, true);
        System.out.println("ID: " + cust_id);
        return;
    }
}