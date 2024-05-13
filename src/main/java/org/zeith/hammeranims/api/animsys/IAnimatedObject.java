package org.zeith.hammeranims.api.animsys;

import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
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
	
	World getAnimatedObjectWorld();
	
	Vector3d getAnimatedObjectPosition();
}