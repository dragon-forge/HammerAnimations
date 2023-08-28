package org.zeith.hammeranims.api.animation;

import org.zeith.hammeranims.api.animsys.ConfiguredAnimation;

/**
 * Animation source allows for creating a new instance of configured animation to then start in an animation system.
 */
public interface IAnimationSource
{
	AnimationLocation getLocation();
	
	ConfiguredAnimation configure();
}