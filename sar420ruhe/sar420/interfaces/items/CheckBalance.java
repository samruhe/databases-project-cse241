package interfaces.items;

import java.util.Scanner;

import io.IOHandler;

public class CheckBalance {
    public CheckBalance() {
        super();
    }

    public static void checkBalance(Scanner in) {
        IOHandler.print("\nCheck your balance:");
        IOHandler.print("------------------------------");
        IOHandler.print("Please enter your card number, without spaces");
        IOHandler.printPrompt();
        String cardNumber = IOHandler.getCardNumber(in);
        System.out.println("Card Number: " + cardNumber);

        IOHandler.print("Please enter your expiration date in the form, 'MMYY'");
        IOHandler.printPrompt();
        String cardExp = IOHandler.getCardExpiration(in);
        System.out.println("Card Expiriation: " + cardExp);

        IOHandler.print("Please enter your security code");
        IOHandler.printPrompt();
        String cardSecurity = IOHandler.getCardSecurity(in);
        System.out.println("Card Security: " + cardSecurity);

        IOHandler.print("Please enter your pin");
        IOHandler.printPrompt();
        String cardPin = IOHandler.getCardPin(in);
        System.out.println("Card Pin: " + cardPin);
    }
}