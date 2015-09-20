package edu.peer;

import java.io.Serializable;
import java.util.ArrayList;

public class ClientDetails implements Serializable{

	String ipAddress;
	String port;
	ArrayList<String> fileNames;

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public ArrayList<String> getFileNames() {
		return fileNames;
	}

	public void setFileNames(ArrayList<String> fileNames) {
		this.fileNames = fileNames;
	}

}
