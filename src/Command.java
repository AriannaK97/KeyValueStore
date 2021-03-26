import java.util.Arrays;
import java.util.Scanner;

public class Command {

    private Scanner sc = new Scanner(System.in);

    public void readFromConsole(){

        String command;
        String[] splitArray = new String[2];

        System.out.println("Please enter next command. . .");
        command = sc.nextLine();
        while(!command.equals("QUIT")) {

            splitArray = extractCommand(command);
            commandDispatcher(splitArray[0], splitArray[1]);

            System.out.println("Please enter next command. . .");
            command = sc.nextLine();
        }
        System.out.println("Terminating. . .");
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

    private void commandDispatcher(String commandName, String commandTail){
        switch (commandName) {
            case "PUT" -> put(commandTail);
            case "GET" -> get(commandTail);
            case "QUERY" -> query(commandTail);
            case "DELETE" -> delete(commandTail);
        }
    }

    private void put(String commandTail){
        String keyId;
        String[] payload;
        String[] wordSplit = commandTail.split(":");
        Parser.putLineInTrie(wordSplit);
    }

    private void get(String commandTail){
        System.out.println(commandTail + Arrays.toString(Trie.search(commandTail)));
    }

    private void delete(String commandTail){

    }

    private void query(String commandTail){

    }

}
