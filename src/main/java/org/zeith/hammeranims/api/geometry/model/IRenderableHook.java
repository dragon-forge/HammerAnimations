package org.zeith.hammeranims.api.geometry.model;

import com.mojang.blaze3d.vertex.PoseStack;
import org.zeith.hammeranims.core.client.render.IVertexRenderer;

public interface IRenderableHook
{
	IRenderableHook NOTHING = (matrixEntryIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha) ->
	{};
	
	void render(PoseStack.Pose matrixEntryIn, IVertexRenderer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha);
}