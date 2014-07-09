package TrafficDetector;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
/**
 * This class is used to handle exception erros
 * In a case of a serious problem the user is allowed to close
 * the program on demand
 * 
 * @author Panagiotis
 *
 */
public class ProblemAlert extends JFrame implements ActionListener {

	private JLabel label1;
	private JButton cancel;
	private JPanel panel;
	private JLabel label2;
	private JLabel label3;
	private JButton yes;

	public ProblemAlert() {

		super("Problem Alert!");
		setIconImage(Toolkit.getDefaultToolkit().getImage(
				getClass().getResource("/resources/icon.jpg")));
		setSize(310, 250);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);

		setLocationRelativeTo(null);

		panel = new JPanel();
		label1 = new JLabel("Oops..! Something is wrong!");
		label2 = new JLabel("The file is not found!");
		label3 = new JLabel(
				"Do you want to close the program");
		cancel = new JButton("Cancel");
		yes = new JButton("Yes");

		label1.setLocation(70, 30);
		label1.setSize(300, 30);
		label1.setVisible(true);
		panel.add(label1);

		panel.setSize(310, 250);
		panel.setLayout(null);

		label2.setLocation(85, 50);
		label2.setSize(250, 30);
		panel.add(label2);

		cancel.setLocation(150, 140);
		cancel.setSize(100, 30);
		cancel.addActionListener(this);
		panel.add(cancel);

		label3.setLocation(53, 100);
		label3.setSize(300, 30);
		panel.add(label3);

		yes.setLocation(30, 140);
		yes.setSize(100, 30);
		yes.addActionListener(this);
		panel.add(yes);

		add(panel);

		setVisible(true);

	}

	@Override
	public void actionPerformed(ActionEvent event) {

		if (event.getSource().equals(cancel)) {
			setVisible(false);
		}
		if (event.getSource().equals(yes)) {
			System.exit(0);

		}
	}
}

	
