package org.zeith.hammeranims.api.animation.interp.keyframes;

import dev.zeith.lzvm.LzVariableStore;
import org.jetbrains.annotations.NotNull;
import org.zeith.hammeranims.api.animation.interp.*;

public class KeyFrame
		implements IKeyFrame
{
	protected final double time;
	protected final BaseInterpolation vec;
	
	public KeyFrame(double time, BaseInterpolation vec)
	{
		this.time = time;
		this.vec = vec;
	}
	
	public static KeyFrame createIdleKeyFrame(double startTime, BaseInterpolation modelIdleVec)
	{
		return new KeyFrame(startTime, modelIdleVec);
	}
	
	@Override
	public double getTime()
	{
		return time;
	}
	
	@Override
	public BaseInterpolation getVec(KeyFrameState state)
	{
		return vec;
	}
	
	@Override
	public KeyFrame withNewTime(double time)
	{
		return new KeyFrame(time, vec);
	}
	
	@Override
	public KeyFrameInstance newInstance(LzVariableStore vars)
	{
		return new KeyFrameInstance(time, new Vec3Animation(vec, vars));
	}
	
	@Override
	public String toString()
	{
		return "KeyFrame{" +
				"time=" + time +
				", vec=" + vec +
				'}';
	}
	
	public static class KeyFrameInstance
			implements IKeyFrameInstance
	{
		private final double time;
		private final Vec3Animation vec;
		
		public KeyFrameInstance(double time, Vec3Animation vec)
		{
			this.time = time;
			this.vec = vec;
		}
		
		@Override
		public double getTime()
		{
			return time;
		}
		
		@Override
		public @NotNull Vec3Animation getVec(KeyFrameState state)
		{
			return vec;
		}
	}
}