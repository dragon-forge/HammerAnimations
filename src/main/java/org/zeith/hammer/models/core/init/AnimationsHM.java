package org.zeith.hammer.models.core.init;

import org.zeith.hammer.models.api.animation.IAnimationContainer;
import org.zeith.hammer.models.api.annotations.*;

@RegisterAnimations
public class AnimationsHM
{
	@AnimKey("test")
	public static final IAnimationContainer TEST = IAnimationContainer.create();
}