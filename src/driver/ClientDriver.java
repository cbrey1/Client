package driver;

import client.Client;

public class ClientDriver {

	/**
	 * Entry point for Client Application
	 * @param args
	 */
	public static void main(String[] args) {
		Client client = new Client();
		client.start();
	}
}
