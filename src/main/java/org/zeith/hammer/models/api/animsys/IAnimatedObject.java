package org.zeith.hammer.models.api.animsys;

public interface IAnimatedObject
{
	void createSystem(AnimationSystem.Builder builder);
	
	AnimationSystem getAnimationSystem();
}