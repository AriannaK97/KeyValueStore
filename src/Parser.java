import java.io.*;
import java.util.Scanner;

public class Parser {

    private Scanner sc;
    private File file;
    private Trie trie;

    Parser(String inputFile, Trie trie) throws Exception{
        this.file = new File(inputFile);
        this.sc = new Scanner(file);
        this.trie = trie;
    }

    /*
    * Parses the input file given and return a trie populated
    * with the data from the input file
    * */
    Trie readFile() throws Exception{
        String currentLine;
        String[] wordSplit;
        String[] payload;
        String keyId;

        while (sc.hasNextLine()){

            currentLine = sc.nextLine();
            wordSplit = currentLine.split(":");
            putLineInTrie(wordSplit);
        }
        return trie;
    }

    public static void putLineInTrie(String[] wordSplit) {
        String keyId;
        String[] payload;
        keyId = wordSplit[0].replace("\"", "").replaceAll("\\s+", "");
        payload = new String[wordSplit.length-1];

        for (int i = 1; i < wordSplit.length; i++){
            payload[i-1] = wordSplit[i];
        }

        Trie.insert(keyId, payload);
    }


}
