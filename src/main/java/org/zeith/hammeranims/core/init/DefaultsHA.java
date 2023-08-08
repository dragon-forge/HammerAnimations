package org.zeith.hammeranims.core.init;

import org.zeith.hammeranims.api.animation.*;
import org.zeith.hammeranims.api.annotations.*;
import org.zeith.hammeranims.api.time.TimeFunction;
import org.zeith.hammeranims.core.contents.sources.*;
import org.zeith.hammeranims.core.contents.time.LinearTimeFunction;

@RegisterAnimationSourceTypes
@RegisterTimeFunctions
@RegisterAnimations
public interface DefaultsHA
{
	@Key("tile_entity")
	TileAnimationSourceType TILE_TYPE = new TileAnimationSourceType();
	
	@Key("entity")
	EntityAnimationSourceType ENTITY_TYPE = new EntityAnimationSourceType();
	
	@Key("null")
	IAnimationContainer NULL_ANIMATION = IAnimationContainer.create();
	
	@Key("linear")
	TimeFunction LINEAR_TIME = new LinearTimeFunction();
	
	AnimationHolder NULL_ANIM = new AnimationHolder(NULL_ANIMATION, "null");
}