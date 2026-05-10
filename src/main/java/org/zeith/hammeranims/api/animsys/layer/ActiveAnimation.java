package org.zeith.hammeranims.api.animsys.layer;

import com.google.common.collect.ImmutableMap;
import dev.zeith.lzvm.LzVariableStore;
import net.minecraft.nbt.*;
import org.zeith.hammeranims.HammerAnimations;
import org.zeith.hammeranims.api.animation.*;
import org.zeith.hammeranims.api.animation.data.*;
import org.zeith.hammeranims.api.animsys.ConfiguredAnimation;
import org.zeith.hammeranims.api.utils.ICompoundSerializable;
import org.zeith.hammeranims.core.init.DefaultsHA;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ActiveAnimation
		implements ICompoundSerializable
{
	public double activationTime;
	
	// Nano time implementation
	public boolean useNanoTime;
	public long freezeRelativeNanoTime = -1L;
	public long gameFreezeNanos = -1L;
	public long activationTimeNanos = System.nanoTime();
	
	// Properties
	public ConfiguredAnimation config;
	
	public boolean firedActions;
	
	// May be used to tweak animation's weight while it's active!
	public float realTimeWeight = 1F;
	
	public int lastTick;
	
	public final Map<String, BoneAnimationInstance> bones;
	
	public ActiveAnimation(CompoundTag tag, LzVariableStore vars)
	{
		deserializeNBT(tag);
		this.bones = instantiateBones(this.config, vars);
	}
	
	public ActiveAnimation(ConfiguredAnimation config, LzVariableStore vars)
	{
		this.config = config;
		this.bones = instantiateBones(config, vars);
	}
	
	public double elapsedSeconds(double sysTime)
	{
		if(!useNanoTime)
			return sysTime - activationTime;
		
		long now = System.nanoTime();
		
		// Add support for game pausing
		if(HammerAnimations.PROXY.isGamePaused())
		{
			if(gameFreezeNanos <= 0L)
				gameFreezeNanos = now;
			now = gameFreezeNanos;
		} else if(gameFreezeNanos > 0L)
		{
			long passsed = System.nanoTime() - gameFreezeNanos;
			this.activationTimeNanos += passsed;
			gameFreezeNanos = -1L;
		}
		
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
		return config.animation == null
				|| (config.loopMode == LoopMode.ONCE && (
				config.animation.getData() == null
						|| elapsedSeconds(sysTime) * config.speed >= getLengthSeconds()
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
		tag.putFloat("ActiveWeight", realTimeWeight);
		if(useNanoTime)
		{
			if(this.freezeRelativeNanoTime > 0L)
				tag.putLong("FrozenRelativeNanoTime", this.freezeRelativeNanoTime);
			tag.putLong("NanoRelTime", System.nanoTime() - this.activationTimeNanos);
		}
		return tag;
	}
	
	@Override
	public void deserializeNBT(CompoundTag tag)
	{
		config = new ConfiguredAnimation(tag);
		this.activationTime = tag.getDouble("ActivationTime");
		this.firedActions = tag.getBoolean("FiredActions");
		if(tag.contains("ActiveWeight", Tag.TAG_ANY_NUMERIC)) this.realTimeWeight = tag.getFloat("ActiveWeight");
		if(useNanoTime)
		{
			if(tag.contains("FrozenRelativeNanoTime"))
				this.freezeRelativeNanoTime = tag.getLong("FrozenRelativeNanoTime");
			if(tag.contains("NanoRelTime")) this.activationTimeNanos = System.nanoTime() - tag.getLong("NanoRelTime");
			else this.activationTimeNanos = System.nanoTime();
		} else this.realTimeWeight = 1F;
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