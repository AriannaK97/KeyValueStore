package Broker;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Logger;

import Broker.PortIPManager.ServerCredentials;

public class Broker {

    private static final PortIPManager portIPManager = new PortIPManager();
    private static int k;

    /**
     * Select a random set of servers. Used to distribute data among servers.
     * */
    private static Map<Integer, ServerCredentials> selectRandom_k_Servers(){
        Map<Integer, ServerCredentials> kSets = new HashMap<>();
        Map<Integer, ServerCredentials> PortMap = portIPManager.getIP_PortMap();
        int mapRandKey;

        int iterator = 0;
        while(iterator < k){
            mapRandKey = new Random().nextInt(PortMap.size());
            if(!kSets.containsKey(mapRandKey)){
                kSets.put(mapRandKey, PortMap.get(mapRandKey));
                iterator+=1;
            }
        }
        return kSets;
    }

    /**
     * Send records to the servers to initialize the trie structure
     * */
    public static void sendLineToServerToIndex(String inputFile){
        try {

            File file = new File(inputFile);
            Scanner sc = new Scanner(file);
            String currentLine, str;
            ServerCredentials serverCredentials;

            while (sc.hasNextLine()){
                currentLine = sc.nextLine();
                Map<Integer, ServerCredentials> kServers = selectRandom_k_Servers();

                for (Map.Entry<Integer, ServerCredentials> entry : kServers.entrySet()){
                    serverCredentials = entry.getValue();
                    serverCredentials.getDout().writeUTF("PUT "+ currentLine);
                    serverCredentials.getDout().flush();
                    str = serverCredentials.getDin().readUTF();
                    System.out.println(entry.getValue().getPort() + " " + str);

                }
            }

        }catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Wait for a new command from command line to dispatch it to all the
     * online servers given that enough of them are online.
     * */
    private static void dispatchCommandToServers() throws IOException {

        Map<Integer, ServerCredentials> PortMap = portIPManager.getIP_PortMap();
        Scanner sc = new Scanner(new InputStreamReader(System.in));
        ServerCredentials serverCredentials;
        String command;
        String str = "";
        boolean answeredQuery = false;

        System.out.println("Type next command . . .");
        command = sc.nextLine();
        while (!str.equals("stop")) {

            if (!portIPManager.checkIfSufficientOnlineServers(k) && command.contains("DELETE")) {
                System.err.println("Cannot proceed to delete record");
                System.out.println("Type next command . . .");
                command = sc.nextLine();
                continue;
            }

            for (Map.Entry<Integer, ServerCredentials> entry : PortMap.entrySet()) {
                try {
                    if(entry.getValue().isOnline()) {
                        serverCredentials = entry.getValue();
                        serverCredentials.getDout().writeUTF(command);
                        serverCredentials.getDout().flush();
                        str = serverCredentials.getDin().readUTF();
                        if (str.equals("stop")) {
                            entry.getValue().setOnline(false);
                            entry.getValue().getDin().close();
                            entry.getValue().getDout().close();
                            entry.getValue().getSocket().close();
                        }

                        if (!answeredQuery && !str.contains("NOT FOUND") && !str.equals("stop")) {
                            answeredQuery = true;
                            System.out.println(str);
                        }
                    }

                } catch (Exception e) {
                    Logger.getLogger("ExceptionLog");
                    entry.getValue().setOnline(false);
                    portIPManager.checkIfSufficientOnlineServers(k);
                }

            }

            if (!answeredQuery)
                System.out.println("NOT FOUND");
            answeredQuery = false;
            System.out.println("Type next command . . .");
            command = sc.nextLine();

        }

    }


    /**
     * Parse and check correctness of input arguments passed in main
     * */
    public static void parseBrokerInputArgs(String[] args) throws Exception{

        if(args.length == 6 && args[0].equals("-s") && args[2].equals("-i") && args[4].equals("-k"))
            return;
        System.err.println("Wrong arguments for Broker.");
        throw new Exception();

    }


    /**
     * Broker main class
     * */
    public static void main(String[] args)throws Exception{

        try {
            parseBrokerInputArgs(args);
        }catch (Exception e) {
            e.printStackTrace();
        }

        k = Integer.parseInt(args[5]);
        portIPManager.readServerFile(args[1]);
        sendLineToServerToIndex(args[3]);
        dispatchCommandToServers();

    }

}
