package org.zeith.hammeranims.api.geometry.model;

import net.minecraft.world.phys.Vec3;
import org.zeith.hammeranims.api.geometry.constrains.IBoneConstraints;

import static net.minecraft.world.phys.Vec3.ZERO;

public class GeometryTransforms
{
	public static final Vec3 ONE = new Vec3(1, 1, 1);
	
	public Vec3 translation;
	public Vec3 rotation; // (in degrees)
	public Vec3 scale;
	
	public GeometryTransforms(Vec3 translation, Vec3 rotation, Vec3 scale)
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
			translation = new Vec3(x, y, z);
		}
		
		if(constraints.hasRotation())
		{
			double x = Math.max(constraints.getMinRotationX(), Math.min(constraints.getMaxRotationX(), rotation.x));
			double y = Math.max(constraints.getMinRotationY(), Math.min(constraints.getMaxRotationY(), rotation.y));
			double z = Math.max(constraints.getMinRotationZ(), Math.min(constraints.getMaxRotationZ(), rotation.z));
			rotation = new Vec3(x, y, z);
		}
		
		if(constraints.hasScale())
		{
			double x = Math.max(constraints.getMinScaleX(), Math.min(constraints.getMaxScaleX(), scale.x));
			double y = Math.max(constraints.getMinScaleY(), Math.min(constraints.getMaxScaleY(), scale.y));
			double z = Math.max(constraints.getMinScaleZ(), Math.min(constraints.getMaxScaleZ(), scale.z));
			scale = new Vec3(x, y, z);
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