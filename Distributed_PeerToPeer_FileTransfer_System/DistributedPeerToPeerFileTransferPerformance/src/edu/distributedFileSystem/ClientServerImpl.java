package edu.distributedFileSystem;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author aditya
 *
 */
public class ClientServerImpl implements Runnable {

	public ConcurrentHashMap<String, ArrayList<String>> hashMap;
	private Socket socket;
	public static final int FILE_SIZE = 1024000;
	private Properties property = null;

	public ClientServerImpl(Socket socket, ConcurrentHashMap<String, ArrayList<String>> hashMap) {
		this.socket = socket;
		this.hashMap = hashMap;
		property = new Properties();
		try {
			property.load(new FileInputStream(new File("./resources/config.properties")));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		ObjectOutputStream objectOutputStream = null;
		ObjectInputStream objectInputStream = null;
		try {
			objectInputStream = new ObjectInputStream(socket.getInputStream());
			objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
			while (true) {
				String entry = (String) objectInputStream.readObject();
				String operation = entry.valueOf(entry.charAt(0));
				switch (operation) {
				case "1":
					register(entry.substring(1, 25).trim(), entry.substring(25).trim());
					break;
				case "2":
					ArrayList listOfAllAddress = getFileAddress(entry.substring(1, 25).trim());
					objectOutputStream.writeObject(listOfAllAddress);
					objectOutputStream.flush();
					break;
				case "3":
					ArrayList listOfAllFiles = getAllFileNames();
					objectOutputStream.writeObject(listOfAllFiles);
					objectOutputStream.flush();
					break;
				case "4":
					ArrayList listOfAllClients = getFileAddress(entry.substring(1, 25).trim());
					objectOutputStream.writeObject(listOfAllClients);
					objectOutputStream.flush();
					break;
				}
			}
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param key
	 * @param value
	 */
	public void register(String fileName, String machineAddress) {
		if (null != hashMap.get(fileName)) {
			if (!hashMap.get(fileName).contains(machineAddress.substring(0,machineAddress.lastIndexOf("_")))) {
				hashMap.get(fileName).add(machineAddress.substring(0,machineAddress.lastIndexOf("_")));
			}
		} else {
			ArrayList<String> arrayList = new ArrayList<String>();
			arrayList.add(machineAddress.substring(0,machineAddress.lastIndexOf("_")));
			hashMap.put(fileName, arrayList);
		}
		String[] address = machineAddress.split("_");
		if (null != address[2] && "true".equalsIgnoreCase(address[2])) {
			downloadFile(machineAddress, fileName,property.getProperty("clientData"));
		}
	}

	private void downloadFile(String peerId, String fileName, String clientDownloadPath) {
		Socket socketToConnectClient = null;
		ObjectInputStream serverObjectInputStream = null;
		FileOutputStream fileOutputStream = null;
		BufferedOutputStream bufferOutputStream = null;
		PrintWriter printWriter = null;
		try {
			int bytesRead;
			String[] machineAddress = null;

			machineAddress = peerId.split("_");
			socketToConnectClient = new Socket(machineAddress[0], Integer.parseInt(machineAddress[1]));
			//System.out.println("Connecting to " + machineAddress[0]);
			// Creating a new file and storing
			byte[] mybytearray = new byte[FILE_SIZE];
			InputStream clientInputStream = socketToConnectClient.getInputStream();
			printWriter = new PrintWriter(new OutputStreamWriter(socketToConnectClient.getOutputStream()));
			printWriter.println(fileName);
			printWriter.flush();
			fileOutputStream = new FileOutputStream(clientDownloadPath + "/" + fileName);
			bufferOutputStream = new BufferedOutputStream(fileOutputStream);
			while (-1 != (bytesRead = clientInputStream.read(mybytearray))) {
				bufferOutputStream.write(mybytearray, 0, bytesRead);
			}
			bufferOutputStream.flush();
			//System.out.println("File " + clientDownloadPath + "/" + fileName + " downloaded Successfully.");

		} catch (IOException e) {

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
				if (serverObjectInputStream != null)
					serverObjectInputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @param key
	 * @return String value of that particular key if found, or return null
	 */
	public ArrayList<String> getFileAddress(String key) {
		if (hashMap.containsKey(key)) {
			return hashMap.get(key);
		}
		return null;
	}

	public ArrayList<String> getAllFileNames() {
		ArrayList<String> allFileNames = new ArrayList<String>();
		for (Entry<String, ArrayList<String>> entry : hashMap.entrySet()) {
			allFileNames.add(entry.getKey());
		}
		return allFileNames;
	}
}
