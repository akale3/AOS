package edu.dht;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Properties;

/**
 * @author aditya
 *
 */
public class ClientImpl {

	private boolean isSocketInitialized = false;
	Socket[] socketArray;
	ObjectOutputStream[] outputStreamArray;
	ObjectInputStream[] inputStreamArray;
	private int numberOfServer;

	/**
	 * Calls put method of server.
	 */
	public void put() {
		if (!isSocketInitialized) {
			initializeSockets();
			isSocketInitialized = true;
		}
		String key = null;
		String value = "This is a long text to check performanceThis is a long text to check performanceThis is a long text to check performanceThis is a long text to check performanceThis is a long text to check performanceThis is a long text to check performanceThis is a long text to check performanceThis is a long text to check performanceThis is a long text to check performanceThis is a long text to check performanceThis is a long text to check performanceThis is a long text to check performanceThis is a long text to check performanceThis is a long text to check performanceThis is a long text to check performanceThis is a long text to check performanceThis is a long text to check performanceThis is a long text to check performanceThis is a long text to check performanceThis is a long text to check performance";
		long startTime = System.currentTimeMillis();
		System.out.println("Putting Process Started At " + startTime);
		for (int i = 0; i < 100000; i++) {
			key = Integer.toString(i);
			int serverNumber = getHashKey(key);
			try {
				key = String.format("%1$" + 23 + "s", key);
				String entry = "1" + key + value;
				outputStreamArray[serverNumber].writeObject(entry);
				outputStreamArray[serverNumber].flush();
				boolean isSuccess = (boolean) inputStreamArray[serverNumber].readObject();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		long endTime = System.currentTimeMillis();
		System.out.println("Total time taken to put 100000 operations :" + (endTime - startTime) + " milliseconds");
	}

	/**
	 * Calls get method of server.
	 */
	public void get() {
		if (!isSocketInitialized) {
			initializeSockets();
			isSocketInitialized = true;
		}
		String key = null;
		long startTime = System.currentTimeMillis();
		System.out.println("Getting Process Started At " + startTime);
		for (int i = 0; i < 100000; i++) {
			int serverNumber = getHashKey(key);
			try {
				key = String.format("%1$" + 23 + "s", key);
				String entry = "2" + key;
				outputStreamArray[serverNumber].writeObject(entry);
				outputStreamArray[serverNumber].flush();
				String value = (String) inputStreamArray[serverNumber].readObject();
				if (null != value) {
					System.out.println("value for key \"" + key.trim() + "\" is : " + value);
				} else {
					System.out.println("No such element Found.");
				}
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		long endTime = System.currentTimeMillis();
		System.out.println("Total time taken to Get 100000 operations :" + (endTime - startTime) + " milliseconds");

	}

	/**
	 * Calls delete method of server.
	 */
	public void delete() {
		if (!isSocketInitialized) {
			initializeSockets();
			isSocketInitialized = true;
		}
		String key = null;
		long startTime = System.currentTimeMillis();
		System.out.println("Deleting Process Started At " + startTime);
		for (int i = 0; i < 100000; i++) {
			key = Integer.toString(i);
			int serverNumber = getHashKey(key);
			try {
				key = String.format("%1$" + 23 + "s", key);
				String entry = "3" + key;
				outputStreamArray[serverNumber].writeObject(entry);
				outputStreamArray[serverNumber].flush();
				boolean isSuccess = (boolean) inputStreamArray[serverNumber].readObject();
				if (isSuccess) {
					System.out.println("Element Deleted Successfully");
				} else {
					System.out.println("There is no such element in DHT.");
				}
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		long endTime = System.currentTimeMillis();
		System.out.println("Total time taken to Delete 100000 operations :" + (endTime - startTime) + " milliseconds");

	}

	/**
	 * This method initialze all sockets and its output/input streams and store
	 * them in an array.
	 */
	private void initializeSockets() {
		Properties property = new Properties();
		try {
			property.load(new FileInputStream(new File("./resources/config.properties")));
			numberOfServer = Integer.parseInt(property.getProperty("noOfServers"));
			socketArray = new Socket[numberOfServer];
			outputStreamArray = new ObjectOutputStream[numberOfServer];
			inputStreamArray = new ObjectInputStream[numberOfServer];
			for (int i = 0; i < numberOfServer; i++) {
				socketArray[i] = new Socket(property.getProperty("serverIp" + (i + 1)),
						Integer.parseInt(property.getProperty("serverPort" + (i + 1))));
			}
			for (int i = 0; i < numberOfServer; i++) {
				outputStreamArray[i] = new ObjectOutputStream(socketArray[i].getOutputStream());
				inputStreamArray[i] = new ObjectInputStream(socketArray[i].getInputStream());
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param String
	 * @return int server index
	 */
	private int getHashKey(String key) {
		int hashKey = 0;
		char[] myCharArray = new char[key.length()];
		key.getChars(0, key.length(), myCharArray, 0);
		for (char myChar : myCharArray) {
			hashKey += (int) myChar;
		}
		return (hashKey * 17) % numberOfServer;
	}
}
