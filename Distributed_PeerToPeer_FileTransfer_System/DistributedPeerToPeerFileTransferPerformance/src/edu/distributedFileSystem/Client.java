package edu.distributedFileSystem;

import java.util.Scanner;

/**
 * @author aditya
 *
 */
public class Client {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		// One new thread is created which act as a server to listen other
		// client requests for registering files.
		Thread indexingServerThread = new Thread(new ClientServer());
		indexingServerThread.start();

		// One new thread is created which act as a server to listen other
		// client requests for Sending files.
		Thread threadToSendFiles = new Thread(new ClientToSendFiles());
		threadToSendFiles.start();

		Scanner scanner = new Scanner(System.in);
		String choice = null;
		ClientImpl clientImpl = new ClientImpl();
		do {
			System.out.println("********************************************************************");
			System.out.println("Enter Your Choice : ");
			System.out.println(
					"1.Register Files.\n2.Search for a file name.\n3.Get all file names to download.\n4.Download a File.\n5.Exit");
			choice = scanner.nextLine();
			if ("1".equalsIgnoreCase(choice) || "2".equalsIgnoreCase(choice) || "3".equalsIgnoreCase(choice)
					|| "4".equalsIgnoreCase(choice)) {
				switch (choice) {
				case "1":
					clientImpl.register();
					break;
				case "2":
					clientImpl.searchFile();
					break;
				case "3":
					clientImpl.getAllFileNames();
					break;
				case "4":
					clientImpl.requestFileToDownload();
					break;
				}
			} else if (!"5".equals(choice)) {
				System.out.println("Please Enter Correct Option.");
			}
		} while (!"5".equals(choice));
	}
}
