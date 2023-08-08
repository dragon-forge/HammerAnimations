package org.zeith.hammeranims.api.animation;

import org.zeith.hammeranims.HammerAnimations;
import org.zeith.hammeranims.api.animsys.ConfiguredAnimation;
import org.zeith.hammeranims.core.init.DefaultsHA;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public class AnimationHolder
		implements Supplier<Animation>
{
	public final IAnimationContainer container;
	public final String variant;
	
	public AnimationHolder(IAnimationContainer container, String variant)
	{
		this.container = container;
		this.variant = variant;
	}
	
	@Nonnull
	@Override
	public Animation get()
	{
		Animation animation = container.getAnimations().get(variant);
		if(animation == null)
		{
			if(this == DefaultsHA.NULL_ANIM)
			{
				HammerAnimations.LOG.warn("Unable to find default null animation. This is not supposed to happen!");
				return null;
			}
			return DefaultsHA.NULL_ANIM.get();
		}
		return animation;
	}
	
	public ConfiguredAnimation configure()
	{
		return get().configure();
	}
}