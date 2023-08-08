package org.zeith.hammeranims.api.geometry;

import net.minecraftforge.registries.IForgeRegistryEntry;
import org.zeith.hammeranims.api.utils.IResourceProvider;

public interface IGeometryContainer
		extends IForgeRegistryEntry<IGeometryContainer>
{
	void reload(IResourceProvider provider);
}