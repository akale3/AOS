package edu.distributedFileSystem;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class SendFiles extends Thread {

	public static final int FILE_SIZE = 1024000;
	Socket socket;
	String path;

	public SendFiles(Socket socket, String path) {
		this.socket = socket;
		this.path = path;
	}

	@Override
	public void run() {
		FileInputStream fileInputStream = null;
		BufferedInputStream bufferedInputStream = null;
		OutputStream outputStream = null;
		try {
			BufferedReader inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			//System.out.println("Accepted connection : " + socket);
			String fileName = inputStream.readLine();
			//System.out.println("Requested file name :" + fileName);
			File myFile = new File(this.path + "/" + fileName);
			byte[] mybytearray = new byte[FILE_SIZE];
			fileInputStream = new FileInputStream(myFile);
			bufferedInputStream = new BufferedInputStream(fileInputStream);
			long size = myFile.length();
			int bytesRead;
			outputStream = socket.getOutputStream();
			while (size > 0 && (bytesRead = bufferedInputStream.read(mybytearray, 0,
					(int) Math.min(mybytearray.length, size))) != -1) {
				outputStream.write(mybytearray, 0, bytesRead);
				size -= bytesRead;
			}
			outputStream.flush();
			//System.out.println("Sending " + fileName + "...");
			//System.out.println("File Send Successfully.");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != fileInputStream) {
					fileInputStream.close();
				}
				if (bufferedInputStream != null)
					bufferedInputStream.close();
				if (outputStream != null)
					outputStream.close();
				if (socket != null)
					socket.close();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
