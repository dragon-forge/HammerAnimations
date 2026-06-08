package org.zeith.hammeranims.api.animsys.layer;

import dev.zeith.lzvm.LzVariableStore;
import org.zeith.hammeranims.api.animation.*;
import org.zeith.hammeranims.api.animation.data.*;
import org.zeith.hammeranims.api.animation.interp.Query;
import org.zeith.hammeranims.api.animsys.ConfiguredAnimation;
import org.zeith.hammeranims.core.init.DefaultsHA;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class ActiveAnimation
{
	public final AnimationLayer layer;
	public final Query query;
	
	public double activationTime;
	
	// Properties
	public ConfiguredAnimation config;
	
	public boolean firedActions;
	
	// May be used to tweak animation's weight while it's active!
	public float realTimeWeight = 1F;
	
	public int lastTick;
	
	public final Map<String, BoneAnimationInstance> bones;
	
	public ActiveAnimation(ConfiguredAnimation config, LzVariableStore vars)
	{
		this.layer = layer;
		this.query = query;
		this.config = config;
		this.bones = instantiateBones(this.config, query);
	}
	
	public double elapsedSeconds(double sysTime)
	{
		return sysTime - activationTime;
	}
	
	public boolean isFrozen()
	{
		return HammerAnimations.PROXY.isGamePaused() || layer.frozen;
	}
	
	public Map<String, BoneAnimationInstance> getBoneAnimations()
	{
		return bones;
	}
	
	public boolean isDone(double sysTime)
	{
		IAnimationData data;
		return config.animation == null || (config.loopMode == LoopMode.ONCE && (
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