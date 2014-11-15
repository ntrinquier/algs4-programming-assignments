import java.util.ArrayList;
import java.util.HashMap;

public class BaseballElimination {
    private int numberOfTeams;
    private ArrayList<String> names;
    private HashMap<String, Integer> ids;
    private int[] wins;
    private int[] losses;
    private int[] remainingGames;
    private int[][] remainingGamesDivision;
    private FlowNetwork fn;
    private int source;
    private int sink;
    private int firstTeamVertexId;
    private FordFulkerson ff;
    
    // create a baseball division from given filename
    public BaseballElimination(String filename) {
        In in = new In(filename);
        
        numberOfTeams = in.readInt();

        // Initialization
        names = new ArrayList<String>();
        ids = new HashMap<String, Integer>();
        wins = new int[numberOfTeams];
        losses = new int[numberOfTeams];
        remainingGames = new int[numberOfTeams];
        remainingGamesDivision = new int[numberOfTeams][numberOfTeams];
        
        // Reading
        for (int i = 0; i < numberOfTeams; i++) {
            String team = in.readString();
            names.add(team);
            ids.put(team, i);
            wins[i] = Integer.parseInt(in.readString());
            losses[i] = Integer.parseInt(in.readString());
            remainingGames[i] = Integer.parseInt(in.readString());
            
            for (int j = 0; j < numberOfTeams; j++) {
                int remaining = Integer.parseInt(in.readString());
                remainingGamesDivision[i][j] = remaining;
                remainingGamesDivision[j][i] = remaining;
            }
        }
    }
    
    // number of teams
    public int numberOfTeams() {
        return numberOfTeams;
    }
    
    // all teams
    public Iterable<String> teams() {
        return names;
    }
    
    // number of wins for given team
    public int wins(String team) {
        return wins[getId(team)];
    }
    
    // number of losses for given team
    public int losses(String team) {
        return losses[getId(team)];
    }
    
    // number of remaining games for given team
    public int remaining(String team) {
        return remainingGames[getId(team)];
    }
    
    // number of remaining games between team1 and team2
    public int against(String team1, String team2) {
        return remainingGamesDivision[getId(team1)][getId(team2)];
    }
    
    // is given team eliminated?
    public boolean isEliminated(String team) {
        int teamId = getId(team);
        int potentialWins = wins[teamId]+remainingGames[teamId];
        
        // Trivial case
        for (int w : wins) {
            if (w > potentialWins) {
                return true;
            }
        }

        // Flow network
        fn = new FlowNetwork(numberOfTeams*(numberOfTeams+1)/2+2);
        source = 0;
        sink = fn.V()-1;
        firstTeamVertexId = numberOfTeams*(numberOfTeams-1)/2+1;
        
        int id = 1;
        for (int i = 0; i < numberOfTeams; i++) {
            if (i == teamId) {
                continue;
            }
            
            for (int j = i+1; j < numberOfTeams; j++) {
                if (j == teamId) {
                    continue;
                }
                
                int g = against(i, j);
                
                if (g > 0) {
                    fn.addEdge(new FlowEdge(source, id, g));
                    fn.addEdge(new FlowEdge(id, firstTeamVertexId+i, Double.POSITIVE_INFINITY));
                    fn.addEdge(new FlowEdge(id, firstTeamVertexId+j, Double.POSITIVE_INFINITY));
                }
                
                id++;
            }
            
            fn.addEdge(new FlowEdge(firstTeamVertexId+i, sink, potentialWins-wins[i]));
        }
        
        // FordFulkerson
        ff = new FordFulkerson(fn, source, sink);
        
        for (FlowEdge edge : fn.adj(source)) {
            //System.out.println(edge.flow()+"/"+edge.capacity());
            if (edge.flow() != edge.capacity()) {
                return true;
            }
        }
        
        return false;
    }
    
    // subset R of teams that eliminates given team; null if not eliminated
    public Iterable<String> certificateOfElimination(String team) {
        int teamId = getId(team);
        int potentialWins = wins[teamId]+remainingGames[teamId];
        
        // Trivial case
        for (int i = 0; i < numberOfTeams; i++) {
            if (wins[i] > potentialWins) {
                ArrayList<String> ret = new ArrayList<String>();
                ret.add(names.get(i));
                return ret;
            }
        }
        
        if (!isEliminated(team))
            return null;
        
        ArrayList<String> ret = new ArrayList<String>();
        
        for (int i = firstTeamVertexId; i < sink; i++) {
            if (ff.inCut(i) && i >= firstTeamVertexId)
                ret.add(names.get(i-firstTeamVertexId));
        }
        
        return ret;
    }
    
    // id of team
    private int getId(String team) {
        Integer ret = ids.get(team);
        
        if (ret == null)
            throw new IllegalArgumentException();
        
        return ret.intValue();
    }
    
    // number of remaining games between team1 and team2
    private int against(int team1, int team2) {
        return remainingGamesDivision[team1][team2];
    }
    
    public static void main(String[] args) {
        BaseballElimination division = new BaseballElimination(args[0]);
        for (String team : division.teams()) {
            if (division.isEliminated(team)) {
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String t : division.certificateOfElimination(team)) {
                    StdOut.print(t + " ");
                }
                StdOut.println("}");
            }
            else {
                StdOut.println(team + " is not eliminated");
            }
        }
    }
}
