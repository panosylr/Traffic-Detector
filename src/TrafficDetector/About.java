package TrafficDetector;

import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * A class that provides information about the version and the author of the
 * tool
 * 
 * @author Panagiotis
 */

public class About extends JFrame implements ActionListener {

	JPanel row1 = new JPanel();
	JLabel version = new JLabel("Traffic Detector - Version 0.1 ");

	JPanel row2 = new JPanel();
	JLabel info = new JLabel("Developer: Panagiotis Gialouris ");

	JPanel row3 = new JPanel();
	JLabel school = new JLabel(
			"University of Birmingham - M.Sc. Computer Security");

	JPanel row4 = new JPanel();
	JButton ok = new JButton("OK");

	public About() {

		super("Traffic Detector - About ");
		setIconImage(Toolkit.getDefaultToolkit().getImage(
				getClass().getResource("/resources/icon.jpg")));
		setSize(320, 190);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		FlowLayout layout = new FlowLayout(FlowLayout.CENTER, 10, 10);
		setLayout(layout);

		row1.add(version);
		add(row1);
		row2.add(info);
		add(row2);
		row3.add(school);
		add(row3);
		row4.add(ok);
		ok.addActionListener(this);
		add(row4);

		setVisible(true);

	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		setVisible(false);
	}

}
