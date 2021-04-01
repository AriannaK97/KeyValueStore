package Server;

public class TrieNode {
    private static int NUM_OF_SYMBOLS = 36;

    private TrieNode[] children = new TrieNode[NUM_OF_SYMBOLS];
    private boolean isEndOfWord;
    private String[] payload;

    TrieNode(){
        isEndOfWord = false;
        for (int i = 0; i < NUM_OF_SYMBOLS; i++){
            children[i] = null;
        }
    }

    public TrieNode getChildrenInPosition(int index) {
        return children[index];
    }

    public String[] getChildrenPayload() {
        return payload;
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

    public void setEndOfWordPayload(String[] payload){
        if(this.isEndOfWord){
            this.payload = payload;
        }
    }


}
