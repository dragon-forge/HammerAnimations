package org.zeith.hammeranims.api.geometry.model;

import net.minecraft.util.math.vector.Vector3d;
import org.zeith.hammeranims.api.geometry.constrains.IBoneConstraints;

import static net.minecraft.util.math.vector.Vector3d.ZERO;

public class GeometryTransforms
{
	public static final Vector3d ONE = new Vector3d(1, 1, 1);
	
	public Vector3d translation;
	public Vector3d rotation; // (in degrees)
	public Vector3d scale;
	
	public GeometryTransforms(Vector3d translation, Vector3d rotation, Vector3d scale)
	{
		this.translation = translation;
		this.rotation = rotation;
		this.scale = scale;
	}
	
	public static GeometryTransforms createDefault()
	{
		return new GeometryTransforms(ZERO, ZERO, ONE);
	}
	
	public void applyConstraints(IBoneConstraints constraints)
	{
		if(constraints.hasTranslation())
		{
			double x = Math.max(constraints.getMinTranslationX(), Math.min(constraints.getMaxTranslationX(), translation.x));
			double y = Math.max(constraints.getMinTranslationY(), Math.min(constraints.getMaxTranslationY(), translation.y));
			double z = Math.max(constraints.getMinTranslationZ(), Math.min(constraints.getMaxTranslationZ(), translation.z));
			translation = new Vector3d(x, y, z);
		}
		
		if(constraints.hasRotation())
		{
			double x = Math.max(constraints.getMinRotationX(), Math.min(constraints.getMaxRotationX(), rotation.x));
			double y = Math.max(constraints.getMinRotationY(), Math.min(constraints.getMaxRotationY(), rotation.y));
			double z = Math.max(constraints.getMinRotationZ(), Math.min(constraints.getMaxRotationZ(), rotation.z));
			rotation = new Vector3d(x, y, z);
		}
		
		if(constraints.hasScale())
		{
			double x = Math.max(constraints.getMinScaleX(), Math.min(constraints.getMaxScaleX(), scale.x));
			double y = Math.max(constraints.getMinScaleY(), Math.min(constraints.getMaxScaleY(), scale.y));
			double z = Math.max(constraints.getMinScaleZ(), Math.min(constraints.getMaxScaleZ(), scale.z));
			scale = new Vector3d(x, y, z);
		}
	}
	
	public GeometryTransforms copy()
	{
		return new GeometryTransforms(
				translation,
				rotation,
				scale
		);
	}
}