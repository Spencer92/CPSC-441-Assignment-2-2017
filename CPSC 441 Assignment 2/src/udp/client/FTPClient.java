package udp.client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.Timer;

import udp.segment.Segment;

/**
 * FastFTP Class
 * FastFtp implements a basic FTP application based on UDP data transmission and 
 * alternating-bit stop-and-wait concept
 * @author      XYZ
 * @version     1.0, 1 Feb 2017
 *
 */



public class FTPClient {

	private String server_name;
	private int server_port;
	private String file_name;
	private int timeout;
	private static final int SERVER_PORT = 5555;
	private DataOutputStream outputStream;
	private DataInputStream inputStream;
	private DataOutputStream outputStream2;
	private DataInputStream inputStream2;
	private static final int MAX_BYTE_SIZE = 1000;
	private static final int NO_DATA_RECEIVED = -1;
	private static final int ACK_ZERO = 0;
	private static final int ACK_ONE = 1;
	
	
    /**
     * Constructor to initialize the program 
     * 
     * @param serverName	server name
     * @param server_port	server port
     * @param file_name		name of file to transfer
     * @param timeout		Time out value (in milli-seconds).
     */
	public FTPClient(String server_name, int server_port, String file_name, int timeout) {
	
	/* Initialize values */
		this.server_name = server_name;
		this.server_port = server_port;
		this.file_name = file_name;
		this.timeout = timeout;

		
	}
	

    /**
     * Send file content as Segments
     * 
     */
	public void send() {
		
		try {
			Path path = Paths.get(this.file_name);
			byte [] fileByteInfo = Files.readAllBytes(path);
			Socket socket = new Socket(this.server_name,SERVER_PORT);
			byte checkForReceivedInfo = 1;
			byte [] dataToSend = new byte[MAX_BYTE_SIZE];
			Segment segment = new Segment();
			boolean segmentCheck = true;
			DatagramSocket clientSocket = new DatagramSocket();
			DatagramPacket sendPacket;
			DatagramPacket receivePacket;
			InetAddress IPAddress = InetAddress.getByName("localhost");
			byte [] ACKCheck = new byte[1];
			int indexFileInfo;
			int indexSender;

			
			//Used to write the data file information to the server
			//And see if a response was generated
			outputStream = new DataOutputStream(socket.getOutputStream());
			outputStream.writeUTF(this.file_name);
			outputStream.flush();
			inputStream = new DataInputStream(socket.getInputStream());
			checkForReceivedInfo = inputStream.readByte();

			
			if(checkForReceivedInfo == ACK_ZERO)
			{
				checkForReceivedInfo = NO_DATA_RECEIVED;
				indexFileInfo = 0;
				indexSender = 0;
				
				//Keep sending information out until there
				//No longer is any
				while(indexFileInfo < fileByteInfo.length)
				{
					
					//Used to initiate alternating values
					if(segmentCheck)
					{
						segment.setSeqNum(0);
						segmentCheck = false;
					}
					else
					{
						segment.setSeqNum(1);
						segmentCheck = true;
					}
					
					//Read the information from the file in order to send out a 1000 byte or less segment
					while(indexSender < dataToSend.length && indexFileInfo < fileByteInfo.length && indexSender < MAX_BYTE_SIZE)
					{
						dataToSend[indexSender] = fileByteInfo[indexFileInfo];
						indexSender++;
						indexFileInfo++;
					}
					indexSender = 0;
					
					//prepare data for sending to server
					segment.setPayload(dataToSend);
					sendPacket = new DatagramPacket(segment.getBytes(),segment.getLength(),IPAddress,SERVER_PORT);
					
					
					
					 // Keep sending until a response is given.
					 //Currently doesn't work with unreliable data send
					 
					do
					{
						checkForReceivedInfo = NO_DATA_RECEIVED;
						clientSocket.send(sendPacket);
						receivePacket = new DatagramPacket(ACKCheck,ACKCheck.length);
						clientSocket.receive(receivePacket);
						checkForReceivedInfo = receivePacket.getData()[0];	
						
						//segmentCheck is false for when receiving 0
						//and true for receiving 1
					}while((!segmentCheck && checkForReceivedInfo != ACK_ZERO) || 
							(segmentCheck && checkForReceivedInfo != ACK_ONE));
					
				}
			}
			else
			{
				throw new Exception("No response given!");
			}

			
			outputStream.writeByte(0);
			outputStream.close();
			inputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		
		
		/* send logic goes here. You may introduce addtional methods and classes*/
	}

	public boolean waitForResponse(byte readByte)
	{
		return (readByte != 0);	
	}
	

/**
     	* A simple test driver
    	 * 
     	*/
	public static void main(String[] args) {
		
		String server = "localhost";
		String file_name = "";
		int server_port = 8888;
                int timeout = 5000; // milli-seconds (this value should not be changed)

		
		// check for command line arguments
		if (args.length == 3) {
			// either provide 3 parameters
			server = args[0];
			server_port = Integer.parseInt(args[1]);
			file_name = args[2];
		}
		else {
			System.out.println("wrong number of arguments, try again.");
			System.out.println("usage: java FTPClient server port file");
			System.exit(0);
		}

		
		FTPClient ftp = new FTPClient(server, server_port, file_name, timeout);
		
		System.out.printf("sending file \'%s\' to server...\n", file_name);
		ftp.send();
		System.out.println("file transfer completed.");
	}

}
