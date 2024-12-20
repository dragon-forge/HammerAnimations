package org.zeith.hammeranims.core.impl.api.geometry.decoder;

import lombok.AllArgsConstructor;
import lombok.Getter;
import shaded.joml.Vector3f;

@Getter
@AllArgsConstructor
public class ModelLocatorInfo
{
	private final Vector3f origin;
	private final Vector3f rotation;
	private final String name;
}