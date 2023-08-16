package org.zeith.hammeranims.api.animation;

import net.minecraft.resources.ResourceLocation;
import org.zeith.hammeranims.api.HammerAnimationsApi;
import org.zeith.hammeranims.api.utils.EmbeddedLocation;

import java.util.Optional;

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
	
	public Optional<Animation> resolve()
	{
		return Optional.ofNullable(HammerAnimationsApi.animations().getValue(container))
				.map(c -> c.getAnimations().get(key));
	}
}