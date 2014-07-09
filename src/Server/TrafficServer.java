package Server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * This class represents the traffic server of the system. It is used to log the
 * messages coming from the client.
 * 
 * @author Panagiotis
 * 
 */
public class TrafficServer {

	static int port = 50000;
	static ServerSocket serverSocket;
	static Socket clientSocket;
	static BufferedReader input;
	static String logs = "logs.txt";

	public static void startServer() {
		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e3) {
			e3.printStackTrace();
		}

		System.out.println("Traffic Server is listening on port " + port
				+ "...");

		while (true) {

			try {
				clientSocket = serverSocket.accept();
				WorkerThread worker = new WorkerThread(clientSocket);
				Thread t = new Thread(worker);
				t.start();
			} catch (IOException e2) {
				e2.printStackTrace();
			}
			System.out.println("");
			System.out.println("Accepted connection from client");

		}
	}

	public static void main(String[] args) {

		startServer();
	}

}