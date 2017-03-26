package edu.distributedFileSystem;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author aditya
 *
 */
public class ClientServer implements Runnable {

	public ConcurrentHashMap<String, ArrayList<String>> hashMap = new ConcurrentHashMap<String, ArrayList<String>>();

	@Override
	public void run() {
		ServerSocket serverSocket = null;
		Socket socket = null;
		try {
			Properties property = new Properties();
			property.load(new FileInputStream(new File("./resources/config.properties")));
			// get current system port where it will listen all requests.
			int serverPort = Integer.parseInt(property.getProperty("currnetMachinePort"));
			serverSocket = new ServerSocket(serverPort);

			System.out.println("Client " + serverPort + " is ready to put elements in DHT.");
			System.out.println("Waiting...");
			while (true) {
				socket = serverSocket.accept();
				// Accepting a request from another peer and creating a new
				// Thread which will process further requests to put, get,
				// delete.
				ClientServerImpl clientServerImpl = new ClientServerImpl(socket, hashMap);
				Thread t1 = new Thread(clientServerImpl);
				t1.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
