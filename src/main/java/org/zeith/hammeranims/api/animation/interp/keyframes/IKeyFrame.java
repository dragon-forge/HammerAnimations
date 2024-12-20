package org.zeith.hammeranims.api.animation.interp.keyframes;

import org.zeith.hammeranims.api.animation.interp.BaseInterpolation;

public interface IKeyFrame
{
	double getTime();
	
	BaseInterpolation getVec(KeyFrameState state);
	
	IKeyFrame withNewTime(double time);
	
	enum KeyFrameState
	{
		PREV,
		NEXT
	}
}