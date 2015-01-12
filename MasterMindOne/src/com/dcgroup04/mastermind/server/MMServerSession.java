package com.dcgroup04.mastermind.server;

import java.net.*;
import java.util.Arrays;
import java.io.*;

import com.dcgroup04.mastermind.network.MMNetworking;
import com.dcgroup04.mastermind.network.MMPacketCodes;

/**
 * @author Rytis Paulauskas, Sandro Victoria Arena, Alessandro Rodi
 * 
 */
public class MMServerSession {

	private Socket socket;
	private boolean gameStarted;
	private byte[] answer;
	private int rounds;
	private static final int BUFSIZE = 4; // Size of receive buffer

	/**
	 * Creates a new MMServerSession with a new state of the game
	 * 
	 * @param socket
	 *            a socket with an active connection
	 */
	public MMServerSession(Socket socket) {
		this.socket = socket;
		this.gameStarted = false;
		this.answer = new byte[4];
		this.rounds = 0;
	}

	/**
	 * Starts the game session and keeps it running until the client exits
	 * 
	 * @throws IOException
	 *             if an I/O error occurred while receiving or sending messages
	 */
	public void startSession() throws IOException {
		byte[] request = null;
		while (!Arrays.equals(request,
				MMPacketCodes.GAMECLOSECONNECTION.getCode())) {
			request = new byte[BUFSIZE]; // Receive buffer

			// Receive the request
			MMNetworking.receiveMessage(request, this.socket);

			// Handle the request based on what the received code is
			byte[] response = handleRequest(request);

			// Send back the response to the client
			MMNetworking.sendMessage(response, this.socket);
		}

		// Closing the socket
		this.socket.close();
	}

	/**
	 * Handles the request based on what the received code is
	 * 
	 * @param request
	 *            the message that the client sent
	 * @return the response that will be sent back to the client
	 */
	private byte[] handleRequest(byte[] request) {
		byte[] response;
		// Return the answer if the player requests it
		if (Arrays.equals(request, MMPacketCodes.GAMEANSWERREQUEST.getCode())) {
			response = this.answer;
		} else {
			if (!this.gameStarted) {
				// Generate the answer with the test functionality
				if (!Arrays.equals(request,
						MMPacketCodes.GAMESTARTREQUEST.getCode()))
					this.answer = request.clone();
				else
					// Generate a random answer
					this.answer = MMGame.generateAnswer();

				this.gameStarted = true;
				response = MMPacketCodes.GAMESTARTRESPONSE.getCode();
			} else {
				if (Arrays.equals(request,
						MMPacketCodes.GAMEOVERREQUEST.getCode())) {
					
					// Reset the state before sending back the response
					resetState();
					response = MMPacketCodes.GAMEOVERRESPONSE.getCode();
				} else
					// Process a regular round
					response = MMGame.playRound(request.clone(), this.answer,
							this.rounds++);
			}
		}
		return response;
	}

	/**
	 * Resets the game state once the game is over.
	 * 
	 */
	private void resetState() {
		this.rounds = 0;
		this.gameStarted = false;
	}
}
