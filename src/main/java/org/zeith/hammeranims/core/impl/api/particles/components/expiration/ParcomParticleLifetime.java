package org.zeith.hammeranims.core.impl.api.particles.components.expiration;

import com.google.gson.*;
import org.zeith.hammeranims.api.animation.interp.InterpolatedDouble;
import org.zeith.hammeranims.api.particles.components.itf.IParticleInitialize;
import org.zeith.hammeranims.api.particles.components.itf.IParticleUpdate;
import org.zeith.hammeranims.api.particles.emitter.BedrockParticle;
import org.zeith.hammeranims.api.particles.emitter.ParticleEmitter;
import org.zeith.hammeranims.api.particles.variables.ParticleVariables;

public class ParcomParticleLifetime
		implements IParticleInitialize, IParticleUpdate
{
	public InterpolatedDouble<ParticleVariables> expression = InterpolatedDouble.zero();
	public boolean max;
	
	public ParcomParticleLifetime(JsonElement elem)
	{
		if(!elem.isJsonObject()) return;
		
		JsonObject element = elem.getAsJsonObject();
		JsonElement expression = null;
		
		if(element.has("expiration_expression"))
		{
			expression = element.get("expiration_expression");
			this.max = false;
		} else if(element.has("max_lifetime"))
		{
			expression = element.get("max_lifetime");
			this.max = true;
		} else
		{
			throw new JsonParseException("No expiration_expression or max_lifetime was found in minecraft:particle_lifetime_expression component");
		}
		
		this.expression = InterpolatedDouble.parse(expression);
	}
	
	@Override
	public void apply(ParticleEmitter emitter, BedrockParticle particle)
	{
		if(this.max)
		{
			particle.lifetime = (int) (this.expression.get(emitter.vars) * 20);
		} else
		{
			particle.lifetime = -1;
		}
	}
	
	@Override
	public void update(ParticleEmitter emitter, BedrockParticle particle)
	{
		if(!this.max && this.expression.get(emitter.vars) != 0)
		{
			particle.dead = true;
		}
	}
}
