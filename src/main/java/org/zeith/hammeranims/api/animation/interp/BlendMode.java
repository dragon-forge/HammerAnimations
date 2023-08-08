package org.zeith.hammeranims.api.animation.interp;

import com.zeitheron.hammercore.utils.java.functions.Function3;
import net.minecraft.util.math.*;
import org.zeith.hammeranims.api.geometry.model.GeometryTransforms;

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
	
	public final Function3<Vec3d, Vec3d, Float, Vec3d> additiveTransform;
	public final Function3<Vec3d, Vec3d, Float, Vec3d> multiplicativeTransform;
	
	BlendMode(Function3<Vec3d, Vec3d, Float, Vec3d> additiveTransform, Function3<Vec3d, Vec3d, Float, Vec3d> multiplicativeTransform)
	{
		this.additiveTransform = additiveTransform;
		this.multiplicativeTransform = multiplicativeTransform;
	}
	
	public static Vec3d lerp(Vec3d a, Vec3d b, float d)
	{
		return new Vec3d(
				(a.x + (b.x - a.x) * d),
				(a.y + (b.y - a.y) * d),
				(a.z + (b.z - a.z) * d)
		);
	}
	
	public static Vec3d mult(Vec3d a, Vec3d b)
	{
		return new Vec3d(a.x * b.x, a.y * b.y, a.z * b.z);
	}
}