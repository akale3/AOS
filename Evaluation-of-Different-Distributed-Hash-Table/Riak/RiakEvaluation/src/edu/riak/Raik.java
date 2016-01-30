package edu.riak;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import com.basho.riak.client.api.RiakClient;
import com.basho.riak.client.api.cap.UnresolvedConflictException;
import com.basho.riak.client.api.commands.kv.DeleteValue;
import com.basho.riak.client.api.commands.kv.FetchValue;
import com.basho.riak.client.api.commands.kv.StoreValue;
import com.basho.riak.client.core.RiakCluster;
import com.basho.riak.client.core.RiakNode;
import com.basho.riak.client.core.query.Location;
import com.basho.riak.client.core.query.Namespace;
import com.basho.riak.client.core.query.RiakObject;
import com.basho.riak.client.core.util.BinaryValue;

public class Raik {

	private ArrayList<RiakNode> listOfNodes;
	private Logger logger;
	private Properties property;
	private String[] keys;
	private int numberOfServer;
	private int numberOfOperations;
	private RiakCluster cluster;
	private RiakObject quoteObject;
	private StoreValue storeOp;
	private Namespace quotesBucket;
	private RiakClient client = null; 
	
	public static class Book {
		public String title;
		public String author;
		public String body;
		public String isbn;
		public Integer copiesOwned;
	}

	public void initializeServer() {

		quoteObject = new RiakObject().setContentType("text/plain");
		storeOp=null;
		quotesBucket = new Namespace("RiakTable");
		numberOfServer = Integer.parseInt(property.getProperty("noOfServers"));
		listOfNodes = new ArrayList<>();
		try {
			for (int i = 0; i < numberOfServer; i++) {
				RiakNode node = null;
				String ip = property.getProperty("serverIp" + (i + 1));
				node = new RiakNode.Builder().withRemoteAddress(ip).withRemotePort(8087).build();
				listOfNodes.add(node);
			}
			cluster = new RiakCluster.Builder(listOfNodes).build();
			cluster.start();
			client = new RiakClient(cluster);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	public void put() throws ExecutionException, InterruptedException {
		String value = "This is a long text to check performance.This is a long text to check performance.This is.";
		long startTime = System.currentTimeMillis();
		int numberOfOperations = Integer.parseInt(property.getProperty("numberOfOperations"));
		logger.info("Putting Process Started At " + startTime);
		for (int i = 0; i < numberOfOperations; i++) {
			int serverNumber = getHashKey(keys[i]);
			Location quoteObjectLocation = new Location(quotesBucket, keys[i]);
			quoteObject.setValue(BinaryValue.create(value));
			storeOp = new StoreValue.Builder(quoteObject)
					.withLocation(quoteObjectLocation)
					.build();
			StoreValue.Response storeOpResp = client.execute(storeOp);
		}
		long endTime = System.currentTimeMillis();
		logger.info("Total time taken to put " + numberOfOperations + " operations on Redis Server :"
				+ (endTime - startTime) + " milliseconds\n");
	}

	public void get() throws UnresolvedConflictException, ExecutionException, InterruptedException {
		long startTime = System.currentTimeMillis();
		logger.info("Getting Process Started At " + startTime);
		for (int i = 0; i < numberOfOperations; i++) {
			Location quoteObjectLocation = new Location(quotesBucket, keys[i]);
			FetchValue fetchOp = new FetchValue.Builder(quoteObjectLocation)
					.build();
			RiakObject fetchedObject = client.execute(fetchOp).getValue(RiakObject.class);
		}
		long endTime = System.currentTimeMillis();
		logger.info("Total time taken to Get " + numberOfOperations + " operations on Redis Server :"
				+ (endTime - startTime) + " milliseconds\n");
	}

	public void delete() throws ExecutionException, InterruptedException {
		long startTime = System.currentTimeMillis();
		logger.info("Deleting Process Started At " + startTime);
		for (int i = 0; i < numberOfOperations; i++) {
			Location quoteObjectLocation = new Location(quotesBucket, keys[i]);
			DeleteValue deleteOp = new DeleteValue.Builder(quoteObjectLocation)
					.build();
			client.execute(deleteOp);
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
		logger = Logger.getLogger(Raik.class.getName());
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
