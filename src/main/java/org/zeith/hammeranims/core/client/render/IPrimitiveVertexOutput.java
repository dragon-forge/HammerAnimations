package org.zeith.hammeranims.core.client.render;

import org.zeith.hammeranims.core.client.render.vertex.VertexType;

public interface IPrimitiveVertexOutput
{
	void vertex(float x, float y, float z,// position
				float red, float green, float blue, float alpha, // color
				float u, float v, // tex
				int packedOverlay, int packedLight, //
				float nx, float ny, float nz, // normal
				VertexType vType
	);
}