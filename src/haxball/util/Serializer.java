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

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Serializer
{
	public static byte[] intToByteArray (int value)
	{
		return ByteBuffer.allocate(4).putInt(value).array();
	}

	public static int byteArrayToInt (@NonNull byte data[])
	{
		return ByteBuffer.wrap(data).getInt();
	}

	public static byte[] floatToByteArray (float value)
	{
		return ByteBuffer.allocate(4).putFloat(value).array();
	}

	public static float byteArrayToFloat (@NonNull byte data[])
	{
		return ByteBuffer.wrap(data).getFloat();
	}

	public static byte[] serializeDimension (@NonNull Dimension d)
	{
		return serializeDimension(ConnectionType.NormalConnection, d);
	}

	public static byte[] serializeDimension (@NonNull ConnectionType type, @NonNull Dimension d)
	{
		if (type == ConnectionType.LaggyConnection)
			return ("{\"width\":" + d.getWidth() + ",\"height\":" + d.getHeight() + "}\0")
					.getBytes(StandardCharsets.UTF_8);

		byte width[] = intToByteArray(d.getWidth());
		byte height[] = intToByteArray(d.getHeight());
		return new byte[] { width[0], width[1], width[2], width[3], height[0], height[1], height[2], height[3] };
	}

	public static byte[] serializePoint (@NonNull Point p)
	{
		return serializePoint(ConnectionType.NormalConnection, p);
	}

	public static byte[] serializePoint (@NonNull ConnectionType type, @NonNull Point p)
	{
		if (type == ConnectionType.LaggyConnection)
			return ("{\"x\":" + p.getX() + ",\"y\":" + p.getY() + "}\0")
					.getBytes(StandardCharsets.UTF_8);

		byte x[] = floatToByteArray(p.getX());
		byte y[] = floatToByteArray(p.getY());
		return new byte[] { x[0], x[1], x[2], x[3], y[0], y[1], y[2], y[3] };
	}
	
	public static Point deserializePoint (@NonNull byte[] data)
	{
		Point point = new Point();
		byte[] b = { data[0], data[1], data[2], data[3] };
		point.setX(byteArrayToFloat(b));
		b = new byte[] { data[4], data[5], data[6], data[7] };
		point.setY(byteArrayToFloat(b));
		return point;
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

	public static byte[] serializeGoal (@NonNull Goal goal)
	{
		byte start[] = serializePoint(goal.getStart());
		byte end[] = serializePoint(goal.getEnd());
		return new byte[] { start[0], start[1], start[2], start[3], start[4], start[5], start[6], start[7], end[0],
				end[1], end[2], end[3], end[4], end[5], end[6], end[7] };
	}

	public static byte[] serializeState (Ball ball, byte score0, byte score1, Collection<Player> players)
	{
		return serializeState(ConnectionType.NormalConnection, ball, score0, score1, players);
	}

	public static byte[] serializeState (@NonNull ConnectionType type, @NonNull Ball ball, byte score0, byte score1,
			@NonNull Collection<Player> players)
	{
		if (type == ConnectionType.LaggyConnection)
		{
			StringBuilder sb = new StringBuilder();
			sb.append("{\"ball\":{\"x\":").append(ball.getPosition().getX())
					.append(",\"y\":").append(ball.getPosition().getY())
					.append(",\"velocity-x\":").append(ball.getVelocity().getX())
					.append(",\"velocity-y\":").append(ball.getVelocity().getY())
					.append("},");
			sb.append("\"score\":{\"red\":").append(score0).append(",\"blue\":").append(score1).append("},");
			sb.append("\"players\":[");
			for (Player p : players)
			{
				sb.append("{\"id\":").append(p.getId())
						.append(",\"x\":").append(p.getPosition().getX())
						.append(",\"y\":").append(p.getPosition().getY())
						.append(",\"velocity-x\":").append(p.getVelocity().getX())
						.append(",\"velocity-y\":").append(p.getVelocity().getY());
				sb.append("},");
			}
			sb.deleteCharAt(sb.length() - 1);
			sb.append("]}\0");
			return sb.toString().getBytes(StandardCharsets.UTF_8);
		}

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		baos.write(0xff);
		baos.write(score0);
		baos.write(score1);
		baos.write(serializePoint(ball.getPosition()), 0, 8);
		baos.write(serializePoint(ball.getVelocity()), 0, 8);
		for (Player p : players)
		{
			baos.write(p.getId());
			baos.write(p.isShooting() ? 0x01 : 0x02);
			baos.write(serializePoint(p.getPosition()), 0, 8);
			baos.write(serializePoint(p.getVelocity()), 0, 8);
		}
		baos.write(0x00);

		return baos.toByteArray();
	}
}
