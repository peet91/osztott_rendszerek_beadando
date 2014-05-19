package bead.dht;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class StartDHT {
	public static void main(String[] args) throws RemoteException {
		final int n = Integer.parseInt(args[0]);
		
		DHTNode[] DHTNodes = new DHTNode[n];
		
		int nodePort;
		for (int i = 0; i < n; i++) {
			nodePort = 10000 + i;
			
			DHTNodes[i] = new DHTNode(nodePort);
			DHTNodes[i].start();
		}
		
		final int port = 1099;
		System.out.println("Creating registry at default port (" + port + ")");
		Registry rmiRegistry = LocateRegistry.createRegistry(port);
		//noobdebug
		rmiRegistry.rebind("dht", new DHTFileServer(n));
		
		System.out.println("registry: " + rmiRegistry.toString());
	}
}
