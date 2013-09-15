
package WumpusGame;

public class Edge {
    
    public String from;
    public String to;
    public String line;
    public int distance;
    
    public Edge(String n1, String n2, String l, int d) {
        
	from = n1;
	to = n2;
	line = l;
	distance = d;
    }
    
}
