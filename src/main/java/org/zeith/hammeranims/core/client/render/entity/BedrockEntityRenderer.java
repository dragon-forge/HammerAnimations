package org.zeith.hammeranims.core.client.render.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.vector.Vector3f;
import org.jetbrains.annotations.NotNull;
import org.zeith.hammeranims.api.tile.IAnimatedEntity;
import org.zeith.hammeranims.core.client.render.entity.proc.HeadLookProcessor;
import org.zeith.hammeranims.core.init.ContainersHA;

public abstract class BedrockEntityRenderer<T extends LivingEntity & IAnimatedEntity>
		extends LivingRenderer<T, BedrockModelWrapper<T>>
{
	public BedrockEntityRenderer(EntityRendererManager pContext)
	{
		super(pContext, new BedrockModelWrapper<>(RenderType::entitySolid, ContainersHA.BILLY_GEOM), 0.5F);
		addProcessors(model);
	}
	
	@Override
	public void render(@NotNull T pEntity, float pEntityYaw, float pPartialTicks, @NotNull MatrixStack pMatrixStack, @NotNull IRenderTypeBuffer pBuffer, int pPackedLight)
	{
		model.entity = pEntity;
		model.buffers = pBuffer;
		super.render(pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
		model.entity = null;
		model.buffers = null;
	}
	
	@Override
	protected void setupRotations(@NotNull T pEntityLiving, @NotNull MatrixStack pMatrixStack, float pAgeInTicks, float pRotationYaw, float pPartialTicks)
	{
		super.setupRotations(pEntityLiving, pMatrixStack, pAgeInTicks, pRotationYaw, pPartialTicks);
		pMatrixStack.translate(0, 1.5F, 0);
		pMatrixStack.mulPose(Vector3f.XP.rotationDegrees(180f));
		pMatrixStack.mulPose(Vector3f.YP.rotationDegrees(180f));
	}
	
	protected void addProcessors(BedrockModelWrapper<T> model)
	{
		model.addProcessor(new HeadLookProcessor<>("head"));
	}
}