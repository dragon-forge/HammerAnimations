package org.zeith.hammeranims.core.contents.time;

import org.zeith.hammeranims.api.animsys.AnimationSystem;
import org.zeith.hammeranims.api.animsys.layer.ActiveAnimation;
import org.zeith.hammeranims.api.time.TimeFunction;

public class LinearTimeFunction
		extends TimeFunction
{
	public static final float FREEZE_SPEED = 0.0F;
	
	@Override
	public double computeTime(AnimationSystem system, double sysTime, float partialTicks, ActiveAnimation animation)
	{
		return (sysTime - animation.activationTime) * animation.config.speed + animation.config.startTime;
	}
}