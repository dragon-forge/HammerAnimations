package org.zeith.hammeranims.api.animsys;

import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public interface IAnimatedObject
{
	void setupSystem(AnimationSystem.Builder builder);
	
	AnimationSystem getAnimationSystem();
	
	AnimationSource getAnimationSource();
	
	Level getAnimatedObjectWorld();
	
	Vec3 getAnimatedObjectPosition();
}