package org.zeith.hammeranims.api.geometry.model;

import org.zeith.hammeranims.joml.Vector3f;

import javax.annotation.Nullable;
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
}