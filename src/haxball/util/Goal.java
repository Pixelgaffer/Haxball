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

import lombok.*;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

@AllArgsConstructor @ToString
public class Goal
{
	@NonNull @Getter @Setter
	private Vector2D start, end;

	public static Goal[] getDefaultGoals (@NonNull Dimension field)
	{
		return new Goal[] {
				new Goal(new Vector2D(5, field.getHeight() / 3f),
						new Vector2D(5, field.getHeight() / 3f * 2)),
				new Goal(new Vector2D(field.getWidth() - 5, field.getHeight() / 3f),
						new Vector2D(field.getWidth() - 5, field.getHeight() / 3f * 2))
		};
	}
}
