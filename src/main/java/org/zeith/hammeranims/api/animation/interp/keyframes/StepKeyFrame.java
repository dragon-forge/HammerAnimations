package org.zeith.hammeranims.api.animation.interp.keyframes;

import org.zeith.hammeranims.api.animation.interp.BaseInterpolation;

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
}