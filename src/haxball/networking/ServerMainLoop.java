/**
 * ServerMainLoop
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

import haxball.util.*;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.HashMap;

@RequiredArgsConstructor
public class ServerMainLoop implements Runnable {
	@NonNull
	@Getter
	private Dimension fieldSize;

	@NonNull
	@Getter
	private Goal goals[];

	@Getter
	private Ball ball;

	@NonNull
	@Getter
	private Collection<Player> players;
	@NonNull
	@Getter
	private Collection<ConnectionHandler> connectionHandlers;

	private HashMap<Player, ConnectionHandler> handlers;

	@Getter
	private boolean stopped;

	public void stop() {
		stopped = true;
	}

	public void keyPressed(@NonNull Player player, byte key) {
		player.setLastInput(key);
	}
	
	private float friction = 0.005f;
	private float speed = 0.5f;

	@Override
	public void run() {
		System.out.println("ServerMainLoop running");

		ball = new Ball(getFieldSize());
		byte score0 = 0, score1 = 0;

		while (!stopped) {
			
			for(Player player : players) {
				if(player.getVelocity().getX() > 0) {
					player.getVelocity().setX(Math.max(player.getVelocity().getX() - friction, 0));
				}
				if(player.getVelocity().getX() < 0) {
					player.getVelocity().setX(Math.min(player.getVelocity().getX() - friction, 0));
				}
				if(player.getVelocity().getY() > 0) {
					player.getVelocity().setY(Math.max(player.getVelocity().getY() - friction, 0));
				}
				if(player.getVelocity().getY() < 0) {
					player.getVelocity().setY(Math.min(player.getVelocity().getY() - friction, 0));
				}
				
				byte input = player.getLastInput();
				
				if((input & 0b00_00_00_01) != 0) {
					player.getVelocity().setY(speed);
				}
				if((input & 0b00_00_00_10) != 0) {
					player.getVelocity().setX(-speed);
				}
				if((input & 0b00_00_01_00) != 0) {
					player.getVelocity().setY(-speed);
				}
				if((input & 0b00_00_10_00) != 0) {
					player.getVelocity().setX(speed);
				}
				
				player.getPosition().setX(player.getPosition().getX() + player.getVelocity().getX());
				player.getPosition().setY(player.getPosition().getY() + player.getVelocity().getY());
			}
			
			
			
			
			for (ConnectionHandler handler : connectionHandlers)
				handler.writeState(ball, score0, score1, players);

			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
