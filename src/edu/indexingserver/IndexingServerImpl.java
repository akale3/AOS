package edu.indexingserver;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

import edu.peer.ClientDetails;

public class IndexingServerImpl extends Thread {

	Socket socket;
	ConcurrentHashMap<String, ArrayList<String>> indexingMap;

	public IndexingServerImpl(Socket socket, ConcurrentHashMap<String, ArrayList<String>> indexingmap) {
		this.socket = socket;
		this.indexingMap = indexingmap;
	}

	public void run() {
		ObjectOutputStream objectOutputStream = null;
		ObjectInputStream objectInputStream = null;
		try {
			objectInputStream = new ObjectInputStream(socket.getInputStream());
			String operation = (String) objectInputStream.readObject();
			if ("Register".equalsIgnoreCase(operation)) {
				ClientDetails clientDetails = (ClientDetails) objectInputStream.readObject();
				registerPeer(clientDetails.getIpAddress(), clientDetails.getFileNames());
			} else if ("SearchFile".equalsIgnoreCase(operation)) {
				String fileName = (String) objectInputStream.readObject();
				ArrayList<String> peerAddresses = getPeerAddress(fileName);
				objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
				objectOutputStream.writeObject(peerAddresses);
			}
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != objectOutputStream) {
					objectOutputStream.close();
				}
				if (null != objectInputStream) {
					objectInputStream.close();
				}
				if (null != socket) {
					socket.close();
				}
			} catch (IOException e) {
				System.out.println("Error in Closing Object Input/Output Stream.");
				e.printStackTrace();
			}
		}
	}

	public void registerPeer(String peerId, ArrayList<String> fileNames) throws RemoteException {
		System.out.println(
				"Registering Client : " + peerId + "\nFiles Registered...\n" + Arrays.toString(fileNames.toArray()));
		for (String fileName : fileNames) {
			if (!indexingMap.containsKey(fileName)) {
				ArrayList<String> peerIds = new ArrayList<String>();
				peerIds.add(peerId);
				indexingMap.put(fileName, peerIds);
			} else {
				indexingMap.get(fileName).add(peerId);
			}
		}
		System.out.println("Client registered successfully.");
	}

	public ArrayList<String> getPeerAddress(String fileName) throws RemoteException {
		if (indexingMap.containsKey(fileName)) {
			return indexingMap.get(fileName);
		}
		return null;
	}
}
