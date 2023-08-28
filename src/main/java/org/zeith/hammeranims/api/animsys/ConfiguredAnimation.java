package org.zeith.hammeranims.api.animsys;

import com.zeitheron.hammercore.utils.base.Cast;
import net.minecraft.nbt.*;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;
import org.zeith.hammeranims.HammerAnimations;
import org.zeith.hammeranims.api.HammerAnimationsApi;
import org.zeith.hammeranims.api.animation.*;
import org.zeith.hammeranims.api.animsys.actions.*;
import org.zeith.hammeranims.api.animsys.layer.*;
import org.zeith.hammeranims.api.time.TimeFunction;
import org.zeith.hammeranims.api.utils.ICompoundSerializable;
import org.zeith.hammeranims.core.init.DefaultsHA;
import org.zeith.hammeranims.core.utils.InstanceHelpers;

import java.time.Duration;
import java.util.*;

public class ConfiguredAnimation
		implements ICompoundSerializable
{
	public Animation animation;
	public float weight = 1F; // [0; 1]
	public float speed = 1F; // 1x
	public float startTime = 0F;
	public boolean reverse = false;
	public float transitionTime = 0.25F; // 0.25 sec
	public TimeFunction timeFunction = DefaultsHA.LINEAR_TIME;
	public boolean important = false;
	public LoopMode loopMode = LoopMode.ONCE;
	
	public ConfiguredAnimation next;
	
	public final List<AnimationActionInstance> onFinish = new ArrayList<>();
	
	public static ConfiguredAnimation noAnimation()
	{
		return DefaultsHA.NULL_ANIMATION_SYNTETIC.configure();
	}
	
	public ConfiguredAnimation(NBTTagCompound tag)
	{
		deserializeNBT(tag);
	}
	
	public ConfiguredAnimation(Animation animation)
	{
		setAnimation(animation);
	}
	
	public Animation getAnimation()
	{
		return Cast.or(animation, DefaultsHA.NULL_ANIMATION_SYNTETIC);
	}
	
	public boolean same(ConfiguredAnimation other)
	{
		return this.speed == other.speed
				&& this.weight == other.weight
				&& this.loopMode == other.loopMode
				&& this.startTime == other.startTime
				&& this.transitionTime == other.transitionTime
				&& this.timeFunction == other.timeFunction
				&& this.reverse == other.reverse
				&& this.animation == other.animation;
	}
	
	public void setAnimation(Animation animation)
	{
		if(animation == null) animation = DefaultsHA.NULL_ANIMATION_SYNTETIC;
		
		this.animation = animation;
		this.loopMode = animation.getData().getLoopMode();
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
				.speed(0);
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
	
	public ActiveAnimation activate(AnimationLayer layer)
	{
		ActiveAnimation aa = new ActiveAnimation(this);
		aa.startTime = layer.startTime -
				startTime; // shift time backwards to make the effect of animation being on a given timeframe
		return aa;
	}
	
	@Override
	public NBTTagCompound serializeNBT()
	{
		NBTTagCompound tag = InstanceHelpers.newNBTCompound();
		tag.setString("Time", timeFunction.getRegistryKey().toString());
		tag.setString("Animation", animation.getLocation().toString());
		tag.setFloat("Weight", weight);
		tag.setBoolean("Reverse", reverse);
		tag.setFloat("Speed", speed);
		tag.setFloat("StartTime", startTime);
		tag.setFloat("TransitionTime", transitionTime);
		if(next != null) tag.setTag("Next", next.serializeNBT());
		tag.setByte("LoopMode", (byte) (loopMode != null ? loopMode.ordinal() : LoopMode.ONCE.ordinal()));
		
		if(!this.onFinish.isEmpty())
		{
			NBTTagList onFinish = InstanceHelpers.newNBTList();
			for(AnimationActionInstance finish : this.onFinish)
				onFinish.appendTag(finish.serializeNBT());
			tag.setTag("OnFinish", onFinish);
		}
		
		return tag;
	}
	
	@Override
	public void deserializeNBT(NBTTagCompound tag)
	{
		this.timeFunction = HammerAnimationsApi.timeFunctions().getValue(new ResourceLocation(tag.getString("Time")));
		if(this.timeFunction == null) this.timeFunction = DefaultsHA.LINEAR_TIME;
		
		this.setAnimation(new AnimationLocation(tag.getString("Animation")).resolve().orElse(null));
		this.weight = tag.getFloat("Weight");
		this.reverse = tag.getBoolean("Reverse");
		this.speed = tag.getFloat("Speed");
		this.startTime = tag.getFloat("StartTime");
		this.transitionTime = tag.getFloat("TransitionTime");
		
		if(tag.hasKey("Next", Constants.NBT.TAG_COMPOUND)) next = new ConfiguredAnimation(tag.getCompoundTag("Next"));
		
		loopMode = LoopMode.values()[tag.getByte("LoopMode") % LoopMode.VALUE_COUNT];
		
		NBTTagList onFinish = tag.getTagList("OnFinish", Constants.NBT.TAG_COMPOUND);
		this.onFinish.clear();
		for(int i = 0; i < onFinish.tagCount(); i++)
		{
			AnimationActionInstance a = AnimationActionInstance.of(onFinish.getCompoundTagAt(i));
			if(a != null && !a.isEmpty()) this.onFinish.add(a);
		}
	}
}