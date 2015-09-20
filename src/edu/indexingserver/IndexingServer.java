package edu.indexingserver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class IndexingServer {

	public static final ConcurrentHashMap<String, ArrayList<String>> indexingMap = new ConcurrentHashMap<String, ArrayList<String>>();
	
	public static void main(String[] args) {
		ServerSocket serverSocket = null;
		try {
			System.out.println("Starting indexing server....");
			Properties property = new Properties();
			property.load(new FileInputStream(
					new File("/home/aditya/workspace/AOS/FinalNapsterPeerToPeer/resources/config.properties")));

			serverSocket = new ServerSocket(Integer.parseInt(property.get("serverPort").toString()));
			System.out.println("Indexing Server Started.");
			while (true) {
				Socket socket = serverSocket.accept();
				IndexingServerImpl indexingServerImpl = new IndexingServerImpl(socket,indexingMap);
				Thread indexingServerThread = new Thread(indexingServerImpl);
				indexingServerThread.start();
			}

		} catch (FileNotFoundException e) {
			System.out.println("Configuration File not found.");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			if(null != serverSocket){
				try {
					serverSocket.close();
				} catch (IOException e) {
					System.out.println("Error in Closing Socket.");
					e.printStackTrace();
				}
			}
		}																																																																								
	}
}
