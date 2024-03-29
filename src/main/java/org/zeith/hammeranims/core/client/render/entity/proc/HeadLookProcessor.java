package org.zeith.hammeranims.core.client.render.entity.proc;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import org.zeith.hammeranims.api.geometry.model.IGeometricModel;
import org.zeith.hammeranims.api.tile.IAnimatedEntity;

public class HeadLookProcessor<T extends LivingEntity & IAnimatedEntity>
		implements IProcessor<T>
{
	private final String headBoneName;
	
	public HeadLookProcessor(String headBoneName)
	{
		this.headBoneName = headBoneName;
	}
	
	@Override
	public void process(T entity, IGeometricModel model, float partialTick)
	{
		var bone = model.getBone(this.headBoneName);
		if(bone == null) return;
		
		boolean falling = entity.getFallFlyingTicks() > 4;
		boolean swimming = entity.isVisuallySwimming();
		var headRot = bone.getRotation();
		float swimAmount = entity.getSwimAmount(partialTick);
		float headPitch = -Mth.lerp(partialTick, entity.xRotO, entity.getXRot());
		boolean shouldSit = entity.isPassenger() && entity.getVehicle() != null && entity.getVehicle().shouldRiderSit();
		float bodyYaw = -Mth.rotLerp(partialTick, entity.yBodyRotO, entity.yBodyRot);
		float headYaw = -Mth.rotLerp(partialTick, entity.yHeadRotO, entity.yHeadRot);
		float netHeadYaw = headYaw - bodyYaw;
		if(shouldSit && entity.getVehicle() instanceof LivingEntity)
		{
			LivingEntity livingentity = (LivingEntity) entity.getVehicle();
			bodyYaw = -Mth.rotLerp(partialTick, livingentity.yBodyRotO, livingentity.yBodyRot);
			netHeadYaw = headYaw - bodyYaw;
			
			float wrapped = Mth.wrapDegrees(netHeadYaw);
			if(wrapped < -85.0F) wrapped = -85.0F;
			if(wrapped >= 85.0F) wrapped = 85.0F;
			
			bodyYaw = headYaw - wrapped;
			if(wrapped * wrapped > 2500.0F) bodyYaw += wrapped * 0.2F;
			
			netHeadYaw = headYaw - bodyYaw;
		}
		
		headRot.add(0.0F, Mth.DEG_TO_RAD * netHeadYaw, 0.0F);
		
		if(falling)
		{
			headRot.add(0.7853982F, 0.0F, 0.0F);
		} else if(swimAmount > 0.0F)
		{
			if(swimming)
			{
				headRot.add(this.rotlerpRad(swimAmount, headRot.x(), 0.7853982F), 0.0F, 0.0F);
			} else
			{
				headRot.add(this.rotlerpRad(swimAmount, headRot.x(), Mth.DEG_TO_RAD * headPitch), 0.0F, 0.0F);
			}
		} else
		{
			headRot.add(Mth.DEG_TO_RAD * headPitch, 0.0F, 0.0F);
		}
	}
	
	private float rotlerpRad(float angleIn, float maxAngleIn, float mulIn)
	{
		float f = (mulIn - maxAngleIn) % 6.2831855F;
		if(f < -Mth.PI) f += 2 * Mth.PI;
		if(f >= Mth.PI) f -= 2 * Mth.PI;
		return maxAngleIn + angleIn * f;
	}
}
