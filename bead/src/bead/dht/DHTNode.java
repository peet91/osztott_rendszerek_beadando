package bead.dht;

import java.util.*;
import java.net.*;
import java.io.*;

public class DHTNode extends Thread {
	
	int host;
	int port;
	int lowerBound;
	int upperBound;
	int[][] fingerTable = new int[16][2];
	
	Map<String, String> files = new HashMap<String, String>();
	
	public DHTNode() {
		
	}
	
	public DHTNode(int paramPort) {
		
		host = 65432;
		
		this.port = paramPort;
	}
	
	@Override
	public void run()  {	
		try {
			
			connectToDHTMain();
			
			ServerSocket dhtNodeServerSocket;
			
			dhtNodeServerSocket = new ServerSocket(port);
			
			startServer(dhtNodeServerSocket);
			
			files = new HashMap<String, String>();
			
			andDoTheHarlemShake(dhtNodeServerSocket);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public void connectToDHTMain() throws UnknownHostException, IOException{
		
		Socket socket;
		
		socket = new Socket("localhost", host);
		
		PrintWriter pw;
		
		pw = new PrintWriter(socket.getOutputStream());
		
		//sending port to the DHTServer
		System.out.println("Port to server: " + port);
		pw.println(port);
		pw.flush();
		//closing printWriter and socket  
		pw.close();
		socket.close();
		
	}
	
	public void startServer(ServerSocket dhtNodeServerSocket) throws UnknownHostException, IOException{

		Socket dhtServerListenerSocket = dhtNodeServerSocket.accept();
		
		System.out.println("Accepted connection from server on port: " + port);
		BufferedReader br = new BufferedReader(
				new InputStreamReader(dhtServerListenerSocket.getInputStream()));
		
		//taking the stuff from the server
		lowerBound = Integer.parseInt(br.readLine());
		upperBound = Integer.parseInt(br.readLine());
		for (int i = 0; i < 16; i++) {
			fingerTable[i][0] = Integer.parseInt(br.readLine());
			fingerTable[i][1] = Integer.parseInt(br.readLine());
		}
		System.out.println("Succesfully recieved data from DHTServer: ");
		System.out.println("lowerBoud: " + lowerBound);
		System.out.println("upperBoud: " + upperBound);
		
		System.out.println("ID     Port");
		for (int i = 0; i < 16; i++) {
			System.out.println("fingerTable[" + i + "]:" + fingerTable[i][0] + "     " +fingerTable[i][1]);
		}
		
		
		br.close();
		dhtServerListenerSocket.close();
		
	}
	
	private void andDoTheHarlemShake(ServerSocket dhtNodeServerSocket) throws UnknownHostException, IOException{
		// TODO Auto-generated method stub

		
		//main loop
		while( true ) {
			Socket userSocket = dhtNodeServerSocket.accept();
			
			BufferedReader ubr = new BufferedReader(
					new InputStreamReader(userSocket.getInputStream()));
			System.out.println("Accepted user connection, waiting for command.");
			
			String input = "";
			input = ubr.readLine();
			
			//System.out.println(input);
			
			//case of upload
			if (input.startsWith("upload")) {
				System.out.println("Doin' da upload");
				String fileName = input.substring(7);
				System.out.println("filename: " + fileName);
				String line;
				String fileContent = ""; 
				
				while ((line = ubr.readLine()) != null) {
					fileContent += line;
				}
				System.out.println("filecontent: " + fileContent);
				int fileID = Crc16.crc(fileName);
				
				boolean own = false;
				
				if (lowerBound > upperBound && ( fileID >= lowerBound || fileID <= upperBound) ) {
					own = true;
				}
				else if (lowerBound < upperBound && fileID >= lowerBound && fileID <= upperBound) {
					own = true;
				}
				
				if ( own ) {
					System.out.println("File reached it's destination. Uploading...");
					files.put(fileName, fileContent);
				}
				else {
					System.out.println("This isn't the destination node, forwarding file to another one...");
					int destinationID;
					int destinationPort;
					
					if ( upperBound < fileID && fileID < fingerTable[0][0] ) {
						destinationID = fingerTable[0][0];
						destinationPort = fingerTable[0][1];
					}
					else {
						int i = 1;
						
						while (i < 16 && fileID < fingerTable[i][0]) {
							i++;
						}
						destinationID = fingerTable[i - 1][0];
						destinationPort = fingerTable[i - 1][1];
					}
					System.out.println("DEBUG Destination: ID:" + destinationID + " Port: " +destinationPort);
					Socket destinationNode = new Socket("localhost", destinationPort);
					PrintWriter dnpw = new PrintWriter(destinationNode.getOutputStream());
					String message = "upload " + fileName;
					System.out.println("DEBUG: message to upload: " + message);
					dnpw.println(message);
					dnpw.flush();
					BufferedReader dnbr = new BufferedReader(
							new StringReader(fileContent));
					while ((line = dnbr.readLine()) != null) {
						System.out.println("forwarding file content: " + line);
						dnpw.println(line);
						dnpw.flush();
					}
					dnpw.close();
					dnbr.close();
					destinationNode.close();
				}
				//dhtNodeServerSocket.close();
			}
			//case of lookup
			else if (input.startsWith("lookup")) {
				System.out.println("Doin' da lookup");
				String fileName = input.substring(7);
				System.out.println("filename: " + fileName);
				
				int fileID = Crc16.crc(fileName);
				
				boolean own = false;
				
				if (lowerBound > upperBound && ( fileID >= lowerBound || fileID <= upperBound) ) {
					own = true;
				}
				else if (lowerBound < upperBound && fileID >= lowerBound && fileID <= upperBound) {
					own = true;
				}
				
				if ( own ) {
					System.out.println("This node is responsible for the file");
					PrintWriter upw = new PrintWriter(userSocket.getOutputStream());
					
					if (files.containsKey(fileName)) {
						System.out.println("found");
						BufferedReader ubr2 = new BufferedReader(new StringReader(files.get(fileName)));
						
						upw.println("found");
						upw.flush();
						
						String line;
						while ((line = ubr2.readLine()) != null) {
							upw.println(line);
							upw.flush();
						}
						ubr2.close();
					}
					else {
						System.out.println("not-found");
						upw.println("not-found");
						upw.flush();
					}
					upw.close();
					userSocket.close();
				}
				else {
					System.out.println("File isn't at this node, looking for it at another node...");
					int destinationID;
					int destinationPort;
					
					if ( upperBound < fileID && fileID < fingerTable[0][0] ) {
						destinationID = fingerTable[0][0];
						destinationPort = fingerTable[0][1];
					}
					else {
						int i = 1;
						
						while (i < 16 && fileID < fingerTable[i][0]) {
							i++;
						}
						destinationID = fingerTable[i - 1][0];
						destinationPort = fingerTable[i - 1][1];
					}
					
					Socket destinationNode = new Socket("localhost", destinationPort);
					PrintWriter dnpw = new PrintWriter(destinationNode.getOutputStream());
					String message = "lookup " + fileName;
					System.out.println("DEBUG: message to upload: " + message);
					dnpw.println(message);
					dnpw.flush();
					BufferedReader snbr = new BufferedReader(
							new InputStreamReader(destinationNode.getInputStream()));
					PrintWriter upw = new PrintWriter(userSocket.getOutputStream());
					
					String file = snbr.readLine();
					
					String line;
					
					if (file.equals("found")) {
						upw.println(file);
						upw.flush();
						while((line = snbr.readLine()) != null) {
							upw.println(line);
							upw.flush();
						}
					}
					else {
						upw.println("not-found");
						upw.flush();
					}
					destinationNode.close();
					upw.close();
					snbr.close();
					dnpw.close();
				}
				//dhtNodeServerSocket.close();
			}
		}
	}
}
