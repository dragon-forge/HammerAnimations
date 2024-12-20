package org.zeith.hammeranims.api.geometry;

import shaded.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.zeith.hammeranims.api.geometry.constrains.IGeometryConstraints;
import org.zeith.hammeranims.api.geometry.data.IGeometryData;
import org.zeith.hammeranims.api.geometry.model.IGeometricModel;
import org.zeith.hammeranims.api.geometry.model.IPositionalModel;
import org.zeith.hammeranims.api.utils.IHammerReloadable;
import org.zeith.hammeranims.core.impl.api.geometry.GeometryContainerImpl;

public interface IGeometryContainer
		extends IHammerReloadable
{
	IGeometryData getGeometry();
	
	/**
	 * Get the constraints of this geometry.
	 * The constraints file is usually placed in the same directory as the base geometry.
	 * Example:
	 * - Geometry: /assets/hammeranims/bedrock/geometry/billy.geo.json
	 * - Constraints: /assets/hammeranims/bedrock/geometry/billy.constraints.json
	 */
	@NotNull
	IGeometryConstraints getConstraints();
	
	/**
	 * Gets the registry key associated with this geometry container.
	 * This key can be used to identify and retrieve the container from registry.
	 *
	 * @return The {@link ResourceLocation} registry key.
	 */
	ResourceLocation getRegistryKey();
	
	void setRegistryKey(ResourceLocation id);
	
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