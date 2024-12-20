package org.zeith.hammeranims.api.geometry.model;

import org.zeith.hammeranims.core.client.render.IVertexOutput;
import org.zeith.hammeranims.core.utils.PoseStack;

public class RenderData
{
	public int combinedLightIn = 0xF000D0, combinedOverlayIn;
	public float red = 1, green = 1, blue = 1, alpha = 1;
	public IVertexOutput renderer = IVertexOutput.fromPrimitive((x, y, z, red1, green1, blue1, alpha1, u, v, packedOverlay, packedLight, nx, ny, nz, vType) -> {});
	public PoseStack pose = new PoseStack();
	
	public void prepare()
	{
		pose.reset();
	}
}