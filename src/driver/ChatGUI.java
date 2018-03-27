package driver;

import java.awt.EventQueue;
import javax.swing.JFrame;
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
import client.ClientConnection;

import javax.swing.JScrollPane;
import java.awt.Label;

public class ChatGUI {

	private JFrame frameChatApplication;
	private JTextField inputMessageField;
	private JTextArea allMessagesTextArea;
	private JTextArea activeUsersTextArea;
	private JLabel usernameLabel;
	private JButton enterButton;
	private Canvas chatCanvas;
	private JScrollPane scrollPane;
	private JScrollPane activeUsersScrollPane;
	
	private ClientConnection clientConnection;

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
		initializeUsernameLabel();
		initializeActiveUsersTextArea();

		this.clientConnection = new ClientConnection(this);
		this.clientConnection.start();
		sendMessageUponExit();
	}
	
	/**
	 * Set's the text for the usernameLabel
	 * 
	 * @precondition username != null
	 * @postcondition usernameLabel.getText() == username
	 * 
	 * @param username The username
	 */
	public void setUsernameLabel(String username) {
		this.usernameLabel.setText(username);
	}
	
	/**
	 * Adds a message from a client to the text area
	 * 
	 * @precondition message != null
	 * @postcondition allMessagesTextArea.getText() == message
	 * 
	 * @param username The username
	 */
	public void addMessageToChat(String message) {
		this.allMessagesTextArea.setText(message);
	}
	
	/**
	 * Returns the messages within the chat
	 * 
	 * @return Chat messages
	 */
	public String getChatMessages() {
		return this.allMessagesTextArea.getText();
	}
	
	/**
	 * Sets the activeUsersTextArea text to contain all of the active Usernames
	 * 
	 * @precondition users != null
	 * @postcondition activeUsersTextArea.getText() == users
	 * 
	 * @param users Active users
	 */
	public void setActiveUsersText(String users) {
		this.activeUsersTextArea.setText(users);
	}

	private void initializeFrame() {
		frameChatApplication = new JFrame();
		frameChatApplication.setTitle("Chat Application");
		frameChatApplication.setResizable(false);
		frameChatApplication.setBounds(100, 100, 772, 421);
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
				clientConnection.sendMessageToServer("i" + clientConnection.getUsername());
				clientConnection.sendMessageToServer(clientConnection.getCurrentTime() + " Server: " + clientConnection.getUsername() + " has left the chat."); 
				inputMessageField.setText("");
				System.exit(0);
			}
		});
	}

	private void initializeEnterButton() {
		enterButton = new JButton("Send");
		enterButton.setFont(new Font("Tahoma", Font.PLAIN, 18));
		frameChatApplication.getRootPane().setDefaultButton(enterButton);
		enterButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (inputMessageField.getText().equalsIgnoreCase("quit")) {
					frameChatApplication.dispatchEvent(new WindowEvent(frameChatApplication, WindowEvent.WINDOW_CLOSING));
				}
				else {
					clientConnection.sendMessageToServer(clientConnection.getCurrentTime() + " " + clientConnection.getUsername() + ": " + inputMessageField.getText());
					inputMessageField.setText("");
				}
			}
		});

		enterButton.setBounds(638, 315, 103, 46);
		frameChatApplication.getContentPane().add(enterButton);
	}

	private void initializeInputMessageField() {
		inputMessageField = new JTextField();
		inputMessageField.setFont(new Font("Tahoma", Font.PLAIN, 16));
		inputMessageField.setBounds(171, 315, 455, 46);
		frameChatApplication.getContentPane().add(inputMessageField);
		inputMessageField.setColumns(10);
	}

	private void initializeUsernameLabel() {
		usernameLabel = new JLabel("");
		usernameLabel.setHorizontalAlignment(SwingConstants.CENTER);
		usernameLabel.setFont(new Font("Tahoma", Font.PLAIN, 18));
		usernameLabel.setBounds(12, 315, 147, 46);
		frameChatApplication.getContentPane().add(usernameLabel);
	}

	private void initializeActiveUsersTextArea() {
		Label activeUsersLabel = new Label("Active Users");
		activeUsersLabel.setFont(new Font("Dialog", Font.PLAIN, 16));
		activeUsersLabel.setBounds(38, 19, 103, 24);
		frameChatApplication.getContentPane().add(activeUsersLabel);
		
		activeUsersScrollPane = new JScrollPane();
		activeUsersScrollPane.setBounds(12, 49, 147, 253);
		frameChatApplication.getContentPane().add(activeUsersScrollPane);
		
		activeUsersTextArea = new JTextArea();
		activeUsersTextArea.setFont(new Font("Monospaced", Font.PLAIN, 18));
		activeUsersScrollPane.setViewportView(activeUsersTextArea);
		activeUsersTextArea.setEditable(false);
	}

	private void initializeTextArea() {
		scrollPane = new JScrollPane();
		scrollPane.setAutoscrolls(true);
		scrollPane.setBounds(171, 13, 570, 289);
		frameChatApplication.getContentPane().add(scrollPane);
		allMessagesTextArea = new JTextArea();
		scrollPane.setViewportView(allMessagesTextArea);
		allMessagesTextArea.setFont(new Font("Monospaced", Font.PLAIN, 15));
		allMessagesTextArea.setEditable(false);
		DefaultCaret caret = (DefaultCaret) allMessagesTextArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.OUT_BOTTOM);
	}
}