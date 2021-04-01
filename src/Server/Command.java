package Server;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;

public class Command {

    private Scanner sc = new Scanner(System.in);
    private Trie trie;

    Command(Trie trie){
        this.trie = trie;
    }

    public void readFromStream(Socket s) throws IOException {

        String command = "";
        String str = "ok";
        String[] splitArray;

        DataInputStream din = new DataInputStream(s.getInputStream());
        DataOutputStream dout = new DataOutputStream(s.getOutputStream());

        command=din.readUTF();
        while(!command.equals("stop")){
            System.out.println("client says: "+command);
            splitArray = extractCommand(command);
            str = commandDispatcher(splitArray[0], splitArray[1]);
            dout.writeUTF(str);
            dout.flush();
            command=din.readUTF();
        }

        str = "stop";
        dout.writeUTF(str);
        dout.flush();
        System.out.println("Server " + s.getInetAddress() + " stopped working. . .");

    }

    private String[] extractCommand(String input){
        String[] _ret = new String[2];
        String[] splitArray = input.split(" ");
        _ret[0] = splitArray[0];
        _ret[1] = "";
        for (int i = 1; i < splitArray.length; i++){
            _ret[1] += splitArray[i];
        }
        return _ret;
    }

    public String commandDispatcher(String commandName, String commandTail){
        switch (commandName) {
            case "PUT" : return put(commandTail);
            case "GET" : return get(commandTail);
            case "QUERY" : return query(commandTail);
            case "DELETE" : return delete(commandTail);
        }
        return "Command " + commandName + " not found.";
    }

    private void putLineInTrie(String[] wordSplit) {
        String keyId;
        String[] payload;
        keyId = wordSplit[0].replace("\"", "").replaceAll("\\s+", "");
        payload = new String[wordSplit.length-1];

        for (int i = 1; i < wordSplit.length; i++){
            payload[i-1] = wordSplit[i];
        }

        Trie.insert(keyId, payload);

    }

    private String put(String commandTail){
        String keyId;
        String[] wordSplit = commandTail.split(":");
        putLineInTrie(wordSplit);
        return "ok";
    }

    private String get(String commandTail){
        return (commandTail + " " + Arrays.toString(Trie.search(commandTail)).replace("[ ]", ""));
    }

    private String delete(String commandTail){
        return "deleted";
    }

    private String query(String commandTail){
        return "Query";
    }

}
