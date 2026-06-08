package org.zeith.hammeranims.api.geometry.model;

import lombok.Getter;
import net.minecraft.util.ResourceLocation;
import org.zeith.hammeranims.HammerAnimations;
import org.zeith.hammeranims.core.client.render.IVertexRenderer;
import org.zeith.hammeranims.core.client.render.vertex.IVertexOperator;
import org.zeith.hammeranims.core.utils.PoseStack;

public class RenderData
{
	public static final IVertexOperator[] NO_OP = new IVertexOperator[0];
	public static final ResourceLocation MISSING_TEXTURE = new ResourceLocation("missing");
	
	@Getter
	public IVertexRenderer renderer = IVertexRenderer.DUMMY;
	
	public int lighting = 0xF000D0, overlay;
	public float red = 1, green = 1, blue = 1, alpha = 1;
	public PoseStack pose = new PoseStack();
	
	public ResourceLocation texture;
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
	
	public void apply()
	{
		apply(NO_OP);
	}
	
	public void apply(IVertexOperator... operators)
	{
		HammerAnimations.PROXY.initRD(this);
		renderer = renderer.apply(operators);
	}
}