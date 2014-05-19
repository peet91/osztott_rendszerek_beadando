package bead.dht;

import java.io.IOException;
import java.rmi.*;
import java.util.List;

public interface DHTFileUtils extends Remote{

	public List<String> lookup(String filename) throws RemoteException, UnknownHostException, IOException;
	
	public void upload(String filename, List<String> filecontent) throws RemoteException, UnknownHostException, IOException;
}
