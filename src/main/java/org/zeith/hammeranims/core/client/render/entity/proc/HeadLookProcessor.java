package org.zeith.hammeranims.core.client.render.entity.proc;

import net.minecraft.entity.EntityLivingBase;
import org.zeith.hammeranims.api.geometry.model.*;
import org.zeith.hammeranims.api.tile.IAnimatedEntity;
import org.zeith.hammeranims.joml.Math;
import org.zeith.hammeranims.joml.Vector3f;

public class HeadLookProcessor<T extends EntityLivingBase & IAnimatedEntity>
		implements IProcessor<T>
{
	private final String headBoneName;
	
	public HeadLookProcessor(String headBoneName)
	{
		this.headBoneName = headBoneName;
	}
	
	@Override
	public void process(T entity, IGeometricModel model, float partialTick, float netHeadYaw, float headPitch)
	{
		IRenderableBone bone = model.getBone(this.headBoneName);
		if(bone == null) return;
		
		Vector3f rot = bone.getRotation();
		
		rot.sub(headPitch * Math.DEG_TO_RAD, netHeadYaw * Math.DEG_TO_RAD, 0);
	}
}
