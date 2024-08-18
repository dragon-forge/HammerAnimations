package org.zeith.hammeranims.core.utils;

import org.zeith.hammerlib.HammerLib;
import org.zeith.hammerlib.util.java.Cast;

import java.nio.*;

public class MinecraftHelper
{
	public static final double DEG_TO_RAD = Math.toRadians(1);
	public static final float DEG_TO_RAD_F = (float) Math.toRadians(1);
	
	public static FloatBuffer createFloatBuf(int size)
	{
		return createByteBuf(size << 2).asFloatBuffer();
	}
	
	public static ByteBuffer createByteBuf(int size)
	{
		return ByteBuffer.allocateDirect(size).order(ByteOrder.nativeOrder());
	}
	
	public static <T> Class<T> fetchClass(String name)
	{
		try
		{
			return Cast.cast(Class.forName(name));
		} catch(ClassNotFoundException ignored)
		{
		} catch(Throwable e)
		{
			if(e.getMessage().contains("invalid dist"))
			{
				HammerLib.LOG.warn("Attempted to load class from invalid dist: " + name, e);
			}
		}
		return null;
	}
}