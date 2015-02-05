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

import haxball.util.Ball;
import haxball.util.Dimension;
import haxball.util.Goal;
import haxball.util.Goal.GoalPost;
import haxball.util.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.Random;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

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
	
	private Random random = new Random();

	public void stop() {
		stopped = true;
	}

	public void keyPressed(@NonNull Player player, byte key) {
		player.setLastInput(key);
	}

	private float friction = 0.05f;
	private float speed = 3.0f;
	private float acceleration = 0.2f;
	private float shootingPower = 12.5f;

	@Override
	public void run() {

		resetField();
		byte score0 = 0, score1 = 0;

		while (!stopped) {
			// Move players
			for (Player player : players) {

				byte input = player.getLastInput();

				float vx = 0;
				float vy = 0;

				if ((input & 0b00_00_00_01) != 0) {
					vy += speed;
				}
				if ((input & 0b00_00_00_10) != 0) {
					vx -= speed;
				}
				if ((input & 0b00_00_01_00) != 0) {
					vy -= speed;
				}
				if ((input & 0b00_00_10_00) != 0) {
					vx += speed;
				}
				if ((input & 0b00_01_00_00) != 0) {
					player.setShooting(true);
				} else {
					player.setShooting(false);
				}

				Vector2D v = new Vector2D(vx, vy);
				if (v.distance(Vector2D.ZERO) > 0) {
					v = v.normalize().scalarMultiply(speed);
				}

				player.velocity = player.velocity.add(v.subtract(player.velocity).scalarMultiply(acceleration));
				player.position = player.position.add(player.velocity);
			}

			// Move ball
			ball.velocity = ball.velocity.scalarMultiply(1 - friction);
			ball.position = ball.position.add(ball.velocity);


			// Check for collisions
			for (Player p : players) {
				// collisions between players
				for (Player p0 : players) {
					if (!p.equals(p0)) {
						p.uncollide(p0);
					}
				}
				p.uncollide(ball);
				p.setInsideMap(true);

				for(Goal goal : goals) {
					for(GoalPost post : goal.getPosts()) {
						p.uncollide(post);
					}
				}
				
				if(p.isShooting() && p.position.distance(ball.position) < (p.getRadius() + ball.getRadius()) * 1.4f) {
					Vector2D norm = ball.position.subtract(p.position).normalize();
					ball.velocity = ball.velocity.add(norm.scalarMultiply(shootingPower));
				}
				
			}
			int result;
			if((result = ball.setInsideMap(false)) >= 0) {
				if(result == 0) {
					score0++;
				}
				else {
					score1++;
				}
				resetField();
			}
			
			// send position to every connection
			for (ConnectionHandler handler : connectionHandlers) {
				handler.writeState(ball, score0, score1, players);
			}
			// sleep
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		} // while !stopped
	}
	
	public void resetField() {
		ball = new Ball(getFieldSize());
		for(Player p : players) {
			p.position = new Vector2D(random.nextDouble() * fieldSize.getWidth() * 0.5 + (p.isTeam() ? 0 : fieldSize.getWidth() * 0.5), random.nextDouble() * fieldSize.getHeight());
			p.velocity = Vector2D.ZERO;
		}
	}
}
