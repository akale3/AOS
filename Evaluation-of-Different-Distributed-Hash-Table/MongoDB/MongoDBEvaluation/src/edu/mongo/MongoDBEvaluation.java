package edu.mongo;

import java.util.Scanner;

public class MongoDBEvaluation {

	public static void main(String[] args) {

		Scanner scanner = new Scanner(System.in);
		String choice = null;
		MongoDBClient mongoImpl = new MongoDBClient();

		mongoImpl.createRandomKeys();
		mongoImpl.initializeLogger();
		mongoImpl.initializeServer();

		do {
			System.out.println("********************************************************************");
			System.out.println("Enter Your Choice : ");
			System.out.println("1.Put Element In DHT.\n2.Get Element From DHT.\n3.Delete Element From DHT.\n4.Exit");
			choice = scanner.nextLine();
			if ("1".equalsIgnoreCase(choice) || "2".equalsIgnoreCase(choice) || "3".equalsIgnoreCase(choice)) {
				switch (choice) {
				case "1":
					mongoImpl.put();
					break;
				case "2":
					mongoImpl.get();
					break;
				case "3":
					mongoImpl.delete();
					break;
				}
			} else if (!"4".equals(choice)) {
				System.out.println("Please Enter Correct Option.");
			}
		} while (!"4".equals(choice));

	}
}