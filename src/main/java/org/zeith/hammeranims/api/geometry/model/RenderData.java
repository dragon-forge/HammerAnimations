package org.zeith.hammeranims.api.geometry.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.util.ResourceLocation;

public class RenderData
{
	public static final ResourceLocation MISSING_TEXTURE = new ResourceLocation("missing");
	
	public IVertexBuilder buffer;
	public int lighting, overlay;
	public float red = 1F, green = 1F, blue = 1F, alpha = 1F;
	public MatrixStack pose;
	
	public RenderData apply(MatrixStack pose, IVertexBuilder vertices, int light, int overlay)
	{
		this.pose = pose;
		this.buffer = vertices;
		this.lighting = light;
		this.overlay = overlay;
		return this;
	}
}