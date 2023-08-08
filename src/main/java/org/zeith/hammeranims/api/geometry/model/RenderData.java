package org.zeith.hammeranims.api.geometry.model;

import net.minecraft.util.ResourceLocation;

public class RenderData
{
	public static final ResourceLocation MISSING_TEXTURE = new ResourceLocation("missing");
	
	public ResourceLocation texture;
	// public Function<String, RenderType> renderTypeByBone = bone -> RenderType.entityCutout(MISSING_TEXTURE);
	// public PoseStack pose;
}