package org.zeith.hammeranims.core.impl.api.geometry;

import lombok.Builder;
import lombok.Value;
import org.zeith.hammeranims.joml.Vector3f;

@Value
@Builder(toBuilder = true)
public class GeometryLocator
{
	String name;
	Vector3f offset;
	Vector3f rotation;
}