package edu.dht;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.Properties;
import java.util.Scanner;

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
	 * Send key value to a particular client to store in HashTable
	 */
	public void put() {
		if (!isSocketInitialized) {
			initializeSockets();
			isSocketInitialized = true;
		}
		Scanner scanner = new Scanner(System.in);
		System.out.println("Enter Key to PUT element in DHT");
		String key = scanner.nextLine();
		System.out.println("Enter Value");
		String value = scanner.nextLine();
		boolean isValidKey = isKeyValid(key);
		boolean isValidvalue = isValueValid(value);
		if (isValidKey && isValidvalue) {
			int serverNumber = getHashKey(key);
			System.out.println("putting key-value pair on server : " + (serverNumber + 1));
			try {
				key = String.format("%1$" + 23 + "s", key);
				String entry = "1" + key + value;
				outputStreamArray[serverNumber].writeObject(entry);
				outputStreamArray[serverNumber].flush();
				boolean isSuccess = (boolean) inputStreamArray[serverNumber].readObject();
				if (isSuccess) {
					System.out.println("Put Successful.");
				} else {
					System.out.println("There occured some issue while storing this key value pair.");
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("please enter valid key-value text.");
		}
	}

	/**
	 * Get value from a particular client
	 */
	public void get() {
		if (!isSocketInitialized) {
			initializeSockets();
			isSocketInitialized = true;
		}
		String key = null;
		Scanner scanner = new Scanner(System.in);
		System.out.println("Enter Key to GET element from DHT");
		key = scanner.nextLine();
		boolean isValidKey = isKeyValid(key);
		if (isValidKey) {
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
		} else {
			System.out.println("please enter valid key text.");
		}
	}

	/**
	 * Delete key value from a particular client.
	 */
	public void delete() {
		if (!isSocketInitialized) {
			initializeSockets();
			isSocketInitialized = true;
		}
		String key = null;
		boolean isValidKey = false;
		Scanner scanner = new Scanner(System.in);
		System.out.println("Enter Key to DELETE element from DHT");
		key = scanner.nextLine();
		isValidKey = isKeyValid(key);
		if (isValidKey) {
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

	/**
	 * @param String
	 *            key
	 * @return boolean # Check whether entered key is correct or not
	 */
	private static boolean isKeyValid(String key) {
		if (null != key) {
			if ("".equals(key)) {
				return false;
			}
			byte[] keyBytes;
			try {
				keyBytes = key.getBytes("UTF-8");
				if (keyBytes.length > 23) {
					return false;
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	/**
	 * @param String
	 *            value
	 * @return boolean # Check whether entered value is correct or not
	 */
	private static boolean isValueValid(String value) {
		if (null != value) {
			if ("".equals(value)) {
				return false;
			}
			byte[] keyBytes;
			try {
				keyBytes = value.getBytes("UTF-8");
				if (keyBytes.length > 1000) {
					return false;
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return true;
	}
}
