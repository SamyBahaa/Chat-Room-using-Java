package AdminClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class AdminClient {
    public static final int BROADCAST_PORT = 3001;
    public static final String Section_1 = "1";
    public static final String Section_2 = "2";
//    public static boolean end = false;

    public static void main(String[] args) throws IOException {
        System.out.println("Welcome, Which group do you want to broadcast your\n" +
                           "message to? (1) Section-1 or (2) Section-2");
        Scanner scanner = new Scanner(System.in);

        String groupSectionID = (scanner.nextInt()) == 1 ? Section_1 : Section_2;

        Socket s = new Socket("localhost", BROADCAST_PORT);

        PrintWriter out = new PrintWriter(s.getOutputStream(), true);
        BufferedReader massageReader = new BufferedReader( new InputStreamReader(s.getInputStream()));
        out.println(groupSectionID);
        String messageInfo = null;
        String messageContent = null;
        if ((messageInfo = massageReader.readLine()) != null) {
            System.out.println(messageInfo);
            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
            messageContent = consoleReader.readLine();
        }
        while (!"end".equals(messageContent)) {
            out.println(messageContent);
        }
        scanner.close();
        massageReader.close();
        out.close();
        s.close();
    }
}
