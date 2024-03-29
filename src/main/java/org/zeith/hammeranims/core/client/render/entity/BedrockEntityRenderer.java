package org.zeith.hammeranims.core.client.render.entity;

import com.zeitheron.hammercore.utils.base.Cast;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.entity.EntityLivingBase;
import org.zeith.hammeranims.api.geometry.IGeometryContainer;
import org.zeith.hammeranims.api.geometry.model.RenderData;
import org.zeith.hammeranims.api.tile.IAnimatedEntity;
import org.zeith.hammeranims.core.client.render.entity.proc.HeadLookProcessor;

public abstract class BedrockEntityRenderer<T extends EntityLivingBase & IAnimatedEntity>
		extends RenderLivingBase<T>
{
	protected final BedrockModelWrapper<T> model;
	
	public BedrockEntityRenderer(RenderManager pContext, IGeometryContainer geometry, float shadowSize)
	{
		super(pContext, new BedrockModelWrapper<>(geometry), shadowSize);
		this.model = Cast.cast(getMainModel());
		addProcessors(this.model);
	}
	
	@Override
	protected void renderModel(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor)
	{
		boolean flag = this.isVisible(entity);
		boolean flag1 = !flag && !entity.isInvisibleToPlayer(Minecraft.getMinecraft().player);
		
		if(!flag && !flag1) return;
		
		RenderData data = model.renderData;
		data.prepare();
		data.texture = getEntityTexture(entity);
		data.combinedLightIn = entity.getBrightnessForRender();
		
		model.entity = entity;
		model.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
		model.entity = null;
	}
	
	protected void addProcessors(BedrockModelWrapper<T> model)
	{
		model.addProcessor(new HeadLookProcessor<>("head"));
	}
}