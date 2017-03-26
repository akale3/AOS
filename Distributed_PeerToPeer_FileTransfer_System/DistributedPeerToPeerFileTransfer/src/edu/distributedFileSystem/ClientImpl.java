package edu.distributedFileSystem;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Scanner;

/**
 * @author aditya
 *
 */
public class ClientImpl {

	private boolean isSocketInitialized = false;
	public static final int FILE_SIZE = 1024000;
	Socket[] socketArray;
	ObjectOutputStream[] outputStreamArray;
	ObjectInputStream[] inputStreamArray;
	private int numberOfServer;
	Properties property;
	private String portForSendingFile;
	private String currentMachineIp;
	private String replica1;
	private String replica2;

	public ClientImpl() {
		property = new Properties();
		try {
			property.load(new FileInputStream(new File("./resources/config.properties")));
			numberOfServer = Integer.parseInt(property.getProperty("noOfServers"));
			portForSendingFile = property.getProperty("currentMachinePortForSendingFile");
			currentMachineIp = property.getProperty("currentMachineIp");
			replica1 = property.getProperty("replica1");
			replica2 = property.getProperty("replica2");

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Send key value to a particular client to store in HashTable
	 */
	public void register() {
		if (!isSocketInitialized) {
			initializeSockets();
			isSocketInitialized = true;
		}
		System.out.println("Registering files on decentralized indexing server");
		String clientSourcePath = "ClientData";
		File folder = new File(clientSourcePath);
		File[] listOfFiles = folder.listFiles();
		ArrayList<String> fileNames = getFileNames(clientSourcePath, listOfFiles);
		for (String fileName : fileNames) {
			int serverNumber = getHashKey(fileName);
			storeFileDetails(serverNumber, fileName, currentMachineIp, portForSendingFile, "false");
			if (numberOfServer > 2) {
				int nextServerNumber = serverNumber == (numberOfServer - 1) ? 0 : serverNumber + 1;
				storeFileDetails(nextServerNumber, fileName, currentMachineIp, portForSendingFile, "false");
				if ("true".equalsIgnoreCase(replica1)) {
					storeFileDetails(nextServerNumber, fileName,
							property.getProperty("serverIp" + (nextServerNumber + 1)),
							property.getProperty("fileTransferPortServer" + (nextServerNumber + 1)), replica1);
				}
				int previousServerNumber = serverNumber == 0 ? (numberOfServer - 1) : serverNumber - 1;
				storeFileDetails(previousServerNumber, fileName, currentMachineIp, portForSendingFile, "false");
				if ("true".equalsIgnoreCase(replica2)) {
					storeFileDetails(previousServerNumber, fileName,
							property.getProperty("serverIp" + (previousServerNumber + 1)),
							property.getProperty("fileTransferPortServer" + (previousServerNumber + 1)), replica2);
				}
			}

		}
	}

	/**
	 * Send key value to a particular client to store in HashTable
	 */
	private void storeFileDetails(int serverNumber, String fileName, String machineIp, String port, String replica) {
		try {
			String value = "1" + String.format("%1$" + 24 + "s", fileName) + machineIp + "_" + port + "_" + replica;
			outputStreamArray[serverNumber].writeObject(value);
			outputStreamArray[serverNumber].flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method display client address where file is located
	 */
	public void searchFile() {
		if (!isSocketInitialized) {
			initializeSockets();
			isSocketInitialized = true;
		}
		String fileName;
		try {
			Scanner scanner = new Scanner(System.in);
			System.out.println("Enter a file name to Search");
			fileName = scanner.nextLine();
			int serverNumber = getHashKey(fileName);
			String newFileName = String.format("%1$" + 24 + "s", fileName);
			newFileName = "2" + newFileName;
			outputStreamArray[serverNumber].writeObject(newFileName);
			outputStreamArray[serverNumber].flush();
			ArrayList<String> addresses = (ArrayList) inputStreamArray[serverNumber].readObject();
			System.out.println("File is Present on following Locations");
			int j = 1;
			for (String address : addresses) {
				System.out.println(j++ + ". " + address);
			}
		} catch (IOException | ClassNotFoundException e) {
			System.out.println("Either Client on which the file is located is Down.\nPlease try after some time.");
			// e.printStackTrace();
		}
	}

	/**
	 * This method connects with every server to get file names which are stored
	 * on them.
	 */
	public void getAllFileNames() {
		if (!isSocketInitialized) {
			initializeSockets();
			isSocketInitialized = true;
		}
		ArrayList<String> allFileNames = new ArrayList<>();
		for (int i = 0; i < socketArray.length; i++) {
			try {
				String operation = "3";
				outputStreamArray[i].writeObject(operation);
				outputStreamArray[i].flush();
				ArrayList<String> fileNames = (ArrayList) inputStreamArray[i].readObject();
				for (String fileName : fileNames) {
					if (!allFileNames.contains(fileName)) {
						allFileNames.add(fileName);
					}
				}
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		System.out.println("All registered files.");
		int j = 1;
		for (String name : allFileNames) {
			System.out.println(j++ + ". " + name);
		}
	}

	/**
	 * Get value from a particular client
	 */
	public void requestFileToDownload() {
		if (!isSocketInitialized) {
			initializeSockets();
			isSocketInitialized = true;
		}

		Scanner scanner = new Scanner(System.in);
		String fileName = null;
		ArrayList<String> peerIds = null;
		try {

			System.out.println("Enter a file name to download");
			fileName = scanner.nextLine();
			int serverNumber = getHashKey(fileName);
			String newFileName = String.format("%1$" + 24 + "s", fileName);
			newFileName = "4" + newFileName;
			try {
				outputStreamArray[serverNumber].writeObject(newFileName);
				outputStreamArray[serverNumber].flush();
				peerIds = (ArrayList) inputStreamArray[serverNumber].readObject();
			} catch (IOException e) {
				try {
					int nextServerNumber = serverNumber == (numberOfServer - 1) ? 0 : serverNumber + 1;
					outputStreamArray[nextServerNumber].writeObject(newFileName);
					outputStreamArray[nextServerNumber].flush();
					peerIds = (ArrayList) inputStreamArray[nextServerNumber].readObject();
				} catch (IOException e1) {
					int previousServerNumber = serverNumber == 0 ? (numberOfServer - 1) : serverNumber - 1;
					outputStreamArray[previousServerNumber].writeObject(newFileName);
					outputStreamArray[previousServerNumber].flush();
					peerIds = (ArrayList) inputStreamArray[previousServerNumber].readObject();
				}
			}
		} catch (IOException | ClassNotFoundException e2) {
			System.out.println("Either Client on which the file is located is Down.\nPlease try after some time.");
			System.out.println("Or you dont have \"Downloaded\" folder in project path.");
			// e2.printStackTrace();
		}
		downloadFile(peerIds, fileName, "Downloaded");
	}

	/**
	 * @param peerIds
	 * @param fileName
	 * @param clientDownloadPath
	 *            Download a file to a client folder location
	 */
	private void downloadFile(ArrayList<String> peerIds, String fileName, String clientDownloadPath) {
		Scanner scanner = new Scanner(System.in);
		Socket socketToConnectClient = null;
		ObjectInputStream serverObjectInputStream = null;
		FileOutputStream fileOutputStream = null;
		BufferedOutputStream bufferOutputStream = null;
		PrintWriter printWriter = null;
		try {
			if (null != peerIds && peerIds.size() > 0) {
				int bytesRead;
				String[] machineAddress = null;
				if (peerIds.size() > 1) {
					System.out.println(
							"File Present on these following servers.\nPlease Provide an integer input (Server Number) from where you want to download.");
					int i = 1;
					for (String id : peerIds) {
						System.out.println(i + ". " + id);
						i++;
					}
					int input = scanner.nextInt();
					// Creating a connection with a peer having the file which
					// user wants to download
					machineAddress = peerIds.get(input - 1).split("_");
					socketToConnectClient = new Socket(machineAddress[0], Integer.parseInt(machineAddress[1]));
					System.out.println("Connecting to " + machineAddress[0]);
				} else {
					machineAddress = peerIds.get(0).split("_");
					socketToConnectClient = new Socket(machineAddress[0], Integer.parseInt(machineAddress[1]));
					System.out.println("Connecting to " + machineAddress[0]);
				}

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
				System.out.println("File " + clientDownloadPath + "/" + fileName + " downloaded Successfully.");
				printFileContent(clientDownloadPath, fileName);
			} else {
				System.out.println("This File is not available at any client.");
				System.out.println("Please Search another file.");
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println(
					"Currently server containing file is not working. Please choose another server or try after some time.");
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
	 * This method initialze all sockets and its output/input streams and store
	 * them in an array.
	 */
	private void initializeSockets() {
		try {
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
	 * @param folderPath
	 * @param listOfFiles
	 * @return Array list of all file names of the specified path.
	 */
	private static ArrayList<String> getFileNames(String folderPath, File[] listOfFiles) {
		ArrayList<String> files = new ArrayList<String>();
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				files.add(listOfFiles[i].getName());
			}
		}
		return files;
	}

	/**
	 * This method prints the file content to a user.
	 * 
	 * @param filePath
	 *            - Absolute path where file is located
	 * @param fileName
	 */
	private void printFileContent(String filePath, String fileName) {
		BufferedReader bufferReader = null;
		try {
			File file = new File(filePath + "/" + fileName);
			if (file.length() < 10240) {
				System.out.println("\t Displaying " + fileName + " Content : ");
				bufferReader = new BufferedReader(new FileReader(file));
				String line = null;
				while ((line = bufferReader.readLine()) != null) {
					System.out.println("\t\t" + line);
				}
			} else {
				System.out.println("\tNot Displaying " + fileName + " Content as file size is greater than 100KB.");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != bufferReader) {
				try {
					bufferReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
