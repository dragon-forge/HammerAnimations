package org.zeith.hammeranims.api.time;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.zeith.hammeranims.api.HammerAnimationsApi;
import org.zeith.hammeranims.api.animation.LoopMode;
import org.zeith.hammeranims.api.animsys.AnimationSystem;
import org.zeith.hammeranims.api.animsys.layer.ActiveAnimation;

import javax.annotation.Nonnull;

public abstract class TimeFunction
		extends IForgeRegistryEntry.Impl<TimeFunction>
{
	public abstract double computeTime(AnimationSystem system, double sysTime, float partialTicks, ActiveAnimation animation, TimeFunctionInstance instance);
	
	@Nonnull
	protected TimeFunctionInstance createInstance()
	{
		return new TimeFunctionInstance(this);
	}
	
	public TimeFunctionInstance defaultInstance()
	{
		return createInstance();
	}
	
	@Nonnull
	public TimeFunctionInstance deserializeInstance(NBTTagCompound tag)
	{
		TimeFunctionInstance inst = createInstance();
		inst.deserializeNBT(tag);
		return inst;
	}
	
	public double getLengthSeconds(ActiveAnimation animation, TimeFunctionInstance instance)
	{
		return animation.config.animation.getData().getLengthSeconds();
	}
	
	public double getTime(AnimationSystem system, double sysTime, float partialTicks, ActiveAnimation animation, TimeFunctionInstance instance)
	{
		double time = computeTime(system, sysTime, partialTicks, animation, instance);
		
		if(animation.config.animation != null)
		{
			LoopMode mode = animation.config.loopMode;
			
			double duration = getLengthSeconds(animation, instance);
			
			if(animation.config.reverse)
			{
				switch(mode)
				{
					case LOOP:
						return duration > 0 ? duration - (time % duration) : 0;
					case HOLD_ON_LAST_FRAME:
						return Math.max(duration - time, 1.0E-30);
					case ONCE:
						return duration - time;
				}
			}
			
			switch(mode)
			{
				case LOOP:
					return duration > 0 ? time % duration : 0;
				case HOLD_ON_LAST_FRAME:
					return Math.min(time, duration);
				case ONCE:
					return time;
			}
		}
		
		return time;
	}
	
	public final ResourceLocation getRegistryKey()
	{
		return HammerAnimationsApi.timeFunctions().getKey(this);
	}
}