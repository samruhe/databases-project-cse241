package interfaces.items;

import java.util.Scanner;
import java.sql.*;

import io.IOHandler;

public class CheckBalance {
    public CheckBalance() {
        super();
    }

    public static void checkBalance(Scanner in, String cardNumber, String cardExp, String cardSecurity, String cardPin, Connection db) {
        getBalance(cardNumber, cardExp, cardSecurity, cardPin, db);
    }

    private static void getBalance(String num, String exp, String sec, String pin, Connection db) {
        try (PreparedStatement s = db.prepareStatement("SELECT account_number, balance FROM debit_card JOIN checking_acct USING (account_number) WHERE card_number=? AND expiration=? AND security_code=? AND pin=?");
        ) {
            s.setString(1, num);
            s.setString(2, exp);
            s.setString(3, sec);
            s.setString(4, pin);
            ResultSet rs = s.executeQuery();
            
            rs.next();
            IOHandler.print("");
            IOHandler.printBalance(rs.getString(1), rs.getString(2));
        } catch (SQLException ex) {
            IOHandler.print("There was an issue, please try again later.");
        }
    }
}