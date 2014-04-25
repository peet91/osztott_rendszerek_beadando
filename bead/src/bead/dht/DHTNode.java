package bead.dht;

import java.util.*;
import java.net.*;
import java.io.*;

public class DHTNode {
	public static void main(final String[] args) throws UnknownHostException, IOException {
		final int host = 65432;
		
		final int port = Integer.parseInt(args[0]);
		
		Socket socket = new Socket("localhost", host);
		
		PrintWriter pw = new PrintWriter(socket.getOutputStream());
		
		System.out.println("Port to server: " + port);
		pw.println(port);
		pw.flush();
		
		socket.close();
		
	}
}
