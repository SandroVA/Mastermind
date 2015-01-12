package com.dcgroup04.mastermind.network;

/**
 * The MMPacketCodes holds values that contain specific codes that will be sent
 * either the server or the client
 * 
 * @author Rytis Paulauskas, Sandro Victoria Arena, Alessandro Rodi
 */
public enum MMPacketCodes {
	GAMESTARTREQUEST(0x00000000), GAMESTARTRESPONSE(0x0000000A), GAMELOST(
			0xFFFFFFFF), GAMEOVERREQUEST(0xABADF00D), GAMEOVERRESPONSE(
			0xF00DF00D), GAMECLOSECONNECTION(0xDEADDEAD), GAMEERROR(0xEEEEEEEE), GAMEANSWERREQUEST(
			0xAAAAAAAA);

	private int code;

	/**
	 * Creates a new MMPacketCode with the given code
	 * 
	 * @param code
	 *            the code
	 */
	private MMPacketCodes(int code) {
		this.code = code;
	}

	/**
	 * Returns a byte array with the error code
	 * 
	 * @return a byte array with the given error code
	 */
	public byte[] getCode() {
		return MMNetworking.makeByteArrayFromInt(code);
	}

}
