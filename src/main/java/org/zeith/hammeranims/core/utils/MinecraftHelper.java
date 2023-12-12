package org.zeith.hammeranims.core.utils;

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
}