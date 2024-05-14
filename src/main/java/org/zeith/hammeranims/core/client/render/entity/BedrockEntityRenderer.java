package org.zeith.hammeranims.core.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
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
	
	protected RenderType getRenderType(ResourceLocation texture)
	{
		return RenderType.entitySolid(texture);
	}
	
	@Override
	public void render(@NotNull T pEntity, float pEntityYaw, float pPartialTicks, @NotNull PoseStack pMatrixStack, @NotNull MultiBufferSource pBuffer, int pPackedLight)
	{
		model.entity = pEntity;
		model.buffers = pBuffer;
		super.render(pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
		model.entity = null;
		model.buffers = null;
	}
	
	@Override
	protected void setupRotations(@NotNull T pEntityLiving, @NotNull PoseStack pMatrixStack, float pAgeInTicks, float pRotationYaw, float pPartialTicks)
	{
		super.setupRotations(pEntityLiving, pMatrixStack, pAgeInTicks, pRotationYaw, pPartialTicks);
		pMatrixStack.translate(0, 1.5F, 0);
		pMatrixStack.mulPose(Axis.XP.rotationDegrees(180f));
		pMatrixStack.mulPose(Axis.YP.rotationDegrees(180f));
	}
	
	protected void addProcessors(BedrockModelWrapper<T> model)
	{
		model.addProcessor(new HeadLookProcessor<>("head"));
	}
}