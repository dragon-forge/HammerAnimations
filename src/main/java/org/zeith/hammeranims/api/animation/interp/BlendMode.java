package org.zeith.hammeranims.api.animation.interp;

import net.minecraft.world.phys.Vec3;
import org.zeith.hammeranims.api.geometry.model.GeometryTransforms;
import org.zeith.hammerlib.util.java.functions.Function3;

public enum BlendMode
{
	OVERRIDE(
			BlendMode::lerp,
			BlendMode::lerp
	),
	ADD(
			(base, extra, weight) -> base.add(extra.scale(weight)),
			(base, extra, weight) -> mult(base, lerp(GeometryTransforms.ONE, extra, weight))
	),
	SUBTRACT(
			(base, extra, weight) -> base.subtract(extra.scale(weight)),
			(base, extra, weight) -> mult(base, lerp(GeometryTransforms.ONE, extra.scale(-1), weight))
	);
	
	public final Function3<Vec3, Vec3, Float, Vec3> additiveTransform;
	public final Function3<Vec3, Vec3, Float, Vec3> multiplicativeTransform;
	
	BlendMode(Function3<Vec3, Vec3, Float, Vec3> additiveTransform, Function3<Vec3, Vec3, Float, Vec3> multiplicativeTransform)
	{
		this.additiveTransform = additiveTransform;
		this.multiplicativeTransform = multiplicativeTransform;
	}
	
	public static Vec3 lerp(Vec3 a, Vec3 b, float d)
	{
		return new Vec3(
				(a.x + (b.x - a.x) * d),
				(a.y + (b.y - a.y) * d),
				(a.z + (b.z - a.z) * d)
		);
	}
	
	public static Vec3 mult(Vec3 a, Vec3 b)
	{
		return new Vec3(a.x * b.x, a.y * b.y, a.z * b.z);
	}
}