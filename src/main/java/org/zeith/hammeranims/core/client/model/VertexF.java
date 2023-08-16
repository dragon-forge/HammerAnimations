package org.zeith.hammeranims.core.client.model;

import org.zeith.hammeranims.joml.Vector3f;

public class VertexF
{
	private final Vector3f pos;
	private final float u;
	private final float v;
	
	public VertexF(Vector3f pos, float u, float v)
	{
		this.pos = pos;
		this.u = u;
		this.v = v;
	}
	
	public Vector3f getPos()
	{
		return pos;
	}
	
	public float getU()
	{
		return u;
	}
	
	public float getV()
	{
		return v;
	}
}