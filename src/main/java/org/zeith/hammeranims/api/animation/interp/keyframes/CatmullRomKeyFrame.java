package org.zeith.hammeranims.api.animation.interp.keyframes;

import org.zeith.hammeranims.api.animation.interp.BaseInterpolation;

public class CatmullRomKeyFrame
		extends KeyFrame
{
	public CatmullRomKeyFrame(double time, BaseInterpolation vec)
	{
		super(time, vec);
	}
}