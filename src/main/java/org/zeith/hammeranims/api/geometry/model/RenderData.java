package org.zeith.hammeranims.api.geometry.model;

import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public class RenderData
{
	public static final ResourceLocation MISSING_TEXTURE = new ResourceLocation("missing");
	
	public ResourceLocation texture = MISSING_TEXTURE;
	public MultiBufferSource buffers;
	public int lighting, overlay;
	public float red = 1F, green = 1F, blue = 1F, alpha = 1F;
	public Function<String, RenderType> renderTypeByBone = bone -> RenderType.entityCutout(texture);
	public PoseStack pose;
}