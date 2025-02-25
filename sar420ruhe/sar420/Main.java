import java.sql.*;
import java.util.Scanner;

import interfaces.DepositWithdraw;
import interfaces.OpenAccount;
import interfaces.Purchases;
import io.IOHandler;

class Main {
    private final static int NUM_INTERFACES = 3;
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);

        boolean loggedIn = false;
        do {
            String[] creds = IOHandler.getCreds(in);
            try (Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@edgar1.cse.lehigh.edu:1521:cse241", creds[0], creds[1]);
            ) {
                loggedIn = true;
                Connection db = conn;
                do {
                    IOHandler.print("\nPlease select an interface option by typing the number: ");
                    IOHandler.print("\t1: Deposit/Withdraw");
                    IOHandler.print("\t2: Open New Account");
                    IOHandler.print("\t3: Purchases");
                    IOHandler.print("\tq: Quit");
                    IOHandler.printPrompt();

                    int interfaceSelection = IOHandler.getMenuSelection(in, false, NUM_INTERFACES);
                    if (interfaceSelection == 1) DepositWithdraw.menu(in, db);
                    else if (interfaceSelection == 2) OpenAccount.menu(in, db);
                    else if (interfaceSelection == 3) Purchases.menu(in, db);
                    else if (interfaceSelection == 0) {
                        IOHandler.print("\nThank you.");
                        System.exit(0);
                    }
                } while (true);

            } catch (SQLException sqle) {
                if (sqle.toString().contains("invalid username/password"))
                    IOHandler.print("Login error. Re-enter login data:");
                else
                    IOHandler.print("Connect error. Re-enter login data");
            }
        } while (!loggedIn);

        in.close();
    }
}