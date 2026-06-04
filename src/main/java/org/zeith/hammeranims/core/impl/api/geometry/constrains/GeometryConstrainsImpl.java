package org.zeith.hammeranims.core.impl.api.geometry.constrains;

import org.zeith.hammeranims.standalone.utils.Cast;
import org.zeith.hammeranims.api.geometry.constrains.*;

import java.util.*;

public class GeometryConstrainsImpl
		implements IGeometryConstraints
{
	protected final Map<String, BoneConstraintsImpl> bones;
	
	public GeometryConstrainsImpl(Map<String, BoneConstraintsImpl> bones)
	{
		this.bones = bones;
	}
	
	@Override
	public IBoneConstraints getConstraints(String bone)
	{
		return Cast.or(bones.get(bone.toLowerCase(Locale.ROOT)), IBoneConstraints.NONE);
	}
	
	public Map<String, BoneConstraintsImpl> getBones()
	{
		return bones;
	}
}