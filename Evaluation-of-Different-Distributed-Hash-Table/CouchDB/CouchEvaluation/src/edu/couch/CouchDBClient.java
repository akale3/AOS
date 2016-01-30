package edu.couch;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Random;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import com.fourspaces.couchdb.Database;
import com.fourspaces.couchdb.Document;
import com.fourspaces.couchdb.Session;


public class CouchDBClient {

	private Session arrayOfServer[];
	private Database database[];
	private Logger logger;
	private Properties property;
	private String[] keys;
	private int numberOfServer;
	private int numberOfOperations;
	private String databaseName;
	
	public void initializeServer() {
		numberOfServer = Integer.parseInt(property.getProperty("noOfServers"));
		arrayOfServer = new Session[numberOfServer];
		database = new Database[numberOfServer];
		databaseName = keys[0];
		for (int i = 0; i < numberOfServer; i++) {
			arrayOfServer[i] = new Session(property.getProperty("serverIp" + (i + 1)),5984);
			arrayOfServer[i].createDatabase(databaseName);
			database[i] = arrayOfServer[i].getDatabase(databaseName);
		}
	}

	public void put() {
		String value = "This is a long text to check performance.This is a long text to check performance.This is.";
		long startTime = System.currentTimeMillis();
		int numberOfOperations = Integer.parseInt(property.getProperty("numberOfOperations"));
		logger.info("Putting Process Started At " + startTime);
		for (int i = 0; i < numberOfOperations; i++) {
			int serverNumber = getHashKey(keys[i]);
			Document document = new Document();
			document.setId(keys[i]);
	        document.put("value", value);
	        database[serverNumber].saveDocument(document);
		}
		long endTime = System.currentTimeMillis();
		logger.info("Total time taken to put " + numberOfOperations + " operations on MongoDB Server :"
				+ (endTime - startTime) + " milliseconds\n");
	}

	public void get() {
		long startTime = System.currentTimeMillis();
		logger.info("Getting Process Started At " + startTime);
		for (int i = 0; i < numberOfOperations; i++) {
			int serverNumber = getHashKey(keys[i]);
			Document document = database[serverNumber].getDocument(keys[i]);
			String string= (String)document.get("value");
			System.out.println(string);
		}
		long endTime = System.currentTimeMillis();
		logger.info("Total time taken to Get " + numberOfOperations + " operations on MongoDB Server :"
				+ (endTime - startTime) + " milliseconds\n");
	}

	public void delete() {
		long startTime = System.currentTimeMillis();
		logger.info("Deleting Process Started At " + startTime);
		for (int i = 0; i < numberOfOperations; i++) {
			int serverNumber = getHashKey(keys[i]);
			Document document = database[serverNumber].getDocument(keys[i]);
			database[serverNumber].deleteDocument(document);
		}
		long endTime = System.currentTimeMillis();
		logger.info("Total time taken to Delete " + numberOfOperations + " operations on MongoDB Server :"
				+ (endTime - startTime) + " milliseconds\n");
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
			numberOfOperations = Integer.parseInt(property.getProperty("numberOfOperations"));
			keys = new String[numberOfOperations];
			char[] chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();
			Random random = new Random();
			for (int i = 0; i < numberOfOperations; i++) {
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
		logger = Logger.getLogger(CouchDBClient.class.getName());
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
