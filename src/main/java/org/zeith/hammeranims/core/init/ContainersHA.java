package org.zeith.hammeranims.core.init;

import org.zeith.hammeranims.api.animation.*;
import org.zeith.hammeranims.api.annotations.*;
import org.zeith.hammeranims.api.geometry.IGeometryContainer;
import org.zeith.hammeranims.core.contents.actions.PrintHelloWorldAction;
import org.zeith.hammeranims.core.contents.blocks.BlockBilly;

@RegisterAnimationActions
@RegisterAnimations
@RegisterGeometries
public interface ContainersHA
{
	BlockBilly BILLY_BLOCK = new BlockBilly();
	
	@Key("billy")
	IAnimationContainer BILLY_ANIM = IAnimationContainer.create();
	
	@Key("billy")
	IGeometryContainer BILLY_GEOM = IGeometryContainer.create();
	
	@Key("hello_world")
	PrintHelloWorldAction HELLO_WORLD_ACTION = new PrintHelloWorldAction();
	
	AnimationHolder BILLY_WALK = new AnimationHolder(BILLY_ANIM, "walk");
	AnimationHolder BILLY_BREATHE = new AnimationHolder(BILLY_ANIM, "breathe");
}