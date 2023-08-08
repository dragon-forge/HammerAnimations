package org.zeith.hammeranims.api.animsys;

public interface IAnimatedObject
{
	void createSystem(AnimationSystem.Builder builder);
	
	AnimationSystem getAnimationSystem();
}