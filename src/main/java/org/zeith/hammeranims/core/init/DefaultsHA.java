package org.zeith.hammeranims.core.init;

import org.jetbrains.annotations.NotNull;
import org.zeith.hammeranims.api.animation.*;
import org.zeith.hammeranims.api.animation.data.IAnimationData;
import org.zeith.hammeranims.api.time.TimeFunction;
import org.zeith.hammeranims.core.contents.time.LinearTimeFunction;

public interface DefaultsHA
{
	IAnimationContainer NULL_ANIMATION = IAnimationContainer.create();
	
	TimeFunction LINEAR_TIME = new LinearTimeFunction();
	
	Animation NULL_ANIMATION_SYNTETIC = new Animation(NULL_ANIMATION, "null", IAnimationData.EMPTY);
	AnimationHolder NULL_ANIM = new AnimationHolder(NULL_ANIMATION, "null")
	{
		@Override
		public @NotNull Animation get()
		{
			return NULL_ANIMATION_SYNTETIC;
		}
	};
}