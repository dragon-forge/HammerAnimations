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
	
	void setupSystem(AnimationSystem.Builder builder);
	
	AnimationSystem getAnimationSystem();
	
	IObjectSource<?> getAnimationSource();
	
	World getAnimatedObjectWorld();
	
	Vector3d getAnimatedObjectPosition();
}