package org.zeith.hammeranims.core.client.render.vertex;

import com.mojang.blaze3d.vertex.IVertexBuilder;

public class MultiVertexConsumer
		implements IVertexBuilder
{
	private final IVertexBuilder[] all;
	
	public MultiVertexConsumer(IVertexBuilder... all)
	{
		this.all = all;
	}
	
	@Override
	public IVertexBuilder vertex(double p_225582_1_, double p_225582_3_, double p_225582_5_)
	{
		for(IVertexBuilder b : all)
			b.vertex(p_225582_1_, p_225582_3_, p_225582_5_);
		return this;
	}
	
	@Override
	public IVertexBuilder color(int p_225586_1_, int p_225586_2_, int p_225586_3_, int p_225586_4_)
	{
		for(IVertexBuilder b : all)
			b.color(p_225586_1_, p_225586_2_, p_225586_3_, p_225586_4_);
		return this;
	}
	
	@Override
	public IVertexBuilder uv(float p_225583_1_, float p_225583_2_)
	{
		for(IVertexBuilder b : all)
			b.uv(p_225583_1_, p_225583_2_);
		return this;
	}
	
	@Override
	public IVertexBuilder overlayCoords(int p_225585_1_, int p_225585_2_)
	{
		for(IVertexBuilder b : all)
			b.overlayCoords(p_225585_1_, p_225585_2_);
		return this;
	}
	
	@Override
	public IVertexBuilder uv2(int p_225587_1_, int p_225587_2_)
	{
		for(IVertexBuilder b : all)
			b.uv2(p_225587_1_, p_225587_2_);
		return this;
	}
	
	@Override
	public IVertexBuilder normal(float p_225584_1_, float p_225584_2_, float p_225584_3_)
	{
		for(IVertexBuilder b : all)
			b.normal(p_225584_1_, p_225584_2_, p_225584_3_);
		return this;
	}
	
	@Override
	public void vertex(float p_225588_1_, float p_225588_2_, float p_225588_3_, float p_225588_4_, float p_225588_5_, float p_225588_6_, float p_225588_7_, float p_225588_8_, float p_225588_9_, int p_225588_10_, int p_225588_11_, float p_225588_12_, float p_225588_13_, float p_225588_14_)
	{
		for(IVertexBuilder b : all)
			b.vertex(p_225588_1_, p_225588_2_, p_225588_3_, p_225588_4_, p_225588_5_, p_225588_6_, p_225588_7_, p_225588_8_, p_225588_9_, p_225588_10_, p_225588_11_, p_225588_12_, p_225588_13_, p_225588_14_);
	}
	
	@Override
	public void endVertex()
	{
		for(IVertexBuilder b : all)
			b.endVertex();
	}
}
