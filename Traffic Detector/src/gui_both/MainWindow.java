package gui_both;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 * This class represents the main window of the system. In this window the user
 * is able to find the available interfaces of the machine, pick one and start
 * capturing and classification. Finally this form notifies the user about the
 * results
 * 
 * @author Panagiotis
 * 
 */
public class MainWindow extends JFrame implements ActionListener, Runnable {

	private JButton start = new JButton("Find the available interfaces");
	private JLabel devicesFound = new JLabel("", JLabel.RIGHT);
	private JLabel sel = new JLabel("      Selected Interface: ");

	private JList devices = new JList();
	private DefaultListModel listModel = new DefaultListModel();

	private JScrollPane scroll1 = new JScrollPane(devices,
			JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
			JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

	private JEditorPane selection = new JEditorPane("text/html", "");

	private JButton inter = new JButton("Pick Interface");
	String[] streams = { "One Stream (Testing Mode)",
			"Multiple Streams (No Test)" };
	JComboBox box = new JComboBox(streams);

	public static JTextArea results = new JTextArea();
	JScrollPane scroll = new JScrollPane(results,
			JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
			JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

	private JLabel help2 = new JLabel(" ");
	private JButton confirm = new JButton("Start Capturing and Classification");
	private JButton back = new JButton("Back to Menu");

	private JMenuBar menubar = new JMenuBar();
	private JMenu file = new JMenu("File");
	private JMenu help = new JMenu("Help");

	private JMenuItem about = new JMenuItem("About");
	private JMenuItem close = new JMenuItem("Exit");
	private JMenuItem goback = new JMenuItem("Go Back to Main Menu");

	private JPanel panel = new JPanel();

	int number;
	public static String description;
	StyledDocument doc;
	SimpleAttributeSet keyword;

	public MainWindow() {

		super("Traffic Detector - Real time encrypted traffic classifier");
		setIconImage(Toolkit.getDefaultToolkit().getImage(
				getClass().getResource("/resources/icon.jpg")));
		setSize(700, 560);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		panel.setSize(700, 560);
		panel.setLayout(null);

		setLocationRelativeTo(null);

		setJMenuBar(menubar);
		menubar.add(file);
		menubar.add(help);
		help.add(about);
		help.add(close);
		file.add(goback);

		close.addActionListener(this);
		about.addActionListener(this);
		goback.addActionListener(this);

		start.setSize(200, 30);
		start.setLocation(10, 20);
		panel.add(start);
		start.addActionListener(this);

		box.setSelectedIndex(0);
		box.setSize(190, 30);
		box.setLocation(230, 20);
		panel.add(box);

		devicesFound.setSize(200, 40);
		devicesFound.setLocation(10, 50);
		panel.add(devicesFound);

		sel.setSize(210, 40);
		sel.setLocation(450, 50);
		panel.add(sel);
		add(sel);

		scroll1.setSize(440, 150);
		scroll1.setLocation(10, 80);
		panel.add(scroll1);

		selection.setSize(250, 70);
		selection.setLocation(450, 80);
		selection.setEditable(false);
		panel.add(selection);

		inter.setSize(150, 30);
		inter.setLocation(10, 250);
		inter.setEnabled(false);
		inter.setVisible(true);
		inter.addActionListener(this);
		panel.add(inter);

		results.setEditable(false);
		results.setFont(new Font("Tahoma", Font.PLAIN, 12));
		results.setLineWrap(true);
		results.setWrapStyleWord(true);
		scroll.setSize(400, 160);
		scroll.setLocation(10, 340);
		panel.add(scroll);

		confirm.setSize(250, 30);
		confirm.setLocation(10, 300);
		confirm.setEnabled(false);
		confirm.addActionListener(this);
		panel.add(confirm);

		back.setSize(120, 30);
		back.setLocation(550, 470);
		back.addActionListener(this);
		panel.add(back);

		add(panel);

		setVisible(true);

	}

	@Override
	public void actionPerformed(ActionEvent event) {

		if (event.getSource().equals(start)) {
			// retrieves the devices from the specified file and creates the
			// list to be shown
			listModel.clear();
			FixedIntervalDumper capture = new FixedIntervalDumper();
			capture.storeDevices();
			listDevices();
			selection.setText("No interface was selected");
			inter.setEnabled(true);
			sel.setVisible(true);

		}
		if (event.getSource().equals(inter)) {

			// selects the specified interface and informs the appropriate text
			// area
			String choice = devices.getSelectedValue().toString();

			if (choice != null) {

				confirm.setEnabled(true);

				SimpleAttributeSet keyword = null;
				selection.setText("Your selected interface is:\n");

				description = choice.substring(choice.indexOf("}") + 1);
				String numString = choice.substring(0, choice.indexOf(" "));

				number = Integer.parseInt(numString); // parameter for network
														// interface for
														// collectPcap()

				StyledDocument doc = (StyledDocument) selection.getDocument();
				keyword = new SimpleAttributeSet();
				StyleConstants.setBold(keyword, true);

				try {
					doc.insertString(doc.getLength(), "\n " + number + ". "
							+ description, keyword);
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
			}
		} else
			selection.setText("No interface was selected..");

		if (event.getSource().equals(confirm)) {

			// the basic functionality of the system belongs to this button
			// capturing -> test-case creation -> analyzing -> server
			// notification

			results.setText("Now is capturing and dumping packets for "
					+ FixedIntervalDumper.CAPTURE_INTERVAL / 1000
					+ " seconds..");
			results.append("\nNIC is:" + description);
			results.append("\n");
			selection.setText("Your selected interface is: \n" + description);
			this.repaint();
			results.repaint();
			results.revalidate();

			int value = box.getSelectedIndex();

			if (value == 0) {

				UtilityThread single = new UtilityThread(number);
				single.start();
			} else {
				UtilityThreadMultiple multiple = new UtilityThreadMultiple(
						number);
				multiple.start();
			}

		}

		if (event.getSource().equals(close)) {
			System.exit(0);
		}
		if (event.getSource().equals(goback)) {
			results.setText("");
			setVisible(false);
			Entry entry = new Entry();
		}
		if (event.getSource().equals(about)) {
			About about = new About();
		}
		if (event.getSource().equals(back)) {
			results.setText("");
			setVisible(false);
			Entry entry = new Entry();
		}
	}

	public void listDevices() {

		devicesFound.setText("Network interfaces available: ");
		String file = "mydevices.txt";
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e1) {
			ProblemAlert problem = new ProblemAlert();
			e1.printStackTrace();
		}
		String line;
		try {
			while ((line = br.readLine()) != null) {
				listModel.addElement(line + "\n");
			}
			devices.setModel(listModel);

		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
			br.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

	}

	@Override
	public void run() {

	}

}
