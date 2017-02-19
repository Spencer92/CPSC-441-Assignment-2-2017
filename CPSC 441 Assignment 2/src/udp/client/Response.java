package udp.client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import udp.segment.Segment;


/**
 * What this is meant to do is check to see if the data was received
 * Currently non-functional
 * Taken in part from https://docs.oracle.com/javase/7/docs/api/javax/swing/Timer.html
 */
public class Response implements ActionListener
{
	private DatagramSocket clientSocket;
	private Segment receiveSegment;
	private byte readByte;
	private DatagramPacket receivePacket;
	private boolean received = false;
	
	
	public Response(DatagramSocket clientSocket, Segment receiveSegment, byte readByte,
			DatagramPacket receivePacket)
	{
		this.clientSocket = clientSocket;
		this.receiveSegment = receiveSegment;
		this.readByte = readByte;
		this.receivePacket = receivePacket;
	}
	
	

	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		try {
			clientSocket.receive(receivePacket);
			received = true;
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	
	}
	
	public boolean getReceived()
	{
		return this.received;
	}

}
