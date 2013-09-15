
package WumpusGame;

import WumpusGame.Edge;

public class Node {
  
    public Edge step;	
    public int distance;
    public boolean visited;
    public String description;
    public String label;
   
    
    public Node(String l) {
        
      	label = l;
        step = null;
	      visited = false;
	      distance = 0;
    }
		
    public String describe() {
	        String s = "You are in room " + label + ".";
	        return s;
    }
    
}
