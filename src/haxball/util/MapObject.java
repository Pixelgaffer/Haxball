/**
 * MapObject
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
package haxball.util;

import lombok.*;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.util.Vector;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
@RequiredArgsConstructor
@ToString
@EqualsAndHashCode
public abstract class MapObject {
	public Vector2D position = new Vector2D(0, 0);
	public Vector2D velocity = new Vector2D(0, 0);

	@NonNull
	@Getter
	private float radius;

	@NonNull
	private Dimension fieldSize;

	public float getCollisionDistance(@NonNull MapObject other) {
		return (getRadius() + other.getRadius());
	}

	public boolean collidesWith(@NonNull MapObject other) {
		return (position.distance(other.position) <= getCollisionDistance(other));
	}

	public void setInsideMap(boolean middle) {
		if (!isMoveable()) {
			return;
		}

		float distance = middle ? 0 : radius;

		if (position.getX() - distance < 0) {
			position = new Vector2D(distance, position.getY());
			velocity = new Vector2D(-velocity.getX(), velocity.getY());
		}
		if (position.getY() - distance < 0) {
			position = new Vector2D(position.getX(), distance);
			velocity = new Vector2D(velocity.getX(), -velocity.getY());
		}
		if (position.getX() + distance >= fieldSize.getWidth()) {
			position = new Vector2D(fieldSize.getWidth() - distance - 1, position.getY());
			velocity = new Vector2D(-velocity.getX(), velocity.getY());
		}
		if (position.getY() + distance >= fieldSize.getHeight()) {
			position = new Vector2D(position.getX(), fieldSize.getHeight() - distance - 1);
			velocity = new Vector2D(-velocity.getX(), -velocity.getY());
		}
	}
	
	public void uncollide(MapObject other) {
		if (!other.isMoveable()) {
			if (!isMoveable()) {
				return;
			}
			other.uncollide(this);
			return;
		}

		float dist = (float) position.distance(other.position);
		float mindist = getCollisionDistance(other);

		if (dist <= mindist) {
			float overlap = mindist - dist;
			Vector2D direction = other.position.subtract(position).normalize();
			Vector2D sum = other.velocity.add(velocity);
			if(!isMoveable()) {
				other.position = other.position.add(direction.scalarMultiply(overlap));
				other.velocity = other.velocity.add(sum.scalarMultiply(0.5));
			}
			else {
				other.position = other.position.add(direction.scalarMultiply(overlap / 2));
				other.velocity = other.velocity.add(sum.scalarMultiply(0.5));
				direction = direction.negate();
				sum = sum.negate();
				position = position.add(direction.scalarMultiply(overlap / 2));
				velocity = velocity.add(sum.scalarMultiply(0.5));
			}
		}
	}

	public abstract boolean isMoveable();
}
