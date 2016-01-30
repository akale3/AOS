package edu.dht;

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

		Thread clientServerThread = new Thread(new ClientServer());
		clientServerThread.start();

		Scanner scanner = new Scanner(System.in);
		String choice = null;
		ClientImpl clientImpl = new ClientImpl();
		do {
			System.out.println("********************************************************************");
			System.out.println("Enter Your Choice : ");
			System.out.println("1.Put Element In DHT.\n2.Get Element From DHT.\n3.Delete Element From DHT.\n4.Exit");
			choice = scanner.nextLine();
			if ("1".equalsIgnoreCase(choice) || "2".equalsIgnoreCase(choice) || "3".equalsIgnoreCase(choice)) {
				switch (choice) {
				case "1":
					clientImpl.put();
					break;
				case "2":
					clientImpl.get();
					break;
				case "3":
					clientImpl.delete();
					break;
				}
			} else if (!"4".equals(choice)) {
				System.out.println("Please Enter Correct Option.");
			}
		} while (!"4".equals(choice));
	}
}
