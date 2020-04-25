package interfaces.items;

import java.util.Scanner;
import java.sql.*;

import io.IOHandler;

public class Deposit {
    public Deposit() {
        super();
    }

    public static void makeDeposit(Scanner in, String account_number, Connection db) {
        IOHandler.print("\nMake a Deposit to account: ******" + account_number.substring(6));
        IOHandler.print("Please enter the amount you would like to deposit");
        System.out.print("> $");
        double deposit_amount = 0.0;
        do {
            deposit_amount = IOHandler.getDepositAmount(in);
            boolean success_deposit = attemptDeposit(account_number, deposit_amount, db);
            if (success_deposit) {
                IOHandler.print("\nYour deposit was successful. Thank you.");
            }
            return;
        } while (true);
    }

    private static boolean attemptDeposit(String acct_num, double deposit_amount, Connection db) {
        try (PreparedStatement account = db.prepareStatement("UPDATE account SET balance=balance+? WHERE account_number=?");
             PreparedStatement newBalance = db.prepareStatement("SELECT balance FROM account WHERE account_number=?");
        ){
            account.setDouble(1, deposit_amount);
            account.setString(2, acct_num);
            account.executeUpdate();
            
            newBalance.setString(1, acct_num);
            ResultSet rs = newBalance.executeQuery();
            rs.next();
            IOHandler.print("");
            IOHandler.printBalance(acct_num, rs.getString(1));

            return true;
        } catch (SQLException ex) {
            IOHandler.print("\nThere was an error with your deposit. Please try again later.");
            return false;
        }
    }
}