/**
 * ConnectionHandler
 *
 * Copyright (C) 2015 Dominic S. Meiser <meiserdo@web.de>
 *
 * This work is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation; either version 2 of the License, or any later
 * version.
 *
 * This work is distributed in the hope that it will be useful, but without
 * any warranty; without even the implied warranty of merchantability or
 * fitness for a particular purpose. See version 2 and version 3 of the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package haxball.networking;

import haxball.util.Dimension;
import haxball.util.Serializer;
import lombok.Getter;
import lombok.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import static haxball.util.Serializer.serializeDimension;

public class ConnectionHandler implements Runnable
{
	@NonNull @Getter
	private Socket       socket;
	private InputStream  in;
	private OutputStream out;

	@Getter
	private ConnectionType type;

	@NonNull @Getter
	private Dimension fieldSize;

	@NonNull @Getter
	private byte id;

	@Getter
	private String name;

	@Getter
	private boolean gameStarted = false;

	public ConnectionHandler (@NonNull Socket socket, @NonNull Dimension fieldSize, byte id)
	{
		this.socket = socket;
		this.fieldSize = fieldSize;
		this.id = id;

		try
		{
			in = socket.getInputStream();
			out = socket.getOutputStream();

			// read type
			int typeInt = in.read();
			switch (typeInt)
			{
				case 1:
					type = ConnectionType.NormalConnection;
					out.write(id);
					break;
				case 2:
					type = ConnectionType.LaggyConnection;
					out.write(("{ \"id\": " + id + "}").getBytes(StandardCharsets.UTF_8));
					break;
				default:
					throw new IOException("Unknown type: " + typeInt);
			}

			// send dimension size
			out.write(serializeDimension(type, getFieldSize()));
			out.flush();

			// read name from client
			byte b[] = new byte[in.read()];
			if (in.read(b) != b.length)
				throw new IOException();
			name = new String(b, StandardCharsets.UTF_8);
			System.out.println("Connected client " + getSocket() + " as name " + name);
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace();
			try
			{
				socket.close();
			}
			catch (Exception e)
			{
			}
		}
	}

	public void playerConnected (String name) throws IOException
	{
		if (isGameStarted())
			throw new IllegalStateException();
		out.write(0x01);
		byte b[] = name.getBytes(StandardCharsets.UTF_8);
		out.write(Serializer.intToByteArray(b.length));
		out.write(b);
		out.flush();
	}

	public void playerDisconnected (String name) throws IOException
	{
		if (isGameStarted())
			throw new IllegalStateException();
		out.write(0x02);
		byte b[] = name.getBytes(StandardCharsets.UTF_8);
		out.write(Serializer.intToByteArray(b.length));
		out.write(b);
		out.flush();
	}

	public void startGame () throws IOException
	{
		out.write(0x03);
		gameStarted = true;
	}

	@Override
	public void run ()
	{
		try
		{
			// wait until the game has started

			// close socket
			socket.close();
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace();
		}
	}
}
