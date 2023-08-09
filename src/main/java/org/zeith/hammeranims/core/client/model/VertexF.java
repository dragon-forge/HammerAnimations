package org.zeith.hammeranims.core.client.model;

import net.minecraft.world.phys.Vec3;

public class VertexF
{
	public Vec3 vector3D;
	public float texturePositionX;
	public float texturePositionY;
	
	public VertexF(float x, float y, float z, float u, float v)
	{
		this(new Vec3(x, y, z), u, v);
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
	
	public VertexF(Vec3 pos, float u, float v)
	{
		this.vector3D = pos;
		this.texturePositionX = u;
		this.texturePositionY = v;
	}
	
	public Vec3 subtractReverse(Vec3 vec)
	{
		return new Vec3(vec.x - vector3D.x, vec.y - vector3D.y, vec.z - vector3D.z);
	}
}