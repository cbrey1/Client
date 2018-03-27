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
	 * @precondition socket != null, username != null, textArea != null
	 * @postcondition this.getName() = username, this.socket = socket,
	 *                this.dataInputStream = this.socket.getInputStream(),
	 *                this.dataOutputStream = this.socket.getOutputStream(),
	 *                this.activeConnection = false; this.chat = textArea
	 * @param socket
	 *            The Socket the client is using
	 * @param textArea
	 *            ChatTextArea
	 * @param username
	 *            The Client's username
	 */
	public ClientConnection(ChatGUI gui) {
		try {
			this.socket = new Socket("localhost", 6066);;
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
				
				if (!message.equals("no")) {
					if (message.charAt(0) == '_') {
						this.chatGui.setUsernameLabel(message.substring(1));
						this.username = message.substring(1);
						this.sendMessageToServer(this.getCurrentTime() + " Server: " + this.username + " has joined the chat.");
					}
					else {
						if (chatGui.getChatMessages().isEmpty()) {
							this.chatGui.addMessageToChat(message);
						} else {
							this.chatGui.addMessageToChat(chatGui.getChatMessages() + "\n" + message);
						}
					}
				}
				else {
					String username = JOptionPane.showInputDialog("Please enter your username: ");
					this.chatGui.setUsernameLabel(username);
					this.sendMessageToServer(this.getSocketAddress() + " " + username);
					this.username = username;
					this.sendMessageToServer(this.getCurrentTime() + " Server: " + this.username + " has joined the chat.");
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
	 * Returns the current date and time
	 * 
	 * @return The current date and time
	 */
	public String getCurrentTime() {
		return new SimpleDateFormat("MM/dd/yy HH:mm", Locale.ENGLISH).format(new Date());
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
