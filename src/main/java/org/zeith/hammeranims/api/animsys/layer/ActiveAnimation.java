package org.zeith.hammeranims.api.animsys.layer;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import org.zeith.hammeranims.api.animation.AnimationLocation;
import org.zeith.hammeranims.api.animation.LoopMode;
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
	
	// May be used to tweak animation's weight while it's active!
	public float realTimeWeight = 1F;
	
	public ActiveAnimation(HolderLookup.Provider lookup, CompoundTag tag)
	{
		deserializeNBT(lookup, tag);
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
						|| (sysTime - activationTime) * config.speed >= getLengthSeconds()
		));
	}
	
	public AnimationLocation getLocation()
	{
		return config.animation != null ? config.animation.getLocation() : DefaultsHA.NULL_ANIM.get().getLocation();
	}
	
	@Override
	public CompoundTag serializeNBT(HolderLookup.Provider lookup)
	{
		var tag = config.serializeNBT(lookup);
		tag.putDouble("ActivationTime", activationTime);
		tag.putBoolean("FiredActions", firedActions);
		tag.putFloat("ActiveWeight", realTimeWeight);
		return tag;
	}
	
	@Override
	public void deserializeNBT(HolderLookup.Provider lookup, CompoundTag tag)
	{
		config = new ConfiguredAnimation(lookup, tag);
		this.activationTime = tag.getDouble("ActivationTime");
		this.firedActions = tag.getBoolean("FiredActions");
		if(tag.contains("ActiveWeight", Tag.TAG_ANY_NUMERIC)) this.realTimeWeight = tag.getFloat("ActiveWeight");
		else this.realTimeWeight = 1F;
	}
	
	public float getWeight()
	{
		return realTimeWeight * config.weight * config.getAnimation().getData().getWeight();
	}
	
	public double getLengthSeconds()
	{
		return config.timeFunction.getLengthSeconds(this);
	}
}