package TrafficDetector;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to help us overcome GUI difficulties for multithreading.
 * UtilityThread is called in One Stream (Testing Mode).After the results, it
 * calls the test-bedded environment of the system.
 * 
 * @author Panagiotis
 * 
 */
public class UtilityThread extends Thread {

	int number;
	FixedIntervalDumper dumper;
	WekaAnalyser weka;
	CreateWekaFile creator;
	Improve improve;
	String result;

	public UtilityThread(int number) {
		this.number = number;
		dumper = new FixedIntervalDumper();
		creator = new CreateWekaFile();
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
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		weka.trainAndTest();
		MainWindow.results.append("\n");
		MainWindow.results.append("The user " + getUID() + " initiated "
				+ weka.provideSingleDecision() + " traffic");

		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		improve = new Improve();
	}

}
