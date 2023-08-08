package org.zeith.hammeranims.core.init;

import org.zeith.hammeranims.api.animation.IAnimationContainer;
import org.zeith.hammeranims.api.annotations.*;

@RegisterAnimations
public class AnimationsHM
{
	@AnimKey("test")
	public static final IAnimationContainer TEST = IAnimationContainer.create();
}