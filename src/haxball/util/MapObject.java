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
@ToString @EqualsAndHashCode
public class MapObject
{
	@Getter
	public Vector2D position = new Vector2D(0d, 0d);

	@NonNull @Getter
	private float radius;

	@NonNull
	private Dimension fieldSize;

	public float getX ()
	{
		return (float) position.getX();
	}

	public float getY ()
	{
		return (float) position.getY();
	}

	public void setX (float x)
	{
		if (x < 0)
			x = 0;
		if (x > fieldSize.getWidth())
			x = fieldSize.getWidth();
        position = new Vector2D((double)x, position.getY());
	}

	public void setY (float y)
	{
		if (y < 0)
			y = 0;
		if (y > fieldSize.getHeight())
			y = fieldSize.getHeight();
        position = new Vector2D(position.getX(), (double)y);
	}

	public void setPosition (@NonNull Point position)
	{
		setX(position.getX());
		setY(position.getY());
	}

	public void setPosition (float x, float y)
	{
		setX(x);
		setY(y);
	}

	public float getDistance (@NonNull MapObject other)
	{
		float xdist = Math.abs(getX() - other.getX());
		float ydist = Math.abs(getY() - other.getY());
		double dist = xdist * xdist + ydist * ydist;
		return (float)Math.sqrt(dist);
	}

	public float getMinDistance (@NonNull MapObject other)
	{
		return (getRadius() + other.getRadius());
	}

	public boolean collidesWith (@NonNull MapObject other)
	{
		return (getDistance(other) > getMinDistance(other));
	}

	public void uncollide (@NonNull MapObject other)
	{
		float dist = getDistance(other);
		float mindist = getMinDistance(other);
		if (dist > mindist)
		{
			float distplus = (float)((dist - mindist) / 2.0);
			float ydiff = Math.abs(getY() - other.getY()) / 2f;
			float yminus = ydiff * distplus / dist;
			float xminus = (float)(
					Math.sqrt(Math.pow(dist + distplus, 2) - Math.pow(ydiff + yminus, 2))
							- Math.abs(getX() - other.getX()) / 2.0);

			if (getX() > other.getX())
			{
				setX(getX() + xminus);
				other.setX(other.getX() - xminus);
			}
			else
			{
				setX(getX() - xminus);
				other.setX(other.getX() + xminus);
			}

			if (getY() > other.getY())
			{
				setY(getY() + yminus);
				other.setY(other.getY() - yminus);
			}
			else
			{
				setY(getY() - yminus);
				other.setY(other.getY() + yminus);
			}
		}
	}
}
