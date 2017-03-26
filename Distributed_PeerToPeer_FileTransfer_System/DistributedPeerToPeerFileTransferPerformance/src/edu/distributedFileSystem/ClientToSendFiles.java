package edu.distributedFileSystem;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;

public class ClientToSendFiles implements Runnable {

	public ClientToSendFiles() {

	}

	@Override
	public void run() {
		try {
			Properties property = new Properties();
			ServerSocket serverSocket = null;
			Socket socket = null;
			try {
				property.load(new FileInputStream(new File("./resources/config.properties")));
				String clientSourcePath = property.getProperty("clientData");
				int portForSendingFile;
				try {
					portForSendingFile = Integer.parseInt(property.getProperty("currentMachinePortForSendingFile"));
					serverSocket = new ServerSocket(portForSendingFile);
				} catch (NumberFormatException e) {
					System.out.println(
							"\"clientPort\" value in config.properties is not correct.\nStarting Client on port (8888)");
					portForSendingFile = 8888;
					serverSocket = new ServerSocket(portForSendingFile);
				}

				// Client created a Server socket and ready to accept other peer
				// request.
				while (true) {
					//System.out.println("Client is ready to send files on port " + portForSendingFile);
					//System.out.println("Waiting...");
					socket = serverSocket.accept();
					// Accepting a request from another peer and creating a new
					// Thread to process each request to send file to that peer.
					SendFiles sendFiles = new SendFiles(socket, clientSourcePath);
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
