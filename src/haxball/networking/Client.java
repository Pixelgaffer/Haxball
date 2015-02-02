package haxball.networking;

import haxball.util.Dimension;
import haxball.util.Serializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import lombok.val;

public class Client implements Runnable {

	private Socket socket;
	private InputStream in;
	private OutputStream out;

	@Getter
	private Dimension fieldSize;
	@Getter
	private byte id;
	@Getter
	private boolean team;
	@Getter
	private Map<Byte, String> names = new HashMap<Byte, String>();

	@Getter
	private List<UserListener> userAddedListeners = new ArrayList<UserListener>();
	@Getter
	private List<UserListener> userRemovedListeners = new ArrayList<UserListener>();
	@Getter
	private List<Runnable> gameStartedListeners = new ArrayList<Runnable>();
	
	@Getter
	@Setter
	private boolean running;

	@Getter
	private boolean gameStarted;
	
	@Getter
	private byte score0;
	@Getter
	private byte score1;
	

	public Client(String ipAdress, int port, String name) {
		try {
			socket = new Socket(ipAdress, port);
			in = socket.getInputStream();
			out = socket.getOutputStream();

			out.write(0x01);
			System.out.println("Send 1");
			
			System.out.println("Reading id");
			byte[] idBuffer = new byte[1];
			in.read(idBuffer);
			id = idBuffer[0];
			System.out.println("Read");
			
			System.out.println("Reading Field Size");
			byte[] fieldSizeBuffer = new byte[8];
			in.read(fieldSizeBuffer);
			fieldSize = Serializer.deserializeDimension(fieldSizeBuffer);
			System.out.println("Read Field Size");
			
			byte[] nameBuffer = name.getBytes(StandardCharsets.UTF_8);
			System.out.println("Sending length");
			out.write(nameBuffer.length);
			System.out.println("Sending name");
			out.write(nameBuffer);
			System.out.println("Finished");

			new Thread(this).start();

		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		running = true;

		try {
			while (running) {
				byte[] codeBuffer = new byte[1];
				in.read(codeBuffer);
				byte code = codeBuffer[0];

				if (!gameStarted) {
					switch (code) {
					case 0x01: {
						byte[] buffer = new byte[2];
						in.read(buffer);
						byte uid = buffer[0];
						byte nameLength = buffer[1];
						buffer = new byte[nameLength];
						in.read(buffer);
						String name = new String(buffer, StandardCharsets.UTF_8);
						names.put(uid, name);
						for(val listener : userAddedListeners) {
							listener.userChange(code, names.get(code));
						}
						break;
					}
					case 0x02: {
						byte[] buffer = new byte[1];
						in.read(buffer);
						String name = names.get(code);
						names.remove(buffer[0]);
						for(val listener : userRemovedListeners) {
							listener.userChange(code, name);
						}
						break;
					}
					case 0x03: case 0x04: {
						team = code == 0x04;
						gameStarted = true;
						for(val listener : gameStartedListeners) {
							listener.run();
						}
						break;
					}
					}
					return;
				}
				
				byte[] buffer = new byte[1];
				in.read(buffer);
				if(buffer[0] == 0xFF) {
					buffer = new byte[2];
					in.read(buffer);
					score0 = buffer[0];
					score1 = buffer[1];
					
					buffer = new byte[4];
					
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public static interface UserListener {
		public void userChange(byte id, String name);
	}

}
