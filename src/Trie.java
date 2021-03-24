public class Trie {

    static TrieNode root;

    Trie(){
        root = new TrieNode();
    }

    public static TrieNode getRoot() {
        return root;
    }
    static void insert(String key){
        int level;
        int length = key.length();
        int index;

        TrieNode pCrawl = root;

        for (level = 0; level < length; level++)
        {
            index = key.charAt(level) - 'a';
            if (pCrawl.getChildrenInPosition(index) == null)
                pCrawl.setChildrenInPosition(index, new TrieNode());

            pCrawl = pCrawl.getChildrenInPosition(index);
        }

        // mark last node as leaf
        pCrawl.setEndOfWord(true);
    }

    // Returns true if key presents in trie, else false
    static boolean search(String key) {
        int level;
        int length = key.length();
        int index;
        TrieNode pCrawl = root;

        for (level = 0; level < length; level++){
            index = key.charAt(level) - 'a';

            if (pCrawl.getChildrenInPosition(index) == null)
                return false;

            pCrawl = pCrawl.getChildrenInPosition(index);
        }
        return (pCrawl != null && pCrawl.isEndOfWord());
    }
}
