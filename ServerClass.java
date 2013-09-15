package WumpusGame;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.sql.*;

/**
 *
 * @author s0205911
 */
public class ServerMainClass {

 public static void main(String args[]) throws Exception
    {
            
        String result = "";            

        DatagramSocket serverSocket = new DatagramSocket(9875);
        byte[] receiveData = new byte[1024];
        byte[] sendData = new byte[1024];

        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        serverSocket.receive(receivePacket);
        String sentence = new String( receivePacket.getData());
        System.out.println("RECEIVED:" + sentence);
        InetAddress IPAddress = receivePacket.getAddress();
        int port = receivePacket.getPort();
        
        World w = new World();
        //String capitalizedSentence =  w.action(sentence);
        String str[] = sentence.split(",");
        
        
        
        //take action
        switch (str[0])
        {
                case "move":result = w.MoveAction(str[1].trim());
                break;
                case "shoot":result = w.ShootAction(str[1].trim());
                break;
                case "drop": w.DropAction(str[1].trim(),str[2].trim(),str[3].trim(),str[4].trim()); result = "done";
                break;
                
                case "describe": result = w.describe(str[1].trim()); 
                break;
                    
                case "initial": w.start(); result = w.describe(str[1].trim());
                break;
        }
        
       
        //************************************************
        sendData = result.getBytes();
        DatagramPacket sendPacket =
        new DatagramPacket(sendData, sendData.length, IPAddress, port);
        serverSocket.send(sendPacket);
        //************************************************
 }
                
 
}
