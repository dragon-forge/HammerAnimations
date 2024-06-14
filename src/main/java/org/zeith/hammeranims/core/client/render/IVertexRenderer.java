package org.zeith.hammeranims.core.client.render;

import com.mojang.blaze3d.vertex.VertexConsumer;
import org.zeith.hammeranims.core.client.render.vertex.IVertexOperator;
import org.zeith.hammerlib.util.colors.ColorHelper;

public interface IVertexRenderer
{
	void vertex(float x, float y, float z,// position
				float red, float green, float blue, float alpha, // color
				float u, float v, // tex
				int packedOverlay, int packedLight, //
				float nx, float ny, float nz // normal
	);
	
	static IVertexRenderer wrap(VertexConsumer bb)
	{
		return (x, y, z, red, green, blue, alpha, u, v, packedOverlay, packedLight, nx, ny, nz) ->
				bb.addVertex(x, y, z, ColorHelper.packARGB(red, green, blue, alpha), u, v, packedOverlay, packedLight, nx, ny, nz);
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