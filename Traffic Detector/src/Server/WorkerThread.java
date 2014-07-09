package Server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * This class represents the work that should be done by the server in order to
 * support multithreading client handling and receive multiple connections in
 * parallel.
 * 
 * @author Panagiotis
 * 
 */
public class WorkerThread implements Runnable {
	BufferedReader input;
	Socket clientSocket;
	String logs = "logs.txt";
	long mydate;

	public WorkerThread(Socket clientSocket) {
		this.clientSocket = clientSocket;

	}

	/**
	 * work() method is the piece of code that will be put in the run() in order
	 * to support multithreading for logging procedures and request handling.
	 * 
	 */

	public void work() {

		try {
			input = new BufferedReader(new InputStreamReader(
					clientSocket.getInputStream()));
		} catch (IOException e2) {
			e2.printStackTrace();
		}

		String s;
		try {
			while ((s = input.readLine()) != null) {
				System.out.println("Client sent: " + s);

				Date date = new Date();
				FileWriter append = new FileWriter(logs, true);
				BufferedWriter writer = new BufferedWriter(append);
				writer.write(s + "       || Date and Time: " + date.toString()
						+ "\n");
				writer.close();

			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		System.out.println("Ended connection with client");
		System.out.println("");

		try {
			input.close();
			clientSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {

		work();
	}
}
