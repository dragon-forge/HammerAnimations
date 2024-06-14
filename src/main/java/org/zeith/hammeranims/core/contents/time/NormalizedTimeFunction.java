package org.zeith.hammeranims.core.contents.time;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;
import org.zeith.hammeranims.api.animsys.AnimationSystem;
import org.zeith.hammeranims.api.animsys.layer.ActiveAnimation;
import org.zeith.hammeranims.api.time.TimeFunction;
import org.zeith.hammeranims.api.time.TimeFunctionInstance;

import java.time.Duration;

public class NormalizedTimeFunction
		extends LinearTimeFunction
{
	@Override
	public double computeTime(AnimationSystem system, double sysTime, float partialTicks, ActiveAnimation animation, TimeFunctionInstance instance)
	{
		double rawTime = super.computeTime(system, sysTime, partialTicks, animation, instance);
		double originalDuration = super.getLengthSeconds(animation, instance);
		double remappedDuration = getLengthSeconds(animation, instance);
		return rawTime * originalDuration / remappedDuration;
	}
	
	@Override
	public double getLengthSeconds(ActiveAnimation animation, TimeFunctionInstance instance)
	{
		return instance instanceof Instance ? ((Instance) instance).duration : super.getLengthSeconds(animation, instance);
	}
	
	@NotNull
	@Override
	protected TimeFunctionInstance createInstance()
	{
		return new Instance(this, 1);
	}
	
	public Instance of(double duration)
	{
		return new Instance(this, duration);
	}
	
	public Instance of(Duration duration)
	{
		return of(duration.toMillis() / 1000D);
	}
	
	public static class Instance
			extends TimeFunctionInstance
	{
		protected double duration;
		
		public Instance(TimeFunction function, double duration)
		{
			super(function);
			this.duration = duration;
		}
		
		@Override
		public CompoundTag serializeNBT(HolderLookup.Provider provider)
		{
			var tag = super.serializeNBT(provider);
			tag.putDouble("NDuration", duration);
			return tag;
		}
		
		@Override
		public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt)
		{
			super.deserializeNBT(provider, nbt);
			duration = nbt.getDouble("NDuration");
		}
	}
}