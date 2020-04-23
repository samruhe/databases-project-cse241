package interfaces.items;

import java.util.Scanner;

import io.IOHandler;

public class Deposit {
    public Deposit() {
        super();
    }

    public static void makeDeposit(Scanner in) {
        System.out.println("\nMake a Deposit:");
        System.out.println("-------------------------------------------------");
        System.out.println("Please enter your card number, without spaces");
        System.out.print("> ");
    }
}