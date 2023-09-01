package org.zeith.hammeranims.core.init;

import org.zeith.hammeranims.api.animation.*;
import org.zeith.hammeranims.api.animation.data.IAnimationData;
import org.zeith.hammeranims.api.animsys.actions.AnimationAction;
import org.zeith.hammeranims.api.annotations.*;
import org.zeith.hammeranims.api.time.TimeFunction;
import org.zeith.hammeranims.core.contents.actions.EmptyAnimationAction;
import org.zeith.hammeranims.core.contents.time.LinearTimeFunction;

@RegisterAnimationActions
@RegisterTimeFunctions
@RegisterAnimations
public interface DefaultsHA
{
	@Key("null")
	IAnimationContainer NULL_ANIMATION = IAnimationContainer.create();
	
	@Key("linear")
	TimeFunction LINEAR_TIME = new LinearTimeFunction();
	
	@Key("empty")
	AnimationAction EMPTY_ACTION = new EmptyAnimationAction();
	
	AnimationHolder NULL_ANIM = NULL_ANIMATION.holder("null");
	
	Animation NULL_ANIMATION_SYNTETIC = new Animation(NULL_ANIMATION, "null", IAnimationData.EMPTY);
}