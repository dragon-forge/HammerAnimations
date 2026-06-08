package org.zeith.hammeranims.api.particles.variables;

import dev.zeith.lzvm.jvm.LzExpression;
import org.zeith.hammeranims.api.animation.interp.*;
import org.zeith.hammeranims.api.particles.emitter.*;
import org.zeith.hammeranims.joml.*;

public class ParticleVariables
		extends BaseQuery
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
	protected void registerVariables(IVariableRegistrar reg)
	{
		super.registerVariables(reg);
		reg.registerR("variable.emitter_age", () -> emitter_age);
		reg.registerR("variable.emitter_lifetime", () -> emitter_lifetime);
		reg.registerR("variable.emitter_random_1", () -> emitter_random_1);
		reg.registerR("variable.emitter_random_2", () -> emitter_random_2);
		reg.registerR("variable.emitter_random_3", () -> emitter_random_3);
		reg.registerR("variable.emitter_random_4", () -> emitter_random_4);
		reg.registerR("variable.particle_pos.x", () -> particle_pos.x);
		reg.registerR("variable.particle_pos.y", () -> particle_pos.y);
		reg.registerR("variable.particle_pos.z", () -> particle_pos.z);
		reg.registerR("variable.particle_pos.distance", () -> particle_pos.distance);
		reg.registerR("variable.particle_speed.x", () -> particle_speed.x);
		reg.registerR("variable.particle_speed.y", () -> particle_speed.y);
		reg.registerR("variable.particle_speed.z", () -> particle_speed.z);
		reg.registerR("variable.particle_speed.distance", () -> particle_speed.distance);
		reg.registerR("variable.entity_scale", () -> entity_scale);
		reg.registerR("variable.particle_age", () -> particle_age);
		reg.registerR("variable.particle_lifetime", () -> particle_lifetime);
		reg.registerR("variable.particle_random_1", () -> particle_random_1);
		reg.registerR("variable.particle_random_2", () -> particle_random_2);
		reg.registerR("variable.particle_random_3", () -> particle_random_3);
		reg.registerR("variable.particle_random_4", () -> particle_random_4);
		reg.registerR("variable.particle_bounces", () -> particle_bounces);
	}
	
	public void update(BedrockParticle particle, ParticleEmitter emitter, float partialTicks)
	{
	}
	
	public void putUpdate(String key, LzExpression value)
	{
		if(!key.startsWith("variable.")) return;
		getOrCreateRWVar(key, 0).set(value.get());
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