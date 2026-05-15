package org.zeith.hammeranims.api.animation.interp.keyframes;

import dev.zeith.lzvm.LzVariableStore;
import org.zeith.hammeranims.api.animation.interp.*;

public class CatmullRomKeyFrame
		extends KeyFrame
{
	public CatmullRomKeyFrame(double time, BaseInterpolation vec)
	{
		super(time, vec);
	}
	
	@Override
	public KeyFrameInstance newInstance(LzVariableStore vars)
	{
		return new CatmullRomKeyFrameInstance(time, new Vec3Animation(vec, vars));
	}
	
	public static class CatmullRomKeyFrameInstance
			extends KeyFrameInstance
	{
		public CatmullRomKeyFrameInstance(double time, Vec3Animation vec)
		{
			super(time, vec);
		}
	}
}