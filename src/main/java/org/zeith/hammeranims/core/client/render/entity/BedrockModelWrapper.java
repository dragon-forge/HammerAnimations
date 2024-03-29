package org.zeith.hammeranims.core.client.render.entity;

import com.zeitheron.hammercore.utils.base.Cast;
import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.zeith.hammeranims.api.HammerAnimationsApi;
import org.zeith.hammeranims.api.animsys.AnimationSystem;
import org.zeith.hammeranims.api.geometry.IGeometryContainer;
import org.zeith.hammeranims.api.geometry.event.RefreshStaleModelsEvent;
import org.zeith.hammeranims.api.geometry.model.*;
import org.zeith.hammeranims.api.tile.IAnimatedEntity;
import org.zeith.hammeranims.core.client.render.entity.proc.*;
import org.zeith.hammeranims.joml.Math;

public class BedrockModelWrapper<T extends Entity & IAnimatedEntity>
		extends ModelBase
{
	protected final CentralModelProcessor<T> processor = new CentralModelProcessor<>();
	
	protected final IGeometryContainer modelContainer;
	protected IGeometricModel model;
	
	protected final RenderData renderData = new RenderData();
	public T entity;
	
	public BedrockModelWrapper(IGeometryContainer geometry)
	{
		this.modelContainer = geometry;
		HammerAnimationsApi.EVENT_BUS.register(this);
	}
	
	public void addProcessor(IProcessor<? super T> proc)
	{
		processor.add(proc);
	}
	
	@SubscribeEvent
	public void refreshGeometry(RefreshStaleModelsEvent e)
	{
		model = modelContainer.createModel();
	}
	
	public void applyAnimSystem(float partialTicks, AnimationSystem system)
	{
		GeometryPose pose = model.emptyPose();
		system.applyAnimation(partialTicks, pose);
		model.applyPose(pose);
		
		IRenderableBone root = model.getRoot();
		root.reset();
		root.getRotation().add(Math.DEG_TO_RAD * 180, Math.DEG_TO_RAD * 180, 0);
		root.getTranslation().add(0, -24, 0);
	}
	
	@Override
	public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn)
	{
		if(entityIn instanceof IAnimatedEntity)
		{
			IAnimatedEntity iae = (IAnimatedEntity) entityIn;
			applyAnimSystem(ageInTicks % 1, iae.getAnimationSystem());
			processor.apply(Cast.cast(entityIn), model, ageInTicks % 1, netHeadYaw, headPitch);
		}
	}
	
	@Override
	public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale)
	{
		model.renderModel(renderData);
	}
}