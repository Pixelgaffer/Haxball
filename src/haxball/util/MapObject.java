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

@AllArgsConstructor(access = AccessLevel.PROTECTED)
@RequiredArgsConstructor
@ToString @EqualsAndHashCode
public class MapObject
{
	@Getter
	private Point position = new Point();

	@NonNull @Getter
	private float radius;

	@NonNull
	private Dimension fieldSize;

	public float getX ()
	{
		return position.getX();
	}

	public float getY ()
	{
		return position.getY();
	}

	public void setX (float x)
	{
		if ((x < 0) || (x > fieldSize.getWidth()))
			return;
		position.setX(x);
	}

	public void setY (float y)
	{
		if ((y < 0) || (y > fieldSize.getHeight()))
			return;
		position.setY(y);
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

	public boolean collidesWith (@NonNull MapObject other)
	{
		float xdist = Math.abs(getX() - other.getX());
		float ydist = Math.abs(getY() - other.getY());
		double dist = xdist * xdist + ydist * ydist;
		float mindist = getRadius() + other.getRadius();
		return (dist > mindist * mindist);
	}
}
