package org.zeith.hammeranims.api.utils;

import net.minecraft.resources.ResourceLocation;
import org.zeith.hammeranims.HammerAnimations;

import java.util.Objects;

public class EmbeddedLocation
{
	public final ResourceLocation container;
	public final String key;
	
	public EmbeddedLocation(String path)
	{
		String[] split = path.split("!", 2);
		this.container = new ResourceLocation(split[0]);
		this.key = split[1];
	}
	
	public EmbeddedLocation(ResourceLocation container, String key)
	{
		this.container = container;
		this.key = key;
	}
	
	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		EmbeddedLocation that = (EmbeddedLocation) o;
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
		return container + "!" + key;
	}
	
	public void warn(String message)
	{
		HammerAnimations.LOG.warn("[" + this + "]: {}", message);
	}
}