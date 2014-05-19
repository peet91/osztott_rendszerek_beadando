package bead.test;

import java.util.*;
import java.io.*;
import java.net.*;

class DHTClientLookup 
{
  public static void main(String[] args)
    throws Exception
    {
      
      // végpont
      String gep = "localhost";
      int port = 12345;
      Socket s = new Socket(gep,port);

      BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
      PrintWriter pw = new PrintWriter(s.getOutputStream());
      	/*    
        pw.println("upload fajlnev");
        pw.flush();
        pw.println("ASDASDASDASASDASDASDASDASDASDAS");
        pw.flush();
        */
 		
        pw.println("lookup fajlnev");
        pw.flush();
        
        String line;

        System.out.println("File content: ");
        while((line = br.readLine()) != null) {
        	System.out.println(line);
        }
        
      br.close();
      pw.close();
      s.close();
    }
}
