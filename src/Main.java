import java.util.Arrays;

public class Main {
    public static void main(String[] args){

        if(args.length < 1){
            System.err.println("Usage: java Driver <inputFile>");
            System.exit(1);
        }

        try{
            Trie trie = new Trie();
            Parser parser = new Parser(args[0], trie);
            parser.readFile();
            Command command = new Command();
            command.readFromConsole();
        } catch (Exception ex){
            System.err.println(ex.getMessage());
        }

    }
}
