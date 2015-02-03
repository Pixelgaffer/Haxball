/**
 * Player
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

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString @EqualsAndHashCode(callSuper = true)
public class Player extends MapObject
{
	public static final float RADIUS = 3f / 100f;

	public Player (byte id, String name)
	{
		super(RADIUS);
		this.id = id;
		this.name = name;
	}

	@Getter
	private byte id;

	@Getter
	private String name;
	
	@Getter @Setter
	private Point velocity = new Point();
	
	@Setter @Getter
	private boolean team;
	
	@Getter @Setter
	private boolean shooting;

	@Getter @Setter
	private byte lastInput;
}
