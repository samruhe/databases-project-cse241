package interfaces;

import java.util.Scanner;
import java.sql.Connection;

import io.IOHandler;

public class Purchases {
    public Purchases() {
        super();
    }

    public static void menu(Scanner in, Connection db) {
        IOHandler.print("Make a purchase!!");
    }
}