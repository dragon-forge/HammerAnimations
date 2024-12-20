package org.zeith.hammeranims.api.animsys.layer;

import org.zeith.hammeranims.api.animation.AnimationLocation;
import org.zeith.hammeranims.api.animation.LoopMode;
import org.zeith.hammeranims.api.animation.data.IAnimationData;
import org.zeith.hammeranims.api.animsys.ConfiguredAnimation;
import org.zeith.hammeranims.core.init.DefaultsHA;

public class ActiveAnimation
{
	public double activationTime;
	
	// Properties
	public ConfiguredAnimation config;
	
	public boolean firedActions;
	
	// May be used to tweak animation's weight while it's active!
	public float realTimeWeight = 1F;
	
	public int lastTick;
	
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
	
	public float getWeight()
	{
		return realTimeWeight * config.weight * config.getAnimation().getData().getWeight();
	}
	
	public double getLengthSeconds()
	{
		return config.timeFunction.getLengthSeconds(this);
	}
}