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
			byte [] fileBytes = Files.readAllBytes(path);
			ServerSocket serverSocket = new ServerSocket(this.server_port);
			Socket socket = new Socket(this.server_name,SERVER_PORT);
			Socket socket2 = new Socket(this.server_name, this.server_port);
			byte readByte = 1;
			byte [] arrayByte = new byte[MAX_BYTE_SIZE];
			Segment segment = new Segment();
			boolean segmentCheck = true;
			int waitTime = this.timeout;
			DatagramSocket clientSocket = new DatagramSocket();
			DatagramPacket sendPacket;
			DatagramPacket receivePacket;
			InetAddress IPAddress = InetAddress.getByName("localhost");
			byte [] ACKCheck = new byte[1];
			Segment receiveSegment = null;
			Timer timer;
			
			String stream = "";

			outputStream = new DataOutputStream(socket.getOutputStream());
			outputStream.writeUTF(this.file_name);
			outputStream.flush();
			inputStream = new DataInputStream(socket.getInputStream());
			

			readByte = inputStream.readByte();
			
			
			outputStream2 = new DataOutputStream(socket2.getOutputStream());
			inputStream2 = new DataInputStream(socket2.getInputStream());
			
			if(readByte == 0)
			{
				readByte = -1;
				int indexFileArray = 0;
				int indexByteArray = 0;
				while(indexFileArray < fileBytes.length)
				{
					while(indexByteArray < arrayByte.length && indexFileArray < fileBytes.length && indexByteArray < MAX_BYTE_SIZE)
					{
						arrayByte[indexByteArray] = fileBytes[indexFileArray];
						indexByteArray++;
						indexFileArray++;
					}
					indexByteArray = 0;
					segment.setPayload(arrayByte);
					segment.setSeqNum(0);
					System.out.println(segment.getLength());
					System.out.println(segment.getBytes().length);
					System.out.println(segment.getPayload().length);
					sendPacket = new DatagramPacket(segment.getBytes(),segment.getLength(),IPAddress,SERVER_PORT);
					
					
					if(segmentCheck)
					{
						segment.setSeqNum(0);
						do
						{
							readByte = -1;
							clientSocket.send(sendPacket);
							receivePacket = new DatagramPacket(ACKCheck,ACKCheck.length);
							
/*							ActionListener getResponse = new ActionListener(){
								@Override
								public void actionPerformed(ActionEvent arg0) {
									try {
										clientSocket.receive(receivePacket);
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									
								}
									};*/
							
//							Response getResponse = new Response(clientSocket, receiveSegment, readByte, receivePacket);
							clientSocket.receive(receivePacket);
							System.out.println(receivePacket);
//							timer = new Timer(5000,getResponse);
//							timer.start();
							
//								receiveSegment = new Segment(receivePacket);
								readByte = receivePacket.getData()[0];	
									
/*							while(timer.isRunning())
							{
								clientSocket.receive(receivePacket);
								receiveSegment = new Segment(receivePacket);
								readByte = receiveSegment.getBytes()[0];
								break;
							}*/
							
//							outputStream2.write(segment.getSeqNum()); //Using outputStream causes server closure
//							outputStream2.write(segment.getPayload());
//							outputStream2.flush();
/*
							while(waitTime != 0 && readByte != 0)
							{
								readByte = inputStream.readByte();
								waitTime--;
							}*/
						}while(readByte != 0);
						System.out.println("Recieved ack 0");
						segmentCheck = false;
					}
					else
					{
						segment.setSeqNum(1);
						do
						{
							outputStream2.write(segment.getSeqNum()); //Using outputStream causes server closure
							outputStream2.write(segment.getPayload());
							outputStream2.flush();
							
							while(waitTime != 0 && readByte != 1)
							{
								wait(1);
								readByte = inputStream.readByte();
								waitTime--;
							}
							
						}while(readByte != 1);
						segmentCheck = true;
					}

					readByte = -1;
					waitTime = this.timeout;
				}
			}
			else
			{
				throw new Exception("No response given!");
			}

			System.out.println(readByte);
			
			
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
