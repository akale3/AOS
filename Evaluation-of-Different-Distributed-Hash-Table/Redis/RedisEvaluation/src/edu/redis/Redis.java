package edu.redis;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Random;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import redis.clients.jedis.Jedis;

public class Redis {

	private Jedis arrayOfServer[];
	private Logger logger;
	private Properties property;
	private String[] keys;
	private int numberOfServer;
	private int numberOfOperations;

	public void initializeServer() {
		numberOfServer = Integer.parseInt(property.getProperty("noOfServers"));
		arrayOfServer = new Jedis[numberOfServer];
		for (int i = 0; i < numberOfServer; i++) {
			String ip = property.getProperty("serverIp" + (i + 1));
			arrayOfServer[i] = new Jedis(ip);
		}
	}

	public void put() {
		String value = "This is a long text to check performance.This is a long text to check performance.This is.";
		long startTime = System.currentTimeMillis();
		int numberOfOperations = Integer.parseInt(property.getProperty("numberOfOperations"));
		logger.info("Putting Process Started At " + startTime);
		for (int i = 0; i < numberOfOperations; i++) {
			int serverNumber = getHashKey(keys[i]);
			arrayOfServer[serverNumber].set(keys[i], value);
		}
		long endTime = System.currentTimeMillis();
		logger.info("Total time taken to put " + numberOfOperations + " operations on Redis Server :"
				+ (endTime - startTime) + " milliseconds\n");
	}

	public void get() {
		long startTime = System.currentTimeMillis();
		logger.info("Getting Process Started At " + startTime);
		for (int i = 0; i < numberOfOperations; i++) {
			int serverNumber = getHashKey(keys[i]);
			arrayOfServer[serverNumber].get(keys[i]);
		}
		long endTime = System.currentTimeMillis();
		logger.info("Total time taken to Get " + numberOfOperations + " operations on Redis Server :"
				+ (endTime - startTime) + " milliseconds\n");
	}

	public void delete() {
		long startTime = System.currentTimeMillis();
		logger.info("Deleting Process Started At " + startTime);
		for (int i = 0; i < numberOfOperations; i++) {
			int serverNumber = getHashKey(keys[i]);
			arrayOfServer[serverNumber].del(keys[i]);
		}
		long endTime = System.currentTimeMillis();
		logger.info("Total time taken to Delete " + numberOfOperations + " operations on Redis Server :"
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
		logger = Logger.getLogger(Redis.class.getName());
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
