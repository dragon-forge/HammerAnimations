package org.zeith.hammeranims.api.animsys;

import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.zeith.hammerlib.abstractions.sources.IObjectSource;

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
	
	void setupSystem(AnimationSystem.Builder builder);
	
	AnimationSystem getAnimationSystem();
	
	IObjectSource<?> getAnimationSource();
	
	Level getAnimatedObjectWorld();
	
	Vec3 getAnimatedObjectPosition();
}