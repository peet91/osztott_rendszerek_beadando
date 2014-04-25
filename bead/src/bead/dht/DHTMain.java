package bead.dht;

import java.util.*;
import java.net.*;
import java.io.*;

public class DHTMain {
	
	private final static int port = 65432;
	
	public static void main(final String[] args) throws IOException{
		final int n = Integer.parseInt(args[0]);
		
		ServerSocket dhtServer = new ServerSocket(port);
		
		int[] clientPortArray = new int[n];
		int[] clientIdArray = new int[n];
		
		Random rand = new Random();
		
		for (int i = 0; i < n; i++) {
			clientPortArray[i] = 0;
			
			Socket dhtClient = dhtServer.accept();
			final BufferedReader br = new BufferedReader(
				new InputStreamReader(dhtClient.getInputStream()));
			
			final String clientPort = br.readLine();
			clientPortArray[i] = Integer.parseInt(clientPort);
			System.out.println("Accepted port from client[" + i + "]: " + clientPortArray[i]);
		}
		
		for (int i = 0; i < n; i++) {
			clientIdArray[i] = rand.nextInt( 65536 );
			
			System.out.println("Client Id generated:[" + i + "]: " + clientIdArray[i]);
		}
		
		dhtServer.close();
	}
}
