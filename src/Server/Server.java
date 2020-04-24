package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.ArrayList;



public class Server {
    public static final int SUBSCRIBE_PORT = 3000;
    public static final int BROADCAST_PORT = 3001;
    public static int totalClients = 0;
    public static ArrayList<Groups> groupsArrayList = new ArrayList<>();


    public static void main(String[] args){
        System.out.println("The server started .. ");
        /**
         * in this server chat we assume that we have only two groups section-1 and section-2 and each section can handel
         * a maximum member of 3 and here we initialize the group array list.the member client allow only to choose the
         *  one group and listen to what will the admin send to this groups
         */
        ArrayList<Members> groupSection_1 = new ArrayList<>();
        ArrayList<Members> groupSection_2 = new ArrayList<>();
        Groups Section_1 = new Groups("Section_1",groupSection_1),Section_2=new Groups("Section_2",groupSection_2) ;
        groupsArrayList.add(Section_1);
        groupsArrayList.add(Section_2);
        /**
         * we use this thread for subscription to either one of the two sections, and in the constructor we pass
         * total client number + 600  , the groupe array list to choose between the 2 sections
         */
        new Thread() {
            public void run() {
                try {
                    ServerSocket ss = new ServerSocket(SUBSCRIBE_PORT);
                    while (true) {
                            new SUBSCRIBE_Service(ss.accept(),600 + totalClients++,groupsArrayList).start();
                            System.out.println("Total Number of clients = "+ totalClients);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
        /**
         * we use this thread fro broadcasting the message from the admins member  to the group that they will
         * choose to send their message to.
         */
        new Thread() {
            public void run() {
                try {
                    ServerSocket ss = new ServerSocket(BROADCAST_PORT);
                    while (true) {
                            new BROADCAST_Service(ss.accept(),groupsArrayList).start();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
    private static class BROADCAST_Service extends Thread{
        Socket socket;
        ArrayList<Groups> groupsArrayList = new ArrayList<>();
        public BROADCAST_Service(Socket socket,ArrayList<Groups> groupsArrayList)  {
            this.socket = socket;
            this.groupsArrayList=groupsArrayList;
        }

        public void run() {
            try {
                PrintWriter out = new PrintWriter(this.socket.getOutputStream(), true);
                BufferedReader groupBR = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
                String groupSectionID = groupBR.readLine();
                DatagramSocket clientSocket = new DatagramSocket();
                String str  = "What is the message to be broadcast?";
                out.println(str);
                while (groupSectionID.equals("1")) {
                    String message = null;
                    Groups Section1 =groupsArrayList.get(0);
                    if ((message=groupBR.readLine())!= null && !message.equals("end")){
                            // convert to byte
                            byte[] sendData = new byte[1024];
                            sendData=message.getBytes();
                            // get the IP Address of the server
                            InetAddress IPAddress = InetAddress.getByName("localhost");
                            for (int i=0; i<Section1.membersArrayList.size() ;i++){
                                int clientID= Section1.membersArrayList.get(i).getID();
                                //create packet
                                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, clientID);
                                clientSocket.send(sendPacket);
                            }
                    }else{
                        break;
                    }
                }
                while (groupSectionID.equals("2")) {
                    String message = null;
                    Groups Section2 =groupsArrayList.get(1);
                    if ((message=groupBR.readLine())!= null && !message.equals("end")){
                        // convert to byte
                        byte[] sendData = new byte[1024];
                        sendData=message.getBytes();
                        // get the IP Address of the server
                        InetAddress IPAddress = InetAddress.getByName("localhost");
                        for (int i=0; i<Section2.membersArrayList.size() ;i++){
                            int clientID= Section2.membersArrayList.get(i).getID();
                            //create packet
                            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, clientID);
                            clientSocket.send(sendPacket);
                        }
                    }else{
                        break;
                    }
                }
                out.close();
                groupBR.close();
                socket.close();
                clientSocket.close();
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
            System.out.println("Subscribe Service Connection with Client #" + clientID + "at socket " + socket);
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
                        while(true){

                        }

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
                        while(true){

                        }
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

    /**
     * the server contain two inner class that it is implemented as required from the PDF
     */
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
                return true;
            }
            return false;
        }
    }
}
