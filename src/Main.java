import Server.Command;
import Server.Trie;

import java.util.Arrays;

public class Main {
    public static void main(String[] args){

        if(args.length < 1){
            System.err.println("Usage: java Driver <inputFile>");
            System.exit(1);
        }

        try{
            Trie trie = new Trie();
            Command command = new Command(trie);
            command.readFile(args[1]);
        } catch (Exception ex){
            System.err.println(ex.getMessage());
        }

    }
}