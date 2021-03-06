package haxball.networking;

import haxball.util.Ball;
import haxball.util.Dimension;
import haxball.util.Player;
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

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

public class Client implements Runnable {

	private Socket socket;
	private InputStream in;
	private OutputStream out;

	@Getter
	private Dimension fieldSize;
	@Getter
	private Player player;
	@Getter
	private Map<Byte, Player> players = new HashMap<Byte, Player>();

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
	@Getter
	private Ball ball;
	

	public Client(String ipAdress, int port, String name) {
		try {
			socket = new Socket(ipAdress, port);
			in = socket.getInputStream();
			out = socket.getOutputStream();

			out.write(0x01);
			System.out.println("Send 1");

			System.out.println("Reading Field Size");
			byte[] fieldSizeBuffer = new byte[8];
			in.read(fieldSizeBuffer);
			fieldSize = Serializer.deserializeDimension(fieldSizeBuffer);
			System.out.println("Read Field Size");

			System.out.println("Reading id");
			byte[] idBuffer = new byte[1];
			in.read(idBuffer);
			System.out.println(idBuffer[0]);
			player = new Player(idBuffer[0], name, new Dimension());
			players.put(idBuffer[0], player);
			System.out.println("Read");

			{
				byte[] trash = new byte[33];
				in.read(trash);
			}

			byte[] nameBuffer = name.getBytes(StandardCharsets.UTF_8);
			System.out.println("Sending length");
			out.write(nameBuffer.length);
			System.out.println("Sending name");
			out.write(nameBuffer);
			System.out.println("Finished");

			ball = new Ball(new Vector2D(-100, -100),  new Vector2D(0, 0), fieldSize);
			
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
				if (!gameStarted) {
					byte[] codeBuffer = new byte[1];
					in.read(codeBuffer);
					byte code = codeBuffer[0];
					switch (code) {
					case 0x01: {
						byte[] buffer = new byte[2];
						in.read(buffer);
						byte uid = buffer[0];
						byte nameLength = buffer[1];
						buffer = new byte[nameLength];
						in.read(buffer);
						String name = new String(buffer, StandardCharsets.UTF_8);
						Player player = new Player(uid, name, fieldSize);
						players.put(uid, player);
						System.out.println("Player " + player.getName() + " has Joined!");
						for(val listener : userAddedListeners) {
							listener.userChange(code, players.get(code));
						}
						break;
					}
					case 0x02: {
						byte[] buffer = new byte[1];
						in.read(buffer);
						Player player = players.get(buffer[0]);
						players.remove(buffer[0]);
						for(val listener : userRemovedListeners) {
							listener.userChange(code, player);
						}
						break;
					}
					case 0x03: case 0x04: {
						System.out.println("0x03/0x04");
						while(true) {
							player.setTeam(code == 0x03);
							byte[] buffer = new byte[1];
							in.read(buffer);
							if(buffer[0] == 0x00) {
								break;
							}
							Player player = players.get(buffer[0]);
							System.out.println(buffer[0] + ": " + player);
							buffer = new byte[1];
							in.read(buffer);
							player.setTeam(buffer[0] == 0x01);
						}
						gameStarted = true;
						for(val listener : gameStartedListeners) {
							listener.run();
						}
						break;
					}
					}
				}
				else {
					byte[] buffer = new byte[1];
					in.read(buffer);
					if(buffer[0] == (byte)0xFF) {
						buffer = new byte[2];
						in.read(buffer);
						score0 = buffer[0];
						score1 = buffer[1];
						
						buffer = new byte[8];
						in.read(buffer);
                        ball.position = Serializer.deserializeVector(buffer);
						buffer = new byte[8];
						in.read(buffer);
                        ball.velocity = Serializer.deserializeVector(buffer);
						
						while(true) {
							buffer = new byte[1];
							in.read(buffer);
							if(buffer[0] == 0x00) {
								break;
							}
							Player player = players.get(buffer[0]);
							buffer = new byte[1];
							in.read(buffer);
							player.setShooting(buffer[0] == 0x01);
							buffer = new byte[8];
							in.read(buffer);
                            player.position = Serializer.deserializeVector(buffer);
							buffer = new byte[8];
							in.read(buffer);
                            player.velocity = Serializer.deserializeVector(buffer);
						}
					}
				}				
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public void sendInput(boolean up, boolean left, boolean down, boolean right, boolean space) throws IOException {
		byte input = 0b00000000;
		input |= up ? 0b00000001 : 0b00000000;
		input |= left ? 0b00000010 : 0b00000000;
		input |= down ? 0b00000100 : 0b00000000;
		input |= right ? 0b00001000 : 0b00000000;
		input |= space ? 0b00010000 : 0b00000000;
		out.write(input);
	}
	
	public static interface UserListener {
		public void userChange(byte id, Player player);
	}

}
