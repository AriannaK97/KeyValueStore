package Broker;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Logger;

public class PortIPManager {

    private int totalNumOfServers;
    private static final Map<Integer, ServerCredentials> IP_PortMap = new HashMap<>();

    /**
     * Auxiliary class for server information
     * */
    class ServerCredentials{
        private InetAddress ip;
        private int port;
        private Socket socket = null;
        private DataInputStream din;
        private DataOutputStream dout;
        private boolean isOnline;

        ServerCredentials(InetAddress ip, int port) throws IOException {
            try{
                this.ip = ip;
                this.port = port;
                this.socket = new Socket(ip, port);
                this.din = new DataInputStream(socket.getInputStream());
                this.dout = new DataOutputStream(socket.getOutputStream());
                this.isOnline = true;
            }catch (IOException ioException){
                throw new IOException("Server with ip: " + ip.toString() + " listening to port: " + port + " is down. Connection refused.");
            }
        }

        public boolean isOnline() {
            return isOnline;
        }

        public InetAddress getIp() {
            return ip;
        }

        public int getPort() {
            return port;
        }

        public Socket getSocket() {
            return socket;
        }

        public void setOnline(boolean online) {
            isOnline = online;
        }

        public DataInputStream getDin() {
            return din;
        }

        public DataOutputStream getDout() {
            return dout;
        }

    }

    public int getTotalNumOfServers() { return totalNumOfServers; }

    public Map<Integer, ServerCredentials> getIP_PortMap() {
        return IP_PortMap;
    }

    /**
     * Returns true if a sufficient number of servers is still online
     * */
    public boolean checkIfSufficientOnlineServers(int k) {

        int onlineServerCounter = 0;
        ServerCredentials serverCredentials;
        String str;

        for (Map.Entry<Integer, ServerCredentials> entry : IP_PortMap.entrySet()){
            try {
                serverCredentials = entry.getValue();
                serverCredentials.getDout().writeUTF("STILL_ALIVE?");
                serverCredentials.getDout().flush();
                str = serverCredentials.getDin().readUTF();
                if (str.equals("yes"))
                    onlineServerCounter += 1;
            }catch (Exception e){
                Logger.getLogger("ExceptionLog");
                entry.getValue().setOnline(false);
            }
        }

        /*If no server is left online*/
        if(onlineServerCounter == 0){
            System.err.println("WARNING: No server is up and running.\nExit...");
            System.exit(1);
        }

        /*If more or equal to k servers are left online*/
        if(this.totalNumOfServers - onlineServerCounter >= k){
            System.err.println("WARNING: k or more servers are down, therefore there is no guarantee for the correctness of the output. . .");
            return false;
        }
        return true;
    }


    /**
     * Reads server file and invokes connection to servers
     * All data needed for the connection via sockets are stored in the
     * ServerCredentials class
     * */
    public void readServerFile(String fileName) throws IOException {

        File file = new File(fileName);
        Scanner sc = new Scanner(file);
        String currentLine;
        String[] splitArray;
        ServerCredentials newCredentials;
        int id = 0;

        while (sc.hasNextLine()){
            currentLine = sc.nextLine();
            splitArray = currentLine.split(" ");
            try{
                newCredentials = new ServerCredentials(InetAddress.getByName(splitArray[0]), Integer.parseInt(splitArray[1]));
                IP_PortMap.put(id, newCredentials);
                id+=1;
            }catch (IOException ioException){
                System.err.println(ioException.getMessage());
            }
        }

        this.totalNumOfServers = IP_PortMap.size();

    }

}
