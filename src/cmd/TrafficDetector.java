package cmd;

public class TrafficDetector {
	
	public TrafficDetector(){
		
	}

	public static void main(String[] args) {
		
		FixedIntervalDumper capture = new FixedIntervalDumper();
		capture.collectPcap();
		CreateMultipleStream creator = new CreateMultipleStream();
		creator.create();
		WekaAnalyser weka = new WekaAnalyser();
		weka.trainAndTest();

	}

}
