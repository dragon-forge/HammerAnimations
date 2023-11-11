package org.zeith.hammeranims.api.animsys.layer;

import net.minecraft.nbt.CompoundTag;
import org.zeith.hammeranims.api.animation.*;
import org.zeith.hammeranims.api.animation.data.IAnimationData;
import org.zeith.hammeranims.api.animsys.ConfiguredAnimation;
import org.zeith.hammeranims.api.utils.ICompoundSerializable;
import org.zeith.hammeranims.core.init.DefaultsHA;

public class ActiveAnimation
		implements ICompoundSerializable
{
	public double activationTime;
	
	// Properties
	public ConfiguredAnimation config;
	
	public boolean firedActions;
	
	public ActiveAnimation(CompoundTag tag)
	{
		deserializeNBT(tag);
	}
	
	public ActiveAnimation(ConfiguredAnimation config)
	{
		this.config = config;
	}
	
	public boolean isDone(double sysTime)
	{
		IAnimationData data;
		return config.animation == null
				|| (config.loopMode == LoopMode.ONCE && (
				(data = config.animation.getData()) == null
						|| (sysTime - activationTime) * config.speed >= data.getLengthSeconds()
		));
	}
	
	public AnimationLocation getLocation()
	{
		return config.animation != null ? config.animation.getLocation() : DefaultsHA.NULL_ANIM.get().getLocation();
	}
	
	@Override
	public CompoundTag serializeNBT()
	{
		var tag = config.serializeNBT();
		tag.putDouble("ActivationTime", activationTime);
		tag.putBoolean("FiredActions", firedActions);
		return tag;
	}
	
	@Override
	public void deserializeNBT(CompoundTag tag)
	{
		config = new ConfiguredAnimation(tag);
		this.activationTime = tag.getDouble("ActivationTime");
		this.firedActions = tag.getBoolean("FiredActions");
	}
	
	public float getWeight()
	{
		return config.weight * config.getAnimation().getData().getWeight();
	}
}