package edu.riak;

import java.util.Scanner;
import java.util.concurrent.ExecutionException;

public class RiakEvaluation {

	public static void main(String[] args) throws ExecutionException, InterruptedException {

		Scanner scanner = new Scanner(System.in);
		String choice = null;
		Raik raikImpl = new Raik();
		
		raikImpl.createRandomKeys();
		raikImpl.initializeLogger();
		raikImpl.initializeServer();
		
		do {
			System.out.println("********************************************************************");
			System.out.println("Enter Your Choice : ");
			System.out.println("1.Put Element In DHT.\n2.Get Element From DHT.\n3.Delete Element From DHT.\n4.Exit");
			choice = scanner.nextLine();
			if ("1".equalsIgnoreCase(choice) || "2".equalsIgnoreCase(choice) || "3".equalsIgnoreCase(choice)) {
				switch (choice) {
				case "1":
					raikImpl.put();
					break;
				case "2":
					raikImpl.get();
					break;
				case "3":
					raikImpl.delete();
					break;
				}
			} else if (!"4".equals(choice)) {
				System.out.println("Please Enter Correct Option.");
			}
		} while (!"4".equals(choice));
	
	}
}