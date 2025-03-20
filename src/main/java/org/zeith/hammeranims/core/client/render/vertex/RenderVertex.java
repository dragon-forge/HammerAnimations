package org.zeith.hammeranims.core.client.render.vertex;

import lombok.*;

@With
@AllArgsConstructor
public class RenderVertex
{
	public float x, y, z;
	public float red, green, blue, alpha;
	public float u, v;
	public int packedOverlay, packedLight;
	public float nx, ny, nz;
}