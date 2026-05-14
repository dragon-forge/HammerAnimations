package org.zeith.hammeranims.api.animsys.layer;

import dev.zeith.lzvm.LzVariableStore;
import org.zeith.hammeranims.api.animation.*;
import org.zeith.hammeranims.api.animation.data.*;
import org.zeith.hammeranims.api.animsys.ConfiguredAnimation;
import org.zeith.hammeranims.core.init.DefaultsHA;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class ActiveAnimation
{
	public double activationTime;
	
	// Nano time implementation
	public boolean useNanoTime;
	public long freezeRelativeNanoTime = -1L;
	public long activationTimeNanos = System.nanoTime();
	
	// Properties
	public ConfiguredAnimation config;
	
	public boolean firedActions;
	
	// May be used to tweak animation's weight while it's active!
	public float realTimeWeight = 1F;
	
	public int lastTick;
	
	public final Map<String, BoneAnimationInstance> bones;
	
	public ActiveAnimation(ConfiguredAnimation config, LzVariableStore vars)
	{
		this.config = config;
		this.bones = instantiateBones(this.config, vars);
	}
	
	public double elapsedSeconds(double sysTime)
	{
		if(!useNanoTime)
			return sysTime - activationTime;
		
		long now = System.nanoTime();
		
		long nt = now - this.activationTimeNanos;
		
		if(freezeRelativeNanoTime > 0) nt = freezeRelativeNanoTime;
		
		return TimeUnit.NANOSECONDS.toMicros(nt) / 1_000_000D;
	}
	
	public Map<String, BoneAnimationInstance> getBoneAnimations()
	{
		return bones;
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
	
	protected Map<String, BoneAnimationInstance> instantiateBones(ConfiguredAnimation config, LzVariableStore vars)
	{
		Map<String, BoneAnimationInstance> bones = new HashMap<>();
		for(Map.Entry<String, BoneAnimation> e : config.animation.getData().getBoneAnimations().entrySet())
			bones.put(e.getKey(), new BoneAnimationInstance(e.getValue(), vars));
		return bones;
	}
}