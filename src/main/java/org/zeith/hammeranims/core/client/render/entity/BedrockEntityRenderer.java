package org.zeith.hammeranims.core.client.render.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mojang.blaze3d.vertex.VertexBuilderUtils;
import lombok.var;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import org.jetbrains.annotations.NotNull;
import org.zeith.hammeranims.api.geometry.IGeometryContainer;
import org.zeith.hammeranims.api.tile.IAnimatedEntity;
import org.zeith.hammeranims.core.client.render.entity.proc.HeadLookProcessor;
import org.zeith.hammeranims.core.client.render.vertex.AccumulatingVertexConsumer;
import org.zeith.hammeranims.core.client.render.vertex.MultiVertexConsumer;

import java.util.ArrayList;
import java.util.List;

public abstract class BedrockEntityRenderer<T extends LivingEntity & IAnimatedEntity>
		extends LivingRenderer<T, BedrockModelWrapper<T>>
{
	private static class SelfRef<T extends LivingEntity & IAnimatedEntity>
	{
		BedrockEntityRenderer<T> self;
	}
	
	public BedrockEntityRenderer(EntityRendererManager pContext, IGeometryContainer geometry, float shadowSize)
	{
		this(pContext, geometry, shadowSize, new SelfRef<>());
	}
	
	private BedrockEntityRenderer(EntityRendererManager pContext, IGeometryContainer geometry, float shadowSize, SelfRef<T> ref)
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
	public void render(@NotNull T pEntity, float pEntityYaw, float pPartialTicks, @NotNull MatrixStack pMatrixStack, @NotNull IRenderTypeBuffer pBuffer, int pPackedLight)
	{
		var rp = getRenderPasses(pEntity);
		var entType = getRenderType(getTextureLocation(pEntity));
		
		List<AccumulatingVertexConsumer.IntoSource> accumulators = rp != null ? new ArrayList<>() : null;
		IRenderTypeBuffer multiBuffer = rp != null ? type ->
		{
			if(type == entType) switch(rp.size())
			{
				case 0:
					return new MultiVertexConsumer();
				case 1:
					return pBuffer.getBuffer(rp.get(0));
				case 2:
					return VertexBuilderUtils.create(AccumulatingVertexConsumer.register(accumulators, rp.get(0)), AccumulatingVertexConsumer.register(accumulators, rp.get(1)));
				default:
					return new MultiVertexConsumer(rp.stream().map(t2 -> AccumulatingVertexConsumer.register(accumulators, t2)).toArray(IVertexBuilder[]::new));
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