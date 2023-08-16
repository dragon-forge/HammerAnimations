package org.zeith.hammeranims.api.geometry.model;

import net.minecraft.util.ResourceLocation;
import org.zeith.hammeranims.core.client.render.IVertexRenderer;

public class RenderData
{
	public static final ResourceLocation MISSING_TEXTURE = new ResourceLocation("missing");
	
	public int combinedLightIn = 0xF000D0, combinedOverlayIn;
	public float red = 1, green = 1, blue = 1, alpha = 1;
	public ResourceLocation texture;
	public IVertexRenderer renderer;
}