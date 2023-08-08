package org.zeith.hammeranims.api.utils;

import net.minecraft.util.ResourceLocation;

import java.util.Optional;

@FunctionalInterface
public interface IResourceProvider
{
	Optional<byte[]> read(ResourceLocation path);
	
	default Optional<String> readAsString(ResourceLocation path)
	{
		return read(path).map(String::new);
	}
}