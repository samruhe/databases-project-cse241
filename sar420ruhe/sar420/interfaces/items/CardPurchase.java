package interfaces.items;

import java.util.Scanner;
import java.sql.*;

import io.IOHandler;

public class CardPurchase {
    public CardPurchase() {
        super();
    }

    public static void makePurchase(Scanner in, String vendor, boolean isDebit, Connection db) {
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

            if (isDebit) {
                IOHandler.print("Please enter your pin");
                IOHandler.printPrompt();
                cardPin = IOHandler.getCardPin(in);
            }

            success = findAccount(in, cardNumber, cardExp, cardSecurity, cardPin, isDebit, db);
        } while (!success);

        do {
            IOHandler.print("\nPlease enter the amount of the purchase");
            System.out.print("> $");
            double purchase_amount = IOHandler.getWithdrawAmount(in, false);
            boolean purchase_success = attemptPurchase(cardNumber, cardExp, cardSecurity, cardPin, purchase_amount, vendor, isDebit, db);
            if (purchase_success) return;
        } while(true);
    }

    private static boolean findAccount(Scanner in, String num, String exp, String sec, String pin, boolean isDebit, Connection db) {
        try (PreparedStatement debit = db.prepareStatement("SELECT account_number, balance FROM debit_card JOIN checking_acct USING (account_number) WHERE card_number=? AND expiration=? AND security_code=? AND pin=?");
             PreparedStatement credit = db.prepareStatement("SELECT credit_limit, outstanding_balance FROM credit_card WHERE card_number=? AND expiration=? AND security_code=?");
        ) {
            ResultSet rs;
            if (isDebit) {
                debit.setString(1, num);
                debit.setString(2, exp);
                debit.setString(3, sec);
                debit.setString(4, pin);
                rs = debit.executeQuery();
            } else {
                credit.setString(1, num);
                credit.setString(2, exp);
                credit.setString(3, sec);
                rs = credit.executeQuery();
            }
            
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

    private static boolean attemptPurchase(String num, String exp, String sec, String pin, double amount, String vendor, boolean isDebit, Connection db) {
        try (PreparedStatement debit = db.prepareStatement("SELECT account_number, balance, debit_card.customer_id FROM debit_card JOIN checking_acct USING (account_number) WHERE card_number=? AND expiration=? AND security_code=? AND pin=?");
             PreparedStatement credit = db.prepareStatement("SELECT credit_limit, outstanding_balance, customer_id FROM credit_card WHERE card_number=? AND expiration=? AND security_code=?");
             PreparedStatement purcDebit = db.prepareStatement("UPDATE account SET balance=? WHERE account_number=?");
             PreparedStatement purcCredit = db.prepareStatement("UPDATE credit_card SET outstanding_balance=? WHERE card_number=? AND expiration=? AND security_code=?");
             PreparedStatement purcTrans = db.prepareStatement("INSERT INTO purchase (trans_id,amount,time,customer_id,account_number,vendor) VALUES (?,?,sysdate,?,?,?)");
        ) {
            ResultSet rs;
            if (isDebit) {
                debit.setString(1, num);
                debit.setString(2, exp);
                debit.setString(3, sec);
                debit.setString(4, pin);
                rs = debit.executeQuery();

                rs.next();
                String account_number = rs.getString(1);
                double balance = rs.getDouble(2);
                String cust_id = rs.getString(3);

                if (balance - amount < 0) {
                    IOHandler.print("\nYou do not have enough in your account to pay for this.");
                    IOHandler.print("Try another card or cancel.");
                    return true;
                } else {
                    purcDebit.setString(1, String.format("%.2f", balance - amount));
                    purcDebit.setString(2, account_number);
                    purcDebit.executeUpdate();

                    long trans_id = makeTransID(db);
                    purcTrans.setString(1, String.valueOf(trans_id));
                    purcTrans.setString(2, String.format("%.2f", amount));
                    purcTrans.setString(3, cust_id);
                    purcTrans.setString(4, account_number);
                    purcTrans.setString(5, vendor);
                    purcTrans.executeUpdate();

                    IOHandler.print("\nThank you for your purchase.");
                    return true;
                }

            } else {
                credit.setString(1, num);
                credit.setString(2, exp);
                credit.setString(3, sec);
                rs = credit.executeQuery();
                
                rs.next();
                double credit_limit = rs.getDouble(1);
                double balance = rs.getDouble(2);
                String cust_id = rs.getString(3);

                if (balance + amount <= credit_limit) {
                    purcCredit.setString(1, String.format("%.2f", balance + amount));
                    purcCredit.setString(2, num);
                    purcCredit.setString(3, exp);
                    purcCredit.setString(4, sec);
                    purcCredit.executeUpdate();

                    long trans_id = makeTransID(db);
                    purcTrans.setString(1, String.valueOf(trans_id));
                    purcTrans.setString(2, String.format("%.2f", amount));
                    purcTrans.setString(3, cust_id);
                    purcTrans.setString(4, null);
                    purcTrans.setString(5, vendor);
                    purcTrans.executeUpdate();

                    IOHandler.print("\nThank you for you purchase.");
                    return true;
                } else {
                    IOHandler.print("\nYou will excede your credit limit with this purchase.");
                    IOHandler.print("Try another card or cancel.");
                    return true;
                }
            }
        } catch (SQLException ex) {
            IOHandler.print("\nThere was an issue, please try again");
            return false;
        }
    }

    private static long makeTransID(Connection db) {
        long trans_id = 0;

        try (PreparedStatement s = db.prepareStatement("(SELECT trans_id FROM purchase) UNION (SELECT trans_id FROM atm_withdraw) UNION (SELECT trans_id FROM teller_withdraw) UNION (SELECT trans_id FROM teller_deposit)");
        ) {
            boolean unique = false;
            
            do {
                trans_id = (long) Math.floor(Math.random() * 9_000_000_000L) + 1_000_000_000L;
                
                ResultSet rs = s.executeQuery();
                while (rs.next()) {
                    if (rs.getLong(1) == trans_id) {
                        unique = false;
                        break;
                    }
                    unique = true;
                }
            } while (!unique);

        } catch (SQLException ex) {
            IOHandler.print("There was an issue, exiting");
            System.exit(0);
        }
        return trans_id;
        
    }
}