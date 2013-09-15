package WumpusGame;

import WumpusGame.Edge;

public class Treasure {
  
   public String type;
   public String room_ID;
   public int quantity;
    
    public Treasure(String t,String r,int q) {
        
       type = t;
       room_ID = r;
       quantity = q;
    }
    public Treasure()
    {
        
    }
		
}
