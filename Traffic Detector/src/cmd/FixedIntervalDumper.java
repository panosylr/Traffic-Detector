package cmd;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.jnetpcap.Pcap;
import org.jnetpcap.PcapDumper;
import org.jnetpcap.PcapIf;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;
import org.jnetpcap.protocol.JProtocol;

/**
 * This example uses pcap library to capture live packets and dump them to
 * console.Similar program can be written to dump the packet to a file. Packets
 * are captured for a certain amount of time and dumped to console. After the
 * time interval expires pcap closes
 * 
 * @author Panagiotis
 * 
 */
public class FixedIntervalDumper implements Runnable{

	public final int CAPTURE_INTERVAL = 20 * 1000; // 10 seconds interval
	String outputFilename = "captured.pcap";
	PcapDumper dumper;

	
	
	public void collectPcap() {
		List<PcapIf>alldevs = new ArrayList<PcapIf>(); // Will be filled with NICs
		StringBuilder errbuf = new StringBuilder(); // For any error msgs

		/***************************************************************************
		 * First get a list of devices on this system
		 **************************************************************************/
		int r = Pcap.findAllDevs(alldevs, errbuf);
		if (r == Pcap.NOT_OK || alldevs.isEmpty()) {
			System.err.printf("Can't read list of devices, error is %s",
					errbuf.toString());
			return;
		}
		
		int i = 0; 
        System.out.println("Network devices found:");
        System.out.println("");
  
       
        for (PcapIf device : alldevs) {  
            String description =  
                (device.getDescription() != null) ? device.getDescription()  
                    : "No description available";  
            System.out.printf("#%d: %s [%s]\n", i++, device.getName(), description);  
        } 
        
        System.out.println("");
        System.out.print("Pick one interface (starting from #0): ");
        Scanner in = new Scanner(System.in);
        i = in.nextInt();  
		PcapIf device = (PcapIf) alldevs.get(i); // We know we have at least 1 device

		/***************************************************************************
		 * Second we open up the selected device
		 **************************************************************************/
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
		
		System.out.println("");
		System.out.printf("Now is dumping packets for " + (CAPTURE_INTERVAL / 1000)
				+ " Secs\n");

		/***************************************************************************
		 * third we create a packet handler which receive packets and break the
		 * pcap to end capture after predefined time interval
		 **************************************************************************/
		

		PcapPacketHandler<Pcap> jpacketHandler = new PcapPacketHandler<Pcap>() {

			@Override
			public void nextPacket(PcapPacket packet, Pcap pcap) {
				
			dumper.dump(packet);

				if (System.currentTimeMillis() > interval) {
					dumper.close();
					pcap.breakloop();
					pcap.close();
					System.out.println("pcap file collected and ready for analysis..");
					System.out.println("");

				}

			}
		};

		/***************************************************************************
		 * Fourth we open the dump file and enter the loop.
		 **************************************************************************/
		dumper = pcap.dumpOpen(outputFilename);
		pcap.loop(Pcap.LOOP_INFINITE, JProtocol.IP4_ID, jpacketHandler, pcap);
	

	}



	@Override
	public void run() {
		collectPcap();
	}


}