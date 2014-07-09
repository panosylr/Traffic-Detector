package TrafficDetector;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is responsible for informing the traffic server with the results
 * of the client
 * 
 * @author Panagiotis
 * 
 */

public class SendInfo {

	static Socket socket;
	static OutputStream output;
	static List<String> toSend;
	static DataOutputStream dos;
	static String result;
	static String serverAddress = "192.168.56.101";
	static int port = 50000;
	static String localhost = "localhost";

	public SendInfo() {

	}

	/**
	 * getResults() is responsible for returning a list with the results after
	 * the classification process
	 * 
	 */

	public static List<String> getResults() {

		List<String> tokens = new ArrayList<String>();
		tokens.add("TOR");
		tokens.add("SSLWEB");
		tokens.add("SSLp2p");
		tokens.add("SSH");
		tokens.add("SCP");
		tokens.add("SKYPE");

		List<String> sendMe = new ArrayList<String>();

		try {
			BufferedReader reader = new BufferedReader(new FileReader(
					"labeled.arff"));
			try {
				String line = "";
				String[] lineArray = new String[17];
				String myclass = "";
				int streamCounter = 0;
				for (int i = 0; i < 100; i++) {

					line = reader.readLine();

					if ((line != null) && (i >= 22)) {
						lineArray = line.split(",");
						myclass = lineArray[17];

						for (String s : tokens) {

							Boolean found = myclass.equals(s);
							if ((found)) {
								streamCounter++;

								result = ("Stream " + streamCounter
										+ ": The user "
										+ System.getProperty("user.name")
										+ " initiated " + s + " traffic.\n");

								sendMe.add(result);

							}

							if (s.equals("SKYPE") && (found = false)) {
								System.out.println("No pattern found");
							}

						}

					}
				}

			} catch (IOException e) {
				e.printStackTrace();
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		return (sendMe);

	}

	/**
	 * sendToServer initiates a TCP connection with the traffic server based on
	 * sockets. the method is able to send the list retrieved ealier to the
	 * server.
	 * 
	 */

	public void sendToServer() {
		try {

			socket = new Socket(serverAddress, port);

			toSend = new ArrayList<String>();

			toSend = getResults();

			output = socket.getOutputStream();
			dos = new DataOutputStream(output);

			for (String content : toSend) {

				dos.writeBytes(content);

			}

			dos.close();
			output.close();
			socket.close();

		} catch (UnknownHostException e) {
			e.printStackTrace();
			CommunicationAlert problem = new CommunicationAlert();
		} catch (IOException e) {
			CommunicationAlert alert = new CommunicationAlert();
			e.printStackTrace();
		}

	}
	/**
	 * Method used to send the results to a localhost server
	 * in a case of problematic connectivity
	 * 
	 */
	
	public void sendToLocalhost() {
		try {

			socket = new Socket(localhost, port);

			toSend = new ArrayList<String>();

			toSend = getResults();

			output = socket.getOutputStream();
			dos = new DataOutputStream(output);

			for (String content : toSend) {

				dos.writeBytes(content);

			}

			dos.close();
			output.close();
			socket.close();

		} catch (UnknownHostException e) {
			e.printStackTrace();
			CommunicationAlert problem = new CommunicationAlert();
		} catch (IOException e) {
			CommunicationAlert alert = new CommunicationAlert();
			e.printStackTrace();
		}

	}

}
