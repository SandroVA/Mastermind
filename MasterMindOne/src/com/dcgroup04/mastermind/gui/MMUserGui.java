package com.dcgroup04.mastermind.gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JOptionPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;

import java.awt.Font;

import javax.swing.JTextArea;
import javax.swing.JScrollPane;

import com.dcgroup04.mastermind.client.MMClient;
import com.dcgroup04.mastermind.network.MMPacketCodes;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.Arrays;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * The class for the GUI of the user
 * 
 * @author Rytis Paulauskas, Sandro Victoria Arena, Alessandro Rodi
 * 
 */
public class MMUserGui {

	private JFrame frame;
	private JTextField txtInput1;
	private JTextField txtInput2;
	private JTextField txtInput3;
	private JTextField txtInput4;
	private JTextArea txtrTries;
	private JLabel nbOfTriesLabel;
	private JPanel labelPanel;

	private JButton btnSetAnswer;
	private JButton btnStartGame;
	private JButton btnConnect;
	private JButton btnDisconnect;
	private JButton btnResign;
	private JButton btnSubmit;

	private boolean testMode;
	private boolean isConnectedToServer;

	private MMClient client;

	private final String instructions = "Welcome to Mastermind!\n\n"
			+ "In this game you attempt to crack a 4 digit code with the help of minor hints!\n\n"
			+ "When you submit your guess, you will know how many digits are in the right place"
			+ " and how many are the right digits but in the wrong place.\n\n"
			+ "Please connect to the server, start the game, and then enter "
			+ "4 digits between 1 and 8 before pressing on 'Submit'.";

	/**
	 * Starts the GUI
	 * 
	 */
	public static void startGUI() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MMUserGui window = new MMUserGui();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Enables the testing mode of the program
	 * 
	 */
	private void enableTesting() {
		JOptionPane.showMessageDialog(this.frame,
				"Please enter your test answer and press Submit", "Error",
				JOptionPane.INFORMATION_MESSAGE);

		// Set to test mode
		testMode = true;
		btnSubmit.setEnabled(true);
		btnStartGame.setEnabled(false);
		btnSetAnswer.setEnabled(false);
		nbOfTriesLabel.setText("0");
	}

	/**
	 * Resigns the game
	 * 
	 */
	private void resign() {
		endGame("You resigned.");
	}

	/**
	 * Submit values that the player inputed
	 * 
	 */
	private void submitValues() {
		if (txtInput1.getText().length() == 1
				&& txtInput2.getText().length() == 1
				&& txtInput3.getText().length() == 1
				&& txtInput4.getText().length() == 1) {
			if (testMode)
				sendAnswer();
			else
				sendGuesses();
		} else
			JOptionPane.showMessageDialog(this.frame,
					"Please enter 4 guesses between 1 and 8.", "Error",
					JOptionPane.WARNING_MESSAGE);
	}

	/**
	 * Starts the game
	 * 
	 */
	private void startGame() {
		byte[] response = sendRequestToServer(MMPacketCodes.GAMESTARTREQUEST
				.getCode());

		// Starts the game
		if (Arrays.equals(response, MMPacketCodes.GAMESTARTRESPONSE.getCode())) {
			txtrTries.setText("");
			nbOfTriesLabel.setText("0");
			btnResign.setEnabled(true);
			btnSubmit.setEnabled(true);
			btnStartGame.setEnabled(false);
			btnSetAnswer.setEnabled(false);
			JOptionPane.showMessageDialog(this.frame, "Welcome to Mastermind!",
					"Welcome!", JOptionPane.INFORMATION_MESSAGE);
		}
	}

	/**
	 * Disconnects from the server
	 * 
	 */
	private void disconnectFromServer() {
		// Disable buttons related to the game
		btnConnect.setEnabled(true);
		btnStartGame.setEnabled(false);
		btnDisconnect.setEnabled(false);
		btnResign.setEnabled(false);
		btnSubmit.setEnabled(false);
		btnSetAnswer.setEnabled(false);
		btnSetAnswer.setEnabled(false);

		try {
			client.closeConnection();
		} catch (IOException ioe) {
			JOptionPane.showMessageDialog(this.frame,
					"An error has occured while disconnecting from the server: "
							+ ioe.getMessage(), "Error",
					JOptionPane.WARNING_MESSAGE);
		}

		// Set back the instructions
		txtrTries.setText(this.instructions);
		isConnectedToServer = false;
	}

	/**
	 * Connects to the server
	 * 
	 */
	private void connectToServer() {
		String ip;
		client = null;

		while (client == null) {
			try {
				// Get the IP
				ip = JOptionPane
						.showInputDialog("Please enter the IP of the server you wish to connect to.");
				System.out.println(ip);

				// Make sure the IP is valid
				while (!validateIP(ip)) {
					ip = JOptionPane
							.showInputDialog("Invalid IP, please enter the IP of the server you wish to connect to.");
				}
				System.out.println(ip);
				client = new MMClient(ip, 50000);
			} catch (Exception e) {
				JOptionPane.showMessageDialog(this.frame,
						"Cannot connect to the server: " + e.getMessage(),
						"Network error", JOptionPane.WARNING_MESSAGE);
				return;
			}
		}

		// Enable options to start the game
		btnConnect.setEnabled(false);
		btnStartGame.setEnabled(true);
		btnDisconnect.setEnabled(true);
		btnSetAnswer.setEnabled(true);
		isConnectedToServer = true;
		btnSetAnswer.setEnabled(true);
	}

	/**
	 * Sends the answer to the server for testing purposes
	 * 
	 */
	private void sendAnswer() {
		byte[] request = { Byte.parseByte(txtInput1.getText()),
				Byte.parseByte(txtInput2.getText()),
				Byte.parseByte(txtInput3.getText()),
				Byte.parseByte(txtInput4.getText()) };
		byte[] response = sendRequestToServer(request);

		// Start the game with the given answer
		if (Arrays.equals(response, MMPacketCodes.GAMESTARTRESPONSE.getCode())) {
			testMode = false;
			txtrTries.setText("");
			btnResign.setEnabled(true);
			btnSubmit.setEnabled(true);
			btnStartGame.setEnabled(false);
			btnSetAnswer.setEnabled(false);
			JOptionPane.showMessageDialog(this.frame, "Welcome to Mastermind!",
					"Welcome!", JOptionPane.INFORMATION_MESSAGE);
		}
	}

	/**
	 * Sends the guesses of the player
	 * 
	 */
	private void sendGuesses() {
		// Get the request from the text fields
		byte[] request = { Byte.parseByte(txtInput1.getText()),
				Byte.parseByte(txtInput2.getText()),
				Byte.parseByte(txtInput3.getText()),
				Byte.parseByte(txtInput4.getText()) };

		// Make sure the response is sent successfully
		byte[] response = sendRequestToServer(request);
		if (response == null)
			return;

		nbOfTriesLabel.setText(Integer.toString(Integer.parseInt(nbOfTriesLabel
				.getText()) + 1));
		clearInput();

		// Check if the player lost
		if (Arrays.equals(response, MMPacketCodes.GAMELOST.getCode())) {
			txtrTries.setText(txtrTries.getText() + "Try #10: " + request[0]
					+ " " + request[1] + " " + request[2] + " " + request[3]);

			// Get the answer from the server
			response = sendRequestToServer(MMPacketCodes.GAMEANSWERREQUEST
					.getCode());
			if (response == null)
				return;

			endGame("Sorry, you have lost the game! The answer was "
					+ response[0] + "" + response[1] + "" + response[2] + ""
					+ response[3]);
		} else {
			String message = client.parseResponse(response, request);
			txtrTries.setText(txtrTries.getText() + message);

			if (response[1] == 4)
				endGame("You won the game!");
		}
	}

	/**
	 * Ends the current game
	 * 
	 * @param message
	 *            the message that will be displayed once the game is over
	 */
	private void endGame(String message) {
		btnStartGame.setEnabled(true);
		btnResign.setEnabled(false);
		btnSetAnswer.setEnabled(true);
		btnSubmit.setEnabled(false);

		byte[] response = sendRequestToServer(MMPacketCodes.GAMEOVERREQUEST
				.getCode());

		// Make sure the server sent the correct response
		if (Arrays.equals(response, MMPacketCodes.GAMEOVERRESPONSE.getCode()))
			JOptionPane.showMessageDialog(this.frame, message, "Game",
					JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * Validates the IP entered by the user
	 * 
	 * @param ip
	 *            the IP entered by the user
	 * @return true if the IP is valid, false otherwise
	 */
	private boolean validateIP(String ip) {
		ip.trim();
		boolean valid = ip // Look to match the ip format or localhost
				.matches("^([1-2]?[0-9]{1,2}\\.[1-2]?[0-9]{1,2}\\.[1-2]?[0-9]{1,2}\\.[1-2]?[0-9]{1,2})$|^localhost$");

		if (!valid)
			return false;
		else {
			if (!ip.equals("localhost")) {
				// Split the input into 4 to check they are in the right
				// range(0-255)
				String[] numbers = ip.split("\\.");
				for (int i = 0; i < 4; i++) {
					int number = Integer.parseInt(numbers[i]);
					if (number < 0 || number > 255) {
						valid = false;
						break;
					}
				}
			}
			return valid;
		}
	}

	/**
	 * Validates the input to only allow the user to enter 1-8 for guesses
	 * 
	 * @param e
	 *            the key event
	 */
	private void validateInput(KeyEvent e) {
		// If the user did not enter 1-8, set that text field's value to empty
		// string
		JTextField textInput = (JTextField) e.getSource();
		try {
			int textInputNumber = Integer.parseInt(textInput.getText());
			if (textInputNumber < 1 || textInputNumber > 8)
				textInput.setText("");
			else
				textInput.transferFocus();
		} catch (NumberFormatException nfe) {
			textInput.setText("");
		}
	}

	/**
	 * Clears the input of the player
	 * 
	 */
	private void clearInput() {
		txtInput1.setText("");
		txtInput2.setText("");
		txtInput3.setText("");
		txtInput4.setText("");
	}

	/**
	 * Sends a message to the server and displays an error message if an error
	 * occured
	 * 
	 * @return the response from the server
	 */
	private byte[] sendRequestToServer(byte[] request) {
		byte[] response = null;

		// Get the answer from the server
		try {
			response = client.sendRequest(request);
		} catch (Exception e) {
			// If an error occurred, disconnect and display the error to the
			// error
			disconnectFromServer();
			JOptionPane.showMessageDialog(this.frame, e.getMessage(), "Error",
					JOptionPane.WARNING_MESSAGE);
		}
		return response;
	}

	/**
	 * Create the application.
	 */
	public MMUserGui() {
		initialize();
		testMode = false;
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if (isConnectedToServer)
					disconnectFromServer();
			}
		});
		frame.setResizable(false);
		frame.setBounds(100, 100, 540, 545);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel inputPanel = new JPanel();

		btnSubmit = new JButton("Submit");

		btnSubmit.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (btnSubmit.isEnabled())
					submitValues();
			}
		});
		btnSubmit.setEnabled(false);

		JLabel lblNewLabel = new JLabel("Enter your guesses:");
		lblNewLabel.setFont(new Font("Dialog", Font.BOLD | Font.ITALIC, 14));
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);

		labelPanel = new JPanel();

		JPanel gameButtonPanel = new JPanel();

		JPanel titlePanel = new JPanel();

		JPanel gamePanel = new JPanel();
		GroupLayout groupLayout = new GroupLayout(frame.getContentPane());
		groupLayout
				.setHorizontalGroup(groupLayout
						.createParallelGroup(Alignment.LEADING)
						.addGroup(
								groupLayout
										.createSequentialGroup()
										.addGroup(
												groupLayout
														.createParallelGroup(
																Alignment.LEADING)
														.addGroup(
																groupLayout
																		.createSequentialGroup()
																		.addGap(42)
																		.addComponent(
																				gamePanel,
																				GroupLayout.PREFERRED_SIZE,
																				261,
																				GroupLayout.PREFERRED_SIZE)
																		.addPreferredGap(
																				ComponentPlacement.UNRELATED)
																		.addGroup(
																				groupLayout
																						.createParallelGroup(
																								Alignment.LEADING)
																						.addComponent(
																								gameButtonPanel,
																								0,
																								0,
																								Short.MAX_VALUE)
																						.addComponent(
																								labelPanel,
																								GroupLayout.PREFERRED_SIZE,
																								186,
																								GroupLayout.PREFERRED_SIZE)))
														.addGroup(
																groupLayout
																		.createSequentialGroup()
																		.addContainerGap()
																		.addComponent(
																				inputPanel,
																				GroupLayout.PREFERRED_SIZE,
																				522,
																				GroupLayout.PREFERRED_SIZE))
														.addGroup(
																groupLayout
																		.createSequentialGroup()
																		.addGap(32)
																		.addComponent(
																				titlePanel,
																				GroupLayout.PREFERRED_SIZE,
																				481,
																				GroupLayout.PREFERRED_SIZE)))
										.addContainerGap(12, Short.MAX_VALUE)));
		groupLayout
				.setVerticalGroup(groupLayout
						.createParallelGroup(Alignment.LEADING)
						.addGroup(
								groupLayout
										.createSequentialGroup()
										.addContainerGap()
										.addComponent(titlePanel,
												GroupLayout.PREFERRED_SIZE, 56,
												GroupLayout.PREFERRED_SIZE)
										.addGroup(
												groupLayout
														.createParallelGroup(
																Alignment.LEADING)
														.addGroup(
																groupLayout
																		.createSequentialGroup()
																		.addPreferredGap(
																				ComponentPlacement.RELATED)
																		.addComponent(
																				gamePanel,
																				GroupLayout.DEFAULT_SIZE,
																				383,
																				Short.MAX_VALUE))
														.addGroup(
																groupLayout
																		.createSequentialGroup()
																		.addGap(23)
																		.addComponent(
																				labelPanel,
																				GroupLayout.PREFERRED_SIZE,
																				58,
																				GroupLayout.PREFERRED_SIZE)
																		.addGap(18)
																		.addComponent(
																				gameButtonPanel,
																				GroupLayout.PREFERRED_SIZE,
																				193,
																				GroupLayout.PREFERRED_SIZE)))
										.addPreferredGap(
												ComponentPlacement.RELATED)
										.addComponent(inputPanel,
												GroupLayout.PREFERRED_SIZE, 60,
												GroupLayout.PREFERRED_SIZE)
										.addContainerGap()));

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setToolTipText("");
		GroupLayout gl_gamePanel = new GroupLayout(gamePanel);
		gl_gamePanel.setHorizontalGroup(gl_gamePanel.createParallelGroup(
				Alignment.LEADING).addGroup(
				gl_gamePanel
						.createSequentialGroup()
						.addContainerGap()
						.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE,
								241, Short.MAX_VALUE).addContainerGap()));
		gl_gamePanel.setVerticalGroup(gl_gamePanel.createParallelGroup(
				Alignment.LEADING).addComponent(scrollPane, Alignment.TRAILING,
				GroupLayout.DEFAULT_SIZE, 371, Short.MAX_VALUE));

		txtrTries = new JTextArea();
		txtrTries.setWrapStyleWord(true);
		txtrTries.setLineWrap(true);
		scrollPane.setViewportView(txtrTries);
		txtrTries.setFont(new Font("Consolas", Font.PLAIN, 15));
		txtrTries.setText(this.instructions);
		txtrTries.setEditable(false);
		gamePanel.setLayout(gl_gamePanel);

		JLabel lblNewLabel_1 = new JLabel("MASTERMIND GAME");
		lblNewLabel_1.setFont(new Font("Century Gothic", Font.BOLD
				| Font.ITALIC, 36));
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
		GroupLayout gl_titlePanel = new GroupLayout(titlePanel);
		gl_titlePanel.setHorizontalGroup(gl_titlePanel.createParallelGroup(
				Alignment.LEADING).addGroup(
				gl_titlePanel
						.createSequentialGroup()
						.addContainerGap()
						.addComponent(lblNewLabel_1, GroupLayout.DEFAULT_SIZE,
								499, Short.MAX_VALUE).addGap(39)));
		gl_titlePanel.setVerticalGroup(gl_titlePanel.createParallelGroup(
				Alignment.TRAILING).addGroup(
				Alignment.LEADING,
				gl_titlePanel
						.createSequentialGroup()
						.addComponent(lblNewLabel_1, GroupLayout.DEFAULT_SIZE,
								45, Short.MAX_VALUE).addContainerGap()));
		titlePanel.setLayout(gl_titlePanel);

		btnConnect = new JButton("Connect to server");
		btnConnect.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (btnConnect.isEnabled())
					connectToServer();
			}
		});

		btnStartGame = new JButton("Start Game");
		btnStartGame.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (btnStartGame.isEnabled())
					startGame();
			}
		});
		btnStartGame.setEnabled(false);

		btnResign = new JButton("Resign");
		btnResign.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (btnResign.isEnabled())
					resign();
			}

		});
		btnResign.setEnabled(false);

		btnDisconnect = new JButton("Disconnect");
		btnDisconnect.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (btnDisconnect.isEnabled())
					disconnectFromServer();
			}
		});
		btnDisconnect.setEnabled(false);

		btnSetAnswer = new JButton("Set Answer");
		btnSetAnswer.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (btnSetAnswer.isEnabled())
					enableTesting();
			}
		});
		btnSetAnswer.setEnabled(false);
		GroupLayout gl_gameButtonPanel = new GroupLayout(gameButtonPanel);
		gl_gameButtonPanel
				.setHorizontalGroup(gl_gameButtonPanel
						.createParallelGroup(Alignment.LEADING)
						.addGroup(
								gl_gameButtonPanel
										.createSequentialGroup()
										.addGap(21)
										.addGroup(
												gl_gameButtonPanel
														.createParallelGroup(
																Alignment.LEADING,
																false)
														.addComponent(
																btnConnect,
																GroupLayout.DEFAULT_SIZE,
																GroupLayout.DEFAULT_SIZE,
																Short.MAX_VALUE)
														.addComponent(
																btnSetAnswer,
																GroupLayout.DEFAULT_SIZE,
																GroupLayout.DEFAULT_SIZE,
																Short.MAX_VALUE)
														.addComponent(
																btnStartGame,
																GroupLayout.DEFAULT_SIZE,
																GroupLayout.DEFAULT_SIZE,
																Short.MAX_VALUE)
														.addComponent(
																btnResign,
																GroupLayout.DEFAULT_SIZE,
																GroupLayout.DEFAULT_SIZE,
																Short.MAX_VALUE)
														.addComponent(
																btnDisconnect,
																GroupLayout.DEFAULT_SIZE,
																GroupLayout.DEFAULT_SIZE,
																Short.MAX_VALUE))
										.addContainerGap(10, Short.MAX_VALUE)));
		gl_gameButtonPanel.setVerticalGroup(gl_gameButtonPanel
				.createParallelGroup(Alignment.LEADING).addGroup(
						gl_gameButtonPanel
								.createSequentialGroup()
								.addComponent(btnSetAnswer)
								.addPreferredGap(ComponentPlacement.RELATED)
								.addComponent(btnConnect)
								.addPreferredGap(ComponentPlacement.UNRELATED)
								.addComponent(btnStartGame)
								.addPreferredGap(ComponentPlacement.UNRELATED)
								.addComponent(btnResign)
								.addPreferredGap(ComponentPlacement.UNRELATED)
								.addComponent(btnDisconnect)
								.addContainerGap(GroupLayout.DEFAULT_SIZE,
										Short.MAX_VALUE)));
		gameButtonPanel.setLayout(gl_gameButtonPanel);

		JLabel lblNbOfTriesText = new JLabel("Number of tries:");
		lblNbOfTriesText.setHorizontalAlignment(SwingConstants.CENTER);

		nbOfTriesLabel = new JLabel("0");
		GroupLayout gl_labelPanel = new GroupLayout(labelPanel);
		gl_labelPanel.setHorizontalGroup(gl_labelPanel.createParallelGroup(
				Alignment.LEADING).addGroup(
				gl_labelPanel
						.createSequentialGroup()
						.addContainerGap()
						.addComponent(lblNbOfTriesText,
								GroupLayout.PREFERRED_SIZE, 111,
								GroupLayout.PREFERRED_SIZE)
						.addGap(18)
						.addComponent(nbOfTriesLabel,
								GroupLayout.PREFERRED_SIZE, 35,
								GroupLayout.PREFERRED_SIZE)
						.addContainerGap(16, Short.MAX_VALUE)));
		gl_labelPanel
				.setVerticalGroup(gl_labelPanel
						.createParallelGroup(Alignment.LEADING)
						.addGroup(
								gl_labelPanel
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												gl_labelPanel
														.createParallelGroup(
																Alignment.BASELINE)
														.addComponent(
																lblNbOfTriesText,
																GroupLayout.PREFERRED_SIZE,
																28,
																GroupLayout.PREFERRED_SIZE)
														.addComponent(
																nbOfTriesLabel))
										.addContainerGap(24, Short.MAX_VALUE)));
		labelPanel.setLayout(gl_labelPanel);

		JPanel textInputPanel = new JPanel();
		GroupLayout gl_inputPanel = new GroupLayout(inputPanel);
		gl_inputPanel.setHorizontalGroup(gl_inputPanel.createParallelGroup(
				Alignment.LEADING).addGroup(
				gl_inputPanel
						.createSequentialGroup()
						.addGap(4)
						.addComponent(lblNewLabel, GroupLayout.PREFERRED_SIZE,
								150, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.UNRELATED)
						.addComponent(textInputPanel,
								GroupLayout.PREFERRED_SIZE, 259,
								GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(btnSubmit, GroupLayout.PREFERRED_SIZE,
								75, GroupLayout.PREFERRED_SIZE).addGap(18)));
		gl_inputPanel
				.setVerticalGroup(gl_inputPanel
						.createParallelGroup(Alignment.LEADING)
						.addGroup(
								gl_inputPanel
										.createSequentialGroup()
										.addGroup(
												gl_inputPanel
														.createParallelGroup(
																Alignment.TRAILING,
																false)
														.addComponent(
																lblNewLabel,
																Alignment.LEADING,
																GroupLayout.DEFAULT_SIZE,
																GroupLayout.DEFAULT_SIZE,
																Short.MAX_VALUE)
														.addGroup(
																Alignment.LEADING,
																gl_inputPanel
																		.createSequentialGroup()
																		.addContainerGap()
																		.addGroup(
																				gl_inputPanel
																						.createParallelGroup(
																								Alignment.TRAILING)
																						.addComponent(
																								btnSubmit)
																						.addComponent(
																								textInputPanel,
																								GroupLayout.PREFERRED_SIZE,
																								38,
																								GroupLayout.PREFERRED_SIZE))))
										.addContainerGap(16, Short.MAX_VALUE)));

		txtInput1 = new JTextField();
		txtInput1.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				txtInput1.setText("");
			}
		});
		txtInput1.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				validateInput(e);
			}
		});
		txtInput1.setColumns(10);

		txtInput2 = new JTextField();
		txtInput2.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				txtInput2.setText("");
			}
		});
		txtInput2.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				validateInput(e);
			}
		});
		txtInput2.setColumns(10);

		txtInput3 = new JTextField();
		txtInput3.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				txtInput3.setText("");
			}
		});
		txtInput3.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				validateInput(e);
			}
		});
		txtInput3.setColumns(10);

		txtInput4 = new JTextField();
		txtInput4.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				txtInput4.setText("");
			}
		});
		txtInput4.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				validateInput(e);
			}
		});
		txtInput4.setColumns(10);
		GroupLayout gl_textInputPanel = new GroupLayout(textInputPanel);
		gl_textInputPanel.setHorizontalGroup(gl_textInputPanel
				.createParallelGroup(Alignment.LEADING).addGroup(
						gl_textInputPanel
								.createSequentialGroup()
								.addContainerGap()
								.addComponent(txtInput1,
										GroupLayout.PREFERRED_SIZE, 53,
										GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(ComponentPlacement.RELATED)
								.addComponent(txtInput2,
										GroupLayout.PREFERRED_SIZE, 53,
										GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(ComponentPlacement.RELATED)
								.addComponent(txtInput3,
										GroupLayout.PREFERRED_SIZE, 53,
										GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(ComponentPlacement.RELATED)
								.addComponent(txtInput4,
										GroupLayout.PREFERRED_SIZE, 53,
										GroupLayout.PREFERRED_SIZE)
								.addContainerGap(83, Short.MAX_VALUE)));
		gl_textInputPanel
				.setVerticalGroup(gl_textInputPanel
						.createParallelGroup(Alignment.LEADING)
						.addGroup(
								gl_textInputPanel
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												gl_textInputPanel
														.createParallelGroup(
																Alignment.BASELINE)
														.addComponent(
																txtInput1,
																GroupLayout.PREFERRED_SIZE,
																20,
																GroupLayout.PREFERRED_SIZE)
														.addComponent(
																txtInput2,
																GroupLayout.PREFERRED_SIZE,
																GroupLayout.DEFAULT_SIZE,
																GroupLayout.PREFERRED_SIZE)
														.addComponent(
																txtInput3,
																GroupLayout.PREFERRED_SIZE,
																GroupLayout.DEFAULT_SIZE,
																GroupLayout.PREFERRED_SIZE)
														.addComponent(
																txtInput4,
																GroupLayout.PREFERRED_SIZE,
																GroupLayout.DEFAULT_SIZE,
																GroupLayout.PREFERRED_SIZE))
										.addContainerGap(18, Short.MAX_VALUE)));
		textInputPanel.setLayout(gl_textInputPanel);
		inputPanel.setLayout(gl_inputPanel);
		frame.getContentPane().setLayout(groupLayout);
	}
}
