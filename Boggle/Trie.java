import java.util.LinkedList;

public class Trie {
    final Node root;
    
    public Trie() {
        root = new Node('_');
    }

    public boolean wordExists(String s) {
        return root.wordExists(s);
    }
    
    public boolean addWord(String s) {
        Node node = root;
        int pos = 0;
        boolean continueSearch = true;
        
        while (continueSearch) {
            continueSearch = false;
            
            if (s.length() == pos) {     
                for (Node son : node.sons) {
                    if (son.c == '*') {
                        return false;
                    }
                }
                break;
            }
            
            for (Node son : node.sons) {
                if (son.c == s.charAt(pos)) {
                    continueSearch = true;
                    node = son;
                    pos++;
                    break;
                }
            }
        }
        
        for (int i = pos; i < s.length(); i++) {
            Node n = new Node(s.charAt(i));
            node.addSon(n);
            node = n;
        }

        node.addSon(new Node('*'));
        return true;
    }
    
    public boolean isPrefix(String s) {
        Node node = root;
        boolean isPrefix = true;
        
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            isPrefix = false;
            for (Node son : node.sons) {
                if (son.c == c) {
                    isPrefix = true;
                    node = son;
                    break;
                }
            }
            
            if (!isPrefix) {
                return false;
            }
        }
        
        return true;
    }
    
    public static String convertToString(LinkedList<Character> l) {
        StringBuilder sb = new StringBuilder();
        for (Character c: l) {
            if (c == 'Q') {
                sb.append("QU");
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}

class Node {
    public char c;
    public LinkedList<Node> sons;
    
    Node(char c) {
        this.c = c;
        this.sons = new LinkedList<Node>();
    }
    
    public void addSon(Node a) {
        int pos = 0;
        for (Node son: sons)
            if (son.c < a.c) {
                pos++;
            } else {
                break;
            }
        
        sons.add(pos, a);
    }
    
    public boolean wordExistsRec(String s, int pos) {
        if (s.length() == pos) {
            for (Node node : sons) {
                if (node.c == '*') {
                    return true;
                }
            }
            return false;
        }

        for (Node node : sons) {
            if (node.c == s.charAt(pos)) {
                return node.wordExistsRec(s, pos+1);
            }
        }
        return false;
    }
    
    public boolean isWord() {
        return this.findSon('*') != null;
    }
    
    public boolean wordExists(String s) {
        return wordExistsRec(s, 0);
    }
    
    public Node findSon(char c) {
        for (Node n : sons) {
            if (n.c == c) {
                return n;
            }
        }
        
        return null;
    }
}
