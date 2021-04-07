package Server;

import java.io.*;
import java.net.Socket;
import java.text.ParseException;
import java.util.Scanner;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;


public class Command {

    private Scanner sc = new Scanner(System.in);
    private Trie trie;

    public Command(Trie trie){
        this.trie = trie;
    }

    public void readFromStream(Socket s) throws IOException, ParseException {

        String command;
        String str;
        String[] splitArray;

        DataInputStream din = new DataInputStream(s.getInputStream());
        DataOutputStream dout = new DataOutputStream(s.getOutputStream());

        command=din.readUTF();
        while(!command.equals("stop")){
            System.out.println("client says: " + command);
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

    private JSONObject jsonParse(String record) throws org.json.simple.parser.ParseException {
        record = record.replace(";", ",");
        Object obj = new JSONParser().parse(record);
        JSONObject jo = (JSONObject) obj;
        System.out.println(jo);
        return jo;
    }

    public Trie readFile(String inputFile) throws FileNotFoundException {
        File file = new File(inputFile);
        Scanner scFile = new Scanner(file);
        String currentLine;
        String[] wordSplit;
        String[] payload;
        String keyId;

        while (scFile.hasNextLine()){
            currentLine = ("{" + scFile.nextLine()+ "}");
            try {
                JSONObject jsonObject = jsonParse(currentLine);
                //printJsonObject(jsonObject);
                this.trie.insert(jsonObject, this.trie);
            } catch (JSONException | org.json.simple.parser.ParseException e) {
                throw new RuntimeException(e);
            }
        }
        return trie;
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

    public String commandDispatcher(String commandName, String commandTail) throws ParseException {
        switch (commandName) {
            case "PUT" : return put(commandTail);
            case "GET" : return get(commandTail);
            case "QUERY" : return query(commandTail);
            case "DELETE" : return delete(commandTail);
        }
        return "Command " + commandName + " not found.";
    }

    private void printJsonObject(JSONObject jo){
        for (Object key : jo.keySet()) {
            Object keyvalue = jo.get((String) key);
            System.out.println("key: "+ key + " value: " + keyvalue);
        }
    }

    private String put(String commandTail) throws ParseException {

        String currentLine = ("{" + commandTail+ "}");
        try {
            /**
             * In case identical keys appear in the same level, we keep the last appeared one
             * */
            JSONObject jsonObject = jsonParse(currentLine);
            //printJsonObject(jsonObject);
            this.trie.insert(jsonObject, this.trie);
        } catch (JSONException | org.json.simple.parser.ParseException e) {
            throw new RuntimeException(e);
        }

        return "ok";
    }

    private String get(String commandTail){
        return (commandTail + " : {" + this.trie.search(commandTail, this.trie) + "}");
    }

    private String delete(String commandTail){
        trie.delete(this.trie.getRoot(), commandTail, 0);
        return "deleted";
    }

    private String query(String commandTail){
        String[] keyArray = commandTail.split("\\.");
        return (commandTail + " : " + this.trie.querySearch(keyArray, 0, this.trie.getRoot()));
    }

}
