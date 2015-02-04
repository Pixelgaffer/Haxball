/**
 * Ball
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

@ToString(callSuper = true) @EqualsAndHashCode(callSuper = true)
public class Ball extends MapObject
{
	public static final float RADIUS = Player.RADIUS / 2f;

	@Getter @Setter
	public Vector2D velocity;

	public Ball (@NonNull Vector2D position, Vector2D velocity, Dimension fieldSize)
	{
		super(position, RADIUS, fieldSize);
		this.velocity = velocity;
	}

	public Ball (Vector2D position, Dimension fieldSize)
	{
		this(position, new Vector2D(0d, 0d), fieldSize);
	}

	public Ball (@NonNull Dimension fieldSize)
	{
		this(new Vector2D(fieldSize.getWidth() / 2f, fieldSize.getHeight() / 2f), new Vector2D(0d, 0d), fieldSize);
	}
}
