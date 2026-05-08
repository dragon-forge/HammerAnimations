package org.zeith.hammeranims.api.animation.interp.keyframes;

import org.jetbrains.annotations.NotNull;
import org.zeith.hammeranims.api.animation.interp.Vec3Animation;

public interface IKeyFrameInstance
{
	double getTime();
	
	@NotNull
	Vec3Animation getVec(IKeyFrame.KeyFrameState state);
}