package interfaces.items;

import java.util.Scanner;
import java.sql.*;
import java.util.ArrayList;

import io.IOHandler;

public class Withdraw {
    public Withdraw() {
        super();
    }

    public static void withdraw(Scanner in, String branch_id, String cardNumber, String cardExp, String cardSecurity, String cardPin, Connection db, boolean isAtm) {
        ArrayList<String> ret;

        ret = findAccount(cardNumber, cardExp, cardSecurity, cardPin, db);

        double withdraw_amount = 0.0;
        if (isAtm) {
            IOHandler.print("Please enter the amount you would like to withdraw");
            System.out.print("(whole number divisible by 20): $");
            do {
                withdraw_amount = IOHandler.getWithdrawAmount(in, isAtm);
                boolean success_withdraw = attemptATMWithdraw(ret.get(0), ret.get(1), branch_id, ret.get(2), withdraw_amount, db);
                if (success_withdraw) {
                    IOHandler.print("\nThank you for your transaction.");
                    IOHandler.print("Don't forget to grab your card.");
                    return;
                }
            } while (true);
        }
    }

    public static void tellerWithdraw(Scanner in, String branch_id, String account_number, Connection db) {
        double balance = 0.0;
        double min_bal = 0.0;
        String cust_id = "";
        double withdraw_amount = 0.0;

        try (PreparedStatement s = db.prepareStatement("SELECT balance, minimum_balance, customer_id FROM account WHERE account_number=?");
        ) {
            s.setString(1, account_number);
            ResultSet rs = s.executeQuery();
            rs.next();
            balance = rs.getDouble(1);
            min_bal = rs.getDouble(2);
            cust_id = rs.getString(3);
        } catch (SQLException ex) {
            IOHandler.print("There was an issue, please try again.");
            return;
        }

        IOHandler.print("\nPlease enter the amount you would like to withdraw");
        System.out.print("> $");
        do {
            withdraw_amount = IOHandler.getWithdrawAmount(in, false);
            boolean success_withdraw = attemptTellerWithdraw(in, branch_id, cust_id, account_number, balance, min_bal, withdraw_amount, db);
            if (success_withdraw) {
                IOHandler.print("\nThank you for your transaction.");
                return;
            }
        } while (true);
    }

    private static ArrayList<String> findAccount(String num, String exp, String sec, String pin, Connection db) {
        try (PreparedStatement s = db.prepareStatement("SELECT account_number, balance, debit_card.customer_id FROM debit_card JOIN checking_acct USING (account_number) WHERE card_number=? AND expiration=? AND security_code=? AND pin=?");
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
                ret.add(rs.getString(3));
                return ret;
            } else {
                IOHandler.print("\nThere is no account matching the entered card information.");
                ArrayList<String> ret = new ArrayList<>();
                return ret;
            }
        } catch (SQLException ex) {
            IOHandler.print("There was an issue, please try again.");
            ArrayList<String> ret = new ArrayList<>();
            return ret;
        }
    }

    private static boolean attemptATMWithdraw(String account_number, String current_balance, String branch_id, String cust_id, double withdraw_amount, Connection db) {
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
            try (PreparedStatement s = db.prepareStatement("UPDATE account SET balance=? WHERE account_number=?");
                 PreparedStatement transInsert = db.prepareStatement("INSERT INTO atm_withdraw (trans_id,amount,time,customer_id,branch_id,account_number) VALUES (?,?,sysdate,?,?,?)");
            ){
                String new_balance = String.format("%.2f", balance - withdraw_amount);
                s.setString(1, new_balance);
                s.setString(2, account_number);
                s.executeUpdate();
                IOHandler.print("");
                IOHandler.printBalance(account_number, new_balance);

                long trans_id = makeTransID(db);
                transInsert.setLong(1, trans_id);
                transInsert.setString(2, String.format("%.2f", withdraw_amount));
                transInsert.setString(3, cust_id);
                transInsert.setString(4, branch_id);
                transInsert.setString(5, account_number);
                transInsert.executeUpdate();
                
                return true;
            } catch (SQLException ex) {
                IOHandler.print("There was an issue, please try again.");
                return false;
            }
        }
    }

    private static boolean attemptTellerWithdraw(Scanner in, String branch_id, String cust_id, String account_number, double balance, double min_bal, double withdraw_amount, Connection db) {
        try (PreparedStatement s = db.prepareStatement("UPDATE account SET balance=? WHERE account_number=?");
             PreparedStatement getPenalty = db.prepareStatement("SELECT penalty FROM savings_acct WHERE account_number=?");
             PreparedStatement transInsert = db.prepareStatement("INSERT INTO teller_withdraw (trans_id,amount,time,customer_id,branch_id,account_number) VALUES (?,?,sysdate,?,?,?)");
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
                        
                        long trans_id = makeTransID(db);
                        transInsert.setLong(1, trans_id);
                        transInsert.setString(2, String.format("%.2f", withdraw_amount + penalty));
                        transInsert.setString(3, cust_id);
                        transInsert.setString(4, branch_id);
                        transInsert.setString(5, account_number);
                        transInsert.executeUpdate();
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

                long trans_id = makeTransID(db);
                transInsert.setLong(1, trans_id);
                transInsert.setString(2, String.format("%.2f", withdraw_amount));
                transInsert.setString(3, cust_id);
                transInsert.setString(4, branch_id);
                transInsert.setString(5, account_number);
                transInsert.executeUpdate();

                return true;
            }
            return true;
        } catch (SQLException ex) {
            IOHandler.print("There was an issue, please try again.");
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