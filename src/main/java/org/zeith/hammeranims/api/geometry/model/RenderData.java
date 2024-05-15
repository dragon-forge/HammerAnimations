package org.zeith.hammeranims.api.geometry.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.util.ResourceLocation;
import org.zeith.hammeranims.core.client.render.IVertexRenderer;
import org.zeith.hammeranims.core.client.render.vertex.IVertexOperator;

public class RenderData
{
	public static final IVertexOperator[] NO_OP = new IVertexOperator[0];
	public static final ResourceLocation MISSING_TEXTURE = new ResourceLocation("missing");
	
	@Deprecated
	public IVertexBuilder buffer;
	
	public IVertexRenderer output;
	public int lighting, overlay;
	public float red = 1F, green = 1F, blue = 1F, alpha = 1F;
	public MatrixStack pose;
	
	public RenderData apply(MatrixStack pose, IVertexBuilder vertices, int light, int overlay)
	{
		return apply(pose, vertices, light, overlay, NO_OP);
	}
	
	public RenderData apply(MatrixStack pose, IVertexBuilder vertices, int light, int overlay, IVertexOperator... operator)
	{
		this.pose = pose;
		this.output = IVertexRenderer.wrap(buffer = vertices).apply(operator);
		this.lighting = light;
		this.overlay = overlay;
		return this;
	}
	
	public IVertexRenderer getOutput()
	{
		return output != null ? output : IVertexRenderer.wrap(buffer);
	}
}