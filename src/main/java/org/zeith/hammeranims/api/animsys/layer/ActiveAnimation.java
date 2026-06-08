package org.zeith.hammeranims.api.animsys.layer;

import net.minecraft.core.HolderLookup;
import com.google.common.collect.ImmutableMap;
import dev.zeith.lzvm.LzVariableStore;
import net.minecraft.nbt.*;
import org.zeith.hammeranims.HammerAnimations;
import org.zeith.hammeranims.api.animation.*;
import org.zeith.hammeranims.api.animation.data.*;
import org.zeith.hammeranims.api.animation.interp.Query;
import org.zeith.hammeranims.api.animsys.ConfiguredAnimation;
import org.zeith.hammeranims.api.utils.ICompoundSerializable;
import org.zeith.hammeranims.core.init.DefaultsHA;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ActiveAnimation
		implements ICompoundSerializable
{
	public final AnimationLayer layer;
	public final Query query;
	
	public double activationTime;
	
	// Nano time implementation
	public long gameFreezeNanos = -1L;
	public long activationTimeNanos = System.nanoTime();
	
	// Properties
	public ConfiguredAnimation config;
	
	public boolean firedActions;
	
	// May be used to tweak animation's weight while it's active!
	public float realTimeWeight = 1F;
	
	public int lastTick;
	
	public final Map<String, BoneAnimationInstance> bones;
	
	public ActiveAnimation(HolderLookup.Provider lookup, AnimationLayer layer, CompoundTag tag, Query query)
	{
		this.layer = layer;
		this.query = query;
		deserializeNBT(lookup, tag);
		this.bones = instantiateBones(this.config, query);
	}
	
	public ActiveAnimation(AnimationLayer layer, ConfiguredAnimation config, Query query)
	{
		this.layer = layer;
		this.query = query;
		this.config = config;
		this.bones = instantiateBones(config, query);
	}
	
	public double elapsedSeconds(double sysTime)
	{
		if(!layer.useNanoTime)
			return sysTime - activationTime;
		
		long now = System.nanoTime();
		
		// Add support for game pausing
		if(isFrozen())
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
		
		return TimeUnit.NANOSECONDS.toMicros(nt) / 1_000_000D;
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
		return config.animation == null || (config.loopMode == LoopMode.ONCE && (
				config.animation.getData() == null
				|| elapsedSeconds(sysTime) * config.speed >= getLengthSeconds()
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
		
		if(layer.useNanoTime)
		{
			tag.putLong("NanoRelTime", System.nanoTime() - this.activationTimeNanos);
			if(this.gameFreezeNanos > 0L) tag.putLong("GameFreezeNano", System.nanoTime() - this.gameFreezeNanos);
		}
		
		return tag;
	}
	
	@Override
	public void deserializeNBT(HolderLookup.Provider lookup, CompoundTag tag)
	{
		this.config = new ConfiguredAnimation(lookup, tag);
		this.activationTime = tag.getDouble("ActivationTime");
		this.firedActions = tag.getBoolean("FiredActions");
		this.realTimeWeight = tag.getFloat("ActiveWeight");
		
		if(layer.useNanoTime)
		{
			if(tag.contains("NanoRelTime")) this.activationTimeNanos = System.nanoTime() - tag.getLong("NanoRelTime");
			else this.activationTimeNanos = System.nanoTime();
			
			if(tag.contains("GameFreezeNano")) this.gameFreezeNanos = System.nanoTime() - tag.getLong("GameFreezeNano");
			else this.gameFreezeNanos = -1L;
		}
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