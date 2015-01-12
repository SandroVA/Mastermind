package com.dcgroup04.application;

import java.net.BindException;

import javax.swing.JOptionPane;

import com.dcgroup04.mastermind.gui.MMUserGui;
import com.dcgroup04.mastermind.server.MMServer;

/** The main application file of the project.
 * 
 * @author Rytis Paulauskas, Sandro Victoria Arena, Alessandro Rodi
 *
 */
public class MMApp {

	/** The main class, as required by java standards
	 * @param args the array of strings, as required by java standards
	 */
	public static void main(String[] args) {
		String[] options = {"Server",
		                    "Client"};
		int choice = JOptionPane.showOptionDialog(null,
		    "Would you like to run the program as the server or as the client?",
		    "Program startup",
		    JOptionPane.YES_NO_OPTION,
		    JOptionPane.QUESTION_MESSAGE,
		    null,
		    options,
		    options[1]);
		
		//If the user chose "Server", start the server
		if(choice == JOptionPane.OK_OPTION)
		{
			MMServer server = new MMServer();
			
			while(true) {
				try {
					server.startServer();
				} catch(BindException be) {
					System.out.println(be.getMessage());
					System.exit(1);
				} catch (Exception e) {
					System.out.println("The server received an unexpected exception: ");
					System.out.println(e.getMessage());
				}
			}
		}
		else //Otherwise start the client
			MMUserGui.startGUI();
	}

}
