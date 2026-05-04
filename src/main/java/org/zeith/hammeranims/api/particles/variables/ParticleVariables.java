package org.zeith.hammeranims.api.particles.variables;

import org.joml.*;
import org.zeith.hammeranims.api.animation.interp.*;
import org.zeith.hammeranims.api.animation.scope.Variables;
import org.zeith.hammeranims.api.particles.emitter.*;
import org.zeith.hammeranims.core.molang.MolangExpressionParser;
import org.zeith.hammeranims.molang.Expression;
import org.zeith.hammeranims.molang.runtime.*;
import org.zeith.hammeranims.molang.runtime.value.MoValue;

import java.util.List;

import static org.zeith.hammeranims.core.molang.MolangExpressionParser.*;

public class ParticleVariables
		implements IVariableAccess
{
	public final Variables variables = new Variables();
	
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
	
	public ParticleVariables()
	{
		MolangExpressionParser.initializeParticleVariables(this);
	}
	
	public void update(BedrockParticle particle, ParticleEmitter emitter, float partialTicks)
	{
		addCustomVariables(
				MolangExpressionParser.getMolangStorage(this),
				particle,
				emitter,
				partialTicks
		);
	}
	
	protected void addCustomVariables(IVariableStorage env, BedrockParticle particle, ParticleEmitter emitter, float partialTicks)
	{
//		env.store(new String[] {"query", "test_value"}, 1);
	}
	
	public void putUpdate(String key, List<Expression> value)
	{
		if(!key.startsWith("variable.")) return;
		MoLangRuntime runtime = variables.computeIfAbsent(RUNTIME_SYMBOL, RUNTIME_FACTORY);
		MoLangEnvironment env = runtime.getEnvironment();
		env.setValue(key, runtime.execute(value));
	}
	
	public void putUpdate(String key, InterpolatedDouble.NumberWrapped<ParticleVariables> value)
	{
		if(!key.startsWith("variable.")) return;
		MoLangRuntime runtime = variables.computeIfAbsent(RUNTIME_SYMBOL, RUNTIME_FACTORY);
		MoLangEnvironment env = runtime.getEnvironment();
		env.setValue(key, MoValue.of(value.get(this)));
	}
	
	@Override
	public Variables getVariables()
	{
		return variables;
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