package edu.dht;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author aditya
 *
 */
public class ClientServerImpl implements Runnable {

	public ConcurrentHashMap<String, String> hashMap;
	private Socket socket;

	public ClientServerImpl(Socket socket, ConcurrentHashMap<String, String> hashMap) {
		this.socket = socket;
		this.hashMap = hashMap;
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
					boolean isSuccess = put(entry.substring(1, 24).trim(), entry.substring(24).trim());
					objectOutputStream.writeObject(isSuccess);
					objectOutputStream.flush();
					break;
				case "2":
					String value = get(entry.substring(1, 24).trim());
					objectOutputStream.writeObject(value);
					objectOutputStream.flush();
					break;
				case "3":
					boolean isDeleted = delete(entry.substring(1, 24).trim());
					objectOutputStream.writeObject(isDeleted);
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
	 * @return true This method put key value pair in hashmap
	 */
	public boolean put(String key, String value) {
		hashMap.put(key, value);
		return true;
	}

	/**
	 * @param key
	 * @return String value of that particular key if found, or return null
	 */
	public String get(String key) {
		if (hashMap.containsKey(key)) {
			return hashMap.get(key);
		}
		return null;

	}

	/**
	 * @param key
	 * @return boolean This method delete entry from hashmap of a particular key
	 *         and returns true if deleted successfully.
	 */
	public boolean delete(String key) {
		if (hashMap.containsKey(key)) {
			hashMap.remove(key);
			return true;
		}
		return false;
	}
}
