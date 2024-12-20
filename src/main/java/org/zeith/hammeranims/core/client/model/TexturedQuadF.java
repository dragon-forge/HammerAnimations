package org.zeith.hammeranims.core.client.model;

import shaded.joml.Vector3f;
import org.zeith.hammeranims.core.client.render.vertex.RenderVertex;
import org.zeith.hammeranims.core.utils.EnumFacing;

public class TexturedQuadF
{
	public final VertexF[] vertices;
	public final RenderVertex[] renderedVertices;
	public final Vector3f normal;
	
	public TexturedQuadF(VertexF[] vertices, boolean mirror, EnumFacing direction)
	{
		this.vertices = vertices;
		this.normal = new Vector3f(direction.offset.x(), direction.offset.y(), direction.offset.z());
		this.renderedVertices = new RenderVertex[vertices.length];
		if(mirror) this.normal.mul(-1.0F, 1.0F, 1.0F);
	}
}