package org.zeith.hammeranims.core.impl.api.geometry.decoder;

public class ModelMaterialInfo
{
	private final int textureWidth;
	private final int textureHeight;
	
	public ModelMaterialInfo(int textureWidth, int textureHeight)
	{
		this.textureWidth = textureWidth;
		this.textureHeight = textureHeight;
	}
	
	public int getTextureHeight()
	{
		return textureHeight;
	}
	
	public int getTextureWidth()
	{
		return textureWidth;
	}
}