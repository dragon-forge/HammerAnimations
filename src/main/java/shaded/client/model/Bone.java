package shaded.client.model;

public class Bone
{
	public float textureWidth;
	public float textureHeight;
	public boolean isHidden;
	public final String name;
	public float offsetX;
	public float offsetY;
	public float offsetZ;
	
	public Bone(String name)
	{
		this.name = name;
	}
	
	public Bone size(int textureWidthIn, int textureHeightIn)
	{
		this.textureWidth = (float) textureWidthIn;
		this.textureHeight = (float) textureHeightIn;
		return this;
	}
}