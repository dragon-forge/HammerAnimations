package org.zeith.hammeranims.api.animsys;

import org.zeith.hammeranims.api.animation.interp.Query;
import org.zeith.hammeranims.standalone.utils.Cast;
import org.zeith.hammeranims.api.animation.*;
import org.zeith.hammeranims.api.animsys.layer.ActiveAnimation;
import org.zeith.hammeranims.api.animsys.layer.AnimationLayer;
import org.zeith.hammeranims.api.time.TimeFunction;
import org.zeith.hammeranims.api.time.TimeFunctionInstance;
import org.zeith.hammeranims.core.init.DefaultsHA;

import java.time.Duration;
import java.util.Objects;

import static org.zeith.hammeranims.core.contents.time.LinearTimeFunction.FREEZE_SPEED;

public class ConfiguredAnimation
	implements IAnimationSource
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
	
	public static ConfiguredAnimation noAnimation()
	{
		return DefaultsHA.NULL_ANIMATION_SYNTETIC.configure();
	}
	
	public ConfiguredAnimation(Animation animation)
	{
		setAnimation(animation);
	}
	
	public ConfiguredAnimation copy()
	{
		ConfiguredAnimation copy = new ConfiguredAnimation(animation);
		copy.weight = weight;
		copy.speed = speed;
		copy.startTime = startTime;
		copy.reverse = reverse;
		copy.transitionTime = transitionTime;
		copy.timeFunction = timeFunction;
		copy.important = important;
		copy.loopMode = loopMode;
		copy.mask = mask;
		copy.next = next != null ? next.copy() : null;
		return copy;
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
		return timeFunction(timeFunction.defaultInstance());
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
	
	@Override
	public AnimationLocation getLocation()
	{
		return animation != null ? animation.getLocation() : null;
	}
	
	@Override
	public ConfiguredAnimation configure()
	{
		return copy();
	}
	
	public ActiveAnimation activate(AnimationLayer layer, Query query)
	{
		ActiveAnimation aa = new ActiveAnimation(this, query);
		aa.activationTime = layer.startTime;
		return aa;
	}
}