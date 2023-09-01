package org.zeith.hammeranims.api.animsys;

import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.zeith.hammerlib.abstractions.sources.IObjectSource;

public interface IAnimatedObject
{
	void setupSystem(AnimationSystem.Builder builder);
	
	AnimationSystem getAnimationSystem();
	
	IObjectSource<?> getAnimationSource();
	
	Level getAnimatedObjectWorld();
	
	Vec3 getAnimatedObjectPosition();
}