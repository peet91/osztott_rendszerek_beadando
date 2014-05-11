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
		//accepting DHTNodes
		for (int i = 0; i < n; i++) {
			clientPortArray[i] = 0;
			
			Socket dhtClient = dhtServer.accept();
			final BufferedReader br = new BufferedReader(
				new InputStreamReader(dhtClient.getInputStream()));
			//generating ID for current node
			final String clientPort = br.readLine();
			clientPortArray[i] = Integer.parseInt(clientPort);
			System.out.println("Accepted port from client[" + i + "]: " + clientPortArray[i]);
		}
		
		//ID generate to individual DHTNodes
		for (int i = 0; i < n; i++) {
			clientIdArray[i] = rand.nextInt( 65536 );
			
			System.out.println("Client Id generated:[" + i + "]: " + clientIdArray[i]);
		}

		Arrays.sort(clientIdArray);
		
		//creating FingerTable array
		//			 ids	ports
		//fingerTable[]		[]
		int[][] fingerTable = new int[16][2];
		
		//generating fingerTable for the individual DHTNodes
		for (int i = 0; i < n; i++) {

			for (int j = 0; j < 16; j++) {
				
				int tmp = (clientIdArray[i]+(int)Math.pow(2,j)) % 65536;
				
				int k = 1;
				
				while (k < n && tmp > clientIdArray[k] ) {
					k++;
				}
				
				if (k == n) {
					fingerTable[j][0] = clientIdArray[0];
					fingerTable[j][1] = clientPortArray[0];
				}
				else {
					fingerTable[j][0] = clientIdArray[k];
					fingerTable[j][1] = clientPortArray[k];
				}
			}
			
			System.out.println("Fingertable for " + i + ". node: ");
			System.out.println("ID     Port");
			for (int a = 0; a < 16; a++) {
				System.out.println(fingerTable[a][0] + "     " + fingerTable[a][1]);
			}
			
		//sending stuff to the individual DHTNodes
		Socket nodeSocket = new Socket("localhost", clientPortArray[i]);
		
		PrintWriter pw = new PrintWriter(nodeSocket.getOutputStream());
		
		int lowerBound;
		int upperBound;
		
		if (i == 0) {
			lowerBound = clientIdArray[n - 1] + 1;
		}
		else {
			lowerBound = clientIdArray[i - 1] + 1;
		}
		
		upperBound = clientIdArray[i];
		//passing upper and lower bounds to the DHTNode
		System.out.println("Sending upper and lower bounds for " + i + ". node with id: " + clientIdArray[i]);
		System.out.println("lowerBound: " + lowerBound + " "+ "upperBound: " + upperBound);
		pw.println(lowerBound);
		pw.flush();
		pw.println(upperBound);
		pw.flush();
		
		//passing fingerTable stuff to the DHTNode
		System.out.println("Sending fingerTable to the DHTNode with id: " + clientIdArray[i]);
		for (int[] f:fingerTable) {
		//	System.out.println(f[0] + " " + f[1]);
			pw.println(f[0]);
			pw.flush();
			pw.println(f[1]);
			pw.flush();
		}
		System.out.println("Fingertable successfully sent.");
		pw.close();
		}	
		System.out.println("Sending was succesful, closing server");
		//closing printWriter and socket
		dhtServer.close();
	}
}
