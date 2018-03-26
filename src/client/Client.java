package client;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.swing.JTextArea;

/*
 * Client class for Multi-Threaded Chat Application
 * Created by Collin Brey, Nnenna Aneke, Rafael Carter and Caleb Farara
 * 
 * @version CS4225 Spring 2018
 */
public class Client {

	private ClientConnection clientConnection;
	private Socket socket;

	/**
	 * Creates a Client object consisting of a ClientConnection, Socket
	 * 
	 * @precondition textChat != null, username != null
	 * @postcondtition this.socket = new Socket("localhost", 6066)
	 *                 this.clientConnection = new ClientConnection, this.socket,
	 *                 this.username)
	 */
	public Client(JTextArea textChat, String username) {
		try {
			this.socket = new Socket("160.10.217.81", 6066);
			this.clientConnection = new ClientConnection(this.socket, textChat, username);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Starts the ClientConnection and sends a message to the server with the
	 * ClientConnection's username
	 * 
	 * @precondition none
	 * @postcondition none
	 */
	public void start() {
		this.clientConnection.start();
		
		this.clientConnection.sendMessageToServer(this.clientConnection.getName() + " has joined the chat.");
	}

	/**
	 * Sends message to server using ClientConnection object
	 * 
	 * @precondition none
	 * @postcondition none
	 * @param message
	 *            Message being sent
	 */
	public void sendMessageToServer(String message) {
		Date now = new Date(); // java.util.Date, NOT java.sql.Date or java.sql.Timestamp!
		String format2 = new SimpleDateFormat("dd/M/yy HH:mm", Locale.ENGLISH).format(now);
		this.clientConnection.sendMessageToServer(format2 + " " + this.clientConnection.getName() + ": " + message);
	}

	/**
	 * Displays exiting message including ClientConnection's username
	 * 
	 * @precondition none
	 * @postcondition none
	 */
	public void exitApplication() {
		this.clientConnection.sendMessageToServer(this.clientConnection.getName() + " has left the chat.");
	}
}
