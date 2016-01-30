package edu.filegeneration;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class FileGeneration {

	public static void main(String[] args) {
		BufferedWriter bufferedWriter = null;
		long startTime = System.currentTimeMillis();
		for (int i = 1; i < 10000; i++) {
			String fileData = createData(10240);
			String fileName = "File_1#" + i + ".txt";
			try {
				bufferedWriter = new BufferedWriter(new FileWriter("ClientData/ClientData1/" + fileName));
				bufferedWriter.write(fileData);
			} catch (IOException e) {
			} finally {
				try {
					if (null != bufferedWriter) {
						bufferedWriter.close();
					}
				} catch (IOException e) {
				}
			}
		}
		long endTime = System.currentTimeMillis();
		System.out.println("Total Time For generation : " + (endTime - startTime) + "millseconds");
	}

	private static String createData(int size) {
		StringBuilder stringBuilder = new StringBuilder(size);
		for (int i = 0; i < size; i++) {
			stringBuilder.append('a');
		}
		return stringBuilder.toString();
	}
}
