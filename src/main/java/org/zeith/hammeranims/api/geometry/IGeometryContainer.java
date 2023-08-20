package org.zeith.hammeranims.api.geometry;

import net.minecraft.resources.ResourceLocation;
import org.zeith.hammeranims.api.HammerAnimationsApi;
import org.zeith.hammeranims.api.geometry.constrains.IGeometryConstraints;
import org.zeith.hammeranims.api.geometry.data.IGeometryData;
import org.zeith.hammeranims.api.geometry.model.*;
import org.zeith.hammeranims.api.utils.IResourceProvider;
import org.zeith.hammeranims.core.impl.api.geometry.GeometryContainerImpl;

import javax.annotation.Nonnull;

public interface IGeometryContainer
{
	void reload(IResourceProvider provider);
	
	IGeometryData getGeometry();
	
	/**
	 * Get the constraints of this geometry.
	 * The constraints file is usually placed in the same directory as the base geometry.
	 * Example:
	 * - Geometry: /assets/hammeranims/bedrock/geometry/billy.geo.json
	 * - Constraints: /assets/hammeranims/bedrock/geometry/billy.constraints.json
	 */
	@Nonnull
	IGeometryConstraints getConstraints();
	
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
	
	default IPositionalModel getPositionalModel()
	{
		return getGeometry().getPositionalModel();
	}
}