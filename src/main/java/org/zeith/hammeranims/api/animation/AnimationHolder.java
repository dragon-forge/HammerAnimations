package org.zeith.hammeranims.api.animation;

import org.zeith.hammeranims.api.HammerAnimationsApi;
import org.zeith.hammeranims.api.animsys.ConfiguredAnimation;
import org.zeith.hammeranims.core.init.DefaultsHA;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public class AnimationHolder
		implements Supplier<Animation>, IAnimationSource
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
		if(animation == null) return DefaultsHA.NULL_ANIMATION_SYNTETIC;
		return animation;
	}
	
	@Override
	public AnimationLocation getLocation()
	{
		return new AnimationLocation(HammerAnimationsApi.animations().getKey(container), variant);
	}
	
	@Override
	public ConfiguredAnimation configure()
	{
		return get().configure();
	}
}