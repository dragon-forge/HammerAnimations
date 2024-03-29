package org.zeith.hammeranims.core.client.render;

import com.mojang.blaze3d.vertex.IVertexBuilder;
import org.zeith.hammeranims.core.client.render.vertex.IVertexOperator;

public interface IVertexRenderer
{
	void vertex(float x, float y, float z,// position
				float red, float green, float blue, float alpha, // color
				float u, float v, // tex
				int packedOverlay, int packedLight, //
				float nx, float ny, float nz // normal
	);
	
	static IVertexRenderer wrap(IVertexBuilder bb)
	{
		return bb::vertex;
	}
	
	default IVertexRenderer apply(IVertexOperator op)
	{
		return op.apply(this);
	}
	
	default IVertexRenderer apply(IVertexOperator... ops)
	{
		IVertexRenderer r = this;
		for(IVertexOperator op : ops) r = op.apply(r);
		return r;
	}
}