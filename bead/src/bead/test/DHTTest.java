package bead.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;

import bead.dht.DHTFileUtils;

public class DHTTest {
	static String directory;
	static String firstFile;
	
	public static void main(String[] args) throws NotBoundException, FileNotFoundException, IOException{
		directory = args[0];
		
		//noobdebug
		System.out.println("directory: " + directory );
		
		Registry registry = LocateRegistry.getRegistry("localhost", 1099);
		
		DHTFileUtils dhtFileUtils = (DHTFileUtils)(registry.lookup("dht"));
		
		List<String> uploadableFiles = new ArrayList<String>();
		
		for (File fileEntry : new File(directory).listFiles()) {
			System.out.println("file in folder " + fileEntry);
			if (fileEntry.length() < 1024) {
				uploadableFiles.add(fileEntry.getName());
			}
		}
		
		firstFile = uploadableFiles.get(0);
		
		//noobdebug
		System.out.println(firstFile);
		
		//upload the files
		for (int i = 0; i < uploadableFiles.size(); i++) {
			
			String fileName = uploadableFiles.get(i);
			List<String> fileContent = parseFile(new File(directory + "\\" + uploadableFiles.get(i)));
			
			//noobdebug
			System.out.println("uploading: " + fileName + " " + fileContent);
			
			dhtFileUtils.upload(fileName, fileContent);
		}
		
		System.out.println("upload OK");
		
		File downloadsDir = new File("downloads");
		
		if (!downloadsDir.exists()) {
			downloadsDir.mkdir(); 	     
		}
		
		List<String> downloadedFileContent = dhtFileUtils.lookup(firstFile);
		//noobdebug
		System.out.println("downloadedFileContent: " + downloadedFileContent);
		
		saveFile(firstFile, downloadedFileContent);
		
		if (dhtFileUtils.lookup("foo.bar") == null) {
			System.out.println("Lookup for file named foo.bar resulted null, szoval orulunk :)");
		}
		else System.out.println("Lookup for file named foo.bar didn't return with null, so something went wrong...");
		
	}
	public static List<String> parseFile(File file) throws FileNotFoundException, IOException {
		BufferedReader br = new BufferedReader(new FileReader(file));
		List<String> fileContent = new ArrayList<String>();
		String tmp;
		
		while( (tmp = br.readLine()) != null) {
			fileContent.add(tmp);
		}
		
		br.close();
		return fileContent;
	}
	
	public static void saveFile(String fileName, List<String> fileContent) throws IOException {
		String rootPath = new File("").getCanonicalPath();
		
		//noobdebug
		System.out.println("rootpath: " + rootPath);
		
		PrintWriter pw = new PrintWriter(new File(rootPath + "\\" + "downloads" + "\\" + fileName));
		
		//noobdebug
		System.out.println("Saving file to path: " + rootPath + "\\" + "downloads" + "\\" + fileName);
		
		for (int i = 0; i < fileContent.size(); i++) {
			pw.println(fileContent.get(i));
		}
		
		pw.flush();
		pw.close();
	}
}
