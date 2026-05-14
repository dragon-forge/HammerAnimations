package org.zeith.hammeranims.api.animation.interp;

import dev.zeith.lzvm.LzVariableStore;
import dev.zeith.lzvm.jvm.LzExpression;
import shaded.joml.Vector3d;

import java.util.function.*;

public class Vec3Animation
{
	protected final LzExpression[] expressions;
	
	protected final Supplier<Vector3d> eval;
	protected final IntToDoubleFunction byComponent;
	
	public Vec3Animation(final BaseInterpolation animation, LzVariableStore vars)
	{
		expressions = animation.instantiate(vars);
		if(expressions.length >= 3)
		{
			this.byComponent = i -> expressions[i].get();
			this.eval = () -> new Vector3d(expressions[0].get(), expressions[1].get(), expressions[2].get());
		} else
		{
			this.byComponent = i -> expressions[0].get();
			this.eval = () -> new Vector3d(expressions[0].get());
		}
	}
	
	public double get(int component)
	{
		return byComponent.applyAsDouble(component);
	}
	
	public shaded.joml.Vector3d get()
	{
		return eval.get();
	}
}