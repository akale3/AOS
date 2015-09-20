package edu.peer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;

public class ClientAsServer implements Runnable{

	String ipAddress;
	public ClientAsServer(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	
	@Override
	public void run() {
		try {
			Properties property = new Properties();
			ServerSocket serverSocket = null;
			Socket socket = null;
			try {
				property.load(new FileInputStream(
						new File("/home/aditya/workspace/AOS/FinalNapsterPeerToPeer/resources/config.properties")));
				int clientPort = Integer.parseInt(property.getProperty("clientPort"));
				serverSocket = new ServerSocket(clientPort);
				while (true) {
					System.out.println("Client " + ipAddress + " is ready to send files.");
					System.out.println("Waiting...");
					socket = serverSocket.accept();
					SendFiles sendFiles = new SendFiles(socket, property.getProperty("peer1path"));
					Thread t1 = new Thread(sendFiles);
					t1.start();
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (serverSocket != null) {
						serverSocket.close();
					}
					if (socket != null) {
						socket.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
