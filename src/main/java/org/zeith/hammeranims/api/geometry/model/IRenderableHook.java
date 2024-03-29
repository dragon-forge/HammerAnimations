package org.zeith.hammeranims.api.geometry.model;

import org.zeith.hammeranims.core.client.render.IVertexRenderer;
import org.zeith.hammeranims.core.utils.IPoseEntry;

public interface IRenderableHook
{
	IRenderableHook NOTHING = (matrixEntryIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha) ->
	{};
	
	void render(IPoseEntry matrixEntryIn, IVertexRenderer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha);
}