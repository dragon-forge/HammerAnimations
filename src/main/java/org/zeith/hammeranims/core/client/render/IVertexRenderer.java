package org.zeith.hammeranims.core.client.render;

import com.mojang.blaze3d.vertex.VertexConsumer;

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
		return bb::vertex;
	}
}