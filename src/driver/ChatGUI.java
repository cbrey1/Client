package driver;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import java.awt.Canvas;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JButton;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.text.DefaultCaret;
import client.Client;
import javax.swing.JScrollPane;

public class ChatGUI {

	private JFrame frameChatApplication;
	private JTextField inputMessageField;
	private JTextArea allMessagesTextArea;
	private JLabel usernameLabel;
	private JButton enterButton;
	private Canvas chatCanvas;

	private Client client;
	private JScrollPane scrollPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ChatGUI window = new ChatGUI();
					window.frameChatApplication.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public ChatGUI() {
		initialize();
		inputMessageField.requestFocus();
	}

	private void initialize() {
		initializeFrame();
		initializeCanvas();
		initializeTextArea();
		initializeInputMessageField();
		initializeEnterButton();

		String username = JOptionPane.showInputDialog("Please enter your username: ");
		this.client = new Client(this.allMessagesTextArea, username);
		initializeUsernameLabel(username);
		this.client.start();

		sendMessageUponExit();
	}

	private void initializeFrame() {
		frameChatApplication = new JFrame();
		frameChatApplication.setTitle("Chat Application");
		frameChatApplication.setResizable(false);
		frameChatApplication.setBounds(100, 100, 614, 421);
		frameChatApplication.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frameChatApplication.getContentPane().setLayout(null);
	}

	private void initializeCanvas() {
		chatCanvas = new Canvas();
		chatCanvas.setBounds(0, 0, 0, 0);
		frameChatApplication.getContentPane().add(chatCanvas);
	}

	private void sendMessageUponExit() {
		frameChatApplication.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				client.exitApplication();
				inputMessageField.setText("");
				System.exit(0);
			}
		});
	}

	private void initializeEnterButton() {
		enterButton = new JButton("Enter");
		frameChatApplication.getRootPane().setDefaultButton(enterButton);
		enterButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				client.sendMessageToServer(inputMessageField.getText());
				inputMessageField.setText("");
			}
		});

		enterButton.setBounds(453, 315, 143, 46);
		frameChatApplication.getContentPane().add(enterButton);
	}

	private void initializeInputMessageField() {
		inputMessageField = new JTextField();
		inputMessageField.setFont(new Font("Tahoma", Font.PLAIN, 16));
		inputMessageField.setBounds(115, 314, 333, 46);
		frameChatApplication.getContentPane().add(inputMessageField);
		inputMessageField.setColumns(10);
	}

	private void initializeUsernameLabel(String username) {
		usernameLabel = new JLabel("");
		usernameLabel.setHorizontalAlignment(SwingConstants.CENTER);
		usernameLabel.setFont(new Font("Tahoma", Font.PLAIN, 18));
		usernameLabel.setBounds(12, 315, 91, 46);
		frameChatApplication.getContentPane().add(usernameLabel);
		usernameLabel.setText(username);
	}

	private void initializeTextArea() {
		scrollPane = new JScrollPane();
		scrollPane.setAutoscrolls(true);
		scrollPane.setBounds(12, 13, 584, 289);
		frameChatApplication.getContentPane().add(scrollPane);
		allMessagesTextArea = new JTextArea();
		scrollPane.setViewportView(allMessagesTextArea);
		allMessagesTextArea.setFont(new Font("Monospaced", Font.PLAIN, 15));
		allMessagesTextArea.setEditable(false);
		DefaultCaret caret = (DefaultCaret) allMessagesTextArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.OUT_BOTTOM);
	}
}