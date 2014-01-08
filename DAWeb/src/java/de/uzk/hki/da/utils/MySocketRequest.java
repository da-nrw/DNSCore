package de.uzk.hki.da.utils;

import java.io.BufferedWriter;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import de.uzk.hki.da.core.ActionDescription;

public class MySocketRequest{
	
	String server;
	int port;
	Socket requestSocket;
	Socket clientSocket;
	List<ActionDescription> myList;
	SocketChannel socketChannel =null;
	
	
	public MySocketRequest(String server, int port) {
		this.server = server;
		this.port = port;
		connect();
	}
	
	private void connect () {
	try {
	   socketChannel = SocketChannel.open();
    socketChannel.connect(new InetSocketAddress(server,port));
    socketChannel.configureBlocking(false);
    } catch (Exception e) {
    	e.printStackTrace();
    }
     
}
	public List<ActionDescription>getActions() {
	List<ActionDescription> myList = null;
		try {
			
		myList = new ArrayList<ActionDescription>();
		PrintWriter writer = new PrintWriter(socketChannel.socket().getOutputStream(), true);
		  
		writer.write("SHOW_ACTIONS"+"\r\n");
	
		ByteBuffer buffer = ByteBuffer.allocate(4092);
		int bytesRead = socketChannel.read(buffer);
		if(bytesRead > 0){
		    buffer.flip();
		    InputStream bais = new ByteArrayInputStream(buffer.array(), 0, buffer.limit());
		    ObjectInputStream ois = new ObjectInputStream(bais); 
		    myList = (List<ActionDescription>)ois.readObject();
		    ois.close();
		}
		} catch(Exception e) {
			e.printStackTrace();
		} 
		return myList;

	}
	
	
}
