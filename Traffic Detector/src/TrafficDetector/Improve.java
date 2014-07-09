package TrafficDetector;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * This class is responsible for providing the test-bedded environment to the
 * system It is called every time the user uses the One Stream mode after the
 * decision making
 * 
 * It is a window that asks the user wether the decision was correct or not. If
 * not, the class notifies the training set by providing the record and the
 * correct pattern
 * 
 * @author Panagiotis
 * 
 */

public class Improve extends JFrame implements ActionListener {

	JButton say;
	JButton bye;
	JButton send;
	JLabel thanks;
	JLabel right;
	JPanel panel;
	JLabel mistake;
	JComboBox yesorno;
	JComboBox patterns;
	String[] yesno = { "No", "Yes" };
	String[] mypatterns = { "TOR", "SSH", "SSLWEB", "SSLp2p", "SCP", "SKYPE" };

	String test = "test-final.arff";
	String train = "TRAIN-FULL.arff";

	public Improve() {

		super("Traffic Detector - Testing Service");
		setIconImage(Toolkit.getDefaultToolkit().getImage(
				getClass().getResource("/resources/icon.jpg")));
		setSize(320, 270);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setLocationRelativeTo(null);

		say = new JButton("Confirm");
		bye = new JButton("Bye!");
		send = new JButton("Send Feedback");
		thanks = new JLabel("Great! Thanks for your feedback!");
		right = new JLabel("The correct pattern was:");
		panel = new JPanel();
		mistake = new JLabel("Is this a wrong decision?");
		yesorno = new JComboBox(yesno);
		patterns = new JComboBox(mypatterns);

		panel.setSize(320, 270);
		panel.setLayout(null);

		mistake.setSize(200, 30);
		mistake.setLocation(10, 10);
		panel.add(mistake);

		yesorno.setSize(60, 30);
		yesorno.setLocation(10, 40);
		panel.add(yesorno);

		patterns.setSize(100, 30);
		patterns.setLocation(10, 120);
		patterns.setVisible(false);
		panel.add(patterns);

		say.setSize(90, 30);
		say.setLocation(90, 40);
		say.addActionListener(this);
		panel.add(say);

		thanks.setSize(250, 30);
		thanks.setLocation(50, 170);
		panel.add(thanks);
		thanks.setVisible(false);

		send.setSize(130, 30);
		send.setLocation(130, 120);
		send.setVisible(false);
		send.addActionListener(this);
		panel.add(send);

		right.setSize(160, 30);
		right.setLocation(10, 90);
		right.setVisible(false);
		panel.add(right);

		bye.setSize(130, 30);
		bye.setLocation(75, 200);
		bye.setVisible(false);
		bye.addActionListener(this);
		panel.add(bye);

		add(panel);
		setVisible(true);

	}

	@Override
	public void actionPerformed(ActionEvent event) {

		if (event.getSource().equals(say)) {

			if (yesorno.getSelectedIndex() == 0) {

				say.setEnabled(false);
				yesorno.setEnabled(false);
				thanks.setVisible(true);
				thanks.setLocation(50, 80);
				bye.setLocation(75, 120);
				bye.setVisible(true);
			}
			if (yesorno.getSelectedIndex() == 1) {

				right.setVisible(true);
				send.setVisible(true);
				patterns.setVisible(true);
			}

		}
		if (event.getSource().equals(send)) {

			yesorno.setEnabled(false);
			patterns.setEnabled(false);
			send.setEnabled(false);
			say.setEnabled(false);

			bye.setVisible(true);
			thanks.setVisible(true);
			appendTrainingSet();
		}
		if (event.getSource().equals(bye)) {
			setVisible(false);
			}
	}

	/**
	 * appendTrainingSet() is called each time the classifier makes a wrong
	 * decision it parses the test case .arff file, isolates the features of the
	 * case, and finally adds the record with the correct (specified by the
	 * user) pattern to the training set.
	 * 
	 */

	public void appendTrainingSet() {

		FileReader freader;
		String line = "";
		try {
			freader = new FileReader(test);
			BufferedReader reader = new BufferedReader(freader);

			for (int i = 0; i <= 22; i++) {
				try {
					line = reader.readLine();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		// record isolation with indexOf() and substring()
		int stop = line.indexOf("?");

		String numbers = line.substring(0, stop);

		String record = numbers + patterns.getSelectedItem(); // record to be
																// added =
																// features+correctPattern

		try {
			FileWriter append = new FileWriter(train, true);
			BufferedWriter writer = new BufferedWriter(append);
			writer.write("\n" + record);
			writer.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
