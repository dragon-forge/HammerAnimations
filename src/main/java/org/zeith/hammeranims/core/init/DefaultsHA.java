package org.zeith.hammeranims.core.init;

import com.zeitheron.hammercore.annotations.*;
import org.zeith.hammeranims.api.animation.*;
import org.zeith.hammeranims.api.animation.data.IAnimationData;
import org.zeith.hammeranims.api.animsys.actions.AnimationAction;
import org.zeith.hammeranims.api.time.TimeFunction;
import org.zeith.hammeranims.core.contents.actions.EmptyAnimationAction;
import org.zeith.hammeranims.core.contents.sources.*;
import org.zeith.hammeranims.core.contents.time.*;

@SimplyRegister
public interface DefaultsHA
{
	@RegistryName("tile_entity")
	TileAnimationSourceType TILE_TYPE = new TileAnimationSourceType();
	
	@RegistryName("entity")
	EntityAnimationSourceType ENTITY_TYPE = new EntityAnimationSourceType();
	
	@RegistryName("null")
	IAnimationContainer NULL_ANIMATION = IAnimationContainer.create();
	
	@RegistryName("linear")
	TimeFunction LINEAR_TIME = new LinearTimeFunction();
	
	@RegistryName("normalized")
	NormalizedTimeFunction NORMALIZED_TIME = new NormalizedTimeFunction();
	
	@RegistryName("empty")
	AnimationAction EMPTY_ACTION = new EmptyAnimationAction();
	
	AnimationHolder NULL_ANIM = NULL_ANIMATION.holder("null");
	
	Animation NULL_ANIMATION_SYNTETIC = new Animation(NULL_ANIMATION, "null", IAnimationData.EMPTY);
}