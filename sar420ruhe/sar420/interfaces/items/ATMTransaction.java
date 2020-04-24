package interfaces.items;

import java.util.Scanner;
import java.sql.*;

import io.IOHandler;
import interfaces.items.CheckBalance;
import interfaces.items.Withdraw;

public class ATMTransaction {
    private final static int NUM_MENUS = 2;

    public ATMTransaction() {
        super();
    }

    public static void menu(Scanner in, Connection db) {
        boolean success = false;
        String cardNumber = "";
        String cardExp = "";
        String cardSecurity = "";
        String cardPin = "";

        do {
            IOHandler.print("\nEnter card Information:");
            IOHandler.print("---------------------------------------------");
            IOHandler.print("Please enter your card number, without spaces");
            IOHandler.printPrompt();
            cardNumber = IOHandler.getCardNumber(in);

            IOHandler.print("Please enter your expiration date in the form, 'MMYY'");
            IOHandler.printPrompt();
            cardExp = IOHandler.getCardExpiration(in);

            IOHandler.print("Please enter your security code");
            IOHandler.printPrompt();
            cardSecurity = IOHandler.getCardSecurity(in);

            IOHandler.print("Please enter your pin");
            IOHandler.printPrompt();
            cardPin = IOHandler.getCardPin(in);

            success = findAccount(cardNumber, cardExp, cardSecurity, cardPin, db);
        } while (!success);

        do {
            IOHandler.print("\nWould you like to check your balance or make a withdraw?");
            IOHandler.print("\t1: Check Balance");
            IOHandler.print("\t2: Withdraw");
            IOHandler.print("\tb: Back");
            IOHandler.print("\tq: Quit");
            IOHandler.printPrompt();

            int menuSelection = IOHandler.getMenuSelection(in, true, NUM_MENUS);

            if (menuSelection == 1) CheckBalance.checkBalance(in, cardNumber, cardExp, cardSecurity, cardPin, db);
            else if (menuSelection == 2) {
                Withdraw.withdraw(in, cardNumber, cardExp, cardSecurity, cardPin, db, true);
                return;
            }
            else if (menuSelection == -1) return;
            else if (menuSelection == 0) {
                IOHandler.print("\nThank you for using the ATM.");
                System.exit(0);
            }
        } while(true);
    }

    private static boolean findAccount(String num, String exp, String sec, String pin, Connection db) {
        try (PreparedStatement s = db.prepareStatement("SELECT account_number, balance FROM debit_card JOIN checking_acct USING (account_number) WHERE card_number=? AND expiration=? AND security_code=? AND pin=?");
        ) {
            s.setString(1, num);
            s.setString(2, exp);
            s.setString(3, sec);
            s.setString(4, pin);
            ResultSet rs = s.executeQuery();
            
            if (rs.next()) {
                return true;
            } else {
                IOHandler.print("\nThere is no account matching the entered card information.");
                // IOHandler.print("Please try re-entering the card information");
                return false;
            }
        } catch (SQLException ex) {
            IOHandler.print("There was an issue, please try again.");
            return false;
        }
    }
}