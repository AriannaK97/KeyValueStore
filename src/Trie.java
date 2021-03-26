public class Trie {

    static TrieNode root;

    Trie(){
        root = new TrieNode();
    }

    public static TrieNode getRoot() {
        return root;
    }
    static void insert(String key, String[] payload){
        int level;
        int length = key.length();
        int index;

        TrieNode pCrawl = root;

        for (level = 0; level < length; level++) {
            index = key.charAt(level);
            index = (index - 'a' > 0) ? index - 'a' : index - '0' + 26;

            if (pCrawl.getChildrenInPosition(index) == null)
                pCrawl.setChildrenInPosition(index, new TrieNode());

            pCrawl = pCrawl.getChildrenInPosition(index);
        }

        // mark last node as leaf
        pCrawl.setEndOfWord(true);
        pCrawl.setEndOfWordPayload(payload);
    }

    // Returns true if key presents in trie, else false
    static String[] search(String key) {
        int level;
        int length = key.length();
        int index;
        TrieNode pCrawl = root;
        String[] output = {"No"};

        for (level = 0; level < length; level++){
            index = key.charAt(level);
            index = (index - 'a' > 0) ? index - 'a' : index - '0' + 26;

            if (pCrawl.getChildrenInPosition(index) == null)
                return output;

            pCrawl = pCrawl.getChildrenInPosition(index);
        }

        return pCrawl.getChildrenPayload();
    }
}
