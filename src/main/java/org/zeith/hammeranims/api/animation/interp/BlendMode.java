package org.zeith.hammeranims.api.animation.interp;

import net.minecraft.util.math.vector.Vector3d;
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
	
	public final Function3<Vector3d, Vector3d, Float, Vector3d> additiveTransform;
	public final Function3<Vector3d, Vector3d, Float, Vector3d> multiplicativeTransform;
	
	BlendMode(Function3<Vector3d, Vector3d, Float, Vector3d> additiveTransform, Function3<Vector3d, Vector3d, Float, Vector3d> multiplicativeTransform)
	{
		this.additiveTransform = additiveTransform;
		this.multiplicativeTransform = multiplicativeTransform;
	}
	
	public static Vector3d lerp(Vector3d a, Vector3d b, float d)
	{
		return new Vector3d(
				(a.x + (b.x - a.x) * d),
				(a.y + (b.y - a.y) * d),
				(a.z + (b.z - a.z) * d)
		);
	}
	
	public static Vector3d mult(Vector3d a, Vector3d b)
	{
		return new Vector3d(a.x * b.x, a.y * b.y, a.z * b.z);
	}
}