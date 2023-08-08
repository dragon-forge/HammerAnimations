package org.zeith.hammeranims.api.animsys;

import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public interface IAnimatedObject
{
	void setupSystem(AnimationSystem.Builder builder);
	
	AnimationSystem getAnimationSystem();
	
	AnimationSource getAnimationSource();
	
	World getAnimatedObjectWorld();
	
	Vec3d getAnimatedObjectPosition();
}