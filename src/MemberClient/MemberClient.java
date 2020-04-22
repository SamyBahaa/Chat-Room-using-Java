package MemberClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class MemberClient {
    public static final int SUBSCRIBE_PORT = 3000;
    public static final String Section_1 = "1";
    public static final String Section_2 = "2";

    public static void main(String[] args) throws IOException {
        System.out.println("Welcome, Which group do you want to join?\n" +
                "(1) Section-1 or (2) Section-2");
        Scanner scanner = new Scanner(System.in);

        String groupSectionID = (scanner.nextInt()) == 1 ? Section_1 : Section_2;

        Socket s = new Socket("localhost", SUBSCRIBE_PORT);

        PrintWriter out = new PrintWriter(s.getOutputStream(), true);
        Scanner messageScanner = new Scanner(new InputStreamReader(s.getInputStream()));
        out.println(groupSectionID);
        String stateMassage = messageScanner.next();

        if (stateMassage.equals("SUCCESS")) {

            String message = messageScanner.nextLine();
            System.out.println(message);
            while(true){

            }

        }else{
            String message = messageScanner.nextLine();
            System.out.println(message);
            scanner.close();
            out.close();
            s.close();
        }
    }
}
