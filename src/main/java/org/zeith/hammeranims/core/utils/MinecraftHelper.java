package org.zeith.hammeranims.core.utils;

import java.nio.*;

public class MinecraftHelper
{
	public static FloatBuffer createFloatBuf(int size)
	{
		return createByteBuf(size << 2).asFloatBuffer();
	}
	
	public static ByteBuffer createByteBuf(int size)
	{
		return ByteBuffer.allocateDirect(size).order(ByteOrder.nativeOrder());
	}
}