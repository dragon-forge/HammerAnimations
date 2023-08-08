package org.zeith.hammer.models.api.geometry;

import net.minecraftforge.registries.IForgeRegistryEntry;
import org.zeith.hammer.models.api.utils.IResourceProvider;

public interface IGeometryContainer
		extends IForgeRegistryEntry<IGeometryContainer>
{
	void reload(IResourceProvider provider);
}