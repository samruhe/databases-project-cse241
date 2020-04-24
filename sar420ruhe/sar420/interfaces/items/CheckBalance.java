package interfaces.items;

import java.util.Scanner;
import java.sql.*;

import io.IOHandler;

public class CheckBalance {
    public CheckBalance() {
        super();
    }

    public static void checkBalance(Scanner in, Connection db) {
        boolean success = false;

        do {
            IOHandler.print("\nCheck your balance:");
            IOHandler.print("---------------------------------------------");
            IOHandler.print("Please enter your card number, without spaces");
            IOHandler.printPrompt();
            String cardNumber = IOHandler.getCardNumber(in);

            IOHandler.print("Please enter your expiration date in the form, 'MMYY'");
            IOHandler.printPrompt();
            String cardExp = IOHandler.getCardExpiration(in);

            IOHandler.print("Please enter your security code");
            IOHandler.printPrompt();
            String cardSecurity = IOHandler.getCardSecurity(in);

            IOHandler.print("Please enter your pin");
            IOHandler.printPrompt();
            String cardPin = IOHandler.getCardPin(in);

            success = runSQL(cardNumber, cardExp, cardSecurity, cardPin, db);
        } while (!success);
    }

    private static boolean runSQL(String num, String exp, String sec, String pin, Connection db) {
        try (PreparedStatement s = db.prepareStatement("SELECT account_number, balance FROM debit_card JOIN checking_acct USING (account_number) WHERE card_number=? AND expiration=? AND security_code=? AND pin=?");
        ) {
            s.setString(1, num);
            s.setString(2, exp);
            s.setString(3, sec);
            s.setString(4, pin);
            ResultSet rs = s.executeQuery();
            
            if (rs.next()) {
                IOHandler.print("");
                IOHandler.printBalance(rs.getString(1), rs.getString(2));
                return true;
            } else {
                IOHandler.print("\nThere is no account matching the entered card information.");
                IOHandler.print("Please try re-entering the card information");
                return false;
            }
        } catch (SQLException ex) {
            IOHandler.print("There was an issue, please try again.");
            return false;
        }
    }
}