import java.util.HashSet;
import java.util.LinkedList;


public class BoggleSolver {
    private Trie trie;
    private LinkedList<LinkedList<Position>> boggleGraph;
    // Initializes the data structure using the given array of strings as the dictionary.
    // (You can assume each word in the dictionary contains only the uppercase letters A through Z.)
    public BoggleSolver(String[] dictionary) {
        this.trie = new Trie();
        for (String word : dictionary) {
            if (word.length() > 2 && word.charAt(word.length()-1) != 'Q') {
                boolean doNotAdd = false;
                StringBuilder sb = new StringBuilder();
                int i = 0;
                while (i < word.length()-1) {
                    if (word.charAt(i) == 'Q') {
                        if (word.charAt(i+1) == 'U') {
                            sb.append('Q');
                            i++;
                        } else {
                            doNotAdd = true;
                            break;
                        }
                    } else {
                        sb.append(word.charAt(i));
                    }
                    i++;
                }
                if (!doNotAdd) {
                    if (word.charAt(word.length()-2) != 'Q') {
                        sb.append(word.charAt(word.length()-1));
                    }
                    this.trie.addWord(sb.toString());
                }
            }
        }
    }

    private void generateBoggleGraph(BoggleBoard board) {
        boggleGraph = new LinkedList<LinkedList<Position>>();
        
        for (int i = 0; i < board.rows(); i++) {
            for (int j = 0; j < board.cols(); j++) {
                boggleGraph.add(new LinkedList<Position>());
                for (int x = i-1; x <= i+1; x++) {
                    if (0 <= x && x < board.rows()) {
                        for (int y = j-1; y <= j+1; y++) {
                            if (0 <= y && y < board.cols()) {
                                boggleGraph.get(i*board.cols()+j).add(new Position(x, y));
                            }
                        }
                    }
                }
            }    
        }
    }
    
    private LinkedList<Position> getAdjacentCubes(BoggleBoard board, int i, int j) {
        return this.boggleGraph.get(i*board.cols()+j);
    }
    
    // Returns the set of all valid words in the given Boggle board, as an Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        boolean[][] mask = new boolean[board.rows()][board.cols()];
        LinkedList<Character> prefix = new LinkedList<Character>();
        HashSet<String> words = new HashSet<String>();
        
        generateBoggleGraph(board);
        
        for (int i = 0; i < board.rows(); i++) {
            for (int j = 0; j < board.cols(); j++) {
                explore(board, prefix, words, mask, i, j, trie.root);
            }
        }

        return words;
    }

    private void explore(BoggleBoard board, LinkedList<Character> prefix, HashSet<String> words, boolean[][] mask, int i, int j, Node n) {
        char c = board.getLetter(i, j);
        Node fils = n.findSon(c);
        if (fils == null) {
            return;
        }
        prefix.addLast(c);
        mask[i][j] = true;

        if (fils.isWord()) {
            words.add(Trie.convertToString(prefix));
        }

        for (Position pos : getAdjacentCubes(board, i, j)) {
            if (!mask[pos.x][pos.y]) {
                explore(board, prefix, words, mask, pos.x, pos.y, fils);
            }
        }

        prefix.removeLast();
        mask[i][j] = false;
    }

    // Returns the score of the given word if it is in the dictionary, zero otherwise.
    // (You can assume the word contains only the uppercase letters A through Z.)
    public int scoreOf(String word) {
        if (!this.trie.wordExists(word.replace("QU", "Q"))) {
            return 0;
        }
        int l = word.length();
        if (l <= 4) {
            return 1;
        } else if (l == 5) {
            return 2;
        } else if (l == 6) {
            return 3;
        } else if (l == 7) {
            return 5;
        }
        return 11;
    }

    public static void main(String[] args) {
        In in = new In(args[0]);
        String[] dictionary = in.readAllStrings();
        BoggleSolver solver = new BoggleSolver(dictionary);
        BoggleBoard board = new BoggleBoard(args[1]);
        int score = 0;
        for (String word : solver.getAllValidWords(board)) {
            StdOut.println(word);
            score += solver.scoreOf(word);
        }
        StdOut.println("Score = " + score);
    }
}