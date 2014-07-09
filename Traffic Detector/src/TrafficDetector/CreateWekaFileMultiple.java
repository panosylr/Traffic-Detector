package TrafficDetector;

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
import org.jnetpcap.packet.format.FormatUtils;
import org.jnetpcap.protocol.network.Ip4;
import org.jnetpcap.protocol.tcpip.Http;
import org.jnetpcap.protocol.tcpip.Tcp;
import org.jnetpcap.protocol.tcpip.Udp;

/**
 * This class is responsible for test case creation. It similar to the
 * CreateWekaFile. The main difference is that this class calculates different
 * TCP/UDP streams in order to be handled differently from the classifier.
 * 
 * This class is used in the Multiple Stream Mode.
 * 
 * added in-line comments for the part of multiple stream computation
 * 
 * @author Panagiotis
 * 
 */
public class CreateWekaFileMultiple {

	/**
	 * Main startup method
	 * 
	 * @param args
	 *            ignored
	 * 
	 * 
	 */

	static void write_rec(String Fname, String rec) {
		// it writes in a text file a record formed from the initial data of a
		// packet
		try {
			FileWriter results = new FileWriter(Fname, true);
			// Create new File.
			BufferedWriter out = new BufferedWriter(results);
			// Create new BufferedWriter out

			out.write(rec);
			out.newLine();
			out.close();
		}

		catch (IOException e) {
			System.out.println("The file cannot be written " + e);
		}

	}

	static int puship(String X[], String Y[], String ips, String ipd,
			int numStreams) {
		// it checks if the pair (ips,ipd) has already stored in the arrays X, Y
		// if not create a new entry
		// it returns the number of streams detected

		int th;
		boolean found = false;
		int j;
		th = numStreams;
		for (j = 1; j <= numStreams; j++) {
			if ((ips.equals(X[j]) && ipd.equals(Y[j]))
					|| (ipd.equals(X[j]) && ips.equals(Y[j])))
				found = true;
		}

		if (found == false) {
			th = numStreams + 1;
			X[th] = ips;
			Y[th] = ipd;
			return th;
		} else
			return th;
	}

	static int findPos(String X[], String Y[], String ips, String ipd,
			int numStreams) {
		// it finds the position of a pair (ips,ipd) or (ipd,ips) in the arrays
		// X,Y
		// a pair like (123.456.789.12, 100.212.10.3) is considered identical
		// with
		// (100.212.10.3, 123.456.789.12 ) as denoting the same stream
		int th;
		boolean found;
		int j;
		th = numStreams;
		found = false;
		for (j = 1; j <= numStreams; j++) {
			if ((ips.equals(X[j]) && ipd.equals(Y[j]))
					|| (ipd.equals(X[j]) && ips.equals(Y[j]))) {
				found = true;
				th = j;
			}
		}

		if (found == false)
			return 0;
		else
			return th;

	}

	static void writeWekaHeaders(String OutFile) {
		final String WekaAtrr[] = { "@relation traffic_analysis", " ",
				"@attribute numPackSent numeric",
				"@attribute numPackRec numeric",
				"@attribute totalPackets numeric",
				"@attribute numberAck numeric",
				"@attribute headerBytesSent numeric",
				"@attribute headerBytesRec numeric",
				"@attribute totalHeaderBytes numeric",
				"@attribute caplenSent numeric",
				"@attribute caplenRec numeric",
				"@attribute minPackLength numeric",
				"@attribute maxPackLength numeric",
				"@attribute avPackLength numeric",
				"@attribute avPayload numeric",
				"@attribute payloadSent numeric",
				"@attribute payloadRec numeric",
				"@attribute totalPayload numeric",
				"@attribute totalSize numeric",
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

	static void scanTxtFile(String InFileName, String OutFilename) {
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
			int sums[][] = new int[100][20]; // array for the totals per stream
												// (100 streams)
			String SourceIp[] = new String[100]; // array with the source IPs
			String DestinIp[] = new String[100]; // array with the destination
													// IPs
			int numOfStreams = 0; // number of streams detected
			int ipPos;
			int fields[] = new int[10]; // 10 is the number of fields

			while ((line = br.readLine()) != null) {
				i++;

				String IpS; // Source IP
				String IpD; // Destination IP

				String XX[] = line.split(",");
				IpS = XX[9].trim();
				IpD = XX[10].trim();
				ClassName = XX[11];
				for (int n = 0; n <= 8; n++)
					fields[n] = Integer.decode(XX[n]);

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
					if ((IpS.equals("") == false) && (IpD.equals("") == false)) // we
																				// ignore
																				// the
																				// packets
																				// without
																				// IPs
					{
						numOfStreams = puship(SourceIp, DestinIp, IpS, IpD,
								numOfStreams); // check for a new stream
												// for the pair
												// SourceIp, DestinIp

						ipPos = findPos(SourceIp, DestinIp, IpS, IpD,
								numOfStreams);
						if (ipPos > 0) { // creating total per stream
							sums[ipPos][0] = sums[ipPos][0] + 1;
							if (sums[ipPos][0] == 1) // the first packet of the
														// stream
							{
								sums[ipPos][10] = totalSize; // minimum packet
																// length
								sums[ipPos][11] = totalSize; // maximum packet
																// length
							} else { // storing the min & max total size
								if (totalSize < sums[ipPos][10])
									sums[ipPos][10] = totalSize; // new minimum
																	// packet
																	// length
								if (totalSize > sums[ipPos][10])
									sums[ipPos][11] = totalSize; // new maximum
																	// packet
																	// length
							}

							if (ack == 1)
								sums[ipPos][4] = sums[ipPos][4] + 1;

							if (inout == 0) {
								// outgoing packet
								sums[ipPos][1] = sums[ipPos][1] + 1; // counter
																		// of
																		// outgoing
																		// packets
								sums[ipPos][5] = sums[ipPos][5] + headerLength;
								sums[ipPos][14] = sums[ipPos][14]
										+ payLoadLength;
								sums[ipPos][8] = sums[ipPos][8] + caplen;

								// numPackSent=numPackSent+1;
								// headerBytesSent=headerBytesSent+headerLength;
								// payloadSent=payloadSent+payLoadLength;
								// caplenSent=caplenSent+caplen;

							} else {
								// incoming packet

								sums[ipPos][2] = sums[ipPos][2] + 1;// counter
																	// of
																	// incoming
																	// packets
								sums[ipPos][6] = sums[ipPos][6] + headerLength;
								sums[ipPos][15] = sums[ipPos][15]
										+ payLoadLength;
								sums[ipPos][9] = sums[ipPos][9] + caplen;

								// numPackRec=numPackRec+1;
								// headerBytesRec=headerBytesRec+headerLength;
								// payloadRec=payloadRec+payLoadLength;
								// caplenRec=caplenRec+caplen;

							}

						}

						sums[ipPos][0] = sums[ipPos][0] + 1;
						sums[ipPos][3] = sums[ipPos][3] + 1;// total packets
						sums[ipPos][7] = sums[ipPos][7] + headerLength;// total
																		// headerbytes
						sums[ipPos][16] = sums[ipPos][16] + payLoadLength; // total
																			// payload
						sums[ipPos][17] = sums[ipPos][17] + totalSize; // total
																		// size

						if (sums[ipPos][3] > 0) {

						}

					} // ip non blank

					// totalPayload=totalPayload+payLoadLength;
					// totalHeaderBytes=totalHeaderBytes+headerLength;
					// totalLength=totalLength+totalSize;
					// totalPackets=totalPackets+1;

				} // only for tcp/udp/http packets

			}
			for (int j = 1; j <= numOfStreams; j++) // for each stream
			{
				numPackSent = sums[j][1];
				numPackRec = sums[j][2];
				totalPackets = sums[j][3];
				numberAck = sums[j][4];
				headerBytesSent = sums[j][5];
				headerBytesRec = sums[j][6];
				totalHeaderBytes = sums[j][7];
				caplenSent = sums[j][8];
				caplenRec = sums[j][9];
				minPackLength = sums[j][10];
				maxPackLength = sums[j][11];
				avPackLength = sums[j][12];
				avPayload = sums[j][13];
				payloadSent = sums[j][14];
				payloadRec = sums[j][15];
				totalPayload = sums[j][16];
				totalLength = sums[j][17];

				final int minNumPack = 30; // minimum number of packet of the
											// detected stream
				if (totalPackets >= minNumPack) {
					avPackLength = totalLength / totalPackets;
					avPayload = totalPayload / totalPackets;
					Record = numPackSent + "," + numPackRec + ","
							+ totalPackets + "," + numberAck + ","
							+ headerBytesSent + "," + headerBytesRec + ","
							+ totalHeaderBytes + ",";
					Record = Record + caplenSent + "," + caplenRec + ",";
					Record = Record + minPackLength + "," + maxPackLength + ","
							+ avPackLength + "," + avPayload + ","
							+ payloadSent + "," + payloadRec + ","
							+ totalPayload + "," + totalLength + ","
							+ ClassName;

					// for each detected possible stream write a line in the
					// arff file.
					out.write(Record);
					out.newLine();

				}
			} // end for each stream

			in.close();
			out.close();

		} catch (IOException e) {
			System.out.println(" the file cannot be opened/read");

		}

	}

	public void create() {

		/***************************************************************************
		 * First we setup error buffer and name for our file
		 **************************************************************************/
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

		//

		System.out.println("Opening file for reading:" + pcapFile);
		/***************************************************************************
		 * Second we open up the selected file using openOffline call
		 **************************************************************************/
		Pcap pcap = Pcap.openOffline(pcapFile, errbuf);

		if (pcap == null) {
			System.err.printf("Error while opening device for capture: "
					+ errbuf.toString());
			return;
		}

		/***************************************************************************
		 * Third we create a packet handler which will receive packets from the
		 * libpcap loop.
		 **************************************************************************/
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

				// System.out.println(packet.getTotalSize());//costas
				// System.out.printf( " total size %-6d caplen= %6d  %6d  ",
				// packet.getTotalSize(),
				// packet.getCaptureHeader().caplen(),packet.getTotalSize()-
				// packet.getCaptureHeader().caplen());//costas

				TotalSize = packet.getTotalSize();
				caplen = packet.getCaptureHeader().caplen();

				PayloadLength = 0;
				headerLength = 0;

				ext = 0;
				ack = 0;
				// ****************************************************
				String ssourceIp = "";
				String destinIp = "";
				String sourceIp = "";
				if (packet.hasHeader(ip)) {
					sourceIp = FormatUtils.ip(ip.source());
					destinIp = FormatUtils.ip(ip.destination());
				}

				if (packet.hasHeader(udp)) // for udp packets
				{
					isudp = 1;
					headerLength = udp.getHeaderLength();
					if (packet.hasHeader(ip)) // check for the existance of ip
					{

						InetAddress ipAddress = null;

						try {
							ipAddress = InetAddress.getByAddress(ip.source()); // get
																				// the
																				// ip
																				// address
							// ipAddress=
							// InetAddress.getByAddress(ip.destination());
						} catch (UnknownHostException e) {
						}

						ssourceIp = ipAddress.getHostAddress(); // get the ip
																// address

						if (sourceIp.startsWith("10.3.")
								|| sourceIp.startsWith("192.168.")
								|| sourceIp.startsWith("10.239.")
								|| sourceIp.startsWith("10.4.")) {
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

						InetAddress ipAddress = null;
						try {
							ipAddress = InetAddress.getByAddress(ip.source());
						} catch (UnknownHostException e) {
						}

						ssourceIp = ipAddress.getHostAddress(); // get the ip
																// address

						if (sourceIp.startsWith("10.3.")
								|| sourceIp.startsWith("192.168.")
								|| sourceIp.startsWith("10.239.")
								|| sourceIp.startsWith("10.4.")) {
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

						InetAddress ipAddress = null;
						try {
							ipAddress = InetAddress.getByAddress(ip.source());
						} catch (UnknownHostException e) {
						}

						ssourceIp = ipAddress.getHostAddress(); // get the ip
																// address

						if (sourceIp.startsWith("10.3.")
								|| sourceIp.startsWith("192.168.")
								|| sourceIp.startsWith("10.239.")
								|| sourceIp.startsWith("10.4.")) {

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
						+ "," + caplen + "," + headerLength + "," + sourceIp
						+ "," + destinIp + "," + user;
				write_rec(file, rec);// write raw data in a text file.

			}
		};

		// capturing all the packets of the Pcap file
		try {
			pcap.loop(Pcap.LOOP_INFINITE, jpacketHandler, "?");
		} finally {

			// Last thing to do is close the pcap handle
			pcap.close();
			System.out.println("End of packet capturing..Pcap file closed");
			System.out.println("Now the analyser is running...");
			scanTxtFile(file, "test-final.arff"); // scans the file.txt and
													// creates a weka file
													// (arff)
		}

	} // end of main
} // end of class
