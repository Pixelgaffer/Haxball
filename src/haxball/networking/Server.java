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
import haxball.util.Goal;
import haxball.util.Player;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class Server implements Runnable
{
	@NonNull @Getter
	private int port;

	@NonNull @Getter
	private Dimension fieldSize;

	@NonNull @Getter
	private Goal goals[];

	@Getter
	private boolean gameStarted = false;

	private byte currentId = Byte.MIN_VALUE;

	private List<ConnectionHandler> connectionHandlers;
	private List<Player>            players;

	public void startGame () throws IOException
	{
		gameStarted = true;

		// check validity of the players
		List<Byte> deletedPlayers = new ArrayList<>();
		players = new ArrayList<>();
		for (int i = 0; i < connectionHandlers.size(); )
		{
			ConnectionHandler handler = connectionHandlers.get(i);
			if (handler.getSocket().isClosed())
			{
				deletedPlayers.add(handler.getId());
				connectionHandlers.remove(i);
				continue;
			}
			players.add(handler.getPlayer());
			i++;
		}

		// create main loop
		ServerMainLoop mainLoop = new ServerMainLoop(getFieldSize(), getGoals(), players, connectionHandlers);

		boolean team = true;
		for (ConnectionHandler handler : connectionHandlers)
		{
			for (Byte id : deletedPlayers)
				handler.playerDisconnected(id);
			handler.getPlayer().setTeam(team);
			handler.startGame(team, players);
			handler.setMainLoop(mainLoop);
			new Thread(handler, "Handler-" + handler.getName()).start();
			team = !team;
		}

		// start main loop
		new Thread(mainLoop, "MainLoop").start();
	}

	@Override
	public void run ()
	{
		connectionHandlers = new ArrayList<>();

		try ( ServerSocket server = new ServerSocket(getPort()) )
		{
			System.out.println("Server opened sucessfully on port " + getPort());
			while (!server.isClosed() && !isGameStarted())
			{
				Socket client = server.accept();
				if (isGameStarted())
				{
					client.close();
					break;
				}
				ConnectionHandler handler = new ConnectionHandler(client, getFieldSize(), getGoals(), currentId++);
				for (ConnectionHandler h : connectionHandlers)
				{
					handler.playerConnected(h.getPlayer());
					h.playerConnected(handler.getPlayer());
				}
				connectionHandlers.add(handler);
				if (currentId == 0)
					currentId++;
			}
		}
		catch (IOException ioe)
		{
			ioe.printStackTrace();
		}
	}

	public static void main (String args[]) throws IOException
	{
		Dimension field = new Dimension(1000, 500);
		Goal goals[] = Goal.getDefaultGoals(field);
		Server server = new Server(1234, field, goals);
		new Thread(server).start();

		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		String line;
		while ((line = in.readLine()) != null)
		{
			if (line.equalsIgnoreCase("start"))
				server.startGame();
		}
	}
}
