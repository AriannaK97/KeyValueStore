package Server;

import org.json.simple.JSONObject;

public class Trie {

    private static final int NUM_OF_SYMBOLS = 62;
    private TrieNode root;

    public Trie(){

        root = new TrieNode();
    }

    public TrieNode getRoot() {

        return this.root;
    }

    void insert(JSONObject jo, Trie currentTrie){
        for (Object key: jo.keySet()){
            Object payload = jo.get((String) key);
            int level;
            int length = ((String) key).length();
            int index;

            TrieNode pCrawl = currentTrie.getRoot();

            for (level = 0; level < length; level++) {
                index = ((String) key).charAt(level);
                index = (index - 'a' >= 0) ? index - 'a' : (index - 'A' >= 0) ? index - 'A' + 26 : index - '0' + 36;

                if (pCrawl.getChildrenInPosition(index) == null)
                    pCrawl.setChildrenInPosition(index, new TrieNode());

                pCrawl = pCrawl.getChildrenInPosition(index);
            }

            // mark last node as leaf
            if (!(payload instanceof JSONObject)) {
                pCrawl.setEndOfWord(true);
                pCrawl.setEndOfWordPayload((String) payload);
            }else{
                if(pCrawl.getPayloadTree()==null)
                    pCrawl.setPayloadTree(new Trie());
                insert((JSONObject) payload, pCrawl.getPayloadTree());
            }
        }
    }


    /**
     * Search and return the value of top level key only and return it if foungetChildrenPayload()==nulld
     * */
    public String search(String key, Trie currentTrie) {
        int level;
        int length = key.length();
        int index;
        String composedAnswer;
        TrieNode pCrawl = currentTrie.getRoot();
        String output = "NOT FOUND";

        for (level = 0; level < length; level++){
            index = key.charAt(level);
            index = (index - 'a' >= 0) ? index - 'a' : (index - 'A' >= 0) ? index - 'A' + 26 : index - '0' + 36;

            if (pCrawl.getChildrenInPosition(index) == null)
                return output;

            pCrawl = pCrawl.getChildrenInPosition(index);
        }

        if (!pCrawl.isEndOfWord())
            composedAnswer = searchSubTree(pCrawl.getPayloadTree().getRoot(), "", 0) ;
        else
            composedAnswer = pCrawl.getChildrenPayload();

        return composedAnswer;
    }

    public String searchSubTree(TrieNode currentRoot, String prevCharInTrie, int numOfKeysInPrevLevel){
        String composedAnswer = "";
        String currentString, currentChar;
        TrieNode pCrawl;
        int numOfKeysInLevel = 0;
        int key;
        int index;

        for(index = 0; index < NUM_OF_SYMBOLS; index++){
            if( currentRoot.getChildrenInPosition(index) != null){
                numOfKeysInLevel += 1;
                key = (index + 'a' >= 97) && ((index + 'a' <= 122)) ? index + 'a' : (index + 'A' - 26 >= 65)
                        && (index + 'A' - 26 <= 90) ? index + 'A' - 26 : index + '0' - 36;
                currentChar = Character.toString(key);
                pCrawl = currentRoot.getChildrenInPosition(index);
                if(numOfKeysInLevel > 1){
                    currentChar = prevCharInTrie + currentChar;
                }
                if(pCrawl.hasPayloadTree()){
                    composedAnswer += currentChar + " : {" + searchSubTree(currentRoot.getChildrenInPosition(index).getPayloadTree().getRoot(), currentChar, numOfKeysInLevel) + "}; ";
                    numOfKeysInLevel = 0;
                }else {
                    composedAnswer += currentChar + searchSubTree(currentRoot.getChildrenInPosition(index), currentChar, numOfKeysInLevel);
                    numOfKeysInLevel = 0;
                }
            }
        }

        currentString = currentRoot.getChildrenPayload();
        if (currentString == null)
            composedAnswer += " : { } ";
        else if (!currentString.equals("-"))
            composedAnswer += " : \"" + currentString + "\"; ";

        return composedAnswer;
    }

    /**
     * Search for nested keys
     * */
    public String querySearch(String[] keysArray, int depth, TrieNode pCrawl) {
        int level;
        int length = keysArray[depth].length();
        int index;
        int arraySize = keysArray.length;
        String composedAnswer = "";

        for (level = 0; level < length; level++){
            index = keysArray[depth].charAt(level);
            index = (index - 'a' >= 0) ? index - 'a' : (index - 'A' >= 0) ? index - 'A' + 26 : index - '0' + 36;

            if (pCrawl.getChildrenInPosition(index) == null)
                return "NOT FOUND";

            pCrawl = pCrawl.getChildrenInPosition(index);
        }

        depth+=1;

        if(depth < arraySize && pCrawl.getChildrenPayload()==null) {
            composedAnswer += querySearch(keysArray, depth, pCrawl.getPayloadTree().root);
        }
        else if(pCrawl.isEndOfWord())
            composedAnswer += pCrawl.getChildrenPayload();
        else
            composedAnswer = "NOT FOUND";

        if(composedAnswer.contains("NOT FOUND") && pCrawl.hasPayloadTree() && depth == arraySize)
            composedAnswer = " {" + searchSubTree(pCrawl.getPayloadTree().getRoot(), "", 0) + "}";


        return composedAnswer;
    }

    // Returns true if root has no children, else false
    boolean isEmpty(TrieNode currentRoot)
    {
        for (int i = 0; i < NUM_OF_SYMBOLS; i++)
            if (currentRoot.getChildrenInPosition(i) != null)
                return false;
        return true;
    }

    /**
     * Delete the top level key of the trie.
     * */
    public TrieNode delete(TrieNode currentRoot, String key, int depth) {
        // If tree is empty
        if (currentRoot == null)
            return null;

        // If last character of key is being processed
        if (depth == key.length()) {

            // This node is no more end of word after
            // removal of given key
            if (currentRoot.isEndOfWord())
                currentRoot.setEndOfWord(false);

            // If given is not prefix of any other word
            if (isEmpty(currentRoot)) {
                currentRoot = null;
            }

            return currentRoot;
        }

        // If not last character, recur for the child
        // obtained using ASCII value
        int index = key.charAt(depth);
        index = (index - 'a' >= 0) ? index - 'a' : (index - 'A' >= 0) ? index - 'A' + 26 : index - '0' + 36;
        currentRoot.setChildrenInPosition(index, delete(currentRoot.getChildrenInPosition(index), key, depth + 1));

        // If root does not have any child (its only child got
        // deleted), and it is not end of another word.
        if (isEmpty(currentRoot) && !currentRoot.isEndOfWord()) {
            currentRoot = null;
        }

        return currentRoot;
    }

}
