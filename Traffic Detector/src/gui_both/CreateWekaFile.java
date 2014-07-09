package gui_both;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
//import java.util.Date;

import org.jnetpcap.Pcap;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;
import org.jnetpcap.protocol.network.Ip4;
import org.jnetpcap.protocol.tcpip.Http;
import org.jnetpcap.protocol.tcpip.Tcp;
import org.jnetpcap.protocol.tcpip.Udp;

/**
 * This class opens a pcap file from a collection of files and using a packet
 * handler it goes into a loop to read all the packets of the current pcap file.
 * It extracts main attributes of the packet and for each packet writes in an
 * output file the corresponding packer attributes.
 * 
 * This class calculates only one stream of traffic per capturing. It is used in
 * the One Stream (Testing mode) of the system.
 * 
 * @author Panagiotis
 * 
 */
public class CreateWekaFile implements Runnable {

	/**
	 * extract method is responsible for parsing values
	 * 
	 */
	public static String extract(String line, int y[]) {

		boolean again = true;
		int st = 0; // index of the first comma (,)
		int th = 0; // index of the next comma (,)
					// these are used for the extraction of a value
					// which is between two commas
		int k = 0;
		String tim = null;
		String ClName = null;
		while (again == true)
		// separating
		{

			int value;

			th = line.indexOf(",", st);
			if (th == -1) {
				again = false;
				tim = line.substring(st, line.length());
				// ClassName=tim; // name of the class of the line
				ClName = tim;

			} else {
				tim = line.substring(st, th);// extract the substring between
												// two commas
				tim = tim.trim(); // trimming the tim
				// timi=(int) Float.parseFloat(tim); // transform as float
				value = Integer.decode(tim); // transform to an integer
				y[k] = value; // store in the y[] which will contain
				st = th + 1;//
			}
			k++;
		} // while again to get token values

		return ClName;
	}

	/**
	 * write_rec method is responsible for writing the results to files
	 * 
	 * @param Fname
	 *            : represents the filename
	 * @param rec
	 *            : represents the record to be written
	 */

	public static void write_rec(String Fname, String rec) {
		try {
			FileWriter results = new FileWriter(Fname, true);
			// Create new File.
			BufferedWriter out = new BufferedWriter(results);
			// Create new BufferedWriter out
			// System.out.println(Fname+" "+ rec);
			out.write(rec);
			out.newLine();
			out.close();
		}

		catch (IOException e) {
			System.out.println("The file cannot be written " + e);
		}

	}

	/**
	 * the following method is responsible for writing the Weka Headers
	 * (attributes) to the output file and prepare the Weka File
	 * 
	 * @param OutFile
	 *            : represents the name of the produced arff file
	 */

	public static void writeWekaHeaders(String OutFile) {
		final String WekaAtrr[] = { "@relation traffic_analysis", " ",
				"@attribute numPackSent integer",
				"@attribute numPackRec integer",
				"@attribute totalPackets integer",
				"@attribute numberAck integer",
				"@attribute headerBytesSent integer",
				"@attribute headerBytesRec integer",
				"@attribute totalHeaderBytes integer",
				"@attribute caplenSent integer",
				"@attribute caplenRec integer",
				"@attribute minPackLength integer",
				"@attribute maxPackLength integer",
				"@attribute avPackLength real", "@attribute avPayload real",
				"@attribute payloadSent integer",
				"@attribute payloadRec integer",
				"@attribute totalPayload integer",
				"@attribute totalSize integer",
				"@attribute class {TOR,SSH,SSLWEB,SSLp2p,SCP,SKYPE}", " ",
				"@data" };

		try {
			FileWriter results = new FileWriter(OutFile); // open for append
			// Create new File.

			BufferedWriter out = new BufferedWriter(results);
			for (int i = 0; i <= 21; i++) {
				out.write(WekaAtrr[i]);
				out.newLine();
			}

			out.close();

		} catch (IOException e) {
			System.out.println(" the file cannot be opened/read");

		}
	}

	/**
	 * the following method is responsible for computing the results in the text
	 * file by calculating element indicators
	 * 
	 * @param InFileName
	 *            : represents the filename to be opened
	 * @param OutFilename
	 *            : represents the filename where the results will be written
	 */

	public static void scanTxtFile(String InFileName, String OutFilename) {
		writeWekaHeaders(OutFilename);
		try {
			int i = 0;
			FileInputStream fstream = new FileInputStream(InFileName); // txt
																		// file
																		// name
																		// for
																		// scanning
																		// features
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			FileWriter results = new FileWriter(OutFilename, true); // open for
																	// append
			// Create new File.
			BufferedWriter out = new BufferedWriter(results);
			// Create new BufferedWriter out

			String line;
			String Record;
			String ClassName = "";
			int numPackSent = 0;
			int numPackRec = 0;
			int totalPackets = 0;

			int minPackLength = 0;
			int maxPackLength = 0;
			float avPackLength = 0;
			float avPayload = 0;

			int totalLength = 0;

			int headerBytesSent = 0;
			int headerBytesRec = 0;
			int totalHeaderBytes = 0;
			int numberAck = 0;
			int payloadSent = 0;
			int payloadRec = 0;
			int totalPayload = 0;

			int totalSize = 0;
			int caplen = 0;
			int inout = 0;
			int ack = 0;
			int istcp = 0;
			int ishttp = 0;
			int isudp = 0;
			int headerLength = 0;
			int payLoadLength = 0;
			int caplenSent = 0;
			int caplenRec = 0;

			int fields[] = new int[10]; // 8 is the number of fields
			while ((line = br.readLine()) != null) {
				i = i + 1;

				ClassName = extract(line, fields);

				istcp = fields[0];
				ishttp = fields[1];
				isudp = fields[2];
				ack = fields[3];
				inout = fields[4];
				totalSize = fields[5];
				payLoadLength = fields[6];
				caplen = fields[7];
				headerLength = fields[8];

				if (istcp == 1 || isudp == 1 || ishttp == 1) // only for
																// tcp/udp/http
																// packets
				{
					if (totalPackets == 1) {
						minPackLength = totalSize;
						maxPackLength = totalSize;

					} else {
						if (totalSize < minPackLength)
							minPackLength = totalSize;

						if (totalSize > maxPackLength)
							maxPackLength = totalSize;
					}

					if (ack == 1)
						numberAck = numberAck + 1;

					if (inout == 0) {
						// outgoing packet
						numPackSent = numPackSent + 1;
						headerBytesSent = headerBytesSent + headerLength;
						payloadSent = payloadSent + payLoadLength;
						caplenSent = caplenSent + caplen;

					} else {
						// incoming packet
						numPackRec = numPackRec + 1;
						headerBytesRec = headerBytesRec + headerLength;
						payloadRec = payloadRec + payLoadLength;
						caplenRec = caplenRec + caplen;

					}

					totalPayload = totalPayload + payLoadLength;
					totalHeaderBytes = totalHeaderBytes + headerLength;
					totalLength = totalLength + totalSize;
					totalPackets = totalPackets + 1;

				}// for tcp

			}

			if (totalPackets > 0) {
				avPackLength = totalLength / totalPackets;
				// avTotalSize=totalLength/totalPackets;
				avPayload = totalPayload / totalPackets;
				Record = numPackSent + "," + numPackRec + "," + totalPackets
						+ "," + numberAck + "," + headerBytesSent + ","
						+ headerBytesRec + "," + totalHeaderBytes + ",";
				Record = Record + caplenSent + "," + caplenRec + ",";
				Record = Record + minPackLength + "," + maxPackLength + ","
						+ avPackLength + "," + avPayload + "," + payloadSent
						+ "," + payloadRec + "," + totalPayload + ","
						+ totalLength + "," + ClassName;

				out.write(Record);
				out.newLine();

			}

			else {
				avPackLength = 0;
			}
			// Record=numPackSent+","+numPackRec+","+totalPackets+","+numberAck+","
			// +headerBytesSent+","+headerBytesRec+ ","+totalHeaderBytes+",";
			// Record=Record+caplenSent+","+caplenRec+",";
			// Record=Record+minPackLength+","+maxPackLength+","+avPackLength+","+avTotalSize+","+avPayload+","+payloadSent+","+payloadRec+","+totalPayload+","+totalSize+","+ClassName;

			// out.write(Record);
			// out.newLine();
			in.close();
			out.close();

		} catch (IOException e) {
			System.out.println(" the file cannot be opened/read");

		}

	}

	/**
	 * The create() method is responsible for pcap parsing. it uses the jNetPcap
	 * API to retrieve element features from the pcap.
	 * 
	 * In addition it uses all the methods described above to provide a flow for
	 * the process of test case creation
	 * 
	 */

	public void create() {

		// First we setup error buffer and name for our file
		final StringBuilder errbuf = new StringBuilder(); // For any error msgs

		final String file = "captured.txt";
		final String pcapFile = "captured.pcap";

		// delete the old txt file by opening it as an output

		try {
			FileWriter results = new FileWriter(file);
			// Create new File.
			BufferedWriter out = new BufferedWriter(results);

			out.close();
		}

		catch (IOException e) {
			System.out.println("The file cannot be opened " + e);
		}

		// Second we open up the selected file using openOffline call
		Pcap pcap = Pcap.openOffline(pcapFile, errbuf);

		if (pcap == null) {
			System.err.printf("Error while opening device for capture: "
					+ errbuf.toString());
			return;
		}

		// Third we create a packet handler which will receive packets from the
		// libpcap loop.
		PcapPacketHandler<String> jpacketHandler = new PcapPacketHandler<String>() {

			public void nextPacket(PcapPacket packet, String user) {

				Tcp tcp = new Tcp();
				Ip4 ip = new Ip4();
				Udp udp = new Udp();

				final Http http = new Http();

				int TotalSize = 0;
				int caplen = 0;
				int ext = 0;
				int ack = 0;
				int istcp = 0;
				int ishttp = 0;
				int isudp = 0;
				int headerLength = 0;
				int PayloadLength = 0;

				TotalSize = packet.getTotalSize();
				caplen = packet.getCaptureHeader().caplen();

				PayloadLength = 0;
				headerLength = 0;

				ext = 0;
				ack = 0;
				// ****************************************************
				if (packet.hasHeader(udp)) // for udp packets
				{
					isudp = 1;
					headerLength = udp.getHeaderLength();
					if (packet.hasHeader(ip)) // check for the existance of ip
					{
						String sourceIP = null; // IP address

						InetAddress ipAddress = null;
						try {
							ipAddress = InetAddress.getByAddress(ip.source()); // get
																				// the
																				// ip
																				// address
						} catch (UnknownHostException e) {
						}

						sourceIP = ipAddress.getHostAddress(); // get the ip
																// address

						if (sourceIP.startsWith("10.239.")
								|| sourceIP.startsWith("192.168.")
								|| sourceIP.startsWith("10.3.")
								|| sourceIP.startsWith("10.4.")) {
							ext = 0; // out-going message
						} else {
							ext = 1; // in-coming message
						}

						ack = 0;

					} // end for header ip

					PayloadLength = udp.getPayloadLength();

				} // end for udp packet.

				else {
					isudp = 0;
				}

				// *******************************************************************
				if (packet.hasHeader(http)) {
					ishttp = 1;
					PayloadLength = http.getPayloadLength();
					if (packet.hasHeader(ip)) //
					{
						String sourceIP = null; //

						InetAddress ipAddress = null;
						try {
							ipAddress = InetAddress.getByAddress(ip.source());
						} catch (UnknownHostException e) {
						}

						sourceIP = ipAddress.getHostAddress(); // get the ip
																// address

						if (sourceIP.startsWith("10.239.")
								|| sourceIP.startsWith("192.168.")
								|| sourceIP.startsWith("10.3.")
								|| sourceIP.startsWith("10.4.")) {
							ext = 0; // internal address
						} else {
							ext = 1;// external
						}

					} // end for header ip
				} else // not http
				{
					ishttp = 0;
				} // end for http

				// **********************************************
				if (packet.hasHeader(tcp)) {
					istcp = 1;
					headerLength = tcp.getHeaderLength();
					if (packet.hasHeader(ip)) // check for existance of ip
												// header
					{
						String sourceIP = null; //

						InetAddress ipAddress = null;
						try {
							ipAddress = InetAddress.getByAddress(ip.source());
						} catch (UnknownHostException e) {
						}

						sourceIP = ipAddress.getHostAddress(); // get the ip
																// address

						if (sourceIP.startsWith("10.239.")
								|| sourceIP.startsWith("192.168.")
								|| sourceIP.startsWith("10.3.")
								|| sourceIP.startsWith("10.4.")) {

							ext = 0; // out-going message
						} else {

							ext = 1; // in-coming message
						}

						if (tcp.flags_ACK() == true)
							ack = 1;
						else
							ack = 0;

					} // end for header ip

					PayloadLength = tcp.getPayloadLength();

				} // end for tcp packet.

				else {
					istcp = 0;
				}

				String rec = istcp + "," + ishttp + "," + isudp + "," + ack
						+ "," + ext + "," + TotalSize + "," + PayloadLength
						+ "," + caplen + "," + headerLength + "," + user;

				write_rec(file, rec);// write raw data in a text file.

			}
		};

		// capturing all the packets of the Pcap file
		try {
			pcap.loop(Pcap.LOOP_INFINITE, jpacketHandler, "?");

		} finally {
			// Last thing to do is close the pcap handle
			pcap.close();
			scanTxtFile(file, "test-final.arff"); // scans the file.txt and
													// creates a weka file
													// (arff)
		}

	} // end for create

	@Override
	public void run() {
		create();

	}
}
