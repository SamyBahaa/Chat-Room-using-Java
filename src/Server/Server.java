package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;



public class Server {
    public static final int SUBSCRIBE_PORT = 3000;
    public static final int BROADCAST_PORT = 3001;
    public static int totalClients = 0;
    public static ArrayList<Groups> groupsArrayList = new ArrayList<>();


    public static void main(String[] args){
        System.out.println("The server started .. ");
        ArrayList<Members> groupSection_1 = new ArrayList<>();
        ArrayList<Members> groupSection_2 = new ArrayList<>();
        Groups Section_1 = new Groups("Section_1",groupSection_1),Section_2=new Groups("Section_2",groupSection_2) ;
        groupsArrayList.add(Section_1);
        groupsArrayList.add(Section_2);
        new Thread() {
            public void run() {
                try {
                    ServerSocket ss = new ServerSocket(SUBSCRIBE_PORT);
                    while (true) {
                            new SUBSCRIBE_Service(ss.accept(),600 + totalClients++,groupsArrayList).start();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();

        new Thread() {
            public void run() {
                try {
                    ServerSocket ss = new ServerSocket(BROADCAST_PORT);
                    while (true) {
                            new BROADCAST_Service(ss.accept()).start();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
    private static class BROADCAST_Service extends Thread{
        Socket socket;

        public BROADCAST_Service(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                PrintWriter out = new PrintWriter(this.socket.getOutputStream(), true);
                BufferedReader groupBR = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
                String groupSectionID = groupBR.readLine();
                if (groupSectionID.equals("1")) {
                    String str  = "What is the message to be broadcast?";
                    out.println(str);
                    String message = null;
                    while((message=groupBR.readLine())!= null){
                            System.out.println(message);

                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
    private static class SUBSCRIBE_Service extends Thread{
        Socket socket;
        int clientID;
        ArrayList<Groups> groupsArrayList = new ArrayList<>();

        public SUBSCRIBE_Service(Socket socket, int clientID, ArrayList<Groups> groupsArrayList) {
            this.socket = socket;
            this.clientID = clientID;
            this.groupsArrayList=groupsArrayList;
            System.out.println("Connection with Client #" + clientID + "at socket " + socket);
        }

        public void run() {
            try {

                PrintWriter out = new PrintWriter(this.socket.getOutputStream(), true);
                BufferedReader groupBR = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
                String groupSectionID = groupBR.readLine();
                if (groupSectionID.equals("1")) {
                    Members M = new Members(clientID);
                    Groups Section1 =groupsArrayList.get(0);
                    boolean State = Section1.addMember(M);

                    if (State) {
                        String str  = "SUCCESS You are successfully added to the group Section-1 with ID =" + clientID;
                        out.println(str);

                    }else{
                        String str  = "FAILURE Sorry, the group reached its maximum count";
                        out.println(str);
                    }

                }else{
                    Members M = new Members(clientID);
                    Groups Section2 =groupsArrayList.get(1);
                    boolean State = Section2.addMember(M);
                    if (State) {
                        String str  = "SUCCESS You are successfully added to the group Section-2 with ID =" + clientID;
                        out.println(str);
                    }else{
                        String str  = "FAILURE Sorry, the group reached its maximum count";
                        out.println(str);
                    }
                }
                out.close();
                groupBR.close();
                socket.close();
                System.out.println("Connection with Client #" + this.clientID + " finished..");
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }


    public static final class Members {
        int ID;

        public Members(int ID) {
            this.ID = ID;
        }

        public int getID() {
            return ID;
        }
    }
    public static final class Groups {
        String Name;
        ArrayList<Members> membersArrayList ;
        final int MaximumNumber = 3;

        public Groups(String name, ArrayList<Members> membersArrayList) {
            Name = name;
            this.membersArrayList = membersArrayList;
        }

        public boolean addMember(Members M){
            if (membersArrayList.size() != MaximumNumber){
                membersArrayList.add(M);
                System.out.println(membersArrayList.size());
                return true;
            }
            return false;
        }
    }
}
