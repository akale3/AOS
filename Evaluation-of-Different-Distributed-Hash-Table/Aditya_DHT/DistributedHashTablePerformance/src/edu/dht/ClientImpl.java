package edu.dht;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Properties;
import java.util.Random;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * @author aditya
 *
 */
public class ClientImpl {

	private static Logger logger;
	private boolean isSocketInitialized = false;
	Socket[] socketArray;
	ObjectOutputStream[] outputStreamArray;
	ObjectInputStream[] inputStreamArray;
	private int numberOfServer;
	Properties property;
	String[] keys;

	/**
	 * Calls put method of server.
	 */
	public void put() {
		if (!isSocketInitialized) {
			initializeSockets();
			isSocketInitialized = true;
		}
		String key = null;
		String value = "This is a long text to check performance.This is a long text to check performance.This is.";
		long startTime = System.currentTimeMillis();
		int numberOfOperations = Integer.parseInt(property.getProperty("numberOfOperations"));
		logger.info("Putting Process Started At " + startTime);
		for (int i = 0; i < numberOfOperations; i++) {
			key = keys[i];
			int serverNumber = getHashKey(key);
			try {
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
		logger.info("Total time taken to put " + numberOfOperations + " operations :" + (endTime - startTime)
				+ " milliseconds\n");
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
		int numberOfOperations = Integer.parseInt(property.getProperty("numberOfOperations"));
		long startTime = System.currentTimeMillis();
		logger.info("Getting Process Started At " + startTime);
		for (int i = 0; i < numberOfOperations; i++) {
			key = keys[i];
			int serverNumber = getHashKey(key);
			try {
				String entry = "2" + key;
				outputStreamArray[serverNumber].writeObject(entry);
				outputStreamArray[serverNumber].flush();
				String value = (String) inputStreamArray[serverNumber].readObject();
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		long endTime = System.currentTimeMillis();
		logger.info("Total time taken to Get " + numberOfOperations + " operations :" + (endTime - startTime)
				+ " milliseconds\n");

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
		int numberOfOperations = Integer.parseInt(property.getProperty("numberOfOperations"));
		long startTime = System.currentTimeMillis();
		logger.info("Deleting Process Started At " + startTime);
		for (int i = 0; i < numberOfOperations; i++) {
			key = keys[i];
			int serverNumber = getHashKey(key);
			try {
				String entry = "3" + key;
				outputStreamArray[serverNumber].writeObject(entry);
				outputStreamArray[serverNumber].flush();
				boolean isSuccess = (boolean) inputStreamArray[serverNumber].readObject();
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		long endTime = System.currentTimeMillis();
		logger.info("Total time taken to Delete " + numberOfOperations + " operations :" + (endTime - startTime)
				+ " milliseconds\n");
	}

	/**
	 * This method initialze all sockets and its output/input streams and store
	 * them in an array.
	 */
	private void initializeSockets() {
		try {
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
		} catch (IOException e) {
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
	 * This method creates random keys of length 10 bytes.
	 */
	public void createRandomKeys() {
		property = new Properties();
		try {
			property.load(new FileInputStream(new File("./resources/config.properties")));
			int numberOfkeys = Integer.parseInt(property.getProperty("numberOfOperations"));
			keys = new String[numberOfkeys];
			char[] chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();
			Random random = new Random();
			for (int i = 0; i < numberOfkeys; i++) {
				StringBuilder sb = new StringBuilder();
				for (int j = 0; j < 10; j++) {
					char c = chars[random.nextInt(chars.length)];
					sb.append(c);
				}
				keys[i] = sb.toString();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void initializeLogger() {
		logger = Logger.getLogger(ClientImpl.class.getName());
		try {
			FileHandler filehandler = new FileHandler();
			filehandler = new FileHandler("./resources/MyLogFile.log", true);
			logger.addHandler(filehandler);
			SimpleFormatter formatter = new SimpleFormatter();
			filehandler.setFormatter(formatter);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
