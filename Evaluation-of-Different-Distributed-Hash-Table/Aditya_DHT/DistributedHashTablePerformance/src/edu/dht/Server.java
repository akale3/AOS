package edu.dht;

public class Server {
	public static void main(String[] args) {
		
		Thread clientServerThread = new Thread(new ClientServer());
		clientServerThread.start();
		
	}
}
