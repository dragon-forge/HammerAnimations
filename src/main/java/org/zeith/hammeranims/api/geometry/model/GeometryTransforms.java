package org.zeith.hammeranims.api.geometry.model;

import net.minecraft.world.phys.Vec3;

public class GeometryTransforms
{
	public static final Vec3 ONE = new Vec3(1, 1, 1);
	
	public Vec3 translation;
	public Vec3 rotation;
	public Vec3 scale;
	
	public GeometryTransforms(Vec3 translation, Vec3 rotation, Vec3 scale)
	{
		this.translation = translation;
		this.rotation = rotation;
		this.scale = scale;
	}
	
	public static GeometryTransforms createDefault()
	{
		return new GeometryTransforms(Vec3.ZERO, Vec3.ZERO, ONE);
	}
}