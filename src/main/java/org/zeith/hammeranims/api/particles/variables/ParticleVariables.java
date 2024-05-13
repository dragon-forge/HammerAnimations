package org.zeith.hammeranims.api.particles.variables;

import org.zeith.hammeranims.api.animation.interp.IVariableAccess;
import org.zeith.hammeranims.api.animation.interp.InterpolatedDouble;
import org.zeith.hammeranims.joml.Vector3d;
import org.zeith.hammeranims.joml.Vector3f;

import java.util.HashMap;
import java.util.function.BiConsumer;

public class ParticleVariables
		extends HashMap<String, Object>
		implements IVariableAccess
{
	public double emitter_age;
	public double emitter_lifetime;
	public double emitter_random_1;
	public double emitter_random_2;
	public double emitter_random_3;
	public double emitter_random_4;
	
	public final DistantVector particle_pos = new DistantVector();
	public final DistantVector particle_speed = new DistantVector();
	
	public double entity_scale = 1F;
	
	public double particle_age;
	public double particle_lifetime;
	public double particle_random_1;
	public double particle_random_2;
	public double particle_random_3;
	public double particle_random_4;
	public int particle_bounces;
	
	@Override
	public void putObjects(BiConsumer<String, Object> storage)
	{
		storage.accept("v", this);
		storage.accept("variable", this);
	}
	
	public void putUpdate(String key, InterpolatedDouble.NumberWrapped<ParticleVariables> value)
	{
		if(!key.startsWith("variable.")) return;
		key = key.substring(9);
		value.update(this);
		put(key, value);
	}
	
	public static class DistantVector
	{
		
		public double x, y, z, distance;
		
		public void set(Vector3d vec)
		{
			distance = vec.length();
			x = vec.x;
			y = vec.y;
			z = vec.z;
		}
		
		public void set(Vector3f vec)
		{
			distance = vec.length();
			x = vec.x;
			y = vec.y;
			z = vec.z;
		}
	}
}