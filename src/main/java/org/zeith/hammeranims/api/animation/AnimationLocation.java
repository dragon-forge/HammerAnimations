package org.zeith.hammeranims.api.animation;

import net.minecraft.util.ResourceLocation;
import org.zeith.hammeranims.HammerAnimations;
import org.zeith.hammeranims.api.HammerAnimationsApi;

import java.util.*;

public final class AnimationLocation
{
	public final ResourceLocation container;
	public final String key;
	
	public AnimationLocation(String path)
	{
		String[] split = path.split("!", 2);
		this.container = new ResourceLocation(split[0]);
		this.key = split[1];
	}
	
	public AnimationLocation(ResourceLocation container, String key)
	{
		this.container = container;
		this.key = key;
	}
	
	public Optional<Animation> resolve()
	{
		return Optional.ofNullable(HammerAnimationsApi.animations().getValue(container))
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
		return container.toString() + '!' + key;
	}
	
	public void warn(String message)
	{
		HammerAnimations.LOG.warn("[" + this + "]: {}", message);
	}
}