package org.zeith.hammeranims.api.animation.interp.keyframes;

import org.zeith.hammeranims.api.animation.interp.BaseInterpolation;

public class KeyFrame
		implements IKeyFrame
{
	private final double time;
	private final BaseInterpolation vec;
	
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
	public String toString()
	{
		return "KeyFrame{" +
				"time=" + time +
				", vec=" + vec +
				'}';
	}
}