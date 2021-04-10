package Server;

public class TrieNode {
    private static int NUM_OF_SYMBOLS = 62;

    private TrieNode[] children = new TrieNode[NUM_OF_SYMBOLS];
    private boolean isEndOfWord;
    private Object payload;
    private Trie payloadTree;

    TrieNode(){
        isEndOfWord = false;
        for (int i = 0; i < NUM_OF_SYMBOLS; i++){
            children[i] = null;
        }
    }

    public boolean hasPayloadTree(){
        return this.payloadTree != null;
    }

    public TrieNode getChildrenInPosition(int index) {
        return children[index];
    }

    public String getChildrenPayload() {
        if(this.payloadTree == null) {
            if (this.payload instanceof Long)
                return String.valueOf(payload);
            else if (this.payload instanceof Double)
                return String.valueOf(payload);
            else if (this.payload instanceof String)
                return (String)payload;
        }
        return "-";
    }

    public void setChildrenInPosition(int index, TrieNode newNode) {

        this.children[index] = newNode;
    }

    public boolean isEndOfWord() {
        return isEndOfWord;
    }

    public void setEndOfWord(boolean endOfWord) {
        isEndOfWord = endOfWord;
    }

    public void setEndOfWordPayload(String payload){
        if(this.isEndOfWord){
            this.payload = payload;
        }
    }

    public Trie getPayloadTree() {
        return payloadTree;
    }

    public void setPayloadTree(Trie payloadTree) {
        this.payloadTree = payloadTree;
    }

    public int getNumOfChildren(){
        int numOfChildren = 0;
        for (int i = 0; i < NUM_OF_SYMBOLS; i++){
            if (children[i] != null){
                numOfChildren += 1;
            }
        }
        return numOfChildren;
    }
}
