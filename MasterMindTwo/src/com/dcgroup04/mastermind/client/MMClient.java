package com.dcgroup04.mastermind.client;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import com.dcgroup04.mastermind.network.MMNetworking;
import com.dcgroup04.mastermind.network.MMPacketCodes;

/**
 * This is the class that takes care of interacting with the server and the GUI
 * 
 * @author Rytis Paulauskas, Sandro Victoria Arena, Alessandro Rodi
 * 
 */
public class MMClient {

	private Socket socket;
	private static final int BUFSIZE = 4; // Size of receive buffer

	/**
	 * Creates a new MMClient connected to the given IP at the given port
	 * 
	 * @param serverIP
	 *            the IP of the server to connect to
	 * @param serverPort
	 *            the port of the server to connect to
	 * @throws UnknownHostException
	 *             if the IP address of the host could not be determined
	 * @throws IOException
	 *             if an I/O error occurs when creating the socket
	 */
	public MMClient(String serverIP, int serverPort)
			throws UnknownHostException, IOException {
		this.socket = new Socket(serverIP, serverPort);
	}

	/**
	 * Sends a request to the server
	 * 
	 * @param request
	 *            the message to send to the server
	 * @return the response from the server
	 * @throws IOException
	 *             if an I/O error occurs while receiving or sending the message
	 */
	public byte[] sendRequest(byte[] request) throws IOException {

		// Send the byte array to the server
		MMNetworking.sendMessage(request, this.socket);

		// Get response from the server
		byte[] response = new byte[BUFSIZE];
		MMNetworking.receiveMessage(response, this.socket);

		// Handle the response given by the server
		return response;
	}

	/**
	 * Sends a request to the server
	 * 
	 * @param request
	 *            the message to send to the server
	 * @throws IOException
	 *             if an I/O error occurs while receiving or sending the message
	 */
	public byte[] sendRequest(int request) throws IOException {
		return sendRequest(MMNetworking.makeByteArrayFromInt(request));
	}

	/**
	 * Closes the connection to the server gracefully
	 * 
	 * @throws IOException
	 *             if an error occurred while trying to close the connection to
	 *             the server
	 */
	public void closeConnection() throws IOException {
		sendRequest(MMPacketCodes.GAMECLOSECONNECTION.getCode());
		this.socket.close();
	}

	/**
	 * Parses the response of the game state for the GUI
	 * 
	 * @param response
	 *            the response received from the server
	 * @param request
	 *            the request sent to the server
	 * @return a message that can be appended to the GUI interface
	 */
	public String parseResponse(byte[] response, byte[] request) {
		int round = response[2];
		int correctGuesses = response[1];
		int incorrectOrder = response[0];

		String result = "Try #" + round + ": " + request[0] + " " + request[1]
				+ " " + request[2] + " " + request[3] + " \nCorrect guesses: "
				+ correctGuesses + "\nIncorrect Order: " + incorrectOrder
				+ "\n\n";
		// Append text to the end of result screen
		return result;
	}
}
