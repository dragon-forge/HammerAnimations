package org.zeith.hammeranims.api.particles.variables;

import dev.zeith.lzvm.jvm.LzExpression;
import dev.zeith.lzvm.op.ReadonlyLzVarOp;
import org.joml.*;
import org.zeith.hammeranims.api.animation.interp.BaseQuery;
import org.zeith.hammeranims.api.particles.emitter.*;

import java.util.function.BiConsumer;

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
	protected void registerVariables(BiConsumer<String, ReadonlyLzVarOp> reg)
	{
		super.registerVariables(reg);
		reg.accept("variable.emitter_age", () -> emitter_age);
		reg.accept("variable.emitter_lifetime", () -> emitter_lifetime);
		reg.accept("variable.emitter_random_1", () -> emitter_random_1);
		reg.accept("variable.emitter_random_2", () -> emitter_random_2);
		reg.accept("variable.emitter_random_3", () -> emitter_random_3);
		reg.accept("variable.emitter_random_4", () -> emitter_random_4);
		reg.accept("variable.particle_pos.x", () -> particle_pos.x);
		reg.accept("variable.particle_pos.y", () -> particle_pos.y);
		reg.accept("variable.particle_pos.z", () -> particle_pos.z);
		reg.accept("variable.particle_pos.distance", () -> particle_pos.distance);
		reg.accept("variable.particle_speed.x", () -> particle_speed.x);
		reg.accept("variable.particle_speed.y", () -> particle_speed.y);
		reg.accept("variable.particle_speed.z", () -> particle_speed.z);
		reg.accept("variable.particle_speed.distance", () -> particle_speed.distance);
		reg.accept("variable.entity_scale", () -> entity_scale);
		reg.accept("variable.particle_age", () -> particle_age);
		reg.accept("variable.particle_lifetime", () -> particle_lifetime);
		reg.accept("variable.particle_random_1", () -> particle_random_1);
		reg.accept("variable.particle_random_2", () -> particle_random_2);
		reg.accept("variable.particle_random_3", () -> particle_random_3);
		reg.accept("variable.particle_random_4", () -> particle_random_4);
		reg.accept("variable.particle_bounces", () -> particle_bounces);
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