import java.io.*;
import java.sql.*;
import java.util.Scanner;

import interfaces.DepositWithdraw;
import io.IOHandler;

class Main {
    private final static int NUM_INTERFACES = 2;
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);

        boolean loggedIn = false;
        do {
            String[] creds = IOHandler.getCreds(in);
            try (Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@edgar1.cse.lehigh.edu:1521:cse241", creds[0], creds[1]);
                    // PreparedStatement selectInstructor = conn.prepareStatement("");
                    // PreparedStatement selectDepartments = conn.prepareStatement("");
                    // PreparedStatement checkDepartment = conn.prepareStatement("");
            ) {
                loggedIn = true;

                System.out.println("\nPlease select an interface option by typing the number: ");
                System.out.println("\t1: Deposit/Withdraw");
                System.out.println("\t2: Purchases");
                System.out.print("> ");

                int interfaceSelection = IOHandler.getMenuSelection(in, NUM_INTERFACES);
                if (interfaceSelection == 1) depositWithdrawInterface();
                else if (interfaceSelection == 2) pruchaseInterface();

            } catch (SQLException sqle) {
                if (sqle.toString().contains("invalid username/password"))
                    System.out.println("Connect error. Re-enter login data:");
                else
                    System.out.println("SQLException: " + sqle);
            }
        } while (!loggedIn);

        in.close();
    }

    private static void depositWithdrawInterface() {
        DepositWithdraw.menu();
    }

    private static void pruchaseInterface() {

    }

}