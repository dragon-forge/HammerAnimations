package org.zeith.hammeranims.api.animation;

import org.zeith.hammeranims.standalone.mc.ResourceLocation;
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