/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package WumpusGame;

import WumpusGame.Edge;
import WumpusGame.Node;
import java.sql.DriverManager;
import java.sql.*;
import javax.print.DocFlavor;
import  java.awt.Dialog; 
import javax.swing.*;
import javax.swing.JFrame; 
import java.util.Hashtable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import javax.swing.JOptionPane;


public final class World {
 	// layout will be represnted as a node list and an edge list

	// In this game, the nodes (rooms) are attractions, which are serviced by 4 tracks, all
	// originating from the park office. The track structure is not modelled explicitly -
	// it is captured in the edge list. It can also be inferred from the node list - the numbers 
	// in the attraction name indicate the track on which the attraction is on.
	// In the node list below, there is NO duplication of attractions - attractions that are at 
	// the junction of two or more tracks are included as comment lines. Note also that there is 
	// only one track servicing adjacent attractions.

	public static String[] rooms = new String[17];

	// Edge format is <from, to, track, distance>
	// Because we are using an array, the types of all elements must be the same.
	// Hence distance is a string, not an int.
	public static String [][] exits = new String[100][4] ;


    Hashtable <String,Node> nodes;
    Hashtable <String,ArrayList>  edges;
    
    
    
    ArrayList <Bats> b = new ArrayList();;
    ArrayList <Treasure> tr = new ArrayList();
    
    private int totalNumberOftreasure = 0;  
    
    Node wumpus; 
    Node location;
    
    //Declare global variables
    private Connection con = null;
    private Statement st = null;
    private ResultSet re = null;  
    
    private int MeetSmallBats = 0;
    private int MeetBigBats = 0;
    
    private int CollectedGold = 0;
    private int CollectedSilver = 0;
    private int CollectedDiamond = 0;
    
    
    public World() {
        //Retreive data from database
        populateArrays();
        
    }
    //Retreive data from database and store in the array
    public void populateArrays()
    {
       
         nodes = new Hashtable();
         edges = new Hashtable();
    
        try
        {
            
        con = DriverManager.getConnection("jdbc:derby://localhost:1527/DBWumpus");
        st = con.createStatement();         
        re = st.executeQuery("Select ROOM from ROOMS");
          
        while (re.next())
        {
            
            String room = re.getString(1);
            
            
            Node n = new Node(room);
            nodes.put(room,n);
            ArrayList <Edge> ale = new ArrayList();
            edges.put(room,ale);
           
        }
        
         String from="";
         String to=""; 
         String line="";   
         Integer d=0;//="";
         
         st = con.createStatement();         
         re = st.executeQuery("Select ROOM_ID,EXIT1_ROOM_ID,EXIT2_ROOM_ID,EXIT3_ROOM_ID from PATHS");
         
         while (re.next())
         {
           from =  re.getString(1);
           
           for(int i=0;i<3;i++)
           {
                to = re.getString(i+2); 
                ArrayList <Edge> ale = edges.get(from);
                ale.add( new Edge(from,to,"t1",1));
                
                
           }
          
         }
        
         int index = 0;
         
         String BatType = "";
         String Bat_ROOM = ""; 
         int BatNumber = 0;
         
         st = con.createStatement();         
         re = st.executeQuery("Select TYPE,ROOM_ID,QUANTITY FROM BATS");
         
         
         
         while (re.next())
         {
         
           BatType =  re.getString(1);
           Bat_ROOM =  re.getString(2);
           BatNumber  =  re.getInt(3);
            
           
           b.add( new Bats(BatType,Bat_ROOM,BatNumber));
           
          
           
         }
        
         String TReasureType = "";
         String Treasure_ROOM = ""; 
         int TReasureNumber = 0;
         
         st = con.createStatement();         
         re = st.executeQuery("Select TYPE,ROOM_ID,QUANTITY FROM TREASURES");
         
         while (re.next())
         {
    
           TReasureType =  re.getString(1);
           Treasure_ROOM =  re.getString(2);
           TReasureNumber  =  re.getInt(3);
            
           
           tr.add( new Treasure(TReasureType,Treasure_ROOM,TReasureNumber));
           
         }
         
            re.close();
            st.close();
        }
        catch(Exception e)
        {
            JOptionPane.showMessageDialog(null, "An ODBC error occurs\n"+e.getMessage(),"Warning", JOptionPane.PLAIN_MESSAGE);
        }
        finally
        {
           
        }
    }

    //Initial the position of player and wumpus
    public void start() {
	 
        
        location = nodes.get("1a");
	wumpus =  nodes.get("8h");
        
       
    }

    public Node getNode(String label) {
	return nodes.get(label);
    }

    public boolean enter(Node n) {
        
	ArrayList <Edge> ale =  edges.get( location.label );
	for (Edge e : ale) {
            if ( (n.label).equals(e.to) ) {
		location = n;
		return true;
            }
	}
	return false;
    }

    public String describe(String l) {
	
        String s = ""; //location.describe();
	//s += "\nThere are exits to:";
	ArrayList <Edge> ale = edges.get(l.trim());
	for (Edge e : ale)
            s += e.to+",";
	//s += "\nThe wumpus is "+distance(location)+" rooms away";
	return s;
    }

    public boolean shoot( Node n ) {
	if ( (n.label).equals(wumpus.label) )
            return true;
	EdgeStack sp = shortestPath(location,n);
	//to do: traverse the path; if it passes through the wumpus room, return true
	return false;
    }

    public String shortestPath(Node to) {
	EdgeStack es = shortestPath(location,to);
	String path = "";
	while (es.count() > 0) {
            Edge e =  es.pop();
            path += "\t"+e.from+" to "+e.to+" using "+e.line+"\n";
	}
	return path;
    }

    // private methods 

    private void create() {

    }

    private Node getNextNode() {
        // choose the node with the shortest distance from the start node
        // that hasn't been visited

        Node next = null;
        int d = Integer.MAX_VALUE;
        Enumeration <Node> en = nodes.elements(); 
        while ( en.hasMoreElements() ) { 
            Node n = en.nextElement();
            if (n.visited)
                continue;
            if (n.distance < d) {
                d = n.distance;
                next = n;
            }
	}
        
        //if(next == null){next = nodes.get("8h");}
        
	return next;
}

    private EdgeStack shortestPath(Node n1, Node n2) {
        
	// To work out the shortest path from n1 to n2, we use Dijkstra's algorithm, as 
	// described in Weiss, M. (2007) Data Structures and Algorithm Analysis in Java, 
	// Pearson Education, p. 337
        
        Enumeration <Node> en = nodes.elements(); 
        while ( en.hasMoreElements() ) { 
            Node n = en.nextElement();

            n.step = null;
            n.distance = Integer.MAX_VALUE;
            n.visited = false;
	}

	n1.distance = 0;
	for (;;) {
            Node n = getNextNode(); 
            if (n == null) 
		break;
            n.visited = true;

            ArrayList <Edge> ale = edges.get(n.label);
            for (Edge e : ale) {
		//Check if each of n or e got null
                if(n == null || e == null){break;}
                
                Node t = nodes.get(e.to);
                //check if t got null
		if(t == null){break;}
                if (!t.visited) {
                    
                    if (n.distance + e.distance < t.distance) {
			t.distance = n.distance + e.distance;
			t.step = e;
                    }

		}
            }
	}

	//The path to n2 is stored in the step fields. 
	EdgeStack path = new EdgeStack();
	Node q = n2;
	while ( q.step != null ) {
            path.push(q.step);
            q = nodes.get(q.step.from);
	}
	return path;

    }
	
    private int distance(Node n1) {
	EdgeStack sp = shortestPath(n1,wumpus);
	return sp.count();
    }
    
    class EdgeStack {
        
        // There is no stack class in java.util, so ...
 
        public ArrayList <Edge> estack;
        
        EdgeStack() {
            estack = new ArrayList();
        }
        
        int count() {
            return estack.size();
        }
        
        void push( Edge e) {
            estack.add(0,e);
        }
        
        Edge pop() {
            if ( estack.isEmpty() )
                return null;
            return estack.remove(0);
        }
    }
    
    
    
    public String MoveAction(String roomId)
    {
        String result = "statu,WumpusLocation,small,big,gold,silver,dimond";
        
        location = nodes.get(roomId.trim());
      
        
        
        if (wumpus.equals(roomId.trim()))
        {
          result = "dead,"+  Integer.toString(distance(location)) +","+MeetSmallBats+","+MeetBigBats+","+CollectedGold+","+CollectedSilver+","+CollectedDiamond;    
          
        }
        
        else
        {
             
           for (int i=0;i<b.size();i++) {
            
               //v = "2a";
               
               if(roomId.equals(b.get(i).room_ID.toString().trim()))
               {
                    switch (b.get(i).type) {
                        case "SMALL":
                            MeetSmallBats++;
                            break;
                        case "BIG":
                            MeetBigBats++;
                            break;
                    }
               }
           }
           
           for (int j=0;j<tr.size();j++)
           {
                if(tr.get(j).room_ID == null ? roomId.trim() == null : tr.get(j).room_ID.equals(roomId.trim()))
                {
                   if("SILVER".equals(tr.get(j).type))
                   {
                       CollectedSilver = CollectedSilver + tr.get(j).quantity;
                       tr.get(j).quantity = 0;
                   }
                    switch (tr.get(j).type) {
                        case "DIAMOND":
                            CollectedDiamond = CollectedDiamond + tr.get(j).quantity;
                            tr.get(j).quantity = 0;
                            break;
                        case "GOLD":
                            CollectedGold = CollectedGold + tr.get(j).quantity;
                            tr.get(j).quantity = 0;
                            break;
                    }
                }
           }
           
           result = "alive,"+ distance(location) +","+MeetSmallBats+","+MeetBigBats+","+CollectedGold+","+CollectedSilver+","+CollectedDiamond;
       

        } 

        return result;
    }
    public String ShootAction(String roomId)
    {
        String result = "WumpusStatus"; 
        
        
        if(wumpus.label.equals(roomId.trim()))
        {
            result = "1";
        }
        else
        {
            //Shortpath for wumpus
            //String WumpusLocation = "10j";
            wumpus =  getNextNode();
            if(wumpus == null) wumpus =  nodes.get("8h");
            //
            location = nodes.get(roomId.trim());
            result = Integer.toString(distance(location)) ;
        }
        
        return result;
    }
    public void DropAction(String roomId,String gold,String silver,String diamond)
    {
           if(!"0".equals(gold))
           {
              CollectedGold = CollectedGold - Integer.parseInt(gold);
              //tr = new ArrayList();
              tr.add( new Treasure("Gold",roomId,Integer.parseInt(gold)));
           }
           
           if(!"0".equals(silver))
           {
              CollectedSilver = CollectedSilver - Integer.parseInt(silver);
              //tr = new ArrayList();
              tr.add( new Treasure("Silver",roomId,Integer.parseInt(silver)));
           }
           
           if(!"0".equals(diamond))
           {
              CollectedDiamond = CollectedDiamond - Integer.parseInt(diamond);
              //tr = new ArrayList();
              tr.add( new Treasure("Diamond",roomId,Integer.parseInt(diamond)));
           }
            
    }
     
}
