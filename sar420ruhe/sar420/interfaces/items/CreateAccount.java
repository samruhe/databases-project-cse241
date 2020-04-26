package interfaces.items;

import java.util.Scanner;
import java.sql.*;
import java.util.ArrayList;

import io.IOHandler;

public class CreateAccount {
    public CreateAccount() {
        super();
    }

    public static void openAccount(Scanner in, String type, String cust_id, Connection db) {
        ArrayList<String> address = new ArrayList<>();
        String add_id = "";
        String name = "";
        boolean newCustomer = false;
        if (cust_id.equals("0")) {
            newCustomer = true;
            cust_id = String.valueOf(makeCustID(db));
            add_id = String.valueOf(makeAddressId(db));

            IOHandler.print("\nPlease enter your full name:");
            IOHandler.printPrompt();
            name = IOHandler.getCustomerName(in);

            IOHandler.print("\nThank you for choosing Nickel Savings and Loans:");
            IOHandler.print("Please enter some information before creating your account");
            address = IOHandler.getNewCustomer(in);
        }

        String account_number = String.valueOf(makeAccountNum(db));

        IOHandler.print("\nHow much would you like to add to your new account?");
        System.out.print("> $");
        double initial_amount = IOHandler.getDepositAmount(in);

        if (type.equals("checking")) {
            openChecking(account_number, initial_amount, cust_id, add_id, address, name, newCustomer, db);
        } else if (type.equals("savings")) {
            openSavings(account_number, initial_amount, cust_id, address, db);
        }
    }

    public static void openChecking(String acct_num, double initial_amount, String cust_id, String add_id, ArrayList<String> address, String name, boolean newCustomer, Connection db) {
        try (PreparedStatement insertCustomer = db.prepareStatement("INSERT INTO customer (customer_id,name,address_id) VALUES (?,?,?)");
             PreparedStatement insertAddress = db.prepareStatement("INSERT INTO address (address_id,line1,line2,city,state,zip) VALUES (?,?,?,?,?,?)");
            PreparedStatement insertAccount = db.prepareStatement("INSERT INTO checking_acct (account_number,balance,minimum_balance,customer_id) VALUES (?,?,?,?)");
        ) {
            if (newCustomer) {
                insertAddress.setString(1, add_id); 
                insertAddress.setString(2, address.get(0));
                insertAddress.setString(3, address.get(1));
                insertAddress.setString(4, address.get(2));
                insertAddress.setString(5, address.get(3));
                insertAddress.setString(6, address.get(4));
                insertAddress.executeUpdate();

                insertCustomer.setString(1, cust_id);
                insertCustomer.setString(2, name);
                insertCustomer.setString(3, add_id);
                insertCustomer.executeUpdate();
            }

            insertAccount.setString(1, acct_num);
            insertAccount.setString(2, String.format("%.2f", initial_amount));
            insertAccount.setString(3, "0");
            insertAccount.setString(4, cust_id);
            insertAccount.executeUpdate();

        } catch (SQLException ex) {
            IOHandler.print("\nThere was an issue. Please try again.");
            return;
        }
    }

    public static void openSavings(String acct_num, double initial_amount, String cust_id, ArrayList<String> address, Connection db) {

    }

    private static long makeCustID(Connection db) {
        long cust_id = 0;

        try (PreparedStatement s = db.prepareStatement("SELECT customer_id FROM customer");
        ) {
            boolean unique = false;
            
            do {
                cust_id = (long) Math.floor(Math.random() * 9_000_000_000L) + 1_000_000_000L;
                
                ResultSet rs = s.executeQuery();
                while (rs.next()) {
                    if (rs.getLong(1) == cust_id) {
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
        return cust_id;
    }

    private static long makeAccountNum(Connection db) {
        long acct_num = 0;

        try (PreparedStatement s = db.prepareStatement("SELECT account_number FROM account");
        ) {
            boolean unique = false;
            
            do {
                acct_num = (long) Math.floor(Math.random() * 9_000_000_000L) + 1_000_000_000L;
                
                ResultSet rs = s.executeQuery();
                while (rs.next()) {
                    if (rs.getLong(1) == acct_num) {
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
        return acct_num;
    }

    private static long makeAddressId(Connection db) {
        long add_id = 0;

        try (PreparedStatement s = db.prepareStatement("SELECT address_id FROM address");
        ) {
            boolean unique = false;
            
            do {
                add_id = (long) Math.floor(Math.random() * 9_000_000_000L) + 1_000_000_000L;
                
                ResultSet rs = s.executeQuery();
                while (rs.next()) {
                    if (rs.getLong(1) == add_id) {
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
        return add_id;
    }
}