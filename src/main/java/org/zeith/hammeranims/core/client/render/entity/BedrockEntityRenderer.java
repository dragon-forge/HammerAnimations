package org.zeith.hammeranims.core.client.render.entity;

import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.zeith.hammeranims.api.geometry.IGeometryContainer;
import org.zeith.hammeranims.api.tile.IAnimatedEntity;
import org.zeith.hammeranims.core.client.render.entity.proc.HeadLookProcessor;
import org.zeith.hammeranims.core.client.render.vertex.AccumulatingVertexConsumer;

import java.util.ArrayList;
import java.util.List;

public abstract class BedrockEntityRenderer<T extends LivingEntity & IAnimatedEntity>
		extends LivingEntityRenderer<T, BedrockModelWrapper<T>>
{
	private static class SelfRef<T extends LivingEntity & IAnimatedEntity>
	{
		BedrockEntityRenderer<T> self;
	}
	
	public BedrockEntityRenderer(EntityRendererProvider.Context pContext, IGeometryContainer geometry, float shadowSize)
	{
		this(pContext, geometry, shadowSize, new SelfRef<>());
	}
	
	private BedrockEntityRenderer(EntityRendererProvider.Context pContext, IGeometryContainer geometry, float shadowSize, SelfRef<T> ref)
	{
		super(pContext, new BedrockModelWrapper<>(t -> ref.self.getRenderType(t), geometry), shadowSize);
		ref.self = this;
		addProcessors(model);
	}
	
	protected List<RenderType> getRenderPasses(T entity)
	{
		return null;
	}
	
	protected RenderType getRenderType(ResourceLocation texture)
	{
		return RenderType.entitySolid(texture);
	}
	
	@Override
	public void render(@NotNull T pEntity, float pEntityYaw, float pPartialTicks, @NotNull PoseStack pMatrixStack, @NotNull MultiBufferSource pBuffer, int pPackedLight)
	{
		var rp = getRenderPasses(pEntity);
		var entType = getRenderType(getTextureLocation(pEntity));
		
		List<AccumulatingVertexConsumer.IntoSource> accumulators = rp != null ? new ArrayList<>() : null;
		MultiBufferSource multiBuffer = rp != null ? type ->
		{
			if(type == entType)
			{
				return switch(rp.size())
				{
					case 0 -> VertexMultiConsumer.create(new VertexConsumer[0]);
					case 1 -> pBuffer.getBuffer(rp.get(0));
					case 2 -> VertexMultiConsumer.create(AccumulatingVertexConsumer.register(accumulators, rp.get(0)), AccumulatingVertexConsumer.register(accumulators, rp.get(1)));
					default -> VertexMultiConsumer.create(rp.stream().map(t2 -> AccumulatingVertexConsumer.register(accumulators, t2)).toArray(VertexConsumer[]::new));
				};
			}
			return pBuffer.getBuffer(type);
		} : pBuffer;
		
		model.entity = pEntity;
		model.buffers = multiBuffer;
		super.render(pEntity, pEntityYaw, pPartialTicks, pMatrixStack, multiBuffer, pPackedLight);
		model.entity = null;
		model.buffers = null;
		
		if(accumulators != null && !accumulators.isEmpty())
			for(AccumulatingVertexConsumer.IntoSource src : accumulators)
				src.applyAndReset(pBuffer);
	}
	
	@Override
	protected void setupRotations(@NotNull T pEntityLiving, @NotNull PoseStack pMatrixStack, float pAgeInTicks, float pRotationYaw, float pPartialTicks, float scale)
	{
		super.setupRotations(pEntityLiving, pMatrixStack, pAgeInTicks, pRotationYaw, pPartialTicks, scale);
		pMatrixStack.translate(0, 1.5F, 0);
		pMatrixStack.mulPose(Axis.XP.rotationDegrees(180f));
		pMatrixStack.mulPose(Axis.YP.rotationDegrees(180f));
	}
	
	protected void addProcessors(BedrockModelWrapper<T> model)
	{
		model.addProcessor(new HeadLookProcessor<>("head"));
	}
}