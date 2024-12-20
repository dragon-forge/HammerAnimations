package org.zeith.hammeranims.core.client.render;

import org.zeith.hammeranims.core.client.render.vertex.*;

public interface IVertexOutput
		extends IPrimitiveVertexOutput
{
	@Override
	default void vertex(float x, float y, float z,// position
						float red, float green, float blue, float alpha, // color
						float u, float v, // tex
						int packedOverlay, int packedLight, //
						float nx, float ny, float nz, // normal
						VertexType vType
	)
	{
		vertex(vType, new RenderVertex(x, y, z, red, green, blue, alpha, u, v, packedOverlay, packedLight, nx, ny, nz));
	}
	
	void vertex(VertexType type, RenderVertex... vertex);
	
	static IVertexOutput fromPrimitive(IPrimitiveVertexOutput out)
	{
		return (type, vertex) ->
		{
			for(RenderVertex rv : vertex)
			{
				out.vertex(rv.x, rv.y, rv.z, rv.red, rv.green, rv.blue, rv.alpha, rv.u, rv.v, rv.packedOverlay, rv.packedLight, rv.nx, rv.ny, rv.nz, type);
			}
		};
	}
}