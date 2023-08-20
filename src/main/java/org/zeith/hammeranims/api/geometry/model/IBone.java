package org.zeith.hammeranims.api.geometry.model;

import org.zeith.hammeranims.joml.Vector3f;

import java.util.Map;

public interface IBone
{
	String getName();
	
	Vector3f getTranslation();
	
	Vector3f getRotation();
	
	Vector3f getScale();
	
	Map<String, ? extends IBone> getChildren();
}