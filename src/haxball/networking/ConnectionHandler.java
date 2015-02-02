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
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import static haxball.util.Serializer.byteArrayToInt;
import static haxball.util.Serializer.serializeDimension;

@RequiredArgsConstructor
public class ConnectionHandler implements Runnable
{
	@NonNull @Getter
	private Socket socket;

	@NonNull @Getter
	private Dimension fieldSize;

	@Getter
	private String name;

	@Getter
	private boolean gameStarted = false;

	public void startGame ()
	{
		gameStarted = true;
	}

	@Override
	public void run ()
	{
		try
		{
			InputStream in = socket.getInputStream();
			OutputStream out = socket.getOutputStream();

			// send dimension size
			out.write(serializeDimension(getFieldSize()));

			// read name from client
			byte b[] = new byte[4];
			if (in.read(b) != 4)
				throw new IOException();
			b = new byte[byteArrayToInt(b)];
			name = new String(b, StandardCharsets.UTF_8);

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
