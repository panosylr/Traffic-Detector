package gui_both;

/**
 * This class is used to help us overcome GUI difficulties for multithreading.
 * UtilityThreadMultiple is called in Multiple Stream Mode. After the results,
 * it calls the Client/Server communication to notify the server about the
 * results.
 * 
 * @author Panagiotis
 * 
 */
public class UtilityThreadMultiple extends Thread {

	int number;
	FixedIntervalDumper dumper;
	WekaAnalyser weka;
	CreateWekaFileMultiple creator;
	SendInfo send;

	public UtilityThreadMultiple(int number) {
		this.number = number;
		dumper = new FixedIntervalDumper();
		creator = new CreateWekaFileMultiple();
		send = new SendInfo();
		weka = new WekaAnalyser();
	}

	public String getUID() {
		String username = System.getProperty("user.name");
		return username;
	}

	@Override
	public void run() {

		dumper.collectPcap(number);
		MainWindow.results
				.append("End of packet capturer. Sample encrypted traffic is now available!\n");
		MainWindow.results.append("Opening file for reading: captured.pcap\n");
		MainWindow.results.append("\n");
		creator.create();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		MainWindow.results
				.append("Features are extracted! Test dataset is created..\n");
		MainWindow.results
				.append("The analyser is running now. The decision will be available soon..\n");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		weka.trainAndTest();
		MainWindow.results.append("\n");
		weka.provideMultipleDecision();
		send.sendToServer();
	}

}
