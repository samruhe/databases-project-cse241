import java.io.*;
import java.sql.*;
import java.util.Scanner;

class Main {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);

        boolean loggedIn = false;
        do {
            String[] creds = getCreds(in);
            try (Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@edgar1.cse.lehigh.edu:1521:cse241", creds[0], creds[1]);
                    PreparedStatement selectInstructor = conn.prepareStatement("SELECT to_char(i_id, '00000') AS i_id, instructor.name AS i_name, to_char(s_id, '00000') AS s_id, student.name AS s_name FROM instructor, advisor, student WHERE instructor.id=advisor.i_id AND student.id=advisor.s_id AND instructor.dept_name=? ORDER BY i_id, s_id");
                    PreparedStatement selectDepartments = conn.prepareStatement("SELECT dept_name FROM department WHERE dept_name LIKE ?");
                    PreparedStatement checkDepartment = conn.prepareStatement("SELECT dept_name FROM department WHERE dept_name=?");
            ) {
                loggedIn = true;

                // searchDepartments(in, selectDepartments);
                // checkDepartment(in, checkDepartment, selectInstructor);

            } catch (SQLException sqle) {
                if (sqle.toString().contains("invalid username/password"))
                    System.out.println("Connect error. Re-enter login data:");
                else
                    System.out.println("SQLException: " + sqle);
            }
        } while (!loggedIn);

        in.close();
    }

    private static String[] getCreds(Scanner in) {
        Console cnsl = System.console();
        String[] creds = new String[2];

        System.out.print("Enter Oracle user id: ");
        String username = in.nextLine();
        System.out.print("Enter Oracle password for " + username + ": ");
        
        // Uncomment next line and comment next two lines if you want password visible when entered
        // String password = in.nextLine();
        char[] pass = cnsl.readPassword();
        String password = String.copyValueOf(pass);
        creds[0] = username;
        creds[1] = password;

        return creds;
    }

}