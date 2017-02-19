package udp.client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import udp.segment.Segment;

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
