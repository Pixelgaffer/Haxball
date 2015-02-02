/**
 * Server
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
import java.net.ServerSocket;
import java.net.Socket;

@RequiredArgsConstructor
public class Server implements Runnable
{
	@NonNull @Getter
	private int port;

	@NonNull @Getter
	private Dimension fieldSize;

	@Getter
	private boolean gameStarted = false;

	public void startGame ()
	{
		gameStarted = true;
	}

	@Override
	public void run ()
	{
		try ( ServerSocket server = new ServerSocket(getPort()) )
		{
			while (!server.isClosed() && !isGameStarted())
			{
				Socket client = server.accept();
				if (isGameStarted())
				{
					client.close();
					break;
				}
			}
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace();
		}
	}
}
