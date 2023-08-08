package org.zeith.hammeranims.api.animation.interp;

import net.minecraft.util.math.Vec3d;

import java.util.function.Function;

public class Vec3Animation
{
	public static final Vec3Animation ZERO = new Vec3Animation(new DoubleInterpolation(InterpolatedDouble.constant(0)));
	public static final Vec3Animation ONE = new Vec3Animation(new DoubleInterpolation(InterpolatedDouble.constant(1)));
	
	protected final BaseInterpolation animation;
	protected final Function<Query, Vec3d> eval;
	
	public Vec3Animation(BaseInterpolation a)
	{
		this.animation = a;
		
		int c = animation.getDoubleCount();
		if(c >= 3)
		{
			this.eval = q ->
			{
				double[] data = animation.get(q);
				return new Vec3d(data[0], data[1], data[2]);
			};
		} else
		{
			this.eval = q ->
			{
				double i = animation.get(q)[0];
				return new Vec3d(i, i, i);
			};
		}
	}
	
	public Vec3d get(Query q)
	{
		return eval.apply(q);
	}
	
	public static Vec3Animation parse(Object obj)
	{
		BaseInterpolation parse = BaseInterpolation.parse(obj);
		if(parse == null) return null;
		return new Vec3Animation(parse);
	}
	
	@Override
	public String toString()
	{
		return "Vec3Animation{" + animation + '}';
	}
}