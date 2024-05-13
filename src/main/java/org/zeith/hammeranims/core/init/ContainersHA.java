package org.zeith.hammeranims.core.init;

import com.zeitheron.hammercore.annotations.*;
import org.zeith.hammeranims.api.animation.*;
import org.zeith.hammeranims.api.geometry.IGeometryContainer;
import org.zeith.hammeranims.api.particles.IParticleContainer;
import org.zeith.hammeranims.core.contents.actions.PrintHelloWorldAction;
import org.zeith.hammeranims.core.contents.blocks.BlockBilly;

@SimplyRegister
public interface ContainersHA
{
	@RegistryName("billy")
	BlockBilly BILLY_BLOCK = new BlockBilly();
	
	@RegistryName("billy")
	IAnimationContainer BILLY_ANIM = IAnimationContainer.create();
	
	@RegistryName("billy_breathe")
	IAnimationContainer BILLY_BREATHE = IAnimationContainer.create();
	
	@RegistryName("billy")
	IGeometryContainer BILLY_GEOM = IGeometryContainer.create();
	
	@RegistryName("hello_world")
	PrintHelloWorldAction HELLO_WORLD_ACTION = new PrintHelloWorldAction();
	
	@RegistryName("rainbow")
	IParticleContainer RAINBOW_PARTICLES = IParticleContainer.create();
	
	AnimationHolder BILLY_WALK = new AnimationHolder(BILLY_ANIM, "walk");
}