package org.zeith.hammeranims.api.animation;

import org.zeith.hammeranims.api.animsys.ConfiguredAnimation;

import java.util.function.UnaryOperator;

/**
 * Animation source allows for creating a new instance of configured animation to then start in an animation system.
 */
public interface IAnimationSource
{
	AnimationLocation getLocation();
	
	ConfiguredAnimation configure();
	
	default IAnimationSource reconfigure(UnaryOperator<ConfiguredAnimation> configurator)
	{
		IAnimationSource prev = this;
		return new IAnimationSource()
		{
			@Override
			public AnimationLocation getLocation()
			{
				return prev.getLocation();
			}
			
			@Override
			public ConfiguredAnimation configure()
			{
				return configurator.apply(prev.configure());
			}
			
			@Override
			public String toString()
			{
				return prev + ".reconfigure(" + configurator + ")";
			}
		};
	}
}