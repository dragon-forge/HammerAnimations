package org.zeith.hammeranims.api.utils;

import net.minecraft.resources.ResourceLocation;

import java.util.*;

@FunctionalInterface
public interface IResourceProvider
{
	Optional<byte[]> read(ResourceLocation path);
	
	default Optional<String> readAsString(ResourceLocation path)
	{
		return read(path).map(String::new);
	}
	
	static IResourceProvider or(List<IResourceProvider> resources)
	{
		return path ->
		{
			Optional<byte[]> read = Optional.empty();
			for(IResourceProvider resource : resources)
			{
				read = resource.read(path);
				if(read.isPresent()) return read;
			}
			return read;
		};
	}
}