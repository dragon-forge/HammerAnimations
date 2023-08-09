package org.zeith.hammeranims.api.geometry;

import net.minecraft.resources.ResourceLocation;
import org.zeith.hammeranims.api.HammerAnimationsApi;
import org.zeith.hammeranims.api.geometry.data.IGeometryData;
import org.zeith.hammeranims.api.geometry.model.IGeometricModel;
import org.zeith.hammeranims.api.utils.IResourceProvider;
import org.zeith.hammeranims.core.impl.api.geometry.GeometryContainerImpl;

public interface IGeometryContainer
{
	void reload(IResourceProvider provider);
	
	IGeometryData getGeometry();
	
	/**
	 * Gets the registry key associated with this geometry container.
	 * This key can be used to identify and retrieve the container from registry.
	 *
	 * @return The {@link ResourceLocation} registry key.
	 */
	default ResourceLocation getRegistryKey()
	{
		return HammerAnimationsApi.geometries().getKey(this);
	}
	
	
	/**
	 * Creates a new instance of a geometry container.
	 *
	 * @return A new instance of {@link IGeometryContainer}.
	 */
	static IGeometryContainer create()
	{
		return new GeometryContainerImpl();
	}
	
	/**
	 * Creates a new instance of a geometry container.
	 *
	 * @return A new instance of {@link IGeometryContainer}.
	 */
	static IGeometryContainer createNoSuffix()
	{
		return new GeometryContainerImpl(".json");
	}
	
	default IGeometricModel createModel()
	{
		return getGeometry().createModel();
	}
}