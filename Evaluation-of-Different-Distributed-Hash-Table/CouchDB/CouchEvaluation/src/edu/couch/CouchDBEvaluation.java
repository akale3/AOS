package edu.couch;

import java.util.Scanner;

public class CouchDBEvaluation {

	public static void main(String[] args) {

		Scanner scanner = new Scanner(System.in);
		String choice = null;
		CouchDBClient couchImpl = new CouchDBClient();

		couchImpl.createRandomKeys();
		couchImpl.initializeLogger();
		//couchImpl.initializeServer();

		do {
			System.out.println("********************************************************************");
			System.out.println("Enter Your Choice : ");
			System.out.println("1.Put Element In DHT.\n2.Get Element From DHT.\n3.Delete Element From DHT.\n4.Exit");
			choice = scanner.nextLine();
			if ("1".equalsIgnoreCase(choice) || "2".equalsIgnoreCase(choice) || "3".equalsIgnoreCase(choice)) {
				switch (choice) {
				case "1":
					couchImpl.put();
					break;
				case "2":
					couchImpl.get();
					break;
				case "3":
					couchImpl.delete();
					break;
				}
			} else if (!"4".equals(choice)) {
				System.out.println("Please Enter Correct Option.");
			}
		} while (!"4".equals(choice));

	}
}