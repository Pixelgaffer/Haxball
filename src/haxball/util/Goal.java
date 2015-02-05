/**
 * Tor
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

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ToString
public class Goal
{
	@NonNull @Getter @Setter
	private Vector2D start, end;

	@Getter
	private GoalPost startPost, endPost;
	
	@Getter
	private float x;

	public Goal (Vector2D vector2d, Vector2D vector2d2, Dimension fieldSize, float x)
	{
		start = vector2d;
		end = vector2d;
		this.x = x;
		startPost = new GoalPost(start, fieldSize);
		startPost = new GoalPost(end, fieldSize);
	}

	public static Goal[] getDefaultGoals (@NonNull Dimension field)
	{
		return new Goal[] {
				new Goal(new Vector2D(5, field.getHeight() / 3f),
						new Vector2D(5, field.getHeight() / 3f * 2), field, 0),
				new Goal(new Vector2D(field.getWidth() - 5, field.getHeight() / 3f),
						new Vector2D(field.getWidth() - 5, field.getHeight() / 3f * 2), field, field.getWidth())
		};
	}
	
	public List<GoalPost> getPosts ()
	{
		return new ArrayList<GoalPost>();
	}

	public boolean hits (MapObject object)
	{
		// check whether y coordinate is between start and end
		if ((object.position.getY() <= start.getY()) ||
				(object.position.getY() >= end.getY()))
			return false;
		// check that the object collides with the line
		return ((object.position.getX() - object.getRadius() >= start.getX()) &&
				(object.position.getX() + object.getRadius() <= start.getX()));
	}
	
	public static class GoalPost extends MapObject
	{

		protected GoalPost (Vector2D position, Dimension fieldSize)
		{
			super(position, Vector2D.ZERO, 10, fieldSize);
		}

		@Override
		public boolean isMoveable ()
		{
			return false;
		}
		
	}
}
