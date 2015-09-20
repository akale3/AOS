package edu.peer;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		String input="";
		ClientImpl clientImpl = new ClientImpl();
		String ipAddress = getCurrentPeerIp();
		clientImpl.registerClient(ipAddress);
		boolean clientWorkingAsServer = false;
		System.out.println("Client is Registered successfully.\nPlease Provide your input.");
		do {
			System.out.println("1. Search a file to download.\n2. Start Client to send files.");
				input= sc.nextLine();
				if ("1".equals(input) || "2".equals(input)
						|| "3".equals(input)) {
					switch(input){
						case "1" :
							clientImpl.searchFileAndDownload();
							break;
						case "2" :
							if(!clientWorkingAsServer){
								Thread t1 = new Thread(new ClientAsServer(ipAddress));
								t1.start();
								clientWorkingAsServer = true;
							}else{
								System.out.println("Client is already up and ready to send files.");
							}
							break;
						case "3" :
							break;
					}
				}else{
					System.out.println("Please enter correct input");
				}
		} while (!"3".equals(input));
	}
	
	private static String getCurrentPeerIp() {
		try {
			return Inet4Address.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return null;
	}
}
