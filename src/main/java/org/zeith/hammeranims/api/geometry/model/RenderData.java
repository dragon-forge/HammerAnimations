package org.zeith.hammeranims.api.geometry.model;

import net.minecraft.util.ResourceLocation;
import org.zeith.hammeranims.HammerAnimations;
import org.zeith.hammeranims.core.client.render.IVertexRenderer;
import org.zeith.hammeranims.core.client.render.vertex.IVertexOperator;
import org.zeith.hammeranims.core.utils.PoseStack;

public class RenderData
{
	public static final ResourceLocation MISSING_TEXTURE = new ResourceLocation("missing");
	
	public int combinedLightIn = 0xF000D0, combinedOverlayIn;
	public float red = 1, green = 1, blue = 1, alpha = 1;
	public ResourceLocation texture;
	public IVertexRenderer renderer = IVertexRenderer.DUMMY;
	public PoseStack pose = new PoseStack();
	public boolean resetPoseAfterDraw = true;
	
	public RenderData(ResourceLocation texture)
	{
		this.texture = texture;
		HammerAnimations.PROXY.initRD(this);
	}
	
	public RenderData()
	{
		this(MISSING_TEXTURE);
	}
	
	public void prepare()
	{
		pose.reset();
	}
	
	public void apply(IVertexOperator... operators)
	{
		HammerAnimations.PROXY.initRD(this);
		renderer = renderer.apply(operators);
	}
}