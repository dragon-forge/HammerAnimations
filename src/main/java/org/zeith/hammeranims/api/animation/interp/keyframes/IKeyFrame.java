package org.zeith.hammeranims.api.animation.interp.keyframes;

import dev.zeith.lzvm.LzVariableStore;
import org.zeith.hammeranims.api.animation.interp.BaseInterpolation;

public interface IKeyFrame
{
	double getTime();
	
	BaseInterpolation getVec(KeyFrameState state);
	
	IKeyFrame withNewTime(double time);
	
	IKeyFrameInstance newInstance(LzVariableStore vars);
	
	enum KeyFrameState
	{
		PREV,
		NEXT
	}
}