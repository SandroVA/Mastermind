package com.dcgroup04.mastermind.server;

import java.net.*;
import java.io.*;

/**
 * This is the high-level class that takes care of accepting sockets and
 * handling game sessions with the client.
 * 
 * @author Rytis Paulauskas, Sandro Victoria Arena, Alessandro Rodi
 * 
 */
public class MMServer {

	public void startServer() throws IOException {

		int servPort = 50000; // Port specified in the specs

		// Create a server socket to accept client connection requests
		ServerSocket servSock = new ServerSocket(servPort);

		// Print out the IP of the server
		System.out.println("Server running at "
				+ InetAddress.getLocalHost().getHostAddress() + " on port "
				+ servPort);

		// Run forever, accepting and servicing connections
		while (true) {
			Socket clntSock = servSock.accept(); // Get client connection

			MMServerSession session = new MMServerSession(clntSock);

			session.startSession();
		}
	}
}