/**
 *  @author Nicolas Trinquier
 */

public class Outcast {
    private WordNet wordnet;

    // constructor takes a WordNet object
    public Outcast(WordNet wordnet) {
        // check arguments
        if (wordnet == null) {
            throw new NullPointerException("Argument should not be null.");
        }
        this.wordnet = wordnet;
    }

    // given an array of WordNet nouns, return an outcast
    public String outcast(String[] nouns) {
        int[][] distances = new int[nouns.length][nouns.length];
        for (int i = 0; i < nouns.length; i++) {
            distances[i][i] = 0;
            for (int j = i+1; j < nouns.length; j++) {
                int d = this.wordnet.distance(nouns[i], nouns[j]);
                distances[i][j] = d;
                distances[j][i] = d;
            }
        }

        int idOutcast = 0;
        int maxDistance = 0;

        for (int i = 0; i < nouns.length; i++) {
            int d = 0;
            for (int j = 0; j < nouns.length; j++) {
                d += distances[i][j];
            }
            if (d > maxDistance) {
                maxDistance = d;
                idOutcast = i;
            }
        }

        return nouns[idOutcast];
    }

    // see test client below
    public static void main(String[] args) {
        WordNet wordnet = new WordNet(args[0], args[1]);
        Outcast outcast = new Outcast(wordnet);
        for (int t = 2; t < args.length; t++) {
            In in = new In(args[t]);
            String[] nouns = in.readAllStrings();
            StdOut.println(args[t] + ": " + outcast.outcast(nouns));
        }
    }
}