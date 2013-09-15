/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package WumpusGame;

import java.net.*;
import java.io.*;
import java.util.Scanner;
/**
 *
 * @author s0205911
 */
public class ClientMainClass {


   public static String ServerTalker(String command) throws Exception
   {
      
       
      BufferedReader inFromUser =
      new BufferedReader(new InputStreamReader(System.in));
      DatagramSocket clientSocket = new DatagramSocket();
      InetAddress IPAddress = InetAddress.getByName("localhost");
      //System.out.println("Enter Student ID:");
      byte[] sendData = new byte[1024];
      byte[] receiveData = new byte[1024];
      
      //String sentence = inFromUser.readLine();
      
      sendData = command.getBytes();
      DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9875);
      clientSocket.send(sendPacket);
      DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
      clientSocket.receive(receivePacket);
      String modifiedSentence = new String(receivePacket.getData());
      System.out.println(modifiedSentence);
      
      //System.out.println("FROM SERVER:" + modifiedSentence);
      
      clientSocket.close();
      
      return modifiedSentence;
      //System.out.println("FROM SERVER:" + modifiedSentence);
         
   }



}
