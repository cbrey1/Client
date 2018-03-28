package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.swing.JOptionPane;

import driver.ChatGUI;

/*
 * ClientConnection class for Multi-Threaded Chat Application
 * Created by Collin Brey, Nnenna Aneke, Rafael Carter and Caleb Farara
 * 
 * @version CS4225 Spring 2018
 */
public class ClientConnection extends Thread {

	private Socket socket;
	private DataInputStream dataInputStream;
	private DataOutputStream dataOutputStream;
	private boolean activeConnection;
	private ChatGUI chatGui;
	private String username;

	/**
	 * Creates a ClientConnection object with passed in socket and the Client's
	 * username
	 * 
	 * @precondition gui != null
	 * @postcondition this.username = "", this.socket = new Socket("localhost", ,
	 *                this.dataInputStream = this.socket.getInputStream(),
	 *                this.dataOutputStream = this.socket.getOutputStream(),
	 *                this.activeConnection = true;
	 * @param socket
	 *            The Socket the client is using
	 * @param textArea
	 *            ChatTextArea
	 * @param username
	 *            The Client's username
	 */
	public ClientConnection(ChatGUI gui) {
		try {
			this.socket = new Socket("localhost", 4225);
			this.dataInputStream = new DataInputStream(this.socket.getInputStream());
			this.dataOutputStream = new DataOutputStream(this.socket.getOutputStream());
			this.activeConnection = true;
			this.chatGui = gui;
			this.username = "";
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sends message input by the user from the console to the server
	 * 
	 * @precondition message != null
	 * @postcondition this.dataOutputStream.flush()
	 * @param message
	 *            Message to be sent by user
	 */
	public void sendMessageToServer(String message) {
		try {
			this.dataOutputStream.writeUTF(message);
			this.dataOutputStream.flush();
		} catch (IOException e) {
			e.printStackTrace();
			this.closeConnection();
		}
	}

	/**
	 * Run method for a ClientConnection thread. Listens for messages sent by the
	 * server.
	 * 
	 * @precondition none
	 * @postcondition none
	 */
	public void run() {
		while (this.activeConnection) {
			try {
				while (this.dataInputStreamUnavailable()) {
					this.waitOneSecond();
				}

				String message = this.dataInputStream.readUTF();
				
				if (ipAddressFound(message)) {
					if (existingUserIsReturning(message)) {
						handleReturningUser(message);
					}
					else if(messageContainsAllActiveUsers(message)) {
						this.chatGui.setActiveUsersText(message.substring(1));
					}
					else {
						sendMessageToAllOtherUsers(message);
					}
				}
				else {
					String[] existingUsernames = message.substring(1).split("\\s+");
					this.createNewUser(existingUsernames);
				}

			} catch (IOException e) {
				this.closeConnection();
			}
		}
	}

	/**
	 * Closes the ClientConnection's DataInputStream, DataOutputStream, Socket and
	 * sets the activeConnection to false
	 * 
	 * @precondition none
	 * @postcondition this.dataInputStream, this.dataOutputStream, this.socket all
	 *                close this.activeConnection = false
	 */
	public void closeConnection() {
		try {
			this.dataInputStream.close();
			this.dataOutputStream.close();
			this.socket.close();
			this.activeConnection = false;
			System.exit(0);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns the address of the current socket being used
	 * 
	 * @return The socket address
	 */
	public String getSocketAddress() {
		return this.socket.getLocalAddress().toString();
	}
	
	/**
	 * Returns the username of the current client
	 * 
	 * @return The client's username
	 */
	public String getUsername() {
		return this.username;
	}
	
	/**
	 * Returns the file name of the chat log consisting of the Client's username and the current time
	 * 
	 * @return The time stamp
	 */
	public String getLogFileName() {
		String username = this.username;
		String time = new SimpleDateFormat("MM-dd-yy_HH-mm", Locale.ENGLISH).format(new Date());
		return "logs/" + username + "_" + time + ".txt";
	}
	
	/**
	 * Returns the current date and time in the form of a time
	 * 
	 * @return The current date and time
	 */
	public String getCurrentTime() {
		return new SimpleDateFormat("MM/dd/yy HH:mm", Locale.ENGLISH).format(new Date());
	}
	
	private void sendMessageToAllOtherUsers(String message) {
		if (chatGui.getChatMessages().isEmpty()) {
			this.chatGui.addMessageToChat(message);
		} else {
			this.chatGui.addMessageToChat(chatGui.getChatMessages() + "\n" + message);
		}
	}

	private void handleReturningUser(String message) {
		this.chatGui.setUsernameLabel(message.substring(1));
		this.username = message.substring(1);
		this.sendMessageToServer("a" + this.username);
		this.sendMessageToServer(this.getCurrentTime() + " Server: " + this.username + " has joined the chat.");
	}

	private void createNewUser(String[] existingUsernames) {
		String username = null;
		
		while (true) {
			username = JOptionPane.showInputDialog("Please enter your username: ");
			username = username.replaceAll("\\s+","");
			boolean usernameFound = false;
			
			for (String name : existingUsernames) {
				if (name.equals(username)) {
					usernameFound = true;
				}
			}
			if (!usernameFound) break;
		}

		this.chatGui.setUsernameLabel(username);
		this.sendMessageToServer(this.getSocketAddress() + " " + username);
		this.username = username;
		this.sendMessageToServer("a" + this.username);
		this.sendMessageToServer(this.getCurrentTime() + " Server: " + this.username + " has joined the chat.");
	}

	private boolean messageContainsAllActiveUsers(String message) {
		return message.charAt(0) == 'u';
	}

	private boolean existingUserIsReturning(String message) {
		return message.charAt(0) == '_';
	}

	private boolean ipAddressFound(String message) {
		return !(message.charAt(0) == 'n');
	}

	private void waitOneSecond() {
		try {
			Thread.sleep(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private boolean dataInputStreamUnavailable() {
		boolean inputStreamAvailable = false;
		try {
			inputStreamAvailable = this.dataInputStream.available() == 0;
		} catch (IOException e) {
		}
		return inputStreamAvailable;
	}
}
