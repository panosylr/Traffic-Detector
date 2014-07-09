package gui_both;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;

import org.jnetpcap.ByteBufferHandler;
import org.jnetpcap.JBufferHandler;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapDumper;
import org.jnetpcap.PcapHandler;
import org.jnetpcap.PcapHeader;
import org.jnetpcap.PcapIf;
import org.jnetpcap.nio.JBuffer;
import org.jnetpcap.packet.PcapPacketHandler;
import java.util.Date;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.protocol.JProtocol;

/**
 * This class uses libpcap library to capture live packets and dump them to a
 * file. packets are captured for a certain amount of time and dumped to the
 * file After the time interval expires pcap closes
 * 
 * @author Panagiotis
 * 
 */
public class FixedIntervalDumper implements Runnable {

	public static final int CAPTURE_INTERVAL = 20 * 1000; // fixed seconds
															// interval
	String outputFilename = "captured.pcap";
	PcapDumper dumper;
	BufferedWriter writer;
	FileWriter fstream;
	int k;

	/**
	 * storeDevices() stores a list of the network interfaces to a file, in
	 * order to be made available to the user in the graphical user interface.
	 * 
	 */

	public void storeDevices() {
		List<PcapIf> alldevs = new ArrayList<PcapIf>(); // Will be filled with
														// NICs
		StringBuilder errbuf = new StringBuilder(); // For any error msgs

		// First get a list of devices on this system
		int r = Pcap.findAllDevs(alldevs, errbuf);
		if (r == Pcap.NOT_OK || alldevs.isEmpty()) {
			System.err.printf("Can't read list of devices, error is %s",
					errbuf.toString());
			return;
		}

		int i = 0;

		try {
			fstream = new FileWriter("mydevices.txt", false);
			writer = new BufferedWriter(fstream);
		} catch (IOException e) {
			e.printStackTrace();
		}

		for (PcapIf device : alldevs) {
			String description = (device.getDescription() != null) ? device
					.getDescription() : "No description available";

			try {
				writer.write(i + " " + device.getName().toString() + " "
						+ description.toString() + "\n");
				i++;
				writer.flush();

			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	/**
	 * collectPcap() allows the capturer to start, capture and dump packets for
	 * the fixed interval that was defined before
	 * 
	 * 
	 * @param k
	 *            : represents the network interface specified by the user
	 */

	public void collectPcap(int k) {
		List<PcapIf> alldevs = new ArrayList<PcapIf>(); // Will be filled with
														// NICs
		StringBuilder errbuf = new StringBuilder(); // For any error msgs

		// First get a list of devices on this system
		int r = Pcap.findAllDevs(alldevs, errbuf);
		if (r == Pcap.NOT_OK || alldevs.isEmpty()) {
			System.err.printf("Can't read list of devices, error is %s",
					errbuf.toString());
			return;
		}

		for (PcapIf device : alldevs) {
			String description = (device.getDescription() != null) ? device
					.getDescription() : "No description available";

		}

		PcapIf device = (PcapIf) alldevs.get(k); // We know we have at least 1
													// device

		// Second we open up the selected device
		int snaplen = 64 * 1024; // Capture all packets, no truncation
		int flags = Pcap.MODE_PROMISCUOUS; // capture all packets
		int timeout = 10 * 1000; // No timeout, non-interactive traffic
		Pcap pcap = Pcap.openLive(device.getName(), snaplen, flags, timeout,
				errbuf);
		if (pcap == null) {
			System.err.printf("Error while opening device for capture: "
					+ errbuf.toString());
			return;
		}

		final long interval = System.currentTimeMillis() + CAPTURE_INTERVAL;

		// third we create a packet handler which receive packets and break the
		// pcap to end capture after predefined time interval

		PcapPacketHandler<Pcap> jpacketHandler = new PcapPacketHandler<Pcap>() {

			@Override
			public void nextPacket(PcapPacket packet, Pcap pcap) {

				dumper.dump(packet);

				if (System.currentTimeMillis() > interval) {
					dumper.close();
					pcap.breakloop();
					pcap.close();

				}

			}
		};

		// Fourth we open the dump file and enter the loop.
		dumper = pcap.dumpOpen(outputFilename);
		pcap.loop(Pcap.LOOP_INFINITE, JProtocol.IP4_ID, jpacketHandler, pcap);

	}

	public String getEnd() {
		return ("End of Capturing.. Captured.pcap is available");
	}

	@Override
	public void run() {
		collectPcap(k);
	}

}