package interfaces.items;

import java.util.Scanner;
import java.sql.*;
import java.util.ArrayList;

import io.IOHandler;

public class Withdraw {
    public Withdraw() {
        super();
    }

    public static void withdraw(Scanner in, Connection db, boolean isAtm) {
        String success = "false";
        ArrayList<String> ret;

        do {
            IOHandler.print("\nMake a withdraw:");
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

            ret = findAccount(cardNumber, cardExp, cardSecurity, cardPin, db);
            success = ret.get(0);
            if (success.equals("false")) {
                IOHandler.print("\nWould you like to:");
                IOHandler.print("\t1: Continue");
                IOHandler.print("\tb: Back");
                IOHandler.print("\tq: Quit");
                int menuSelection = IOHandler.getMenuSelection(in, true, 1);

                if (menuSelection == 9) return;
                else if (menuSelection == 0) {
                    IOHandler.print("\nThank you for using the ATM.");
                    System.exit(0);
                }
            }
        } while (success.equals("false"));

        double withdraw_amount = 0.0;
        IOHandler.print("\nAccount verified");
        if (isAtm) {
            IOHandler.print("--------------------------------------------------");
            IOHandler.print("Please enter the amount you would like to withdraw");
            System.out.print("(whole number divisible by 20): $");
            do {
                withdraw_amount = IOHandler.getWithdrawAmount(in, isAtm);
                boolean success_withdraw = attemptWithdraw(ret.get(1), ret.get(2), withdraw_amount, db);
                if (success_withdraw) {
                    IOHandler.print("\nThank you for your transaction.");
                    IOHandler.print("Don't forget to grab your card.");
                    return;
                }
            } while (true);
        }

    }

    private static ArrayList<String> findAccount(String num, String exp, String sec, String pin, Connection db) {
        try (PreparedStatement s = db.prepareStatement("SELECT account_number, balance, minimum_balance FROM debit_card JOIN checking_acct USING (account_number) WHERE card_number=? AND expiration=? AND security_code=? AND pin=?");
        ) {
            s.setString(1, num);
            s.setString(2, exp);
            s.setString(3, sec);
            s.setString(4, pin);
            ResultSet rs = s.executeQuery();
            
            if (rs.next()) {
                ArrayList<String> ret = new ArrayList<>();
                ret.add("true");
                ret.add(rs.getString(1));
                ret.add(rs.getString(2));
                ret.add(rs.getString(3));
                return ret;
            } else {
                IOHandler.print("\nThere is no account matching the entered card information.");
                // IOHandler.print("Please try re-entering the card information");
                ArrayList<String> ret = new ArrayList<>();
                ret.add("false");
                return ret;
            }
        } catch (SQLException ex) {
            IOHandler.print("There was an issue, please try again.");
            ArrayList<String> ret = new ArrayList<>();
            ret.add("false");
            return ret;
        }
    }

    private static boolean attemptWithdraw(String account_number, String current_balance, double withdraw_amount, Connection db) {
        double balance = 0.0;
        try {
            balance = Double.parseDouble(current_balance);
        } catch (NumberFormatException ex) {
            IOHandler.print("There was an error parsing your current balance.");
            IOHandler.print("Cancelling transaction.");
            System.exit(0);
        }

        if (balance - withdraw_amount < 0) {
            IOHandler.print("You are attempting to withdraw more than you have in your account");
            IOHandler.print("Please enter a lower amount");
            return false;
        } else {
            try (PreparedStatement s = db.prepareStatement("UPDATE checking_acct SET balance=? WHERE account_number=?");
            ){
                String new_balance = String.format("%.2f", balance - withdraw_amount);
                s.setString(1, new_balance);
                s.setString(2, account_number);
                s.executeUpdate();
                IOHandler.print("");
                IOHandler.printBalance(account_number, new_balance);
                return true;
            } catch (SQLException ex) {
                IOHandler.print("There was an issue, please try again.");
                return false;
            }
        }
    }
}