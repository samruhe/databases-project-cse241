package interfaces.items;

import java.util.Scanner;
import java.sql.*;

import io.IOHandler;

public class Deposit {
    public Deposit() {
        super();
    }

    public static void makeDeposit(Scanner in, String branch_id, String account_number, Connection db) {
        IOHandler.print("\nMake a Deposit to account: ******" + account_number.substring(6));
        IOHandler.print("Please enter the amount you would like to deposit");
        System.out.print("> $");
        double deposit_amount = 0.0;
        do {
            deposit_amount = IOHandler.getDepositAmount(in);
            boolean success_deposit = attemptDeposit(account_number, branch_id, deposit_amount, db);
            if (success_deposit) {
                IOHandler.print("\nYour deposit was successful. Thank you.");
            }
            return;
        } while (true);
    }

    private static boolean attemptDeposit(String acct_num, String branch_id, double deposit_amount, Connection db) {
        try (PreparedStatement account = db.prepareStatement("UPDATE account SET balance=balance+? WHERE account_number=?");
             PreparedStatement newBalance = db.prepareStatement("SELECT balance, customer_id FROM account WHERE account_number=?");
             PreparedStatement transInsert = db.prepareStatement("INSERT INTO teller_deposit (trans_id,amount,time,customer_id,branch_id,account_number) VALUES (?,?,sysdate,?,?,?)");
        ){
            account.setDouble(1, deposit_amount);
            account.setString(2, acct_num);
            account.executeUpdate();
            
            newBalance.setString(1, acct_num);
            ResultSet rs = newBalance.executeQuery();
            rs.next();
            IOHandler.print("");
            IOHandler.printBalance(acct_num, rs.getString(1));
            String cust_id = rs.getString(2);

            long trans_id = makeTransID(db);
            transInsert.setLong(1, trans_id);
            transInsert.setString(2, String.format("%.2f", deposit_amount));
            transInsert.setString(3, cust_id);
            transInsert.setString(4, branch_id);
            transInsert.setString(5, acct_num);
            transInsert.executeUpdate();

            return true;
        } catch (SQLException ex) {
            IOHandler.print("\nThere was an error with your deposit. Please try again later.");
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