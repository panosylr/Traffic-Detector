package TrafficDetector;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

public class Entry extends JFrame implements ActionListener {

	JPanel row0 = new JPanel();
	JEditorPane title = new JEditorPane(
			"text/html",
			"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
					+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; "
					+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
					+ "<font face=Georgia size = 10 color=#FAFAD2>"
					+ "<b>TRAFFIC   &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;  DETECTOR</b></font>");

	JPanel row1 = new JPanel();
	ImageIcon image1 = new ImageIcon(getClass().getResource(
			"/resources/msblast.jpg"));
	JLabel label1 = new JLabel(image1);

	JPanel row2 = new JPanel();
	JButton enter = new JButton("Use Traffic Detector");
	JButton cancel = new JButton("Cancel");

	JMenuBar menubar = new JMenuBar();
	JMenu file = new JMenu("File");
	JMenu help = new JMenu("Help");

	JMenuItem about = new JMenuItem("About");
	JMenuItem close = new JMenuItem("Exit");
	

	public Entry() {
		super("Traffic Detector - Real time encrypted traffic classifier");
		setIconImage(Toolkit.getDefaultToolkit().getImage(
				getClass().getResource("/resources/icon.jpg")));
		setSize(430, 360);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		GridLayout layout = new GridLayout(3, 1, 10, 0);
		setLayout(layout);

		setJMenuBar(menubar);

		menubar.add(file);
		menubar.add(help);
		help.add(about);
		file.add(close);

		close.addActionListener(this);
		about.addActionListener(this);

		setVisible(true);

		BorderLayout layout3 = new BorderLayout();
		row0.setLayout(layout3);
		title.setEditable(false);
		row0.add(title, BorderLayout.SOUTH);
		Color bg = new Color(0, 0, 128);
		title.setBackground(bg);
		add(row0);

		FlowLayout layout0 = new FlowLayout(FlowLayout.CENTER, 10, 10);
		row1.setLayout(layout0);
		row1.add(label1);
		add(row1);

		FlowLayout layout1 = new FlowLayout(FlowLayout.CENTER, 10, 10);
		row2.setLayout(layout1);
		row2.add(enter);
		row2.add(cancel);
		enter.addActionListener(this);
		cancel.addActionListener(this);
		add(row2);

		setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent event) {

		if (event.getSource().equals(enter)) {
			setVisible(false);
			MainWindow form = new MainWindow();

		} else if (event.getSource().equals(cancel)) {
			System.exit(0);
		} else if (event.getSource().equals(close)) {
			System.exit(0);
		} else if (event.getSource().equals(about)) {
			About about = new About();
		}

	}
}
