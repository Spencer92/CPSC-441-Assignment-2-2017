package udp.client;

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
		
		
		
		
		
		/* send logic goes here. You may introduce addtional methods and classes*/
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
