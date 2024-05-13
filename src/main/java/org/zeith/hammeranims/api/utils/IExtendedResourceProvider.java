package org.zeith.hammeranims.api.utils;

import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.stream.Collectors;

public interface IExtendedResourceProvider
		extends IResourceProvider
{
	List<byte[]> readAll(ResourceLocation path);
	
	default List<String> readAllAsString(ResourceLocation path)
	{
		return readAll(path).stream().map(String::new).collect(Collectors.toList());
	}
}