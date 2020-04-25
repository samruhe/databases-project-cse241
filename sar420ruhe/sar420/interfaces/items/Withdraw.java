package interfaces.items;

import java.util.Scanner;
import java.sql.*;
import java.util.ArrayList;

import io.IOHandler;

public class Withdraw {
    public Withdraw() {
        super();
    }

    public static void withdraw(Scanner in, String cardNumber, String cardExp, String cardSecurity, String cardPin, Connection db, boolean isAtm) {
        ArrayList<String> ret;

        ret = findAccount(cardNumber, cardExp, cardSecurity, cardPin, db);

        double withdraw_amount = 0.0;
        IOHandler.print("\nAccount verified");
        if (isAtm) {
            IOHandler.print("--------------------------------------------------");
            IOHandler.print("Please enter the amount you would like to withdraw");
            System.out.print("(whole number divisible by 20): $");
            do {
                withdraw_amount = IOHandler.getWithdrawAmount(in, isAtm);
                boolean success_withdraw = attemptWithdraw(ret.get(0), ret.get(1), withdraw_amount, db);
                if (success_withdraw) {
                    IOHandler.print("\nThank you for your transaction.");
                    IOHandler.print("Don't forget to grab your card.");
                    IOHandler.print("--------------------------------------------------");
                    return;
                }
            } while (true);
        }
    }

    public static void tellerWithdraw(Scanner in, String account_number, Connection db) {
        double balance = 0.0;
        double min_bal = 0.0;
        double withdraw_amount = 0.0;

        try (PreparedStatement s = db.prepareStatement("SELECT balance, minimum_balance FROM account WHERE account_number=?");
        ) {
            s.setString(1, account_number);
            ResultSet rs = s.executeQuery();
            rs.next();
            balance = rs.getDouble(1);
            min_bal = rs.getDouble(2);
        } catch (SQLException ex) {
            IOHandler.print("There was an issue, please try again.");
            return;
        }

        IOHandler.print("\nPlease enter the amount you would like to withdraw");
        System.out.print("> $");
        do {
            withdraw_amount = IOHandler.getWithdrawAmount(in, false);
            boolean success_withdraw = attemptTellerWithdraw(in, account_number, balance, min_bal, withdraw_amount, db);
            if (success_withdraw) {
                IOHandler.print("\nThank you for your transaction.");
                return;
            }
        } while (true);
    }

    private static ArrayList<String> findAccount(String num, String exp, String sec, String pin, Connection db) {
        try (PreparedStatement s = db.prepareStatement("SELECT account_number, balance, FROM debit_card JOIN checking_acct USING (account_number) WHERE card_number=? AND expiration=? AND security_code=? AND pin=?");
        ) {
            s.setString(1, num);
            s.setString(2, exp);
            s.setString(3, sec);
            s.setString(4, pin);
            ResultSet rs = s.executeQuery();
            
            if (rs.next()) {
                ArrayList<String> ret = new ArrayList<>();
                ret.add(rs.getString(1));
                ret.add(rs.getString(2));
                return ret;
            } else {
                IOHandler.print("\nThere is no account matching the entered card information.");
                // IOHandler.print("Please try re-entering the card information");
                ArrayList<String> ret = new ArrayList<>();
                return ret;
            }
        } catch (SQLException ex) {
            IOHandler.print("There was an issue, please try again.");
            ArrayList<String> ret = new ArrayList<>();
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
            IOHandler.print("Please enter a smaller amount");
            return false;
        } else {
            try (/*PreparedStatement s = db.prepareStatement("UPDATE checking_acct SET balance=? WHERE account_number=?");*/
                 PreparedStatement s = db.prepareStatement("UPDATE account SET balance=? WHERE account_number=?");
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

    private static boolean attemptTellerWithdraw(Scanner in, String account_number, double balance, double min_bal, double withdraw_amount, Connection db) {
        try (PreparedStatement s = db.prepareStatement("UPDATE account SET balance=? WHERE account_number=?");
             PreparedStatement getPenalty = db.prepareStatement("SELECT penalty FROM savings_acct WHERE account_number=?");
        ) {
            double new_balance = balance - withdraw_amount;
            if (new_balance < 0) {
                IOHandler.print("You are attempting to withdraw more than you have in your account");
                IOHandler.print("Please enter a smaller amount");
                return false;
            }
            if (new_balance < min_bal) {
                if (min_bal == 0.0) {
                    IOHandler.print("You are attempting to withdraw more than you have in your account");
                    IOHandler.print("Please enter a smaller amount");
                    return false;
                } else {
                    IOHandler.print("\nThis withdraw will put you below your minimum account balance.");
                    IOHandler.print("You will recieve a penalty if you continue.");
                    IOHandler.print("\t1: Continue");
                    IOHandler.print("\tb: Back");
                    IOHandler.print("\tq: Quit");

                    int menuSelection = IOHandler.getMenuSelection(in, true, 1);
                    if (menuSelection == 1) {
                        getPenalty.setString(1, account_number);
                        ResultSet rs = getPenalty.executeQuery();
                        rs.next();
                        double penalty = rs.getDouble(1);
                        new_balance -= penalty;
                        if (new_balance < 0) {
                            IOHandler.print("\nThe penalty will cause your account to go below $0.00");
                            IOHandler.print("Cancelling transaction");
                            return true;
                        }
                        s.setString(1, String.format("%.2f", new_balance));
                        s.setString(2, account_number);
                        s.executeUpdate();
                        IOHandler.print("");
                        IOHandler.printBalance(account_number, String.format("%.2f", new_balance));
                        IOHandler.print("There was a penalty of $" + String.format("%.2f", penalty) + " applied to your withdraw.");
                        return true;
                    } else if (menuSelection == -1) return true;
                    else if (menuSelection == 0) {
                        IOHandler.print("Thank you for using the teller service.");
                        System.exit(0);
                    }
                }
            } else {
                s.setString(1, String.format("%.2f", new_balance));
                s.setString(2, account_number);
                s.executeUpdate();
                IOHandler.print("");
                IOHandler.printBalance(account_number, String.format("%.2f", new_balance));
                return true;
            }
            return true;
        } catch (SQLException ex) {
            IOHandler.print("There was an issue, please try again.");
            return false;
        }
    }
}