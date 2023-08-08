package org.zeith.hammeranims.api.animation;

import net.minecraft.util.ResourceLocation;
import org.zeith.hammeranims.api.HammerModelsApi;

import java.util.*;

public final class AnimationLocation
{
	public final ResourceLocation container;
	public final String key;
	
	public AnimationLocation(ResourceLocation container, String key)
	{
		this.container = container;
		this.key = key;
	}
	
	public Optional<Animation> resolve()
	{
		return Optional.ofNullable(HammerModelsApi.animations().getValue(container))
				.map(c -> c.getAnimations().get(key));
	}
	
	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		AnimationLocation that = (AnimationLocation) o;
		return Objects.equals(container, that.container) && Objects.equals(key, that.key);
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(container, key);
	}
	
	@Override
	public String toString()
	{
		return container.toString() + '#' + key;
	}
}