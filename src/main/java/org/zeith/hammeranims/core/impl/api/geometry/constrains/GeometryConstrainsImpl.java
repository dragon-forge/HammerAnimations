package org.zeith.hammeranims.core.impl.api.geometry.constrains;

import org.zeith.hammeranims.api.geometry.constrains.*;

import java.util.*;

public final class GeometryConstrainsImpl
		implements IGeometryConstraints
{
	private final Map<String, BoneConstraintsImpl> bones;
	
	public GeometryConstrainsImpl(Map<String, BoneConstraintsImpl> bones) {this.bones = bones;}
	
	public Map<String, BoneConstraintsImpl> bones() {return bones;}
	
	@Override
	public boolean equals(Object obj)
	{
		if(obj == this) return true;
		if(obj == null || obj.getClass() != this.getClass()) return false;
		GeometryConstrainsImpl that = (GeometryConstrainsImpl) obj;
		return Objects.equals(this.bones, that.bones);
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(bones);
	}
	
	@Override
	public IBoneConstraints getConstraints(String bone)
	{
		IBoneConstraints v = bones.get(bone);
		return v != null ? v : IBoneConstraints.NONE;
	}
	
	@Override
	public String toString()
	{
		return "GeometryConstrainsImpl[" +
			   "bones=" + bones + ']';
	}
}