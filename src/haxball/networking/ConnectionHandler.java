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

import static haxball.util.Serializer.serializeDimension;
import static haxball.util.Serializer.serializeGoal;
import static haxball.util.Serializer.serializeState;
import haxball.util.Ball;
import haxball.util.Dimension;
import haxball.util.Goal;
import haxball.util.Player;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

public class ConnectionHandler implements Runnable {
    @NonNull
    @Getter
    private Socket socket;
    private InputStream in;
    private OutputStream out;

    @NonNull
    @Getter
    private Dimension fieldSize;

    @NonNull
    @Getter
    private byte id;

    @Getter
    private String name;

    @Getter
    private Player player;

    @Getter
    private boolean gameStarted = false;

    @Getter
    @Setter
    private ServerMainLoop mainLoop;

    public ConnectionHandler(@NonNull Socket socket, @NonNull Dimension fieldSize, @NonNull Goal goals[], byte id) {
	this.socket = socket;
	this.fieldSize = fieldSize;
	this.id = id;

	try {
	    in = socket.getInputStream();
	    out = socket.getOutputStream();

	    // send dimension size
	    out.write(serializeDimension(getFieldSize()));
	    out.flush();

	    out.write(id);

	    out.write(goals.length);
	    for (Goal g : goals) {
		out.write(serializeGoal(g));
	    }

	    // read name from client
	    byte b[] = new byte[in.read()];
	    if (in.read(b) != b.length) {
		throw new IOException();
	    }
	    name = new String(b, StandardCharsets.UTF_8);
	    System.out.println("Connected client " + getSocket() + " as name " + name);

	    // create player
	    player = new Player(getId(), getName(), fieldSize);
	    player.position = new Vector2D(100, 100);
	} catch (Exception ioe) {
	    ioe.printStackTrace();
	    try {
		socket.close();
	    } catch (Exception e) {
	    }
	}
    }

    public void playerConnected(Player player) throws IOException {
	if (isGameStarted()) {
	    throw new IllegalStateException();
	}

	out.write(0x01);
	out.write(player.getId());
	byte b[] = player.getName().getBytes(StandardCharsets.UTF_8);
	out.write(b.length);
	out.write(b);
	out.flush();
    }

    public void playerDisconnected(Byte id) throws IOException {
	if (isGameStarted()) {
	    throw new IllegalStateException();
	}
	out.write(0x02);
	out.write(player.getId());
	out.flush();
    }

    public void startGame(boolean team0, Collection<Player> players) throws IOException {
	if (team0) {
	    out.write(0x03);
	}
	else {
	    out.write(0x04);
	}
	for (Player p : players) {
	    out.write(p.getId());
	    out.write(p.isTeam() ? 0x01 : 0x02);
	}
	out.write(0x00);
	gameStarted = true;
    }

    public void writeState(Ball ball, byte score0, byte score1, Collection<Player> players) {
	try {
	    if (!socket.isClosed())
		out.write(serializeState(ball, score0, score1, players));
	} catch (IOException ioe) {
	    ioe.printStackTrace();
	    try {
		socket.close();
	    } catch (IOException e) {
	    }
	}
    }

    @Override
    public void run() {
	try {
	    // wait until the game has started
	    while (!isGameStarted())
		Thread.sleep(10);

	    // when the client sends input data, notify the main loop
	    while (!socket.isClosed()) {
		int read = in.read();
		if (read == -1)
		    socket.close();
		else
		    getMainLoop().keyPressed(getPlayer(), (byte) read);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }
}
