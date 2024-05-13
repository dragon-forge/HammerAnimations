package org.zeith.hammeranims.api.particles.curve;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.zeith.hammeranims.api.animation.interp.InterpolatedDouble;
import org.zeith.hammeranims.api.particles.variables.ParticleVariables;
import org.zeith.hammeranims.joml.Math;

public class ParticleCurve
	implements InterpolatedDouble<ParticleVariables>
{
	public final ParticleCurveType type;
	public final InterpolatedDouble<ParticleVariables>[] nodes;
	public final InterpolatedDouble<ParticleVariables> input;
	public final InterpolatedDouble<ParticleVariables> range;
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
			InterpolatedDouble<ParticleVariables>[] result = new InterpolatedDouble[nodes.size()];
			
			for(int i = 0, c = result.length; i < c; i++)
				result[i] = InterpolatedDouble.parse(nodes.get(i));
			
			this.nodes = result;
		} else this.nodes = new InterpolatedDouble[] { InterpolatedDouble.constant(0), InterpolatedDouble.constant(1), InterpolatedDouble.constant(0) };
	}
	
	@Override
	public double get(ParticleVariables variables)
	{
		return this.computeCurve(variables, this.input.get(variables) / this.range.get(variables));
	}
	
	private double computeCurve(ParticleVariables variables, double factor)
	{
		int length = this.nodes.length;
		
		if(length == 0)
		{
			return 0;
		} else if(length == 1)
		{
			return this.nodes[0].get(variables);
		}
		
		if(factor < 0)
		{
			factor = -(1 + factor);
		}
		
		factor = Math.clamp(0, 1, factor);
		
		if(this.type == ParticleCurveType.HERMITE)
		{
			if(length <= 3)
			{
				return this.nodes[length - 2].get(variables);
			}
			
			factor *= (length - 3);
			int index = (int) factor + 1;
			
			InterpolatedDouble<ParticleVariables> beforeFirst = this.getNode(index - 1);
			InterpolatedDouble<ParticleVariables> first = this.getNode(index);
			InterpolatedDouble<ParticleVariables> next = this.getNode(index + 1);
			InterpolatedDouble<ParticleVariables> afterNext = this.getNode(index + 2);
			
			return cubicHermite(beforeFirst.get(variables), first.get(variables), next.get(variables), afterNext.get(variables), factor % 1);
		}
		
		factor *= length - 1;
		int index = (int) factor;
		
		InterpolatedDouble<ParticleVariables> first = this.getNode(index);
		InterpolatedDouble<ParticleVariables> next = this.getNode(index + 1);
		
		return lerp(first.get(variables), next.get(variables), factor % 1);
	}
	
	private InterpolatedDouble<ParticleVariables> getNode(int index)
	{
		if(index < 0)
		{
			return this.nodes[0];
		} else if(index >= this.nodes.length)
		{
			return this.nodes[this.nodes.length - 1];
		}
		
		return this.nodes[index];
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
}