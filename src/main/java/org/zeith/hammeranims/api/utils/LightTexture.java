package org.zeith.hammeranims.api.utils;

public class LightTexture
{
	public static final int FULL_BRIGHT = 15728880;
	public static final int FULL_SKY = 15728640;
	public static final int FULL_BLOCK = 240;
	
	public static int pack(int pBlockLight, int pSkyLight)
	{
		return pBlockLight << 4 | pSkyLight << 20;
	}
	
	public static int block(int pPackedLight)
	{
		return (pPackedLight & 0xFFFF) >> 4;
	}
	
	public static int sky(int pPackedLight)
	{
		return pPackedLight >> 20 & '\uffff';
	}
}
