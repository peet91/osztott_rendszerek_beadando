package bead.test;

import java.util.*;
import java.io.*;
import java.net.*;

class DHTClientUpload 
{
  public static void main(String[] args)
    throws Exception
    {
      
      // végpont
      String gep = "localhost";
      int port = 12345;
      Socket s = new Socket(gep,port);

      Scanner sc = new Scanner(s.getInputStream());
      PrintWriter pw = new PrintWriter(s.getOutputStream());
      	
        pw.println("upload fajlnev");
        pw.flush();
        pw.println("ASDASDASDASASDASDASDASDASDASDAS");
        pw.println("WQERTQWERTQWERQWRTQWERTQWERQWET");
        pw.println("sajt");
        pw.flush();
        
 		/*
        pw.println("lookup fajlnev");
        pw.flush();
        */
        
        
      sc.close();
      pw.close();
      s.close();
    }
}
