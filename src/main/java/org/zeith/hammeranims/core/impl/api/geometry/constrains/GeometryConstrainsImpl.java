package org.zeith.hammeranims.core.impl.api.geometry.constrains;

import org.zeith.hammeranims.api.geometry.constrains.*;

import java.util.Map;

public record GeometryConstrainsImpl(Map<String, BoneConstraintsImpl> bones)
		implements IGeometryConstraints
{
	@Override
	public IBoneConstraints getConstraints(String bone)
	{
		BoneConstraintsImpl c = bones.get(bone);
		return c != null ? c : IBoneConstraints.NONE;
	}
}