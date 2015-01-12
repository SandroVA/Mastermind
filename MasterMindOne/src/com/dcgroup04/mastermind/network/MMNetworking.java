package com.dcgroup04.mastermind.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;

/**
 * Helper class that takes care of many tasks that are network related.
 * 
 * @author Rytis Paulauskas, Sandro Victoria Arena, Alessandro Rodi
 */
public class MMNetworking {

	/**
	 * Transforms a byte array into an int
	 * 
	 * @param byteArray
	 *            a byte array of length 4
	 * @return an int made from the byte array
	 */
	public static int makeIntFromByteArray(byte[] byteArray) {
		ByteBuffer wrapped = ByteBuffer.wrap(byteArray);
		return wrapped.getInt(); // 1
	}

	/**
	 * Transforms an int into a byte array
	 * 
	 * @param intValue
	 *            an int
	 * @return a byte array of length 4 made from the int
	 */
	public static byte[] makeByteArrayFromInt(int intValue) {
		ByteBuffer dbuf = ByteBuffer.allocate(4);
		dbuf.putInt(intValue);
		return dbuf.array();
	}

	/**
	 * Receive a message sent by a socket
	 * 
	 * @param byteBuffer
	 *            an array of bytes that will hold the response
	 * @param socket
	 *            an active socket
	 * @throws IOException
	 *             if an I/O error occurs while trying to retrieve the response
	 */
	public static void receiveMessage(byte[] byteBuffer, Socket socket)
			throws IOException {
		System.out.println("Handling request at "
				+ socket.getInetAddress().getHostAddress());

		InputStream in = socket.getInputStream();

		int totalBytesRcvd = 0; // Total bytes received so far
		int bytesRcvd; // Bytes received in last read

		// Receive until client closes connection, indicated by -1 return
		while (totalBytesRcvd < byteBuffer.length) {
			if ((bytesRcvd = in.read(byteBuffer, totalBytesRcvd,
					byteBuffer.length - totalBytesRcvd)) == -1)
				throw new SocketException("Connection close prematurely");
			totalBytesRcvd += bytesRcvd;
		}

		System.out.println("Received: " + byteBuffer[0] + byteBuffer[1]
				+ byteBuffer[2] + byteBuffer[3]);
	}

	/**
	 * Sends a message through a socket
	 * 
	 * @param byteBuffer
	 *            the array of bytes that will be sent
	 * @param socket
	 *            an active socket
	 * @throws IOException
	 *             if an I/O error occurs while trying to send the request
	 */
	public static void sendMessage(byte[] byteBuffer, Socket socket)
			throws IOException {
		OutputStream out = socket.getOutputStream();

		// Send back the response to the client
		out.write(byteBuffer);

		System.out.println("Sent: " + byteBuffer[0] + byteBuffer[1]
				+ byteBuffer[2] + byteBuffer[3]);
	}
}
