package Broker;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class PortIPManager {

    private int totalNumOfServers;
    private static final Map<Integer, ServerCredentials> IP_PortMap = new HashMap<>();

    public Map<Integer, ServerCredentials> getIP_PortMap() {
        return IP_PortMap;
    }

    class ServerCredentials{
        private InetAddress ip;
        private int port;
        private Socket socket = null;
        private DataInputStream din;
        private DataOutputStream dout;
        private boolean isOnline;

        ServerCredentials(InetAddress ip, int port) throws IOException {
            this.ip = ip;
            this.port = port;
            this.socket = new Socket(ip, port);
            this.din = new DataInputStream(socket.getInputStream());
            this.dout = new DataOutputStream(socket.getOutputStream());
            this.isOnline = true;
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

    public void checkOnlineServers(int k){
        int onlineServerCounter = 0;
        for (Map.Entry<Integer, ServerCredentials> entry : IP_PortMap.entrySet()){
            if(entry.getValue().isOnline())
                onlineServerCounter+=1;
        }
        if(this.totalNumOfServers - onlineServerCounter >= k)
            System.err.println("WARNING: k or more servers are down, therefore there is no guarantee for the correctness of the output. . .");
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
            newCredentials = new ServerCredentials(InetAddress.getByName(splitArray[0]), Integer.parseInt(splitArray[1]));
            this.IP_PortMap.put(id, newCredentials);
            id+=1;
        }

        this.totalNumOfServers = IP_PortMap.size();

    }

}
