package udp.client;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
			byte readByte = 1;
			byte [] arrayByte = new byte[1];
			
			String stream = "";

			outputStream = new DataOutputStream(socket.getOutputStream());
			outputStream.writeUTF(this.file_name);
			outputStream.flush();
			inputStream = new DataInputStream(socket.getInputStream());

			readByte = inputStream.readByte();
			
			if(readByte == 0)
			{
				
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
                int timeout = 50; // milli-seconds (this value should not be changed)

		
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
