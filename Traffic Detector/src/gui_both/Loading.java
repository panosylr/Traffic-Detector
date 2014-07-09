package gui_both;

import java.awt.Color;
import java.awt.GridLayout;
import java.beans.PropertyChangeEvent;
import java.util.Timer;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JSeparator;

/**
 * A utility class responsible for showing the user that the system is doing
 * something during waiting.
 * 
 * @author Panagiotis
 * 
 */
public class Loading extends JFrame {

	private JProgressBar progress;
	private JLabel label;
	private JLabel td;
	private boolean done;

	public Loading() {

		setSize(300, 200);
		setUndecorated(true);
		setLocationRelativeTo(null);

		progress = new JProgressBar();
		label = new JLabel("Now Loading...", label.CENTER);
		td = new JLabel("TRAFFIC DETECTOR", td.CENTER);
		progress.setIndeterminate(true);
		progress.setMinimum(0);
		progress.setMaximum(100);
		this.td.setSize(150, 30);
		this.td.setLocation(75, 30);
		this.progress.setSize(200, 30);
		this.progress.setLocation(50, 60);
		this.label.setSize(50, 30);
		this.label.setLocation(100, 200);
		add(td);
		add(progress);
		add(label);

		setVisible(true);

		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		Entry entry = new Entry();
		setVisible(false);

	}
}
