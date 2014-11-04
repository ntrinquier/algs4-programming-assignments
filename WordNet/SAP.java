/**
 *  @author Nicolas Trinquier
 */

public class SAP {
    private static final int INFINITY = Integer.MAX_VALUE;
    private Digraph G;

    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        // check arguments
        if (G == null) {
            throw new NullPointerException("Digraph G should not be null.");
        }
        this.G = new Digraph(G);
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        // check arguments
        if (v < 0 || v > G.V()-1) {
            throw new IndexOutOfBoundsException("Vertice v should be between 0 and "+(G.V()-1)+" (v="+v+").");
        } else if (w < 0 || w > G.V()-1) {
            throw new IndexOutOfBoundsException("Vertice w should be between 0 and "+(G.V()-1)+" (w="+w+").");
        }

        BreadthFirstDirectedPaths bfsv = new BreadthFirstDirectedPaths(this.G, v);
        BreadthFirstDirectedPaths bfsw = new BreadthFirstDirectedPaths(this.G, w);
        int minDist = INFINITY;
        boolean found = false;

        for (int i = 0; i < this.G.V(); i++) {
            if (bfsv.hasPathTo(i) && bfsw.hasPathTo(i)) {
                int dist = bfsv.distTo(i)+bfsw.distTo(i); 
                if (dist < minDist) {
                    minDist = dist;
                    found = true;
                }
            }
        }

        if (found) {
            return minDist;
        } else {
            return -1;
        }
    }

    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {
        // check arguments
        if (v < 0 || v > G.V()-1) {
            throw new IndexOutOfBoundsException("Vertice v should be between 0 and "+(G.V()-1)+" (v="+v+").");
        } else if (w < 0 || w > G.V()-1) {
            throw new IndexOutOfBoundsException("Vertice w should be between 0 and "+(G.V()-1)+" (w="+w+").");
        }

        BreadthFirstDirectedPaths bfsv = new BreadthFirstDirectedPaths(this.G, v);
        BreadthFirstDirectedPaths bfsw = new BreadthFirstDirectedPaths(this.G, w);
        int minDist = INFINITY;
        int ancestor = -1;

        for (int i = 0; i < this.G.V(); i++) {
            if (bfsv.hasPathTo(i) && bfsw.hasPathTo(i)) {
                int dist = bfsv.distTo(i)+bfsw.distTo(i); 
                if (dist < minDist) {
                    minDist = dist;
                    ancestor = i;
                }
            }
        }

        return ancestor;
    }

    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        // check arguments
        if (v == null) {
            throw new NullPointerException("v should not be null.");
        } else if (w == null) {
            throw new NullPointerException("w should not be null.");
        }

        BreadthFirstDirectedPaths bfsv = new BreadthFirstDirectedPaths(this.G, v);
        BreadthFirstDirectedPaths bfsw = new BreadthFirstDirectedPaths(this.G, w);
        int minDist = INFINITY;
        boolean found = false;

        for (int i = 0; i < this.G.V(); i++) {
            if (bfsv.hasPathTo(i) && bfsw.hasPathTo(i)) {
                int dist = bfsv.distTo(i)+bfsw.distTo(i); 
                if (dist < minDist) {
                    minDist = dist;
                    found = true;
                }
            }
        }

        if (found) {
            return minDist;
        } else {
            return -1;
        }
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        // check arguments
        if (v == null) {
            throw new NullPointerException("v should not be null.");
        } else if (w == null) {
            throw new NullPointerException("w should not be null.");
        }

        BreadthFirstDirectedPaths bfsv = new BreadthFirstDirectedPaths(this.G, v);
        BreadthFirstDirectedPaths bfsw = new BreadthFirstDirectedPaths(this.G, w);
        int minDist = INFINITY;
        int ancestor = -1;

        for (int i = 0; i < this.G.V(); i++) {
            if (bfsv.hasPathTo(i) && bfsw.hasPathTo(i)) {
                int dist = bfsv.distTo(i)+bfsw.distTo(i); 
                if (dist < minDist) {
                    minDist = dist;
                    ancestor = i;
                }
            }
        }

        return ancestor;
    }

    // do unit testing of this class
    public static void main(String[] args) {
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }
    }
}