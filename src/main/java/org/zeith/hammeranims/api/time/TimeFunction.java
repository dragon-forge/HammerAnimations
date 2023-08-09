package org.zeith.hammeranims.api.time;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.zeith.hammeranims.api.HammerAnimationsApi;
import org.zeith.hammeranims.api.animation.LoopMode;
import org.zeith.hammeranims.api.animsys.AnimationSystem;
import org.zeith.hammeranims.api.animsys.layer.ActiveAnimation;

public abstract class TimeFunction
		extends IForgeRegistryEntry.Impl<TimeFunction>
{
	public abstract double computeTime(AnimationSystem system, double sysTime, float partialTicks, ActiveAnimation animation);
	
	public double getTime(AnimationSystem system, double sysTime, float partialTicks, ActiveAnimation animation)
	{
		double time = computeTime(system, sysTime, partialTicks, animation);
		
		if(animation.config.animation != null)
		{
			LoopMode mode = animation.config.loopMode;
			
			double duration = animation.config.animation.getData().getLengthSeconds();
			
			if(animation.config.reverse)
			{
				switch(mode)
				{
					case LOOP:
						return duration > 0 ? duration - (time % duration) : 0;
					case HOLD_ON_LAST_FRAME:
					case ONCE:
						return Math.max(duration - time, 0);
				}
			}
			
			switch(mode)
			{
				case LOOP:
					return duration > 0 ? time % duration : 0;
				case HOLD_ON_LAST_FRAME:
				case ONCE:
					return Math.min(time, duration);
			}
		}
		
		return time;
	}
	
	public final ResourceLocation getRegistryKey()
	{
		return HammerAnimationsApi.timeFunctions().getKey(this);
	}
}