package org.zeith.hammeranims.core.impl.api.geometry.constrains;

import org.zeith.hammeranims.api.geometry.constrains.*;
import org.zeith.hammerlib.util.java.Cast;

import java.util.Map;

public record GeometryConstrainsImpl(Map<String, BoneConstraintsImpl> bones)
		implements IGeometryConstraints
{
	@Override
	public IBoneConstraints getConstraints(String bone)
	{
		return Cast.or(bones.get(bone), IBoneConstraints.NONE);
	}
}