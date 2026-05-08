package org.zeith.hammeranims.api.animsys.layer;

import net.minecraft.core.HolderLookup;
import com.google.common.collect.ImmutableMap;
import dev.zeith.lzvm.LzVariableStore;
import net.minecraft.nbt.*;
import org.zeith.hammeranims.api.animation.*;
import org.zeith.hammeranims.api.animation.data.*;
import org.zeith.hammeranims.api.animsys.ConfiguredAnimation;
import org.zeith.hammeranims.api.utils.ICompoundSerializable;
import org.zeith.hammeranims.core.init.DefaultsHA;

import java.util.Map;

public class ActiveAnimation
		implements ICompoundSerializable
{
	public double activationTime;
	
	// Properties
	public ConfiguredAnimation config;
	
	public boolean firedActions;
	
	// May be used to tweak animation's weight while it's active!
	public float realTimeWeight = 1F;
	
	public int lastTick;
	
	public final Map<String, BoneAnimationInstance> bones;
	
	public ActiveAnimation(HolderLookup.Provider lookup, CompoundTag tag, LzVariableStore vars)
	{
		deserializeNBT(lookup, tag);
		this.bones = instantiateBones(this.config, vars);
	}
	
	public ActiveAnimation(ConfiguredAnimation config, LzVariableStore vars)
	{
		this.config = config;
		this.bones = instantiateBones(config, vars);
	}
	
	public Map<String, BoneAnimationInstance> getBoneAnimations()
	{
		return bones;
	}
	
	public boolean isDone(double sysTime)
	{
		return config.animation == null
				|| (config.loopMode == LoopMode.ONCE && (
				config.animation.getData() == null
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
	
	public double getLengthSeconds()
	{
		return config.timeFunction.getLengthSeconds(this);
	}
	
	public float getWeight()
	{
		return realTimeWeight * config.weight * config.getAnimation().getData().getWeight();
	}
	
	protected Map<String, BoneAnimationInstance> instantiateBones(ConfiguredAnimation config, LzVariableStore vars)
	{
		ImmutableMap.Builder<String, BoneAnimationInstance> bones = ImmutableMap.builder();
		for(Map.Entry<String, BoneAnimation> e : config.animation.getData().getBoneAnimations().entrySet())
			bones.put(e.getKey(), new BoneAnimationInstance(e.getValue(), vars));
		return bones.build();
	}
}