package org.zeith.hammeranims.core.client.model;

import net.minecraft.util.math.Vec3d;

public class VertexF
{
	public Vec3d vector3D;
	public float texturePositionX;
	public float texturePositionY;
	
	public VertexF(float x, float y, float z, float u, float v)
	{
		this(new Vec3d(x, y, z), u, v);
	}
	
	public VertexF setTexturePosition(float u, float v)
	{
		return new VertexF(this, u, v);
	}
	
	public VertexF(VertexF vt, float u, float v)
	{
		this.vector3D = vt.vector3D;
		this.texturePositionX = u;
		this.texturePositionY = v;
	}
	
	public VertexF(Vec3d pos, float u, float v)
	{
		this.vector3D = pos;
		this.texturePositionX = u;
		this.texturePositionY = v;
	}
}