/**
 * Serializer
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

import haxball.networking.ConnectionType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Serializer
{
	public static byte[] intToByteArray (int value)
	{
		return new byte[] {
				(byte)(value >>> 24),
				(byte)(value >>> 16),
				(byte)(value >>> 8),
				(byte)value
		};
	}

	public static int byteArrayToInt (@NonNull byte data[])
	{
		return (((int)data[3]) | (((int)data[2]) << 8) | (((int)data[1]) << 16) | (((int)data[0]) << 24));
	}

	public static byte[] serializeDimension (@NonNull Dimension d)
	{
		return serializeDimension(ConnectionType.NormalConnection, d);
	}

	public static byte[] serializeDimension (@NonNull ConnectionType type, @NonNull Dimension d)
	{
		if (type == ConnectionType.LaggyConnection)
			return ("{ \"width\": " + d.getWidth() + ", \"height\": " + d.getHeight() + "}")
					.getBytes(StandardCharsets.UTF_8);

		byte width[] = intToByteArray(d.getWidth());
		byte height[] = intToByteArray(d.getHeight());
		return new byte[] { width[0], width[1], width[2], width[3], height[0], height[1], height[2], height[3] };
	}

	public static Dimension deserializeDimension (@NonNull byte data[])
	{
		Dimension d = new Dimension();
		byte b[] = { data[0], data[1], data[2], data[3] };
		d.setWidth(byteArrayToInt(b));
		b = new byte[] { data[4], data[5], data[6], data[7] };
		d.setHeight(byteArrayToInt(b));
		return d;
	}

	public static byte[] serializeState (Point ball, byte score0, byte score1, HashMap<Byte, >)
	{

	}
}
