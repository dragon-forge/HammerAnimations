package org.zeith.hammeranims.api.particles.curve;

import com.google.gson.*;
import dev.zeith.lzvm.LzVariableStore;
import dev.zeith.lzvm.jvm.*;
import org.zeith.hammeranims.api.animation.interp.InterpolatedDouble;
import org.zeith.hammeranims.joml.Math;

public class ParticleCurve
		implements LzFactory
{
	public final ParticleCurveType type;
	public final LzFactory[] nodes;
	public final LzFactory input;
	public final LzFactory range;
	public final String variable;
	
	public ParticleCurve(String variable, JsonObject object)
	{
		this.type = object.has("type") ? ParticleCurveType.fromString(object.get("type").getAsString()) : ParticleCurveType.LINEAR;
		this.input = object.has("input") ? InterpolatedDouble.parse(object.get("input")) : null;
		this.range = object.has("horizontal_range") ? InterpolatedDouble.parse(object.get("horizontal_range")) : null;
		this.variable = variable;
		
		if(object.has("nodes"))
		{
			JsonArray nodes = object.getAsJsonArray("nodes");
			LzFactory[] result = new LzFactory[nodes.size()];
			
			for(int i = 0, c = result.length; i < c; i++)
				result[i] = InterpolatedDouble.parse(nodes.get(i));
			
			this.nodes = result;
		} else this.nodes = new LzFactory[] {InterpolatedDouble.constant(0), InterpolatedDouble.constant(1), InterpolatedDouble.constant(0)};
	}
	
	@Override
	public LzExpression instantiate(LzVariableStore store)
	{
		return new ParticleCurveInstance(
				this,
				LzFactory.instantiate(store, this.nodes),
				this.input.instantiate(store),
				this.range.instantiate(store)
		);
	}
	
	public static double cubicHermite(double y0, double y1, double y2, double y3, double x)
	{
		double a = -0.5 * y0 + 1.5 * y1 - 1.5 * y2 + 0.5 * y3;
		double b = y0 - 2.5 * y1 + 2 * y2 - 0.5 * y3;
		double c = -0.5 * y0 + 0.5 * y2;
		
		return ((a * x + b) * x + c) * x + y1;
	}
	
	public static double lerp(double a, double b, double position)
	{
		return a + (b - a) * position;
	}
	
	public static class ParticleCurveInstance
			implements LzExpression
	{
		protected final ParticleCurve c;
		protected final LzExpression[] nodes;
		protected final LzExpression input;
		protected final LzExpression range;
		
		public ParticleCurveInstance(ParticleCurve c, LzExpression[] nodes, LzExpression input, LzExpression range)
		{
			this.c = c;
			this.nodes = nodes;
			this.input = input;
			this.range = range;
		}
		
		@Override
		public double get(double... args)
		{
			return computeCurve(input.get() / range.get());
		}
		
		@Override
		public LzExpression instantiate(LzVariableStore store)
		{
			return c.instantiate(store);
		}
		
		protected double computeCurve(double factor)
		{
			int length = nodes.length;
			
			if(length == 0)
			{
				return 0;
			} else if(length == 1)
			{
				return nodes[0].get();
			}
			
			if(factor < 0)
			{
				factor = -(1 + factor);
			}
			
			factor = Math.clamp(0, 1, factor);
			
			if(c.type == ParticleCurveType.HERMITE)
			{
				if(length <= 3)
				{
					return nodes[length - 2].get();
				}
				
				factor *= (length - 3);
				int index = (int) factor + 1;
				
				LzExpression beforeFirst = this.getNode(index - 1);
				LzExpression first = this.getNode(index);
				LzExpression next = this.getNode(index + 1);
				LzExpression afterNext = this.getNode(index + 2);
				
				return cubicHermite(beforeFirst.get(), first.get(), next.get(), afterNext.get(), factor % 1);
			}
			
			factor *= length - 1;
			int index = (int) factor;
			
			LzExpression first = this.getNode(index);
			LzExpression next = this.getNode(index + 1);
			
			return lerp(first.get(), next.get(), factor % 1);
		}
		
		protected LzExpression getNode(int index)
		{
			if(index < 0)
			{
				return nodes[0];
			} else if(index >= nodes.length)
			{
				return nodes[nodes.length - 1];
			}
			
			return nodes[index];
		}
	}
}