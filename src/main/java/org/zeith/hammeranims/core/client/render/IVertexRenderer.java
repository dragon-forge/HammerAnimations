package org.zeith.hammeranims.core.client.render;

import com.mojang.blaze3d.vertex.VertexConsumer;
import org.zeith.hammeranims.api.geometry.model.ISplitVertexConsumer;
import org.zeith.hammeranims.core.client.render.vertex.*;
import org.zeith.hammerlib.util.colors.ColorHelper;

public interface IVertexRenderer
{
	void vertex(float x, float y, float z,// position
				float red, float green, float blue, float alpha, // color
				float u, float v, // tex
				int packedOverlay, int packedLight, //
				float nx, float ny, float nz, // normal
				VertexType vType
	);
	
	static IVertexRenderer wrap(VertexConsumer bb)
	{
		return (x, y, z, red, green, blue, alpha, u, v, packedOverlay, packedLight, nx, ny, nz, vType) ->
				bb.addVertex(x, y, z, ColorHelper.packARGB(red, green, blue, alpha), u, v, packedOverlay, packedLight, nx, ny, nz);
	}
	
	static IVertexRenderer wrap(ISplitVertexConsumer bb)
	{
		return (x, y, z, r, g, b, a, u, v, over, light, nx, ny, nz, vType) ->
				bb.byVertexType(vType).addVertex(x, y, z, ColorHelper.packARGB(r, g, b, a), u, v, over, light, nx, ny, nz);
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