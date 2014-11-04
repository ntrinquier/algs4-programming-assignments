import java.util.HashSet;
import java.util.Hashtable;

/**
 *  @author Nicolas Trinquier
 */

public class WordNet {
    private Hashtable<Integer, String> idToSynset;
    private Hashtable<String, HashSet<Integer>> nounToIds;
    private HashSet<String> nouns;
    private Digraph digraph;
    private DirectedCycle dc;
    private SAP sap;

    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        // check arguments
        if (synsets == null) {
            throw new NullPointerException("synsets should not be null.");
        } else if (hypernyms == null) {
            throw new NullPointerException("hypernyms should not be null.");
        }
        // build hashtables and digraph from input
        int numberOfSynsets = this.readSynsets(synsets);
        this.readHypernyms(hypernyms, numberOfSynsets);
        // check if DAG
        this.dc = new DirectedCycle(this.digraph);
        if (this.dc.hasCycle()) {
            throw new IllegalArgumentException("The digraph should not have a cycle.");
        }
        // check if rooted
        int nbOfRoots = 0;
        for (int i = 0; i < this.digraph.V(); i++) {
            if (!this.digraph.adj(i).iterator().hasNext()) { // if outdegree = 0
                nbOfRoots += 1;
                if (nbOfRoots > 1) {
                    break;
                }
            }
        }
        if (nbOfRoots != 1) {
            throw new IllegalArgumentException("The digraph should have only one root.");
        }
        // build sap and outcast
        this.sap = new SAP(this.digraph);
    }

    // returns all WordNet nouns
    public Iterable<String> nouns() {
        return this.nouns;
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        // check arguments
        if (word == null) {
            throw new NullPointerException("word should not be null.");
        }

        return (this.nouns.contains(word));
    }

    // distance between nounA and nounB
    public int distance(String nounA, String nounB) {
        // check arguments
        if (nounA == null) {
            throw new NullPointerException("nounA should not be null.");
        } else if (nounB == null) {
            throw new NullPointerException("nounB should not be null.");
        } else if (!this.isNoun(nounA)) {
            throw new IllegalArgumentException("nounA ("+nounA+") should be a WordNet noun.");
        } else if (!this.isNoun(nounB)) {
            throw new IllegalArgumentException("nounB ("+nounB+") should be a WordNet noun.");
        }

        HashSet<Integer> idsA = this.nounToIds.get(nounA);
        HashSet<Integer> idsB = this.nounToIds.get(nounB);
        return this.sap.length(idsA, idsB);
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral pathw)
    public String sap(String nounA, String nounB) {
        // check arguments
        if (nounA == null) {
            throw new NullPointerException("nounA should not be null.");
        } else if (nounB == null) {
            throw new NullPointerException("nounB should not be null.");
        } else if (!this.isNoun(nounA)) {
            throw new IllegalArgumentException("nounA ("+nounA+") should be a WordNet noun.");
        } else if (!this.isNoun(nounB)) {
            throw new IllegalArgumentException("nounB ("+nounB+") should be a WordNet noun.");
        }
        
        HashSet<Integer> idsA = this.nounToIds.get(nounA);
        HashSet<Integer> idsB = this.nounToIds.get(nounB);
        int idAncestor = this.sap.ancestor(idsA, idsB);
        return this.idToSynset.get(idAncestor);
    }

    // readSynsets: read synsets and preprocess
    private int readSynsets(String synsets) {
        this.idToSynset = new Hashtable<Integer, String>();
        this.nounToIds = new Hashtable<String, HashSet<Integer>>();
        this.nouns = new HashSet<String>();
        int maxID = 0;
        In in = new In(synsets);
        while (in.hasNextLine()) {
            String[] parts = in.readLine().split(",");
            int id = Integer.parseInt(parts[0]);
            this.idToSynset.put(id, parts[1]);
            
            for (String s : parts[1].split(" ")) {
                if (!this.nounToIds.containsKey(s)) {
                    this.nounToIds.put(s, new HashSet<Integer>());
                }
                this.nounToIds.get(s).add(id);
                this.nouns.add(s);
            }
            maxID = Integer.parseInt(parts[0]);
        }

        return maxID+1;
    }

    // readHypernyms: read hypernyms and add edges
    private void readHypernyms(String hypernyms, int numberOfSynsets) {    
        this.digraph = new Digraph(numberOfSynsets); // Because IDs start from 0    
        In in = new In(hypernyms);
        while (in.hasNextLine()) {
            String[] parts = in.readLine().split(",");
            for (int i = 1; i < parts.length; i++) {
                this.digraph.addEdge(Integer.parseInt(parts[0]), Integer.parseInt(parts[i]));
            }
        }
    }

    // do unit testing of this class
    public static void main(String[] args) {
        WordNet wn = new WordNet("synsets.txt", "hypernyms.txt");
        // WordNet wn = new WordNet("synsets.txt", "hypernymsInvalidCycle.txt");
        // WordNet wn = new WordNet("synsets.txt", "hypernymsInvalidTwoRoots.txt");
        
        // isNoun test
        System.out.println("entity: "+wn.isNoun("entity"));
        System.out.println("word: "+wn.isNoun("word"));
        System.out.println("thisisnotaword: "+wn.isNoun("thisisnotaword"));

        // nouns test
        /*
        for (String noun : wn.nouns()) {
            System.out.println(noun);
        }
        */
    }
}