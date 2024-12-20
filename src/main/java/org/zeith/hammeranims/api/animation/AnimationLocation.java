package org.zeith.hammeranims.api.animation;

import shaded.util.ResourceLocation;
import org.zeith.hammeranims.api.utils.EmbeddedLocation;

public final class AnimationLocation
		extends EmbeddedLocation
{
	public AnimationLocation(String path)
	{
		super(path);
	}
	
	public AnimationLocation(ResourceLocation container, String key)
	{
		super(container, key);
	}
}