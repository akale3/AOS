package edu.peer;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Scanner;

public class ClientImpl {

	public void registerClient(String ipAddress) {
		Properties property = new Properties();
		Socket clientSocket = null;
		ObjectOutputStream objectOutputStream = null;
		try {
			property.load(new FileInputStream(
					new File("/home/aditya/workspace/AOS/NapsterPeerToPeer/resources/config.properties")));
			String path = property.getProperty("peer1path");

			File folder = new File(path);
			File[] listOfFiles = folder.listFiles();

			ArrayList<String> fileNames = getFileNames(path, listOfFiles);

			ClientDetails clientDetails = new ClientDetails();
			clientDetails.setFileNames(fileNames);
			clientDetails.setIpAddress(ipAddress);

			clientSocket = new Socket(property.getProperty("serverIp"),
					Integer.parseInt(property.get("serverPort").toString()));
			objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
			objectOutputStream.writeObject("Register");
			objectOutputStream.writeObject(clientDetails);
			objectOutputStream.flush();
		} catch (Exception e) {

		} finally {
			if (null != clientSocket) {
				try {
					clientSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void searchFileAndDownload() {
		Properties property = new Properties();
		Scanner sc = new Scanner(System.in);
		Socket socketToConnectClient = null;
		Socket socketToConnectServer = null;
		ObjectInputStream serverObjectInputStream = null;
		ObjectOutputStream serverObjectOutputStream = null;
		FileOutputStream fileOutputStream = null;
		BufferedOutputStream bufferOutputStream = null;
		PrintWriter printWriter = null;
		try {
			property.load(new FileInputStream(
					new File("/home/aditya/workspace/AOS/FinalNapsterPeerToPeer/resources/config.properties")));
			String path = property.getProperty("peer2path");
			System.out.println("Enter File name to be Download : ");
			String fileName = sc.nextLine();
			socketToConnectServer = new Socket(property.getProperty("serverIp"),
					Integer.parseInt(property.get("serverPort").toString()));
			serverObjectOutputStream = new ObjectOutputStream(socketToConnectServer.getOutputStream());
			serverObjectOutputStream.writeObject("SearchFile");
			serverObjectOutputStream.flush();
			serverObjectOutputStream.writeObject(fileName);
			serverObjectOutputStream.flush();
			serverObjectInputStream = new ObjectInputStream(socketToConnectServer.getInputStream());

			ArrayList<String> peerIds = null;
			peerIds = (ArrayList<String>) serverObjectInputStream.readObject();
			if (null != peerIds && peerIds.size() > 0) {
				int bytesRead;
				int clientPort = Integer.parseInt(property.getProperty("clientPort"));
				socketToConnectClient = new Socket(peerIds.get(0), clientPort);
				System.out.println("Connecting to " + peerIds.get(0));

				byte[] mybytearray = new byte[1024];//Integer.parseInt(property.get("fileSize").toString())
				InputStream clientInputStream = socketToConnectClient.getInputStream();
				printWriter = new PrintWriter(new OutputStreamWriter(socketToConnectClient.getOutputStream()));
				printWriter.println(fileName);
				printWriter.flush();
				fileOutputStream = new FileOutputStream(path + fileName);
				bufferOutputStream = new BufferedOutputStream(fileOutputStream);
				while (-1 != (bytesRead = clientInputStream.read(mybytearray))) {
					bufferOutputStream.write(mybytearray, 0, bytesRead);
				}
				bufferOutputStream.flush();
				System.out.println("File " + path + fileName + " downloaded Successfully.");

			} else {
				System.out.println("This File is not available at any client.");
				System.out.println("Please Search another file.");
			}
		} catch (IOException | ClassNotFoundException e2) {
			e2.printStackTrace();
		} finally {
			try {
				if (fileOutputStream != null)
					fileOutputStream.close();
				if (bufferOutputStream != null)
					bufferOutputStream.close();
				if (printWriter != null)
					printWriter.close();
				if (socketToConnectClient != null)
					socketToConnectClient.close();
				if (socketToConnectServer != null)
					socketToConnectServer.close();
				if (serverObjectOutputStream != null)
					serverObjectOutputStream.close();
				if (serverObjectInputStream != null)
					serverObjectInputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	private static ArrayList<String> getFileNames(String folderPath, File[] listOfFiles) {
		ArrayList<String> files = new ArrayList<String>();
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				files.add(listOfFiles[i].getName());
			} else if (listOfFiles[i].isDirectory()) {
				ArrayList<String> directoryFiles = getFileNames(listOfFiles[i].getAbsolutePath(),
						listOfFiles[i].listFiles());
				files.addAll(directoryFiles);
			}
		}
		return files;
	}
}
