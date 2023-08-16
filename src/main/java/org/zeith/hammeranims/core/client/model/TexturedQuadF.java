package org.zeith.hammeranims.core.client.model;

import org.zeith.hammeranims.joml.Vector3f;
import org.zeith.hammeranims.core.utils.EnumFacing;

public class TexturedQuadF
{
	public final VertexF[] vertices;
	public final Vector3f normal;
	
	public TexturedQuadF(VertexF[] vertices, boolean mirror, EnumFacing direction)
	{
		this.vertices = vertices;
		this.normal = new Vector3f(direction.offset.getX(), direction.offset.getY(), direction.offset.getZ());
		if(mirror) this.normal.mul(-1.0F, 1.0F, 1.0F);
	}
}