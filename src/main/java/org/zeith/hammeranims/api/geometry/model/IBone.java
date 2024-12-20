package org.zeith.hammeranims.api.geometry.model;

import org.jetbrains.annotations.Nullable;
import shaded.joml.Vector3f;
import org.zeith.hammeranims.core.impl.api.geometry.GeometryLocator;

import java.util.Map;

public interface IBone
{
	@Nullable
	IBone getParent();
	
	String getName();
	
	Vector3f getTranslation();
	
	Vector3f getRotation();
	
	Vector3f getScale();
	
	default void reset()
	{
	}
	
	Map<String, ? extends IBone> getChildren();
	
	Map<String, GeometryLocator> getLocators();
}