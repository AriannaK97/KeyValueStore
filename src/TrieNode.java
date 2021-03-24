public class TrieNode {
    private static int NUM_OF_ALPHABET_LETTERS = 26;

    private TrieNode[] children = new TrieNode[NUM_OF_ALPHABET_LETTERS];
    private boolean isEndOfWord;

    TrieNode(){
        isEndOfWord = false;
        for (int i = 0; i < NUM_OF_ALPHABET_LETTERS; i++){
            children[i] = null;
        }
    }

    public TrieNode getChildrenInPosition(int index) {
        return children[index];
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


}
