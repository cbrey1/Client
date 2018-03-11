package client;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

/*
 * Client class for Multi-Threaded Chat Application
 * Created by Collin Brey, Nnenna Aneke, Rafael Carter and Caleb Farara
 * 
 * @version CS4225 Spring 2018
 */
public class Client {

	private ClientConnection clientConnection;
	private Scanner scanner;
	private Socket socket;
	private String username;
	
	/**
	 * Creates a Client object consisting of a ClientConnection, Socket and Username
	 * 
	 * @precondition none
	 * @postcondtition this.scanner = new Scanner(System.in)
	 * 				   this.socket = new Socket("localhost", 6066)
	 * 			       this.username = this.scanner.nextLine()
	 * 				   this.clientConnection = new ClientConnection, this.socket, this.username)
	 */
	public Client() {
		try {
			this.scanner = new Scanner(System.in);
			this.socket = new Socket("localhost", 6066);
			
			System.out.print("Please input your username: ");
			this.username = this.scanner.nextLine();
			
			this.clientConnection = new ClientConnection(this.socket, this.username);
		} 
		catch (UnknownHostException e) {
			e.printStackTrace();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Starts the ClientConnection and sends a message to the server with the Client's username
	 * @precondition none
	 * @postcondition none
	 */
	public void start() {
		this.clientConnection.start();
		this.clientConnection.sendMessageToServer(this.clientConnection.getName() + " has joined the chat");
		this.listenForInput();
	}
	
	private void listenForInput() {
		while(true) {
			while (!scanner.hasNextLine()) {
				this.waitOneSecond();
			}
			
			String message = scanner.nextLine();
			
			if (this.userWantsToQuit(message)) {
				this.displayQuitMessage();
				break;
			}
			
			this.clientConnection.sendMessageToServer(this.clientConnection.getName() + ": " + message);
		}
		this.clientConnection.closeConnection();
	}
	
	private void waitOneSecond() {
		try {
			Thread.sleep(1);
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private boolean userWantsToQuit(String input) { return input.toLowerCase().equals("quit"); }
	
	private void displayQuitMessage() {
		try {
			this.clientConnection.sendMessageToServer(this.username + " has left the chat");
			Thread.sleep(3);
		}
		catch(Exception e) {
			
		}
	}
}
