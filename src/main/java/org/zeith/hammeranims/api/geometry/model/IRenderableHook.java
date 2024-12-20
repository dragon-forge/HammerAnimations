package org.zeith.hammeranims.api.geometry.model;

import org.zeith.hammeranims.core.client.render.IVertexOutput;
import org.zeith.hammeranims.core.utils.PoseStack;

public interface IRenderableHook
{
	IRenderableHook NOTHING = (matrixEntryIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha) ->
	{};
	
	void render(PoseStack.Entry matrixEntryIn, IVertexOutput bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha);
}