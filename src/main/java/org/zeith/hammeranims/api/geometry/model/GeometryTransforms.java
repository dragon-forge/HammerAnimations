package org.zeith.hammeranims.api.geometry.model;

import net.minecraft.util.math.Vec3d;

import static net.minecraft.util.math.Vec3d.ZERO;

public class GeometryTransforms
{
	public static final Vec3d ONE = new Vec3d(1, 1, 1);
	
	public Vec3d translation;
	public Vec3d rotation;
	public Vec3d scale;
	
	public GeometryTransforms(Vec3d translation, Vec3d rotation, Vec3d scale)
	{
		this.translation = translation;
		this.rotation = rotation;
		this.scale = scale;
	}
	
	public static GeometryTransforms createDefault()
	{
		return new GeometryTransforms(ZERO, ZERO, ONE);
	}
}