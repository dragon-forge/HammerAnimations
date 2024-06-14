package org.zeith.hammeranims.core.client.render.entity;

import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.*;
import org.zeith.hammeranims.api.HammerAnimationsApi;
import org.zeith.hammeranims.api.animsys.AnimationSystem;
import org.zeith.hammeranims.api.geometry.IGeometryContainer;
import org.zeith.hammeranims.api.geometry.event.RefreshStaleModelsEvent;
import org.zeith.hammeranims.api.geometry.model.*;
import org.zeith.hammeranims.api.tile.IAnimatedEntity;
import org.zeith.hammeranims.core.client.render.entity.proc.*;

import java.util.function.Function;

public class BedrockModelWrapper<T extends Entity & IAnimatedEntity>
		extends EntityModel<T>
{
	protected final CentralModelProcessor<T> processor = new CentralModelProcessor<>();
	
	protected final IGeometryContainer modelContainer;
	protected IGeometricModel model;
	
	protected final RenderData renderData = new RenderData();
	
	public MultiBufferSource buffers;
	public LivingEntity entity;
	
	public BedrockModelWrapper(Function<ResourceLocation, RenderType> pRenderType, IGeometryContainer geometry)
	{
		super(pRenderType);
		this.modelContainer = geometry;
		HammerAnimationsApi.EVENT_BUS.addListener(this::refreshGeometry);
	}
	
	public void addProcessor(IProcessor<? super T> proc)
	{
		processor.add(proc);
	}
	
	private void refreshGeometry(RefreshStaleModelsEvent e)
	{
		model = modelContainer.createModel();
	}
	
	public void applyAnimSystem(float partialTicks, AnimationSystem system)
	{
		GeometryPose pose = model.emptyPose();
		system.applyAnimation(partialTicks, pose);
		model.applyPose(pose);
	}
	
	@Override
	public void setupAnim(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch)
	{
		applyAnimSystem(ageInTicks % 1, entityIn.getAnimationSystem());
		processor.apply(entityIn, model, ageInTicks % 1);
	}
	
	@Override
	public void renderToBuffer(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, int colorIn)
	{
		renderData.apply(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn).applyColor(colorIn);
		model.renderModel(renderData);
	}
}