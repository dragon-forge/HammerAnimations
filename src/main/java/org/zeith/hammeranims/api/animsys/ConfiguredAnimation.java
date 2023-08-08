package org.zeith.hammeranims.api.animsys;

import net.minecraft.nbt.*;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.*;
import net.minecraftforge.registries.IForgeRegistry;
import org.zeith.hammeranims.HammerAnimations;
import org.zeith.hammeranims.api.HammerAnimationsApi;
import org.zeith.hammeranims.api.animation.*;
import org.zeith.hammeranims.api.animsys.layer.*;
import org.zeith.hammeranims.api.time.TimeFunction;
import org.zeith.hammeranims.core.init.DefaultsHA;
import org.zeith.hammeranims.core.utils.InstanceHelpers;

import java.time.Duration;
import java.util.*;

public class ConfiguredAnimation
		implements INBTSerializable<NBTTagCompound>
{
	public Animation animation;
	public float weight = 1F; // [0; 1]
	public float speed = 1F; // 1x
	public boolean reverse = false;
	public float transitionTime = 0.25F; // 0.25 sec
	public TimeFunction timeFunction = DefaultsHA.LINEAR_TIME;
	public boolean important = false;
	public LoopMode loopMode = LoopMode.ONCE;
	
	public ConfiguredAnimation next;
	
	public final List<AnimationAction> onFinish = new ArrayList<>();
	
	public static ConfiguredAnimation noAnimation()
	{
		Animation aNull = DefaultsHA.NULL_ANIMATION.getAnimations().get("null");
		if(aNull == null)
		{
			HammerAnimations.LOG.warn("Unable to find default null animation. This is not supposed to happen!");
			return new ConfiguredAnimation((Animation) null);
		}
		return DefaultsHA.NULL_ANIM.configure();
	}
	
	public ConfiguredAnimation(NBTTagCompound tag)
	{
		deserializeNBT(tag);
	}
	
	public ConfiguredAnimation(Animation animation)
	{
		setAnimation(animation);
	}
	
	public void setAnimation(Animation animation)
	{
		this.animation = animation;
		if(animation != null)
			loopMode = animation.getData().getLoopMode();
	}
	
	public ConfiguredAnimation weight(float weight)
	{
		this.weight = weight;
		return this;
	}
	
	public ConfiguredAnimation speed(float speed)
	{
		this.speed = speed;
		return this;
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
		aa.startTime = layer.startTime;
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
		tag.setFloat("TransitionTime", transitionTime);
		if(next != null) tag.setTag("Next", next.serializeNBT());
		tag.setByte("LoopMode", (byte) (loopMode != null ? loopMode.ordinal() : LoopMode.ONCE.ordinal()));
		
		if(!this.onFinish.isEmpty())
		{
			NBTTagList onFinish = InstanceHelpers.newNBTList();
			for(AnimationAction finish : this.onFinish)
				onFinish.appendTag(InstanceHelpers.newNBTString(finish.getRegistryKey().toString()));
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
		this.transitionTime = tag.getFloat("TransitionTime");
		
		if(tag.hasKey("Next", Constants.NBT.TAG_COMPOUND)) next = new ConfiguredAnimation(tag.getCompoundTag("Next"));
		
		loopMode = LoopMode.values()[tag.getByte("LoopMode") % LoopMode.VALUE_COUNT];
		
		IForgeRegistry<AnimationAction> actionsReg = HammerAnimationsApi.animationActions();
		
		NBTTagList onFinish = tag.getTagList("OnFinish", Constants.NBT.TAG_STRING);
		this.onFinish.clear();
		for(int i = 0; i < onFinish.tagCount(); i++)
		{
			AnimationAction a = actionsReg.getValue(new ResourceLocation(onFinish.getStringTagAt(i)));
			if(a != null) this.onFinish.add(a);
		}
	}
}