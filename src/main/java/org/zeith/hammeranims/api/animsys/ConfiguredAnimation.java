package org.zeith.hammeranims.api.animsys;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import org.zeith.hammeranims.api.animation.*;
import org.zeith.hammeranims.api.animation.interp.Query;
import org.zeith.hammeranims.api.animsys.actions.AnimationAction;
import org.zeith.hammeranims.api.animsys.actions.AnimationActionInstance;
import org.zeith.hammeranims.api.animsys.layer.ActiveAnimation;
import org.zeith.hammeranims.api.animsys.layer.AnimationLayer;
import org.zeith.hammeranims.api.time.TimeFunction;
import org.zeith.hammeranims.api.time.TimeFunctionInstance;
import org.zeith.hammeranims.api.utils.ICompoundSerializable;
import org.zeith.hammeranims.core.init.DefaultsHA;
import org.zeith.hammeranims.core.utils.InstanceHelpers;

import java.time.Duration;
import java.util.*;

import static org.zeith.hammeranims.core.contents.time.LinearTimeFunction.FREEZE_SPEED;

public class ConfiguredAnimation
		implements ICompoundSerializable, IAnimationSource
{
	public Animation animation;
	public float weight = 1F; // [0; 1]
	public float speed = 1F; // 1x
	public float startTime = 0F;
	public boolean reverse = false;
	public float transitionTime = 0.25F; // 0.25 sec
	public TimeFunctionInstance timeFunction = TimeFunctionInstance.EMPTY;
	public boolean important = false;
	public LoopMode loopMode = LoopMode.ONCE;
	
	public SerializableMask mask = null;
	
	public ConfiguredAnimation next;
	
	public final List<AnimationActionInstance> onFinish = new ArrayList<>();
	
	public static ConfiguredAnimation noAnimation()
	{
		return DefaultsHA.NULL_ANIMATION_SYNTETIC.configure();
	}
	
	public ConfiguredAnimation(HolderLookup.Provider provider, ConfiguredAnimation toCopy)
	{
		this(provider, toCopy.serializeNBT(provider));
	}
	
	public ConfiguredAnimation(HolderLookup.Provider provider, CompoundTag tag)
	{
		deserializeNBT(provider, tag);
	}
	
	public ConfiguredAnimation(Animation animation)
	{
		setAnimation(animation);
	}
	
	public ConfiguredAnimation(ConfiguredAnimation toCopy)
	{
		this.animation = toCopy.animation;
		this.weight = toCopy.weight;
		this.speed = toCopy.speed;
		this.startTime = toCopy.startTime;
		this.reverse = toCopy.reverse;
		this.transitionTime = toCopy.transitionTime;
		this.timeFunction = toCopy.timeFunction;
		this.important = toCopy.important;
		this.loopMode = toCopy.loopMode;
		this.mask = toCopy.mask;
		this.next = toCopy.next != null ? new ConfiguredAnimation(toCopy.next) : null;
		this.onFinish.addAll(toCopy.onFinish);
	}
	
	public Animation getAnimation()
	{
		return animation != null ? animation : DefaultsHA.NULL_ANIMATION_SYNTETIC;
	}
	
	public boolean same(ConfiguredAnimation other)
	{
		return this.speed == other.speed
			   && this.weight == other.weight
			   && this.loopMode == other.loopMode
			   && this.startTime == other.startTime
			   && this.transitionTime == other.transitionTime
			   && this.timeFunction.equals(other.timeFunction)
			   && Objects.equals(this.mask, other.mask)
			   && this.reverse == other.reverse
			   && this.animation == other.animation;
	}
	
	public void setAnimation(Animation animation)
	{
		if(animation == null) animation = DefaultsHA.NULL_ANIMATION_SYNTETIC;
		
		this.animation = animation;
		this.loopMode = animation.getData().getLoopMode();
	}
	
	public ConfiguredAnimation mask(SerializableMask mask)
	{
		this.mask = mask;
		return this;
	}
	
	public ConfiguredAnimation weight(float weight)
	{
		this.weight = weight;
		return this;
	}
	
	public ConfiguredAnimation onFinish(AnimationActionInstance action)
	{
		if(action == null || action.isEmpty())
			return this;
		
		onFinish.add(action);
		
		return this;
	}
	
	public ConfiguredAnimation onFinish(AnimationAction action)
	{
		return onFinish(action.defaultInstance());
	}
	
	public ConfiguredAnimation speed(float speed)
	{
		this.speed = speed;
		return this;
	}
	
	public ConfiguredAnimation startTime(float startTime)
	{
		this.startTime = startTime;
		return this;
	}
	
	public ConfiguredAnimation freezeAt(float time)
	{
		return startTime(time)
				.speed(FREEZE_SPEED);
	}
	
	public ConfiguredAnimation reversed(boolean reverse)
	{
		this.reverse = reverse;
		return this;
	}
	
	public ConfiguredAnimation reversed()
	{
		return reversed(true);
	}
	
	public ConfiguredAnimation transitionTime(float transitionTime)
	{
		this.transitionTime = transitionTime;
		return this;
	}
	
	public ConfiguredAnimation transitionTime(Duration transitionTime)
	{
		this.transitionTime = transitionTime.toMillis() / 1000F;
		return this;
	}
	
	public ConfiguredAnimation timeFunction(TimeFunction timeFunction)
	{
		this.timeFunction = timeFunction.defaultInstance();
		return this;
	}
	
	public ConfiguredAnimation timeFunction(TimeFunctionInstance timeFunction)
	{
		this.timeFunction = timeFunction;
		return this;
	}
	
	public ConfiguredAnimation loopMode(LoopMode loopMode)
	{
		this.loopMode = loopMode;
		return this;
	}
	
	public ConfiguredAnimation important()
	{
		this.important = true;
		return this;
	}
	
	public ConfiguredAnimation important(boolean important)
	{
		this.important = important;
		return this;
	}
	
	public ConfiguredAnimation next(ConfiguredAnimation next)
	{
		this.next = next;
		return this;
	}
	
	public AnimationLocation getLocation()
	{
		return animation != null ? animation.getLocation() : null;
	}
	
	@Override
	public ConfiguredAnimation configure()
	{
		return new ConfiguredAnimation(this);
	}
	
	public ActiveAnimation activate(AnimationLayer layer, Query query)
	{
		ActiveAnimation aa = new ActiveAnimation(this, query);
		aa.activationTime = layer.startTime;
		return aa;
	}
	
	@Override
	public CompoundTag serializeNBT(HolderLookup.Provider provider)
	{
		var tag = InstanceHelpers.newNBTCompound();
		
		if(mask != null) tag.put("Mask", mask.serializeNBT(provider));
		
		tag.put("Time", timeFunction.serializeNBT(provider));
		tag.putString("Animation", animation.getLocation().toString());
		tag.putFloat("Weight", weight);
		tag.putBoolean("Reverse", reverse);
		tag.putFloat("Speed", speed);
		tag.putFloat("StartTime", startTime);
		tag.putFloat("TransitionTime", transitionTime);
		if(next != null) tag.put("Next", next.serializeNBT(provider));
		tag.putByte("LoopMode", (byte) (loopMode != null ? loopMode.ordinal() : LoopMode.ONCE.ordinal()));
		
		if(!this.onFinish.isEmpty())
		{
			var onFinish = InstanceHelpers.newNBTList();
			for(AnimationActionInstance finish : this.onFinish)
				onFinish.add(finish.serializeNBT(provider));
			tag.put("OnFinish", onFinish);
		}
		
		return tag;
	}
	
	@Override
	public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag)
	{
		this.timeFunction = TimeFunctionInstance.of(provider, tag.getCompound("Time"));
		
		this.mask = tag.contains("Mask", Tag.TAG_COMPOUND) ? new SerializableMask(provider, tag.getCompound("Mask")) : null;
		
		this.setAnimation(new AnimationLocation(tag.getString("Animation")).resolve().orElse(null));
		this.weight = tag.getFloat("Weight");
		this.reverse = tag.getBoolean("Reverse");
		this.speed = tag.getFloat("Speed");
		this.startTime = tag.getFloat("StartTime");
		this.transitionTime = tag.getFloat("TransitionTime");
		
		if(tag.contains("Next", Tag.TAG_COMPOUND)) next = new ConfiguredAnimation(provider, tag.getCompound("Next"));
		
		loopMode = LoopMode.values()[tag.getByte("LoopMode") % LoopMode.VALUE_COUNT];
		
		var onFinish = tag.getList("OnFinish", Tag.TAG_COMPOUND);
		this.onFinish.clear();
		for(int i = 0; i < onFinish.size(); i++)
		{
			AnimationActionInstance a = AnimationActionInstance.of(provider, onFinish.getCompound(i));
			if(a != null && !a.isEmpty()) this.onFinish.add(a);
		}
	}
}