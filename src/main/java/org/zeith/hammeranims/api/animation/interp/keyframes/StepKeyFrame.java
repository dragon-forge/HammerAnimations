package org.zeith.hammeranims.api.animation.interp.keyframes;

import dev.zeith.lzvm.LzVariableStore;
import org.jetbrains.annotations.NotNull;
import org.zeith.hammeranims.api.animation.interp.*;

public class StepKeyFrame
		implements IKeyFrame
{
	private final double time;
	private final BaseInterpolation pre;
	private final BaseInterpolation post;
	
	public StepKeyFrame(double time, BaseInterpolation pre, BaseInterpolation post)
	{
		this.time = time;
		this.pre = pre;
		this.post = post;
	}
	
	@Override
	public double getTime()
	{
		return time;
	}
	
	@Override
	public BaseInterpolation getVec(KeyFrameState state)
	{
		return state == KeyFrameState.PREV ? post : pre;
	}
	
	@Override
	public IKeyFrame withNewTime(double time)
	{
		return new StepKeyFrame(time, pre, post);
	}
	
	@Override
	public IKeyFrameInstance newInstance(LzVariableStore vars)
	{
		return new StepKeyFrameInstance(
				time,
				new Vec3Animation(pre, vars),
				new Vec3Animation(post, vars)
		);
	}
	
	public static class StepKeyFrameInstance
			implements IKeyFrameInstance
	{
		private final double time;
		private final Vec3Animation pre;
		private final Vec3Animation post;
		
		public StepKeyFrameInstance(double time, Vec3Animation pre, Vec3Animation post)
		{
			this.time = time;
			this.pre = pre;
			this.post = post;
		}
		
		@Override
		public double getTime()
		{
			return time;
		}
		
		@Override
		public @NotNull Vec3Animation getVec(KeyFrameState state)
		{
			return state == KeyFrameState.PREV ? post : pre;
		}
	}
}