package edu.dht;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author aditya
 *
 */
public class ClientServer implements Runnable {

	public ConcurrentHashMap<String, String> hashMap = new ConcurrentHashMap<String, String>();

	@Override
	public void run() {
		ServerSocket serverSocket = null;
		Socket socket = null;
		try {
			Properties property = new Properties();
			property.load(new FileInputStream(new File("./resources/config.properties")));
			int serverPort = Integer.parseInt(property.getProperty("currnetMachinePort"));
			serverSocket = new ServerSocket(serverPort);

			System.out.println("Client " + serverPort + "is ready to put elements in DHT.");
			System.out.println("Waiting...");
			while (true) {
				socket = serverSocket.accept();
				// Accepting a request from another peer and creating a new
				// Thread to process each request to send file to that peer.
				ClientServerImpl clientServerImpl = new ClientServerImpl(socket, hashMap);
				Thread t1 = new Thread(clientServerImpl);
				t1.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
