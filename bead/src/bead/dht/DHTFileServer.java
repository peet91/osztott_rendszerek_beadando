package bead.dht;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DHTFileServer extends UnicastRemoteObject implements DHTFileUtils {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static List<Integer> ports = new ArrayList<Integer>();
	
	public DHTFileServer(int paramNodes) throws RemoteException {
		
		//noobdebug
		System.out.println("DHTFileServer constructor paramNodes" + paramNodes);
		for (int i = 0; i < paramNodes; i++) {
			ports.add(10000 + i);
		}
	}
	
	@Override
	public List<String> lookup(String filename) throws RemoteException, UnknownHostException, IOException {
		List<String> content = new ArrayList<String>();

		Random rnd = new Random();
		int randomPort = rnd.nextInt(ports.size());
		
		//noobdebug
		System.out.println("upload socket started at: " + randomPort);
		
		Socket s = new Socket("localhost", ports.get(randomPort));
		
		BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
		PrintWriter pw = new PrintWriter(s.getOutputStream());
			
		pw.println("lookup " + filename);
		pw.flush();
		
		String found = br.readLine();
		
		if (found.equals("found")) {
			String tmp;
			while ((tmp = br.readLine()) != null) {
				content.add(tmp);
			}
			
			br.close();
			s.close();
			return content;
		}
		else {
			
			s.close();
			return null;
		}
}

	@Override
	public void upload(String filename, List<String> filecontent)
			throws RemoteException, UnknownHostException, IOException {

		Random rnd = new Random();
		int randomPort = rnd.nextInt(ports.size());
				
		//noobdebug
		System.out.println("upload socket started at: " + randomPort);
		
		Socket s = new Socket("localhost", ports.get(randomPort));
		
		PrintWriter pw = new PrintWriter(s.getOutputStream());
		
		pw.println("upload " + filename);
		pw.flush();
		
		for (int i = 0; i < filecontent.size(); i++) {
			pw.println(filecontent.get(i));
			pw.flush();
		}
		
		pw.close();
		s.close();
	}

}
