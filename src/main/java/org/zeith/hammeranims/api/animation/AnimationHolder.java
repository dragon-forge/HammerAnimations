package org.zeith.hammeranims.api.animation;

import org.jetbrains.annotations.NotNull;
import org.zeith.hammeranims.api.animsys.ConfiguredAnimation;
import org.zeith.hammeranims.core.init.DefaultsHA;

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
	
	@NotNull
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
		return new AnimationLocation(container.getRegistryKey(), variant);
	}
	
	@Override
	public ConfiguredAnimation configure()
	{
		return get().configure();
	}
	
	@Override
	public String toString()
	{
		return "AnimationHolder{" + getLocation() + "}";
	}
}