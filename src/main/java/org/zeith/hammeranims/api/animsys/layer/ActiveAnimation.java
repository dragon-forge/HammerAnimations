package org.zeith.hammeranims.api.animsys.layer;

import net.minecraft.nbt.NBTTagCompound;
import org.zeith.hammeranims.api.animation.*;
import org.zeith.hammeranims.api.animation.data.IAnimationData;
import org.zeith.hammeranims.api.animsys.ConfiguredAnimation;
import org.zeith.hammeranims.api.utils.ICompoundSerializable;
import org.zeith.hammeranims.core.init.DefaultsHA;

public class ActiveAnimation
		implements ICompoundSerializable
{
	public double startTime;
	
	// Properties
	public ConfiguredAnimation config;
	
	public boolean firedActions;
	
	public ActiveAnimation(NBTTagCompound tag)
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
						|| (sysTime - startTime) * config.speed >= data.getLengthSeconds()
		));
	}
	
	public AnimationLocation getLocation()
	{
		return config.animation != null ? config.animation.getLocation() : DefaultsHA.NULL_ANIM.get().getLocation();
	}
	
	@Override
	public NBTTagCompound serializeNBT()
	{
		NBTTagCompound tag = config.serializeNBT();
		tag.setDouble("StartTime", startTime);
		tag.setBoolean("FiredActions", firedActions);
		return tag;
	}
	
	@Override
	public void deserializeNBT(NBTTagCompound tag)
	{
		config = new ConfiguredAnimation(tag);
		this.startTime = tag.getDouble("StartTime");
		this.firedActions = tag.getBoolean("FiredActions");
	}
	
	public float getWeight()
	{
		return config.weight * config.getAnimation().getData().getWeight();
	}
}