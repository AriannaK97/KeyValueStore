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

    /**
     * Insert if the key value does nor already exist in the trie
     * */
    public String insert(JSONObject jo, Trie currentTrie){
        for (Object key: jo.keySet()){
            Object payload = jo.get((String) key);
            int level;
            int length = ((String) key).length();
            int index;

            TrieNode pCrawl = currentTrie.getRoot();

            /*Iterate through the children array of the current node
             * The position of a valid character (letters (upper and lowercase) and numbers) in the array are
             * lowercase letters [0, 25]
             * uppercase letters [26, 51]
             * numbers [52, 61]
             * */
            if(search((String)key, currentTrie).contains("NOT FOUND")) {
                for (level = 0; level < length; level++) {
                    index = ((String) key).charAt(level);
                    index = (index - 'a' >= 0) ? index - 'a' : (index - 'A' >= 0) ? index - 'A' + 26 : index - '0' + 36;    //find the table offset for the current char

                    if (pCrawl.getChildrenInPosition(index) == null)
                        pCrawl.setChildrenInPosition(index, new TrieNode());

                    pCrawl = pCrawl.getChildrenInPosition(index);
                }

                // mark last node as leaf
                if (!(payload instanceof JSONObject)) {
                    pCrawl.setEndOfWord(true);
                    if (payload instanceof Long)
                        pCrawl.setEndOfWordPayload(String.valueOf(payload));
                    else if (payload instanceof Double)
                        pCrawl.setEndOfWordPayload(String.valueOf(payload));
                    else if (payload instanceof String)
                        pCrawl.setEndOfWordPayload((String) payload);
                } else {
                    if (!pCrawl.hasPayloadTree())
                        pCrawl.setPayloadTree(new Trie());
                    insert((JSONObject) payload, pCrawl.getPayloadTree());
                }
            }else{
                return "Duplicate record - Insert failed";
            }
        }
        return "OK";
    }


    /**
     * Search and return the value of top level key only and return it if found getChildrenPayload()==null
     * */
    public String search(String key, Trie currentTrie) {
        int level;
        int length = key.length();
        int index;
        String composedAnswer;
        TrieNode pCrawl = currentTrie.getRoot();
        String output = "NOT FOUND";

        /*Search through the children array of the current node iteratively
         * The position of a valid character (letters (upper and lowercase) and numbers) in the array are
         * lowercase letters [0, 25]
         * uppercase letters [26, 51]
         * numbers [52, 61]
         * */
        for (level = 0; level < length; level++){
            index = key.charAt(level);
            index = (index - 'a' >= 0) ? index - 'a' : (index - 'A' >= 0) ? index - 'A' + 26 : index - '0' + 36;

            if (pCrawl.getChildrenInPosition(index) == null)
                return output;

            pCrawl = pCrawl.getChildrenInPosition(index);
        }

        /*If the node is not end of word search in subtrie else get the node payload/value*/
        if (!pCrawl.isEndOfWord() && pCrawl.hasPayloadTree())
            composedAnswer = searchSubTrie(pCrawl.getPayloadTree().getRoot(), "");
        else if (pCrawl.isEndOfWord())
            composedAnswer = pCrawl.getChildrenPayload();
        else
            composedAnswer = output;

        return composedAnswer;
    }

    public String searchSubTrie(TrieNode currentRoot, String prevCharInTrie){
        String composedAnswer = "";
        String currentString, currentChar;
        TrieNode pCrawl;
        int numOfKeysInLevel = 0;
        int key;
        int index;

        /*Search through the children array of the current node iteratively and their children recursively
         * The position of a valid character (letters (upper and lowercase) and numbers) in the array are
         * lowercase letters [0, 25]
         * uppercase letters [26, 51]
         * numbers [52, 61]
         * */
        for(index = 0; index < NUM_OF_SYMBOLS; index++){

            if( currentRoot.getChildrenInPosition(index) != null){
                numOfKeysInLevel += 1;
                key = (index + 'a' >= 97) && ((index + 'a' <= 122)) ? index + 'a' : (index + 'A' - 26 >= 65)
                        && (index + 'A' - 26 <= 90) ? index + 'A' - 26 : index + '0' - 36;
                currentChar = Character.toString(key);
                pCrawl = currentRoot.getChildrenInPosition(index);

                /*If the current level has more than one children (to proper concatenate the characters and create the word)*/
                if(numOfKeysInLevel > 1 ){
                    currentChar = prevCharInTrie + currentChar;
                }

                /*Behave accordingly if the current node has a nested subtrie else descent further on the nodes of the current trie*/
                if(pCrawl.hasPayloadTree()){
                    composedAnswer += currentChar + " : {" + searchSubTrie(currentRoot.getChildrenInPosition(index).getPayloadTree().getRoot(), "") + "}; ";
                }else {
                    composedAnswer += currentChar + searchSubTrie(currentRoot.getChildrenInPosition(index), currentChar);
                }
            }
        }

        numOfKeysInLevel = 0;
        currentString = currentRoot.getChildrenPayload();
        if (currentRoot.isEndOfWord() && currentString == null)
            composedAnswer += " : { } ";
        else if(currentRoot.isEndOfWord())
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

        /*Search through the children array of the current node iteratively
         * The position of a valid character (letters (upper and lowercase) and numbers) in the array are
         * lowercase letters [0, 25]
         * uppercase letters [26, 51]
         * numbers [52, 61]
         * */
        for (level = 0; level < length; level++){
            index = keysArray[depth].charAt(level);
            index = (index - 'a' >= 0) ? index - 'a' : (index - 'A' >= 0) ? index - 'A' + 26 : index - '0' + 36;

            if (pCrawl.getChildrenInPosition(index) == null)
                return "NOT FOUND";

            pCrawl = pCrawl.getChildrenInPosition(index);
        }

        depth+=1;

        /*Recur to the child for the next key in QUERY*/
        if(depth < arraySize && pCrawl.getChildrenPayload().equals("-") && pCrawl.hasPayloadTree())
            composedAnswer += querySearch(keysArray, depth, pCrawl.getPayloadTree().root);
        else if(pCrawl.isEndOfWord())
            composedAnswer += pCrawl.getChildrenPayload();
        else
            composedAnswer = "NOT FOUND";

        /*If the complex key provided from input does not reach an end of word state,
         *look linearly the in subtrees if they exist. In any other case it returns NOT FOUND
         * */
        if(composedAnswer.contains("NOT FOUND") && pCrawl.hasPayloadTree() && depth == arraySize)
            composedAnswer = " {" + searchSubTrie(pCrawl.getPayloadTree().getRoot(), "") + "}";


        return composedAnswer;
    }

    /**
     * Returns true if root has no children, else false
     * */
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
        /*If tree is empty*/
        if (currentRoot == null)
            return null;

        /*last character of key is being processed*/
        if (depth == key.length()) {

            /*The node is not end of word any longer*/
            if (currentRoot.isEndOfWord())
                currentRoot.setEndOfWord(false);

            /*The node does not have any subTrees*/
            if (currentRoot.hasPayloadTree())
                currentRoot.setPayloadTree(null);

            /*The given given is not prefix of any other word*/
            if (isEmpty(currentRoot)) {
                currentRoot = null;
            }

            return currentRoot;
        }

        /*Recur for the child, if it is not the last character*/
        int index = key.charAt(depth);
        index = (index - 'a' >= 0) ? index - 'a' : (index - 'A' >= 0) ? index - 'A' + 26 : index - '0' + 36;
        currentRoot.setChildrenInPosition(index, delete(currentRoot.getChildrenInPosition(index), key, depth + 1));

        /*In case the root is childless and not endOfWord*/
        if (isEmpty(currentRoot) && !currentRoot.isEndOfWord()) {
            currentRoot = null;
        }

        return currentRoot;
    }

}
