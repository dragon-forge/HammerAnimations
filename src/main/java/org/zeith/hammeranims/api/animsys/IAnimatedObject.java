package org.zeith.hammeranims.api.animsys;

import org.zeith.hammeranims.api.animation.interp.Query;
import shaded.util.math.Vec3d;

public interface IAnimatedObject
{
	default float getAnimatedObjectScale()
	{
		return 1F;
	}
	
	default float getAnimatedObjectWidth()
	{
		return 1F;
	}
	
	default float getAnimatedObjectHeight()
	{
		return 1F;
	}
	
	default float getAnimatedObjectDepth()
	{
		return getAnimatedObjectWidth();
	}
	
	default float getAnimationObjectVolume()
	{
		return 1F;
	}
	
	void setupSystem(AnimationSystem.Builder builder);
	
	AnimationSystem getAnimationSystem();
	
	Vec3d getAnimatedObjectPosition();
	
	default Query createQuery()
	{
		return new Query();
	}
}